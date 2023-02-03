package mobi.waterdog.rest.template.tests.core.persistance.sql

import mobi.waterdog.rest.template.tests.core.model.User
import mobi.waterdog.rest.template.tests.core.persistance.UserRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.select
import java.util.UUID

class UserRepositoryImpl : UserRepository {

    override fun exists(id: UUID): Boolean {
        return UserMappingsTable.select() { UserMappingsTable.id eq id }.count() == 1L
    }

    override fun getById(id: UUID): User? {
        val rst = UserMappingsTable.select { UserMappingsTable.id eq id }.singleOrNull()
        return if (rst != null) {
            resultToModel(rst)
        } else {
            null
        }
    }

    private fun resultToModel(rstRow: ResultRow): User {
        return User(
            rstRow[UserMappingsTable.id].value,
            rstRow[UserMappingsTable.name],
            rstRow[UserMappingsTable.email]
        )
    }
}
