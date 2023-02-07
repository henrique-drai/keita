package mobi.waterdog.rest.template.tests.core

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import mobi.waterdog.rest.template.database.DatabaseConnection
import mobi.waterdog.rest.template.pagination.PageRequest
import mobi.waterdog.rest.template.pagination.PageResponse
import mobi.waterdog.rest.template.tests.conf.EnvironmentConfigurator
import mobi.waterdog.rest.template.tests.containers.PgSQLContainerFactory
import mobi.waterdog.rest.template.tests.core.model.*
import mobi.waterdog.rest.template.tests.core.persistance.RepairRepository
import mobi.waterdog.rest.template.tests.core.persistance.RepairTypeRepository
import mobi.waterdog.rest.template.tests.core.persistance.UserRepository
import mobi.waterdog.rest.template.tests.core.utils.versioning.ApiVersion
import mobi.waterdog.rest.template.tests.module
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.*
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
    private val repairTypeRepository: RepairTypeRepository by inject()
    private val dbc: DatabaseConnection by inject()
    private val appModules by lazy { EnvironmentConfigurator(dbContainer.configInfo()).getDependencyInjectionModules() }

    @BeforeAll
    fun setup() {
        startKoin { modules(appModules) }
    }

    @AfterEach
    fun cleanDatabase() {
        dbc.query {
            val repairTypes =
                repairTypeRepository.list(PageRequest(page = 0, size = Int.MAX_VALUE, sort = listOf(), filter = listOf()))
            repairTypes.forEach {
                repairTypeRepository.delete(it.id)
            }
            repairTypeRepository.count() `should be equal to` 0
        }
    }

    @AfterAll
    fun cleanup() {
        stopKoin()
    }

    @Test
    fun `This test will fail due to Koin initialization`() = testAppWithConfig {}

    private fun testAppWithConfig(test: suspend TestApplicationContext.() -> Unit) {
        testApp(
            {
                module(dbContainer.configInfo())
            },
            test
        )
    }
}
