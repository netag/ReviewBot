package reviewbot

import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.json.shouldNotContainJsonKey
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.style.FreeSpec
import io.kotest.extensions.mockserver.MockServerListener
import io.kotest.matchers.shouldBe
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import module
import org.litote.kmongo.EMPTY_BSON
import org.litote.kmongo.coroutine.CoroutineCollection
import org.mockserver.client.MockServerClient
import org.mockserver.matchers.Times
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.MediaType
import reviewbot.db.DbClient
import reviewbot.db.PagesDao
import reviewbot.db.ReviewDao
import reviewbot.infra.Settings
import java.time.Instant

suspend fun CoroutineCollection<*>.deleteAll() = deleteMany(EMPTY_BSON)

class ReviewBotSpec : FreeSpec({

    listener(MockServerListener(80))

    beforeSpec {
        val s = Settings()
        val dbClient = DbClient(s.MongoConfig)
        ReviewDao(dbClient.db).collection.deleteAll()
        PagesDao(dbClient.db).collection.deleteAll()
    }

    "# trigger-webhook flow" - {
        fun TestApplicationEngine.triggerCall(): TestApplicationCall = handleRequest(HttpMethod.Post, "/trigger") {
            val payload = """
                {
                    "to": "5315069535230088",
                    "for": "106964551485187",
                    "trigger": {
                        "type": "reviewbot.events.Trigger.SaySomething",
                        "something": "test-me"
    
                    }
                }
            """.trimIndent()
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(payload)
        }

        fun TestApplicationEngine.lastReview(): TestApplicationCall = handleRequest(HttpMethod.Get, "/reviews?psid=5315069535230088&pageId=106964551485187&limit=1")

        "when page is not configured" - {
            "returns an error" {
                withTestApplication(Application::module) {
                    with(triggerCall()) {
                        requestHandled shouldBe true
                        response shouldHaveStatus HttpStatusCode.InternalServerError
                    }
                }
            }
        }

        "when page is configured" - {
            fun TestApplicationEngine.setupPage(): TestApplicationCall = handleRequest(HttpMethod.Post, "/page") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody("""{ "name": "test-me", "id": "106964551485187", "accessToken": "fake" }""")
            }

            beforeEach {
                MockServerClient("localhost", 80)
                    .`when`(request().withMethod("POST").withContentType(MediaType.APPLICATION_JSON), Times.once())
                    .respond(
                        response()
                            .withStatusCode(200)
                            .withContentType(MediaType.APPLICATION_JSON)
                            .withBody("""{"recipient_id": "5315069535230088", "message_id": "1"}""")
                    )
            }

            "saves the sent response" {
                withTestApplication(Application::module) {
                    setupPage()

                    with(triggerCall()) {
                        response shouldHaveStatus HttpStatusCode.OK
                        val content = response.content
                        content.shouldContainJsonKeyValue("$.messageId", "1")
                        content.shouldNotContainJsonKey("$.responses")
                    }
                }
            }

            "and user replies" - {
                fun TestApplicationEngine.webhook(replyTs: Instant): TestApplicationCall = handleRequest(HttpMethod.Post, "/webhook") {
                    val reply =
                        """{"object":"page","entry":[{"id":"106964551485187","time":1616958576924,"messaging":[{"sender":{"id":"5315069535230088"},"recipient":{"id":"106964551485187"},"timestamp":${replyTs.toEpochMilli()},"message":{"mid":"mid1","text":"bla bla"}}]}]}"""
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    setBody(reply)
                }

                "adds the reply to the original message" {
                    withTestApplication(Application::module) {
                        val ts = Instant.now()
                        with(webhook(ts)) {
                            response shouldHaveStatus HttpStatusCode.OK
                        }

                        with(lastReview()) {
                            response shouldHaveStatus HttpStatusCode.OK
                            val content = response.content
                            content.shouldContainJsonKeyValue("$[0].messageId", "1")
                            content.shouldContainJsonKeyValue("$[0].responses[0].mid", "mid1")
                            content.shouldContainJsonKeyValue("$[0].responses[0].text", "bla bla")
                        }
                    }
                }
            }
        }
    }
})
