package reviewbot.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import reviewbot.db.ReviewDao

fun Routing.reviews() {
    val dao by inject<ReviewDao>()

    route("/reviews") {
        get {
            val psid = call.parameters["psid"]
            val pageId = call.parameters["pageId"]
            val limit = call.parameters["limit"]?.toInt()

            val (code, body) = when {
                psid == null -> HttpStatusCode.BadRequest to err("missing psid param")
                pageId == null -> HttpStatusCode.BadRequest to err("missing pageId param")
                else -> dao.findLatest(psid, pageId, limit).fold(
                    ifLeft = { HttpStatusCode.InternalServerError to err(it.message!!) },
                    ifRight = { HttpStatusCode.OK to it }
                )
            }
            call.respond(code, body)
        }
    }
}
