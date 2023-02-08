package mobi.waterdog.rest.template.tests.core.persistance.sql

import mobi.waterdog.rest.template.database.createSorts
import mobi.waterdog.rest.template.database.fromFilters
import mobi.waterdog.rest.template.pagination.PageRequest
import mobi.waterdog.rest.template.tests.core.model.Repair
import mobi.waterdog.rest.template.tests.core.model.RepairSaveCommand
import mobi.waterdog.rest.template.tests.core.model.RepairStatus
import mobi.waterdog.rest.template.tests.core.persistance.RepairRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.time.Instant
import java.util.UUID

class RepairRepositoryImpl : RepairRepository {

    override fun exists(id: UUID): Boolean {
        return RepairMappingsTable.select() { RepairMappingsTable.id eq id }.count() == 1L
    }

    override fun getById(id: UUID): Repair? {
        val rst = RepairMappingsTable.select { RepairMappingsTable.id eq id }.singleOrNull()
        return if (rst != null) {
            resultToModel(rst)
        } else {
            null
        }
    }

    override fun save(repair: RepairSaveCommand): Repair {
        val newRepairId = RepairMappingsTable.insert {
            it[createdAt] = repair.createdAt
            it[status] = repair.status.name
            it[userId] = repair.userId
            it[repairTypeId] = repair.repairTypeId
        } get RepairMappingsTable.id

        return Repair(
            newRepairId.value,
            repair.createdAt,
            repair.status,
            repair.userId,
            repair.repairTypeId
        )
    }

    override fun count(pageRequest: PageRequest): Int {
        return RepairMappingsTable.fromFilters(pageRequest.filter).count().toInt()
    }

    override fun delete(id: UUID) {
        RepairMappingsTable.deleteWhere { RepairMappingsTable.id eq id }
    }

    override fun list(pageRequest: PageRequest): List<Repair> {
        return RepairMappingsTable
            .fromFilters(pageRequest.filter)
            .limit(pageRequest.limit, pageRequest.offset.toLong())
            .orderBy(*RepairMappingsTable.createSorts(pageRequest.sort).toTypedArray())
            .map { resultToModel(it) }
    }

    private fun resultToModel(rstRow: ResultRow): Repair {
        return Repair(
            rstRow[RepairMappingsTable.id].value,
            Instant.ofEpochMilli(rstRow[RepairMappingsTable.createdAt].toEpochMilli()),
            RepairStatus.valueOf(rstRow[RepairMappingsTable.status]),
            rstRow[RepairMappingsTable.userId].value,
            rstRow[RepairMappingsTable.repairTypeId].value
        )
    }
}
