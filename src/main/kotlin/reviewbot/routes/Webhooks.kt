package reviewbot.routes

import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import reviewbot.services.*

fun Routing.webhooks() {
    val verifier by inject<WebhookVerificationHandler>()

    route("/webhook") {
        get {
            when (val res = verifier.verify(call)) {
                is Verified -> call.respond(HttpStatusCode.OK, res.challenge)
                MissingParams -> call.respond(HttpStatusCode.BadRequest)
                Failed -> call.respond(HttpStatusCode.Unauthorized)
            }
        }
    }
}
