package reviewbot.services

import io.ktor.application.*
import reviewbot.infra.BotConfig

sealed class VerificationStatus
data class Verified(val challenge: String) : VerificationStatus()
object MissingParams : VerificationStatus()
object Failed : VerificationStatus()

class WebhookVerificationHandler(private val config: BotConfig) {
    fun verify(call: ApplicationCall): VerificationStatus {
        val hubMode = call.request.queryParameters["hub.mode"]
        val verifyToken = call.request.queryParameters["hub.verify_token"]
        val hubChallenge = call.request.queryParameters["hub.challenge"]

        return when {
            hubMode == null || verifyToken == null || hubChallenge == null -> MissingParams
            hubMode == config.webhookVerifyMode && verifyToken == config.webhookVerifyToken -> Verified(hubChallenge)
            else -> Failed
        }
    }
}
