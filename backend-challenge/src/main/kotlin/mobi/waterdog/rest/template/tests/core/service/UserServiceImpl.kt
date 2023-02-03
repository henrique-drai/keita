package mobi.waterdog.rest.template.tests.core.service

import mobi.waterdog.rest.template.database.DatabaseConnection
import mobi.waterdog.rest.template.tests.core.model.User
import mobi.waterdog.rest.template.tests.core.persistance.UserRepository
import org.slf4j.LoggerFactory
import java.util.UUID

class UserServiceImpl (
    private val userRepository: UserRepository,
    private val dbc: DatabaseConnection
) :
    UserService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override suspend fun getUserById(userId: UUID): User? {
        return dbc.suspendedQuery { userRepository.getById(userId) }
    }
}