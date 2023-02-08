package mobi.waterdog.rest.template.tests.core.service

import mobi.waterdog.rest.template.database.DatabaseConnection
import mobi.waterdog.rest.template.pagination.PageRequest
import mobi.waterdog.rest.template.tests.core.model.Repair
import mobi.waterdog.rest.template.tests.core.model.RepairSaveCommand
import mobi.waterdog.rest.template.tests.core.persistance.RepairRepository
import org.slf4j.LoggerFactory

class RepairServiceImpl(
    private val repairRepository: RepairRepository,
    private val dbc: DatabaseConnection
) :
    RepairService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override suspend fun count(pageRequest: PageRequest): Int {
        return dbc.suspendedQuery { repairRepository.count(pageRequest) }
    }

    override suspend fun list(pageRequest: PageRequest): List<Repair> {
        return dbc.suspendedQuery {
            repairRepository.list(pageRequest)
        }
    }

    override suspend fun insertNewRepair(newRepair: RepairSaveCommand): Repair {
        return dbc.suspendedQuery { repairRepository.save(newRepair) }
    }
}
