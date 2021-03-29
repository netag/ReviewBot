package reviewbot.mappers

import reviewbot.events.Messaging
import reviewbot.model.Response

fun Messaging.content(): Response = Response(
    message.mid,
    timestamp,
    message.text,
    message.replyTo,
    message.attachments,
)
