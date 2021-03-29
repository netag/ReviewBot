package reviewbot.services

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.flatMap
import reviewbot.clients.MessengerApi
import reviewbot.db.PagesDao
import reviewbot.db.ReviewDao
import reviewbot.events.MessageTrigger
import reviewbot.model.PageConfig
import reviewbot.model.Review

class TriggerHandler(
    private val messengerApi: MessengerApi,
    private val pageDao: PagesDao,
    private val reviewDao: ReviewDao,
) {
    suspend fun handle(messageTrigger: MessageTrigger): Either<Throwable, Review> = either<Throwable, Review> {
        val page: PageConfig = fetchPage(messageTrigger.`for`.id).bind()
        val (recipientId: String, messageId: String) = messengerApi.send(page.accessToken, messageTrigger.to, messageTrigger.trigger.content()).bind()

        val review = Review(recipientId, page.id, messageTrigger.trigger, messageId)
        reviewDao.insert(review).bind()
        review
    }

    private suspend fun fetchPage(pageId: String): Either<Throwable, PageConfig> =
        pageDao
            .findById(pageId)
            .flatMap { maybePage ->
                Either
                    .fromNullable(maybePage)
                    .mapLeft { RuntimeException("page config not found") }
            }
}
