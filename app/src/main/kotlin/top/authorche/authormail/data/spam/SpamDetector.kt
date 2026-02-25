package top.authorche.authormail.data.spam
import top.authorche.authormail.domain.model.Email
import top.authorche.authormail.domain.model.EmailFolder
import top.authorche.authormail.domain.model.SpamAnalysisResult
import javax.inject.Inject
import javax.inject.Singleton

enum class SpamAction { ALLOW, FLAG_SPAM, MOVE_TRASH }
data class SpamDecision(val email: Email, val result: SpamAnalysisResult?, val action: SpamAction)

@Singleton
class SpamDetector @Inject constructor(
    private val gemini: GeminiSpamProvider,
    private val wl: WhitelistManager,
    private val settings: SettingsRepository,
) {
    suspend fun evaluate(email: Email): SpamDecision {
        if (wl.isWhitelisted(email.from)) return SpamDecision(email, null, SpamAction.ALLOW)
        val result = gemini.analyze(email) ?: return SpamDecision(email, null, SpamAction.ALLOW)
        val s = settings.get()
        val action = when {
            result.is_spam && result.confidence >= s.spamThreshold && s.aiAutoBlocking -> SpamAction.MOVE_TRASH
            result.is_spam && result.confidence >= s.spamThreshold                    -> SpamAction.FLAG_SPAM
            else -> SpamAction.ALLOW
        }
        val enriched = email.copy(
            spamScore = result.confidence,
            spamReason = result.reason,
            folder = when (action) {
                SpamAction.MOVE_TRASH -> EmailFolder.TRASH
                SpamAction.FLAG_SPAM  -> EmailFolder.SPAM
                else                  -> email.folder
            }
        )
        return SpamDecision(enriched, result, action)
    }
}
