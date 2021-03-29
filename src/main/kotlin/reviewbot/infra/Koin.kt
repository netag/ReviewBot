package reviewbot.infra

import io.ktor.client.*
import org.koin.dsl.module
import reviewbot.clients.MessengerApi
import reviewbot.db.DbClient

val koinModules = module {
    single<Settings>(createdAtStart = true) { Settings() }
    single<HttpClient> { configure() }
    single<MessengerApi> { MessengerApi(get<Settings>().BotConfig, get()) }

    single<DbClient> { DbClient(get<Settings>().MongoConfig) }
}
