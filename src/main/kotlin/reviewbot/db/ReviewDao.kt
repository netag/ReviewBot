package reviewbot.db

import arrow.core.Either
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.ReturnDocument
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import reviewbot.events.Messaging
import reviewbot.mappers.content
import reviewbot.model.Review
import java.time.Instant

class ReviewDao(db: CoroutineDatabase) {
    internal val collection = db.getCollection<Review>("reviews").also { coll ->
        runBlocking {
            coll.createIndex(
                index(mapOf(Review::recipient to true, Review::page to true, Review::createdAt to false)),
                IndexOptions().background(true)
            )
        }
    }

    suspend fun insert(review: Review): Either<Throwable, Unit> = Either.catch { collection.insertOne(review) }

    suspend fun addResponseToReview(response: Messaging): Either<Throwable, Review?> {
        return Either.catch {
            collection.findOneAndUpdate(
                filter = and(
                    eq(Review::recipient.name, response.sender.id),
                    eq(Review::page.name, response.recipient.id),
                    Review::createdAt gte response.timestamp.minusSeconds(5 * 60),
                ),
                update = combine(
                    setValue(Review::updatedAt, Instant.now()),
                    addToSet(Review::responses, response.content()),
                ),
                FindOneAndUpdateOptions()
                    .sort(descending(Review::createdAt))
                    .returnDocument(ReturnDocument.AFTER)
            )
        }
    }

    suspend fun findLatest(userId: String, pageId: String, limit: Int?): Either<Throwable, List<Review>> {
        val filter = and(
            eq(Review::recipient.name, userId),
            eq(Review::page.name, pageId),
        )

        return Either.catch {
            collection
                .find(filter)
                .sort(descending(Review::createdAt))
                .limit(limit ?: 10).toList()
        }
    }
}
