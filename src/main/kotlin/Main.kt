import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.ktor.ext.Koin
import org.slf4j.event.Level
import reviewbot.infra.koinModules
import reviewbot.routes.pages
import reviewbot.routes.reviews
import reviewbot.routes.triggers
import reviewbot.routes.webhooks

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(CallLogging) {
        level = Level.INFO
    }

    install(Koin) {
        modules(koinModules)
    }

    install(ContentNegotiation) { json(reviewbot.infra.json) }

    install(StatusPages) {
        exception<SerializationException> { cause ->
            log.error("failed parsing request payload", cause)
            call.respond(HttpStatusCode.BadRequest)
        }

        exception<RuntimeException> { cause ->
            log.error("unhandled exception while handling call", cause)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    routing {
        get("/ping") { call.respond(HttpStatusCode.OK, buildJsonObject { put("status", "pong") }) }
        triggers()
        webhooks()
        pages()
        reviews()
    }
}
