package mobi.waterdog.rest.template.tests.core.model

import kotlinx.serialization.Serializable
import mobi.waterdog.rest.template.validation.Validatable
import org.valiktor.Validator
import org.valiktor.functions.isNotBlank
import java.util.UUID

@Serializable
data class RepairType(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val description: String
) : Validatable<RepairType>() {
    override fun rules(validator: Validator<RepairType>) {
        validator
            .validate(RepairType::name)
            .isNotBlank()
    }
}

@Serializable
data class RepairTypeSaveCommand(
    val name: String,
    val description: String
) : Validatable<RepairTypeSaveCommand>() {
    override fun rules(validator: Validator<RepairTypeSaveCommand>) {
        validator
            .validate(RepairTypeSaveCommand::name)
            .isNotBlank()
    }
}
