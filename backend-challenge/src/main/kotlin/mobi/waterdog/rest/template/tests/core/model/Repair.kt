package mobi.waterdog.rest.template.tests.core.model

import kotlinx.serialization.Serializable
import mobi.waterdog.rest.template.validation.Validatable
import org.valiktor.Validator
import java.time.Instant
import java.util.UUID

@Serializable
data class Repair(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant,
    val status: RepairStatus,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val repairTypeId: UUID
) : Validatable<Repair>() {
    override fun rules(validator: Validator<Repair>) {}
}

@Serializable
data class RepairSaveCommand(
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant,
    val status: RepairStatus,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val repairTypeId: UUID,
) : Validatable<RepairTypeSaveCommand>() {
    override fun rules(validator: Validator<RepairTypeSaveCommand>) {}
}
