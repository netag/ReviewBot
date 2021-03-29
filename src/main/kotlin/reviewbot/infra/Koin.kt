package reviewbot.infra

import io.ktor.client.*
import org.koin.dsl.module
import reviewbot.clients.MessengerApi
import reviewbot.db.DbClient
import reviewbot.db.PagesDao
import reviewbot.db.ReviewDao
import reviewbot.services.TriggerHandler
import reviewbot.services.WebhookEventHandler
import reviewbot.services.WebhookVerificationHandler

val koinModules = module {
    single<Settings>(createdAtStart = true) { Settings() }
    single<HttpClient> { configure() }
    single<MessengerApi> { MessengerApi(get<Settings>().BotConfig, get()) }

    single<DbClient> { DbClient(get<Settings>().MongoConfig) }
    single<PagesDao> { PagesDao(get<DbClient>().db) }
    single<ReviewDao> { ReviewDao(get<DbClient>().db) }

    single<TriggerHandler> { TriggerHandler(get(), get(), get()) }
    single<WebhookEventHandler> { WebhookEventHandler(get()) }
    single<WebhookVerificationHandler> { WebhookVerificationHandler(get<Settings>().BotConfig) }
}
