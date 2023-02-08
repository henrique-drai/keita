package mobi.waterdog.rest.template.tests.core.service

import mobi.waterdog.rest.template.database.DatabaseConnection
import mobi.waterdog.rest.template.pagination.PageRequest
import mobi.waterdog.rest.template.tests.core.model.RepairType
import mobi.waterdog.rest.template.tests.core.model.RepairTypeSaveCommand
import mobi.waterdog.rest.template.tests.core.persistance.RepairTypeRepository
import org.slf4j.LoggerFactory

class RepairTypeServiceImpl(
    private val repairTypeRepository: RepairTypeRepository,
    private val dbc: DatabaseConnection
) :
    RepairTypeService {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    override suspend fun count(pageRequest: PageRequest): Int {
        return dbc.suspendedQuery { repairTypeRepository.count(pageRequest) }
    }

    override suspend fun list(pageRequest: PageRequest): List<RepairType> {
        return dbc.suspendedQuery {
            repairTypeRepository.list(pageRequest)
        }
    }

    override suspend fun insertNewRepairType(newRepairType: RepairTypeSaveCommand): RepairType {
        return dbc.suspendedQuery { repairTypeRepository.save(newRepairType) }
    }
}
