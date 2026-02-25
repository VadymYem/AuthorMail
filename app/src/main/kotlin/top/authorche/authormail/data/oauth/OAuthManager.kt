package top.authorche.authormail.data.oauth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import top.authorche.authormail.domain.model.OAuthProvider
import javax.inject.Inject
import javax.inject.Singleton

// ── ЗАМІНИ на свої дані з Google Cloud Console / Azure ─────────────────────
// Gmail: https://console.cloud.google.com → APIs & Services → Credentials
// Outlook: https://portal.azure.com → App registrations
private object OAuthConfig {
    // Gmail
    const val GMAIL_CLIENT_ID     = "YOUR_GMAIL_CLIENT_ID.apps.googleusercontent.com"
    const val GMAIL_CLIENT_SECRET = "YOUR_GMAIL_CLIENT_SECRET"
    const val GMAIL_AUTH_URL      = "https://accounts.google.com/o/oauth2/v2/auth"
    const val GMAIL_TOKEN_URL     = "https://oauth2.googleapis.com/token"
    const val GMAIL_SCOPE         = "https://mail.google.com/"

    // Outlook
    const val OUTLOOK_CLIENT_ID   = "YOUR_OUTLOOK_CLIENT_ID"
    const val OUTLOOK_AUTH_URL    = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize"
    const val OUTLOOK_TOKEN_URL   = "https://login.microsoftonline.com/common/oauth2/v2.0/token"
    const val OUTLOOK_SCOPE       = "https://outlook.office.com/IMAP.AccessAsUser.All offline_access"

    // Redirect URI — має співпадати з тим що в Google/Azure Console
    const val REDIRECT_URI = "top.authorche.authormail://oauth2callback"
}

data class OAuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
)

@Singleton
class OAuthManager @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val http: OkHttpClient,
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val TAG = "OAuthManager"

    /** Відкриває браузер для авторизації (Custom Tab як у Thunderbird) */
    fun openAuthBrowser(provider: OAuthProvider) {
        val (authUrl, clientId, scope) = when (provider) {
            OAuthProvider.GMAIL -> Triple(OAuthConfig.GMAIL_AUTH_URL, OAuthConfig.GMAIL_CLIENT_ID, OAuthConfig.GMAIL_SCOPE)
            OAuthProvider.OUTLOOK -> Triple(OAuthConfig.OUTLOOK_AUTH_URL, OAuthConfig.OUTLOOK_CLIENT_ID, OAuthConfig.OUTLOOK_SCOPE)
            OAuthProvider.NONE -> return
        }
        val uri = Uri.parse(authUrl).buildUpon()
            .appendQueryParameter("client_id",     clientId)
            .appendQueryParameter("redirect_uri",  OAuthConfig.REDIRECT_URI)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("scope",         scope)
            .appendQueryParameter("access_type",   "offline")
            .appendQueryParameter("prompt",        "consent")
            .build()

        try {
            CustomTabsIntent.Builder().build().launchUrl(ctx, uri)
        } catch (e: Exception) {
            // Fallback — звичайний браузер
            ctx.startActivity(Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
        Log.d(TAG, "OAuth browser opened for $provider")
    }

    /** Обмін коду авторизації на токени */
    suspend fun exchangeCode(provider: OAuthProvider, code: String): OAuthTokens? =
        withContext(Dispatchers.IO) {
            val (tokenUrl, clientId, secret) = when (provider) {
                OAuthProvider.GMAIL -> Triple(OAuthConfig.GMAIL_TOKEN_URL, OAuthConfig.GMAIL_CLIENT_ID, OAuthConfig.GMAIL_CLIENT_SECRET)
                OAuthProvider.OUTLOOK -> Triple(OAuthConfig.OUTLOOK_TOKEN_URL, OAuthConfig.OUTLOOK_CLIENT_ID, "")
                OAuthProvider.NONE -> return@withContext null
            }
            val body = FormBody.Builder()
                .add("code", code)
                .add("client_id", clientId)
                .apply { if (secret.isNotBlank()) add("client_secret", secret) }
                .add("redirect_uri", OAuthConfig.REDIRECT_URI)
                .add("grant_type", "authorization_code")
                .build()
            try {
                val resp = http.newCall(Request.Builder().url(tokenUrl).post(body).build()).execute()
                val raw = resp.body?.string() ?: return@withContext null
                val j = json.parseToJsonElement(raw).jsonObject
                OAuthTokens(
                    accessToken  = j["access_token"]!!.jsonPrimitive.content,
                    refreshToken = j["refresh_token"]?.jsonPrimitive?.content ?: "",
                    expiresIn    = j["expires_in"]?.jsonPrimitive?.content?.toLong() ?: 3600L,
                ).also { Log.d(TAG, "Tokens received for $provider") }
            } catch (e: Exception) { Log.e(TAG, "Token exchange failed", e); null }
        }

    /** Оновлення access token через refresh token */
    suspend fun refreshToken(provider: OAuthProvider, refreshToken: String): OAuthTokens? =
        withContext(Dispatchers.IO) {
            val (tokenUrl, clientId, secret) = when (provider) {
                OAuthProvider.GMAIL -> Triple(OAuthConfig.GMAIL_TOKEN_URL, OAuthConfig.GMAIL_CLIENT_ID, OAuthConfig.GMAIL_CLIENT_SECRET)
                OAuthProvider.OUTLOOK -> Triple(OAuthConfig.OUTLOOK_TOKEN_URL, OAuthConfig.OUTLOOK_CLIENT_ID, "")
                OAuthProvider.NONE -> return@withContext null
            }
            val body = FormBody.Builder()
                .add("refresh_token", refreshToken)
                .add("client_id", clientId)
                .apply { if (secret.isNotBlank()) add("client_secret", secret) }
                .add("grant_type", "refresh_token")
                .build()
            try {
                val resp = http.newCall(Request.Builder().url(tokenUrl).post(body).build()).execute()
                val raw = resp.body?.string() ?: return@withContext null
                val j = json.parseToJsonElement(raw).jsonObject
                OAuthTokens(
                    accessToken  = j["access_token"]!!.jsonPrimitive.content,
                    refreshToken = refreshToken,
                    expiresIn    = j["expires_in"]?.jsonPrimitive?.content?.toLong() ?: 3600L,
                )
            } catch (e: Exception) { Log.e(TAG, "Refresh failed", e); null }
        }
}
