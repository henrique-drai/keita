package mobi.waterdog.rest.template.tests.core.service

import mobi.waterdog.rest.template.tests.core.model.User
import java.util.*

interface UserService {
    suspend fun getUserById(userId: UUID): User?
}