package top.authorche.authormail.data.spam
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.wlDs: DataStore<Preferences> by preferencesDataStore("whitelist")

@Singleton
class WhitelistManager @Inject constructor(@ApplicationContext private val ctx: Context) {
    private val KEY = stringSetPreferencesKey("wl")
    val whitelist: Flow<Set<String>> = ctx.wlDs.data.map { it[KEY] ?: emptySet() }
    suspend fun isWhitelisted(addr: String): Boolean =
        (ctx.wlDs.data.first()[KEY] ?: emptySet()).any { it.equals(addr.trim(), ignoreCase = true) }
    suspend fun add(addr: String)    = ctx.wlDs.edit { it[KEY] = (it[KEY] ?: emptySet()) + addr.trim().lowercase() }
    suspend fun remove(addr: String) = ctx.wlDs.edit { it[KEY] = (it[KEY] ?: emptySet()) - addr.trim().lowercase() }
}
