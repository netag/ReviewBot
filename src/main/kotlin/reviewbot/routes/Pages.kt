package reviewbot.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import reviewbot.db.PagesDao
import reviewbot.model.PageConfig

fun Routing.pages() {
    val service by inject<PagesDao>()

    post("/page") {
        val conf = call.receive<PageConfig>()
        val status = service.insert(conf).fold({ HttpStatusCode.InternalServerError }, { HttpStatusCode.OK })
        call.respond(status)
    }
}
