package mobi.waterdog.rest.template.tests.core.persistance

import mobi.waterdog.rest.template.pagination.PageRequest
import mobi.waterdog.rest.template.tests.core.model.Repair
import mobi.waterdog.rest.template.tests.core.model.RepairSaveCommand
import java.util.UUID

interface RepairRepository {
    fun exists(id: UUID): Boolean
    fun getById(id: UUID): Repair?
    fun save(repair: RepairSaveCommand): Repair
    fun count(pageRequest: PageRequest = PageRequest()): Int
    fun delete(id: UUID)
    fun list(pageRequest: PageRequest = PageRequest()): List<Repair>
}
