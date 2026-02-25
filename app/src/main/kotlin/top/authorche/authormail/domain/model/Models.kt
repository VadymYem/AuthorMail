package top.authorche.authormail.domain.model
import kotlinx.serialization.Serializable

enum class EmailFolder { INBOX, SENT, DRAFTS, SPAM, TRASH, STARRED }
enum class OAuthProvider { GMAIL, OUTLOOK, NONE }

data class Email(
    val id: String,
    val from: String,
    val fromName: String,
    val to: List<String>,
    val subject: String,
    val body: String,
    val snippet: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val isStarred: Boolean = false,
    val folder: EmailFolder = EmailFolder.INBOX,
    val spamScore: Float? = null,
    val spamReason: String? = null,
)

data class EmailAccount(
    val address: String,
    val displayName: String,
    val imapHost: String,
    val imapPort: Int = 993,
    val smtpHost: String,
    val smtpPort: Int = 587,
    val password: String = "",
    val oauthProvider: OAuthProvider = OAuthProvider.NONE,
    val oauthAccessToken: String = "",
    val oauthRefreshToken: String = "",
    val oauthExpiry: Long = 0L,
)

@Serializable
data class SpamAnalysisResult(
    val is_spam: Boolean,
    val confidence: Float,
    val reason: String,
    val category: String = "unknown",
)
