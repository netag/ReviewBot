package reviewbot.db

import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

data class MongoConfig(
    val uri: String,
    val dbName: String,
)

class DbClient(config: MongoConfig) {
    private val client: CoroutineClient = KMongo.createClient(config.uri).coroutine
    val db: CoroutineDatabase = client.getDatabase(config.dbName)
}
