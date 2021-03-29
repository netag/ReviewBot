package reviewbot.infra

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import org.bson.types.ObjectId
import java.time.Instant

val json = kotlinx.serialization.json.Json {
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
        contextual(ObjectId::class, ObjectIdSerializer)
        contextual(Instant::class, InstantSerializer)
    }
}

object ObjectIdSerializer : KSerializer<ObjectId> {
    override val descriptor: SerialDescriptor = String.serializer().descriptor

    override fun deserialize(decoder: Decoder): ObjectId {
        val oid = decoder.decodeString()
        return try {
            ObjectId(oid)
        } catch (_: IllegalArgumentException) {
            throw SerializationException("id: $oid not in valid format")
        }
    }

    override fun serialize(encoder: Encoder, value: ObjectId) {
        encoder.encodeString(value.toString())
    }
}

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = Long.serializer().descriptor

    override fun deserialize(decoder: Decoder): Instant {
        val ts = decoder.decodeLong()
        return Instant.ofEpochMilli(ts)
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeLong(value.toEpochMilli())
    }
}
