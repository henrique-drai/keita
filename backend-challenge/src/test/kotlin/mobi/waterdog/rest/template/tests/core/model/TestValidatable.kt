package mobi.waterdog.rest.template.tests.core.model

import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import mobi.waterdog.rest.template.exception.AppException
import mobi.waterdog.rest.template.exception.ErrorDTO
import mobi.waterdog.rest.template.tests.containers.PgSQLContainerFactory
import mobi.waterdog.rest.template.tests.core.TestApplicationContext
import mobi.waterdog.rest.template.tests.core.testApp
import mobi.waterdog.rest.template.tests.core.utils.json.JsonSettings
import mobi.waterdog.rest.template.tests.core.utils.versioning.ApiVersion
import mobi.waterdog.rest.template.tests.module
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain any`
import org.amshove.kluent.shouldNotBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.koin.test.KoinTest
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
class TestValidatable : KoinTest {

    companion object {
        @Container
        private val dbContainer = PgSQLContainerFactory.newInstance()
    }

    private val apiVersion = ApiVersion.Latest

    private fun testAppWithConfig(test: suspend TestApplicationContext.() -> Unit) {
        testApp(
            {
                module(dbContainer.configInfo())
            },
            test
        )
    }
}
