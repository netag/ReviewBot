@file:Suppress("PropertyName")

package reviewbot.infra

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import reviewbot.db.MongoConfig

data class BotConfig(
    val id: String,
    val secret: String,
    val webhookVerifyToken: String,
    val webhookVerifyMode: String,
    val baseUrl: String,
)

class Settings(config: Config = ConfigFactory.load()) {
    val BotConfig = config.extract<BotConfig>("bot")
    val MongoConfig = config.extract<MongoConfig>("mongo")
}
