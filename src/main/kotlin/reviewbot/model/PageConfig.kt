package reviewbot.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class PageConfig(
    @Contextual val _id: ObjectId? = null,
    val name: String,
    val id: String,
    val accessToken: String,
)
