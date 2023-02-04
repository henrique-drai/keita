package mobi.waterdog.rest.template.tests.core.model

import kotlinx.serialization.Serializable
import mobi.waterdog.rest.template.validation.Validatable
import org.valiktor.Validator
import org.valiktor.functions.isNotBlank

@Serializable
data class RepairType(val name: String, val description: String) : Validatable<RepairType>() {
    override fun rules(validator: Validator<RepairType>) {
        validator
            .validate(RepairType::name)
            .isNotBlank()
        validator
            .validate(RepairType::description)

    }
}