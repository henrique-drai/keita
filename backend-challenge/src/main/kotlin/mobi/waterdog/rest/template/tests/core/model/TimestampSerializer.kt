package mobi.waterdog.rest.template.tests.core.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.sql.Timestamp

object TimestampSerializer : KSerializer<Timestamp> {
    override val descriptor = PrimitiveSerialDescriptor("Timestamp", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: Timestamp) = encoder.encodeLong(value.time)
    override fun deserialize(decoder: Decoder): Timestamp = Timestamp(decoder.decodeLong())
}