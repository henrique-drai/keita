package mobi.waterdog.rest.template.tests.core

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import mobi.waterdog.rest.template.database.DatabaseConnection
import mobi.waterdog.rest.template.pagination.PageRequest
import mobi.waterdog.rest.template.pagination.PageResponse
import mobi.waterdog.rest.template.pagination.PaginationUtils
import mobi.waterdog.rest.template.tests.conf.EnvironmentConfigurator
import mobi.waterdog.rest.template.tests.containers.PgSQLContainerFactory
import mobi.waterdog.rest.template.tests.core.model.Repair
import mobi.waterdog.rest.template.tests.core.model.RepairSaveCommand
import mobi.waterdog.rest.template.tests.core.model.RepairStatus
import mobi.waterdog.rest.template.tests.core.model.RepairType
import mobi.waterdog.rest.template.tests.core.model.RepairTypeSaveCommand
import mobi.waterdog.rest.template.tests.core.model.User
import mobi.waterdog.rest.template.tests.core.model.UserSaveCommand
import mobi.waterdog.rest.template.tests.core.persistance.RepairRepository
import mobi.waterdog.rest.template.tests.core.persistance.RepairTypeRepository
import mobi.waterdog.rest.template.tests.core.persistance.UserRepository
import mobi.waterdog.rest.template.tests.core.utils.json.JsonSettings
import mobi.waterdog.rest.template.tests.core.utils.versioning.ApiVersion
import mobi.waterdog.rest.template.tests.module
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Instant
import java.util.UUID

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestRoutes : KoinTest {
    companion object {
        @Container
        private val dbContainer = PgSQLContainerFactory.newInstance()
    }

    private val apiVersion = ApiVersion.Latest
    private val userRepository: UserRepository by inject()
    private val repairRepository: RepairRepository by inject()
    private val repairTypeRepository: RepairTypeRepository by inject()
    private val dbc: DatabaseConnection by inject()
    private val appModules by lazy { EnvironmentConfigurator(dbContainer.configInfo()).getDependencyInjectionModules() }

    private fun runInKoinContext(block: () -> Unit) {
        stopKoin()
        startKoin {
            modules(appModules)
        }
        block()
        stopKoin()
    }

    @AfterEach
    fun cleanDatabase() = runInKoinContext {
        dbc.query {
            val repairTypes =
                repairTypeRepository.list(PageRequest(page = 0, size = Int.MAX_VALUE, sort = listOf(), filter = listOf()))
            repairTypes.forEach {
                repairTypeRepository.delete(it.id)
            }

            val users =
                userRepository.list(PageRequest(page = 0, size = Int.MAX_VALUE, sort = listOf(), filter = listOf()))
            users.forEach {
                userRepository.delete(it.id)
            }

            repairRepository.count() `should be equal to` 0
        }
    }

    @Nested
    @DisplayName("Test repairs listing")
    inner class TestCarList {
        @Test
        fun `Works without any extra parameters`() = testAppWithConfig {
            var expectedRepairs: List<Repair> = listOf()

            runInKoinContext {
                val newUser = insertUser()
                val newRepairType1 = insertRepairType()
                val newRepairType2 = insertRepairType()

                expectedRepairs = listOf(
                    insertRepair(userId = newUser.id, repairTypeId = newRepairType1.id),
                    insertRepair(userId = newUser.id, repairTypeId = newRepairType1.id),
                    insertRepair(userId = newUser.id, repairTypeId = newRepairType2.id),
                )
            }

            with(client.get("/$apiVersion/repairs")) {
                status `should be equal to` HttpStatusCode.OK
                val res: PageResponse<Repair> = body()
                res.data.size `should be equal to` expectedRepairs.size
                res.data `should be equal to` expectedRepairs
            }
        }

        @Test
        fun `Works with filter by repair type`() = testAppWithConfig {
            var expectedRepairTypeId = UUID.randomUUID()

            runInKoinContext {
                val newUser = insertUser()
                val newRepairType1 = insertRepairType()
                val newRepairType2 = insertRepairType()

                insertRepair(userId = newUser.id, repairTypeId = newRepairType1.id)
                insertRepair(userId = newUser.id, repairTypeId = newRepairType1.id)
                insertRepair(userId = newUser.id, repairTypeId = newRepairType2.id)

                expectedRepairTypeId = newRepairType1.id
            }

            with(client.get("/$apiVersion/repairs?${PaginationUtils.PAGE_FILTER}[repair_type_id]=$expectedRepairTypeId")) {
                status `should be equal to` HttpStatusCode.OK
                val res: PageResponse<Repair> = body()
                res.data.size `should be equal to` 2
            }
        }
    }

    @Test
    fun `Users can create specific repair services`() = testAppWithConfig {
        var newUserId = UUID.randomUUID()

        runInKoinContext {
            val newUser = insertUser()
            newUserId = newUser.id
        }

        val repairType = RepairTypeSaveCommand("name", "description")
        val newRepairTypeId: UUID

        with(
            client.post("/$apiVersion/repair_types") {
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(JsonSettings.toJson(repairType))
            }
        ) {
            status `should be equal to` HttpStatusCode.OK
            val newRepairType: RepairType = body()
            newRepairTypeId = newRepairType.id
            newRepairType.name `should be equal to` repairType.name
            newRepairType.description `should be equal to` repairType.description
        }

        val repair = RepairSaveCommand(getLimitedPrecisionInstant(), RepairStatus.Created, newUserId, newRepairTypeId)

        with(
            client.post("/$apiVersion/repairs") {
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(JsonSettings.toJson(repair))
            }
        ) {
            status `should be equal to` HttpStatusCode.OK
            val newRepair: Repair = body()
            newRepair.userId `should be equal to` repair.userId
            newRepair.repairTypeId `should be equal to` repair.repairTypeId
            newRepair.status `should be equal to` repair.status
            newRepair.createdAt `should be equal to` repair.createdAt
        }
    }

    @Test
    fun `List of created repairs for a user`() = testAppWithConfig {
        var newUsers: List<User>
        var expectedUserId = UUID.randomUUID()

        runInKoinContext {
            newUsers = listOf(insertUser(), insertUser())

            val newRepairType = insertRepairType()

            insertRepair(userId = newUsers[0].id, repairTypeId = newRepairType.id)
            insertRepair(userId = newUsers[0].id, repairTypeId = newRepairType.id)
            insertRepair(userId = newUsers[1].id, repairTypeId = newRepairType.id)

            expectedUserId = newUsers[0].id
        }

        with(client.get("/$apiVersion/repairs?${PaginationUtils.PAGE_FILTER}[user_id]=$expectedUserId")) {
            status `should be equal to` HttpStatusCode.OK
            val res: PageResponse<Repair> = body()
            res.data.size `should be equal to` 2
        }
    }

    private fun insertRepair(
        status: RepairStatus = RepairStatus.Created,
        userId: UUID,
        repairTypeId: UUID,
    ): Repair {
        return dbc.query {
            val newRepair = RepairSaveCommand(getLimitedPrecisionInstant(), status, userId, repairTypeId)
            repairRepository.save(newRepair)
        }
    }

    private fun insertUser(
        name: String = UUID.randomUUID().toString(),
        email: String = generateRandomEmail()
    ): User {
        return dbc.query {
            val newUser = UserSaveCommand(name, email)
            userRepository.save(newUser)
        }
    }

    private fun generateRandomEmail(): String {
        val identifier = UUID.randomUUID().toString().replace("-", ".")
        return "$identifier@mail.com"
    }

    private fun insertRepairType(
        name: String = UUID.randomUUID().toString(),
        description: String = UUID.randomUUID().toString()
    ): RepairType {
        return dbc.query {
            val newRepairType = RepairTypeSaveCommand(name, description)
            repairTypeRepository.save(newRepairType)
        }
    }

    // Limits the instant's precision to milliseconds to make it consistent with sql timestamp.
    private fun getLimitedPrecisionInstant(instant: Instant = Instant.now()): Instant {
        return Instant.ofEpochMilli(instant.toEpochMilli())
    }

    private fun testAppWithConfig(test: suspend TestApplicationContext.() -> Unit) {
        testApp(
            {
                module(dbContainer.configInfo())
            },
            test
        )
    }
}
