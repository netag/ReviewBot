package reviewbot.db

import arrow.core.Either
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.ReturnDocument
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import reviewbot.model.PageConfig

class PagesDao(db: CoroutineDatabase) {
    val collection = db.getCollection<PageConfig>("pages").also { coll ->
        runBlocking { coll.createIndex(ascendingIndex(PageConfig::id), IndexOptions().background(true).unique(true)) }
    }

    suspend fun insert(page: PageConfig): Either<Throwable, Unit> = Either.catch { collection.insertOne(page) }

    suspend fun updateAccessToken(id: String, token: String) = collection.findOneAndUpdate(
        filter = PageConfig::id eq id,
        update = set(PageConfig::accessToken setTo token),
        options = FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
    )

    suspend fun findById(id: String): Either<Throwable, PageConfig?> = Either.catch { collection.findOne(PageConfig::id eq id) }
}
