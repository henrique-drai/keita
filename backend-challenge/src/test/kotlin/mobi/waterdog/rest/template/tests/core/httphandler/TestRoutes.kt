package mobi.waterdog.rest.template.tests.core.httphandler

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import mobi.waterdog.rest.template.database.DatabaseConnection
import mobi.waterdog.rest.template.pagination.PageRequest
import mobi.waterdog.rest.template.pagination.PageResponse
import mobi.waterdog.rest.template.pagination.PaginationUtils
import mobi.waterdog.rest.template.tests.containers.PgSQLContainerFactory
import mobi.waterdog.rest.template.tests.core.TestApplicationContext
import mobi.waterdog.rest.template.tests.core.persistance.UserRepository
import mobi.waterdog.rest.template.tests.core.testApp
import mobi.waterdog.rest.template.tests.core.utils.json.JsonSettings
import mobi.waterdog.rest.template.tests.core.utils.versioning.ApiVersion
import mobi.waterdog.rest.template.tests.module
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be greater than`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.test.KoinTest
import org.koin.test.inject
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
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
    private val dbc: DatabaseConnection by inject()

    private fun testAppWithConfig(test: suspend TestApplicationContext.() -> Unit) {
        testApp(
            {
                module(dbContainer.configInfo())
            },
            test
        )
    }
}
