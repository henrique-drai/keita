package mobi.waterdog.rest.template.tests.core.persistance.sql

import org.jetbrains.exposed.dao.id.UUIDTable

internal object RepairTypeMappingsTable : UUIDTable("repair_types") {
    val name = varchar("name", length = 255)
    val description = text("description")
}