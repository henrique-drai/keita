package test.ktortemplate.core.utils.pagination

import io.ktor.server.testing.TestApplicationEngine
import io.ktor.util.KtorExperimentalAPI
import org.amshove.kluent.`should be equal to`
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.inject
import org.koin.test.KoinTest
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import test.ktortemplate.conf.database.DatabaseConnection
import test.ktortemplate.containers.PgSQLContainerFactory
import test.ktortemplate.core.testApp
import java.util.UUID

@KtorExperimentalAPI
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
class TestTableExtensionsFilters : KoinTest {

    companion object {
        @Container
        private val dbContainer = PgSQLContainerFactory.newInstance()
    }

    private val dbc: DatabaseConnection by inject()

    internal object TestTable : IdTable<String>("tests") {
        override val id: Column<EntityID<String>> = varchar("id", 255).entityId()
        val long = long("long")
        val int = integer("int")
        val string = varchar("string", 255)
        val boolean = bool("boolean")
        val uuid = uuid("uuid")

        override val primaryKey = PrimaryKey(id)
    }

    private val testId: String = "testId"
    private val testLong: Long = 1L
    private val testInt: Int = 1
    private val testString: String = "string"
    private val testBoolean: Boolean = true
    private val testUUID: UUID = UUID.randomUUID()

    @AfterEach
    fun cleanDatabase() {
        dbc.query { TestTable.deleteAll() }
    }

    @Nested
    inner class TestFilterId {
        @Test
        fun `String primary key type should be supported`() = testAppWithConfig {
            insertTestEntry()
            countColumnEntries(TestTable.id.name, testId) `should be equal to` 1
        }

        @Test
        fun `No results should be returned with no entry`() = testAppWithConfig {
            insertTestEntry()
            countColumnEntries(TestTable.id.name, "otherId") `should be equal to` 0
        }
    }

    @Nested
    inner class TestFilterLong {
        @Test
        fun `Long primary key type should be supported`() = testAppWithConfig {
            insertTestEntry()
            countColumnEntries(TestTable.long.name, testLong.toString()) `should be equal to` 1
        }

        @Test
        fun `No results should be returned with no entry`() = testAppWithConfig {
            insertTestEntry()
            countColumnEntries(TestTable.long.name, "2") `should be equal to` 0
        }
    }

    @Nested
    inner class TestFilterInt {
        @Test
        fun `Int type should be supported`() = testAppWithConfig {
            insertTestEntry()
            countColumnEntries(TestTable.int.name, testInt.toString()) `should be equal to` 1
        }

        @Test
        fun `No results should be returned with no entry`() = testAppWithConfig {
            insertTestEntry()
            countColumnEntries(TestTable.int.name, "2") `should be equal to` 0
        }
    }

    @Nested
    inner class TestFilterString {
        @Test
        fun `String type should be supported`() = testAppWithConfig {
            insertTestEntry()
            countColumnEntries(TestTable.string.name, testString) `should be equal to` 1
        }

        @Test
        fun `No results should be returned with no entry`() = testAppWithConfig {
            insertTestEntry()
            countColumnEntries(TestTable.string.name, "otherString") `should be equal to` 0
        }
    }

    @Nested
    inner class TestFilterBoolean {
        @Test
        fun `Boolean type should be supported`() = testAppWithConfig {
            insertTestEntry()
            countColumnEntries(TestTable.boolean.name, testBoolean.toString()) `should be equal to` 1
        }

        @Test
        fun `No results should be returned with no entry`() = testAppWithConfig {
            insertTestEntry()
            countColumnEntries(TestTable.boolean.name, false.toString()) `should be equal to` 0
        }
    }

    @Nested
    inner class TestFilterUUID {
        @Test
        fun `UUID type should be supported`() = testAppWithConfig {
            insertTestEntry()
            countColumnEntries(TestTable.uuid.name, testUUID.toString()) `should be equal to` 1
        }

        @Test
        fun `No results should be returned with no entry`() = testAppWithConfig {
            insertTestEntry()
            countColumnEntries(TestTable.uuid.name, UUID.randomUUID().toString()) `should be equal to` 0
        }
    }

    /**
     * Helpers
     */
    private fun countColumnEntries(columnName: String, columnValue: String): Long {
        return dbc.query {
            TestTable.fromFilters(
                listOf(
                    FilterField(
                        field = columnName,
                        values = listOf(columnValue)
                    )
                )
            ).count()
        }
    }

    private fun insertTestEntry() {
        dbc.query {
            SchemaUtils.create(TestTable)
            TestTable.insert {
                it[id] = testId
                it[long] = testLong
                it[int] = testInt
                it[string] = testString
                it[boolean] = testBoolean
                it[uuid] = testUUID
            }
        }
    }

    private fun <R> testAppWithConfig(test: TestApplicationEngine.() -> R) {
        testApp(dbContainer.configInfo(), test)
    }
}
