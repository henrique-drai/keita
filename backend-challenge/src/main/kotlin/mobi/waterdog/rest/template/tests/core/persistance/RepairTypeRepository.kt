package mobi.waterdog.rest.template.tests.core.persistance

import mobi.waterdog.rest.template.pagination.PageRequest
import mobi.waterdog.rest.template.tests.core.model.RepairType
import mobi.waterdog.rest.template.tests.core.model.RepairTypeSaveCommand
import java.util.UUID

interface RepairTypeRepository {
    fun exists(id: UUID): Boolean
    fun getById(id: UUID): RepairType?
    fun save(repairType: RepairTypeSaveCommand): RepairType
    fun count(pageRequest: PageRequest = PageRequest()): Int
    fun delete(id: UUID)
    fun list(pageRequest: PageRequest = PageRequest()): List<RepairType>
}
