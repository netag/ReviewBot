package reviewbot.infra

data class BotConfig(
    val id: String,
    val secret: String,
    val webhookVerifyToken: String,
    val webhookVerifyMode: String,
    val baseUrl: String,
)
