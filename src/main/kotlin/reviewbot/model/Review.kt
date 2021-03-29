package reviewbot.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import reviewbot.events.Attachments
import reviewbot.events.ReplyTo
import reviewbot.events.Trigger
import java.time.Instant

@OptIn(ExperimentalSerializationApi::class)
@Serializable
inline class Recipient(val id: String)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
inline class Page(val id: String)

@Serializable
data class Response(
    val mid: String,
    @Contextual val timestamp: Instant,
    val text: String? = null,
    val replyTo: ReplyTo? = null,
    val attachments: List<Attachments>? = null,
)

@Serializable
data class Review(
    @Contextual val _id: ObjectId,
    @Contextual val createdAt: Instant,
    @Contextual val updatedAt: Instant,
    val recipient: Recipient,
    val page: Page,
    val triggeredBy: Trigger? = null,
    val messageId: String,
    val responses: List<Response> = emptyList()
) {
    companion object {
        operator fun invoke(recipientId: String, pageId: String, messageId: String, message: Response): Review {
            return Review(
                ObjectId.get(),
                Instant.now(),
                Instant.now(),
                Recipient(recipientId),
                Page(pageId),
                null,
                messageId,
                listOf(message),
            )
        }
    }
}
