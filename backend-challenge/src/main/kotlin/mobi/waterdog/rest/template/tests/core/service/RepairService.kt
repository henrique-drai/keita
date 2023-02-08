package mobi.waterdog.rest.template.tests.core.service

import mobi.waterdog.rest.template.pagination.PageRequest
import mobi.waterdog.rest.template.tests.core.model.Repair
import mobi.waterdog.rest.template.tests.core.model.RepairSaveCommand

interface RepairService {
    suspend fun list(pageRequest: PageRequest): List<Repair>
    suspend fun count(pageRequest: PageRequest): Int
    suspend fun insertNewRepair(newRepair: RepairSaveCommand): Repair
}