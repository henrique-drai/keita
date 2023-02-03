package mobi.waterdog.rest.template.tests.core.persistance

import mobi.waterdog.rest.template.tests.core.model.User
import java.util.UUID

interface UserRepository {
    fun exists(id: UUID): Boolean
    fun getById(id: UUID): User?
}
