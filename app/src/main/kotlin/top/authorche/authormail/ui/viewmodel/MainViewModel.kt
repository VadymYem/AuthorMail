package top.authorche.authormail.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import top.authorche.authormail.data.spam.AppSettings
import top.authorche.authormail.data.spam.SettingsRepository
import top.authorche.authormail.data.spam.WhitelistManager
import top.authorche.authormail.domain.model.Email
import top.authorche.authormail.domain.model.EmailFolder
import javax.inject.Inject

data class UiState(
    val emails: List<Email> = emptyList(),
    val folder: EmailFolder = EmailFolder.INBOX,
    val loading: Boolean = false,
    val error: String? = null,
    val settings: AppSettings = AppSettings(),
    val whitelist: Set<String> = emptySet(),
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository,
    private val wl: WhitelistManager,
) : ViewModel() {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(settingsRepo.flow, wl.whitelist) { s, w -> Pair(s, w) }
                .collect { (s, w) -> _state.update { it.copy(settings = s, whitelist = w) } }
        }
        _state.update { it.copy(emails = demoEmails()) }
    }

    fun saveKey(k: String)           = viewModelScope.launch { settingsRepo.setKey(k) }
    fun setAiOn(v: Boolean)          = viewModelScope.launch { settingsRepo.setAiOn(v) }
    fun setBlock(v: Boolean)         = viewModelScope.launch { settingsRepo.setBlock(v) }
    fun setThresh(v: Float)          = viewModelScope.launch { settingsRepo.setThresh(v) }
    fun addWl(addr: String)          = viewModelScope.launch { wl.add(addr) }
    fun removeWl(addr: String)       = viewModelScope.launch { wl.remove(addr) }
    fun dismissError()               = _state.update { it.copy(error = null) }
    fun selectFolder(f: EmailFolder) = _state.update { it.copy(folder = f) }

    private fun demoEmails() = listOf(
        Email(
            id = "1", from = "alice@example.com", fromName = "Alice",
            to = listOf("me@authormail.com"),
            subject = "Project Update",
            body = "Hello, just a quick update on the project.",
            snippet = "Hello, just a quick update on the project.",
            timestamp = System.currentTimeMillis() - 3_600_000L,
        ),
        Email(
            id = "2", from = "spam@win-prize.com", fromName = "PRIZE WINNER!!!",
            to = listOf("me@authormail.com"),
            subject = "You WON \$1,000,000!",
            body = "Click here to claim your prize immediately!!!",
            snippet = "Click here to claim your prize immediately!!!",
            timestamp = System.currentTimeMillis() - 7_200_000L,
            spamScore = 0.97f,
            spamReason = "Prize scam — classic phishing pattern",
            folder = EmailFolder.SPAM,
        ),
        Email(
            id = "3", from = "bob@work.com", fromName = "Bob Smith",
            to = listOf("me@authormail.com"),
            subject = "Meeting Tomorrow",
            body = "Don't forget we have a meeting tomorrow at 10am.",
            snippet = "Don't forget we have a meeting tomorrow at 10am.",
            timestamp = System.currentTimeMillis() - 86_400_000L,
        ),
    )
}
