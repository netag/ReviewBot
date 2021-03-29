package reviewbot.clients

import arrow.core.Either
import arrow.core.left
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import reviewbot.infra.BotConfig
import reviewbot.model.Recipient

@Serializable
data class Response(
    val recipient_id: String,
    val message_id: String,
)

class MessengerApi(
    config: BotConfig,
    private val httpClient: HttpClient
) {
    private val url = "${config.baseUrl}/messages"

    @Serializable
    data class Payload(
        val recipient: Id,
        val message: Inner,
    ) {

        @Serializable
        data class Id(val id: String)

        @Serializable
        data class Inner(
            val text: String
        )
    }

    suspend fun send(from: String, to: Recipient, content: String): Either<Throwable, Response> {
        val response = httpClient.post<HttpResponse>(url) {
            parameter(AccessToken, from)
            contentType(ContentType.Application.Json)
            body = Payload(Payload.Id(to.id), Payload.Inner(content))
        }

        if (response.status.value != 200) {
            return RuntimeException("failed sending message").left()
        }

        return Either.catch { response.receive<Response>() }
    }

    companion object {
        const val AccessToken = "access_token"
    }
}
