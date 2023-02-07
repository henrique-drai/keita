package mobi.waterdog.rest.template.tests.core.service

import mobi.waterdog.rest.template.pagination.PageRequest
import mobi.waterdog.rest.template.tests.core.model.RepairType
import mobi.waterdog.rest.template.tests.core.model.RepairTypeSaveCommand

interface RepairTypeService {
    suspend fun list(pageRequest: PageRequest): List<RepairType>
    suspend fun count(pageRequest: PageRequest): Int
    suspend fun insertNewRepairType(newRepairType: RepairTypeSaveCommand): RepairType
}