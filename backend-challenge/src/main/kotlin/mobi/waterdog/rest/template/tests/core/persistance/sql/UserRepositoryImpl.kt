package mobi.waterdog.rest.template.tests.core.persistance.sql

import mobi.waterdog.rest.template.database.createSorts
import mobi.waterdog.rest.template.database.fromFilters
import mobi.waterdog.rest.template.pagination.PageRequest
import mobi.waterdog.rest.template.tests.core.model.RepairType
import mobi.waterdog.rest.template.tests.core.model.User
import mobi.waterdog.rest.template.tests.core.model.UserSaveCommand
import mobi.waterdog.rest.template.tests.core.persistance.UserRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
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

    override fun save(user: UserSaveCommand): User {
        val newUserId = UserMappingsTable.insert {
            it[name] = user.name
            it[email] = user.email
        } get UserMappingsTable.id

        return User(newUserId.value, user.name, user.email)
    }

    override fun count(pageRequest: PageRequest): Int {
        return UserMappingsTable.fromFilters(pageRequest.filter).count().toInt()
    }

    override fun delete(id: UUID) {
        UserMappingsTable.deleteWhere { UserMappingsTable.id eq id }
    }

    override fun list(pageRequest: PageRequest): List<User> {
        return UserMappingsTable
            .fromFilters(pageRequest.filter)
            .limit(pageRequest.limit, pageRequest.offset.toLong())
            .orderBy(*UserMappingsTable.createSorts(pageRequest.sort).toTypedArray())
            .map { resultToModel(it) }
    }

    private fun resultToModel(rstRow: ResultRow): User {
        return User(
            rstRow[UserMappingsTable.id].value,
            rstRow[UserMappingsTable.name],
            rstRow[UserMappingsTable.email]
        )
    }
}
