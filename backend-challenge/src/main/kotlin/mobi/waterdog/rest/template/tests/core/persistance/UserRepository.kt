package mobi.waterdog.rest.template.tests.core.persistance

import mobi.waterdog.rest.template.pagination.PageRequest
import mobi.waterdog.rest.template.tests.core.model.User
import mobi.waterdog.rest.template.tests.core.model.UserSaveCommand
import java.util.UUID

interface UserRepository {
    fun exists(id: UUID): Boolean
    fun getById(id: UUID): User?
    fun save(user: UserSaveCommand): User
    fun count(pageRequest: PageRequest = PageRequest()): Int
    fun delete(id: UUID)
    fun list(pageRequest: PageRequest = PageRequest()): List<User>
}
