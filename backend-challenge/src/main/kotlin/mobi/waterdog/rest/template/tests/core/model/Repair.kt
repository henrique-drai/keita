package mobi.waterdog.rest.template.tests.core.model

import kotlinx.serialization.Serializable
import mobi.waterdog.rest.template.validation.Validatable
import org.valiktor.Validator
import java.sql.Timestamp
import java.util.UUID

@Serializable
data class Repair(
    @Serializable(with = TimestampSerializer::class)
    val createdAt : Timestamp,
    val status : RepairStatus,
    @Serializable(with = UUIDSerializer::class)
    val userId : UUID,
    @Serializable(with = UUIDSerializer::class)
    val repairTypeId : UUID
) : Validatable<Repair>(){
    override fun rules(validator: Validator<Repair>) {}
}