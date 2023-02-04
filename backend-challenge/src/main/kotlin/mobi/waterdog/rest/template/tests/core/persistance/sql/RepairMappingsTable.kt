package mobi.waterdog.rest.template.tests.core.persistance.sql

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp

internal object RepairMappingsTable : UUIDTable("repairs") {
    val createdAt = timestamp("created_at")
    val status = varchar("status", length = 255)
    val userId = reference("user_id", UserMappingsTable.id, ReferenceOption.CASCADE)
    val repairTypeId = reference("repair_type_id", RepairTypeMappingsTable.id, ReferenceOption.CASCADE)
}