package reviewbot.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import reviewbot.events.MessageTrigger
import reviewbot.services.TriggerHandler

fun Routing.triggers() {
    val handler by inject<TriggerHandler>()

    post("/trigger") {
        val payload = call.receive<MessageTrigger>()
        handler.handle(payload).fold(
            ifLeft = { call.respond(HttpStatusCode.InternalServerError, err(it.message!!)) },
            ifRight = { review -> call.respond(HttpStatusCode.OK, review) }
        )
    }
}
