package reviewbot.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.json.JsonObject
import org.koin.ktor.ext.inject
import reviewbot.services.*

fun Routing.webhooks() {
    val verifier by inject<WebhookVerificationHandler>()
    val handler by inject<WebhookEventHandler>()

    route("/webhook") {
        get {
            when (val res = verifier.verify(call)) {
                is Verified -> call.respond(HttpStatusCode.OK, res.challenge)
                MissingParams -> call.respond(HttpStatusCode.BadRequest)
                Failed -> call.respond(HttpStatusCode.Unauthorized)
            }
        }

        post {
            val json = call.receive<JsonObject>()
            application.log.info("webhook post: $json")
            when (handler.handle(json)) {
                EventHandled.NotSupported -> call.respond(HttpStatusCode.BadRequest)
                EventHandled.Success -> call.respond("ok")
                EventHandled.Failure -> call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
