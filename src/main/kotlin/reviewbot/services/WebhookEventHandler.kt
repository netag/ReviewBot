package reviewbot.services

import arrow.core.flatMap
import arrow.core.right
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.*
import reviewbot.db.ReviewDao
import reviewbot.events.Entry
import reviewbot.events.Messaging
import reviewbot.infra.json
import reviewbot.mappers.content
import reviewbot.model.Review

sealed class EventHandled {
    object NotSupported : EventHandled()
    object Success : EventHandled()
    object Failure : EventHandled()
}

class WebhookEventHandler(private val reviewDao: ReviewDao) {
    suspend fun handle(event: JsonObject): EventHandled {
        if (!isValid(event)) {
            return EventHandled.NotSupported
        }

        val entries = json.decodeFromJsonElement(ListSerializer(Entry.serializer()), event["entry"]!!)
        val handled = entries.flatMap(Entry::messaging).map { messaging: Messaging ->
            reviewDao.addResponseToReview(messaging).flatMap { maybeReview ->
                // found a matching "thread"
                if (maybeReview != null) {
                    return@flatMap maybeReview.right()
                }

                // new "conversation", adding new entry
                val r = Review(messaging.sender.id, messaging.recipient.id, messaging.message.mid, messaging.content())
                reviewDao.insert(r)
            }
        }

        return if (handled.any { it.isLeft() }) EventHandled.Failure else EventHandled.Success
    }

    private fun isValid(event: JsonObject): Boolean {
        return event["object"]?.jsonPrimitive?.content == "page"
    }
}
