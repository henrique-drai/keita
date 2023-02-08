package mobi.waterdog.rest.template.tests.core.model

import kotlinx.serialization.Serializable
import mobi.waterdog.rest.template.validation.Validatable
import org.valiktor.Validator
import org.valiktor.functions.hasSize
import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotBlank
import java.util.UUID

@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val email: String
) : Validatable<User>() {
    override fun rules(validator: Validator<User>) {
        validator
            .validate(User::name)
            .hasSize(3, 255)
        validator
            .validate(User::email)
            .isNotBlank()
            .isEmail()
    }
}

@Serializable
data class UserSaveCommand(
    val name: String,
    val email: String
) : Validatable<UserSaveCommand>() {
    override fun rules(validator: Validator<UserSaveCommand>) {
        validator
            .validate(UserSaveCommand::name)
            .hasSize(3, 255)
        validator
            .validate(UserSaveCommand::email)
            .isNotBlank()
            .isEmail()
    }
}
