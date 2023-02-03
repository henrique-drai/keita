package mobi.waterdog.rest.template.tests.core.persistance.sql

import org.jetbrains.exposed.dao.id.UUIDTable

internal object UserMappingsTable : UUIDTable("users") {
    val name = varchar("name", length = 255)
    val email = varchar("email", length = 255)
}