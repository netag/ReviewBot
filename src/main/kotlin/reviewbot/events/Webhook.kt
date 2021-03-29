@file:UseSerializers(InstantSerializer::class)

package reviewbot.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import reviewbot.infra.InstantSerializer
import java.time.Instant

/*
    {
      "id": "106964551485187",
      "time": 1616927694501,
      "messaging": [
        {
          "sender": {
            "id": "5315069535230088"
          },
          "recipient": {
            "id": "106964551485187"
          },
          "timestamp": 1616927693979,
          "message": {
            "mid": "m_p2HmG36mbZMc5o1hApbML0fSWIhx6GymbD7RDAtdTeweXahbD3nrApsUV9ewFMrLmyGSgKoujtvrPCM6C9-pew",
            "text": "hop hop"
          }
        }
      ]
    }
 */
@Serializable
data class Entry(
    val id: String,
    val time: Instant,
    val messaging: List<Messaging>
)

@Serializable
data class Id(val id: String)

@Serializable
data class Messaging(
    val sender: Id,
    val recipient: Id,
    val timestamp: Instant,
    val message: Message,
)

@Serializable
data class ReplyTo(val mid: String)

@Serializable
enum class AttachmentType { image, video, audio, file }

@Serializable
data class AttachmentPayload(val url: String)

@Serializable
data class Attachments(
    val type: AttachmentType,
    val payload: AttachmentPayload,
)

@Serializable
data class Message(
    val mid: String,
    val text: String? = null,
    @SerialName("reply_to")
    val replyTo: ReplyTo? = null,
    val attachments: List<Attachments>? = null,
)
