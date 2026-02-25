package top.authorche.authormail.data.spam
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import top.authorche.authormail.domain.model.Email
import top.authorche.authormail.domain.model.SpamAnalysisResult
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "GeminiSpam"
private const val URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"

// Системний промт — надсилається прихованo разом з кожним листом
private val SYSTEM_PROMPT = """
You are a professional email security analyst.
Analyze the email below and determine if it is spam, phishing, or unwanted content.

Consider: suspicious links, manipulative language, money promises, unknown senders,
credential requests, urgent fake warnings.

IMPORTANT: Reply ONLY with a valid JSON object — no markdown, no text outside JSON.
Format: {"is_spam": true/false, "confidence": 0.0-1.0, "reason": "short reason in English", "category": "spam|phishing|promo|personal|unknown"}
""".trimIndent()

@Singleton
class GeminiSpamProvider @Inject constructor(
    private val whitelist: WhitelistManager,
    private val settings: SettingsRepository,
    private val http: OkHttpClient,
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    suspend fun analyze(email: Email): SpamAnalysisResult? = withContext(Dispatchers.IO) {
        if (!settings.get().aiSpamEnabled) return@withContext null
        if (whitelist.isWhitelisted(email.from)) return@withContext null
        val key = settings.get().geminiApiKey.takeIf { it.isNotBlank() } ?: return@withContext null

        val payload = buildPayload("${email.fromName} <${email.from}>", email.subject, email.snippet.take(500))
        val req = Request.Builder()
            .url("$URL?key=$key")
            .post(payload.toRequestBody("application/json".toMediaType()))
            .build()
        try {
            http.newCall(req).execute().use { resp ->
                if (!resp.isSuccessful) { Log.e(TAG, "HTTP ${resp.code}"); return@withContext null }
                parseResponse(resp.body?.string() ?: return@withContext null)
            }
        } catch (e: Exception) { Log.e(TAG, "analyze failed", e); null }
    }

    private fun buildPayload(sender: String, subject: String, snippet: String): String = """
    {"system_instruction":{"parts":[{"text":${SYSTEM_PROMPT.esc()}}]},
     "contents":[{"parts":[{"text":"From: $sender\nSubject: $subject\nBody: $snippet"}]}],
     "generationConfig":{"temperature":0.1,"maxOutputTokens":256}}
    """.trimIndent()

    private fun String.esc() = "\"" + replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n") + "\""

    private fun parseResponse(raw: String): SpamAnalysisResult? = try {
        val text = json.parseToJsonElement(raw)
            .jsonObject["candidates"]!!.jsonArray[0]
            .jsonObject["content"]!!.jsonObject["parts"]!!.jsonArray[0]
            .jsonObject["text"]!!.jsonPrimitive.content
            .trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        json.decodeFromString<SpamAnalysisResult>(text).also { Log.d(TAG, "result=$it") }
    } catch (e: Exception) { Log.e(TAG, "parse failed: $raw", e); null }
}
