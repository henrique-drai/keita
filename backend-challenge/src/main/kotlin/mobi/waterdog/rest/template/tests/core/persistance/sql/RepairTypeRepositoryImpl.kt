package mobi.waterdog.rest.template.tests.core.persistance.sql

import mobi.waterdog.rest.template.database.createSorts
import mobi.waterdog.rest.template.database.fromFilters
import mobi.waterdog.rest.template.pagination.PageRequest
import mobi.waterdog.rest.template.tests.core.model.RepairType
import mobi.waterdog.rest.template.tests.core.model.RepairTypeSaveCommand
import mobi.waterdog.rest.template.tests.core.persistance.RepairTypeRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.UUID

class RepairTypeRepositoryImpl : RepairTypeRepository {

    override fun exists(id: UUID): Boolean {
        return RepairTypeMappingsTable.select() { RepairTypeMappingsTable.id eq id }.count() == 1L
    }

    override fun getById(id: UUID): RepairType? {
        val rst = RepairTypeMappingsTable.select { RepairTypeMappingsTable.id eq id }.singleOrNull()
        return if (rst != null) {
            resultToModel(rst)
        } else {
            null
        }
    }

    override fun save(repairType: RepairTypeSaveCommand): RepairType {
        val newRepairTypeId = RepairTypeMappingsTable.insert {
            it[name] = repairType.name
            it[description] = repairType.description
        } get RepairTypeMappingsTable.id

        return RepairType(newRepairTypeId.value, repairType.name, repairType.description)
    }

    override fun count(pageRequest: PageRequest): Int {
        return RepairTypeMappingsTable.fromFilters(pageRequest.filter).count().toInt()
    }

    override fun delete(id: UUID) {
        RepairTypeMappingsTable.deleteWhere { RepairTypeMappingsTable.id eq id }
    }

    override fun list(pageRequest: PageRequest): List<RepairType> {
        return RepairTypeMappingsTable
            .fromFilters(pageRequest.filter)
            .limit(pageRequest.limit, pageRequest.offset.toLong())
            .orderBy(*RepairTypeMappingsTable.createSorts(pageRequest.sort).toTypedArray())
            .map { resultToModel(it) }
    }

    private fun resultToModel(rstRow: ResultRow): RepairType {
        return RepairType(
            rstRow[RepairTypeMappingsTable.id].value,
            rstRow[RepairTypeMappingsTable.name],
            rstRow[RepairTypeMappingsTable.description]
        )
    }
}