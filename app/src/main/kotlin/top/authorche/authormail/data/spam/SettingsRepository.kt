package top.authorche.authormail.data.spam

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.ds: DataStore<Preferences> by preferencesDataStore("settings")

data class AppSettings(
    val geminiApiKey: String = "",
    val aiSpamEnabled: Boolean = false,
    val aiAutoBlocking: Boolean = false,
    val spamThreshold: Float = 0.75f,
    val dynamicColor: Boolean = true,
)

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val ctx: Context
) {
    private val KEY     = stringPreferencesKey("gemini_key")
    private val AI_ON   = booleanPreferencesKey("ai_on")
    private val BLOCK   = booleanPreferencesKey("auto_block")
    private val THRESH  = floatPreferencesKey("threshold")
    private val DYN_CLR = booleanPreferencesKey("dyn_color")

    val flow: Flow<AppSettings> = ctx.ds.data.map { p ->
        AppSettings(
            geminiApiKey   = p[KEY]     ?: "",
            aiSpamEnabled  = p[AI_ON]   ?: false,
            aiAutoBlocking = p[BLOCK]   ?: false,
            spamThreshold  = p[THRESH]  ?: 0.75f,
            dynamicColor   = p[DYN_CLR] ?: true,
        )
    }

    suspend fun get() = flow.first()
    suspend fun setKey(v: String)    = ctx.ds.edit { it[KEY]    = v }
    suspend fun setAiOn(v: Boolean)  = ctx.ds.edit { it[AI_ON]  = v }
    suspend fun setBlock(v: Boolean) = ctx.ds.edit { it[BLOCK]  = v }
    suspend fun setThresh(v: Float)  = ctx.ds.edit { it[THRESH] = v }
}
