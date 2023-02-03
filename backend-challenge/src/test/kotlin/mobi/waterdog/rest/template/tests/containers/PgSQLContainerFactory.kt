package mobi.waterdog.rest.template.tests.containers

import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import org.testcontainers.containers.PostgreSQLContainer

class KPostgreSQLContainer(image: String) : PostgreSQLContainer<KPostgreSQLContainer>(image) {

    fun configInfo(): ApplicationConfig = MapApplicationConfig(
        Pair("datasource.driver", driverClassName),
        Pair("datasource.jdbcUrl", jdbcUrl),
        Pair("datasource.username", username),
        Pair("datasource.password", password),
        Pair("datasource.pool.defaultIsolation", "TRANSACTION_REPEATABLE_READ"),
        Pair("datasource.pool.maxPoolSize", "5")
    )
}

object PgSQLContainerFactory {

    fun newInstance(
        databaseName: String = "ktortemplate",
        username: String = "ktor",
        password: String = "template",
        reUse: Boolean = true,
        image: String = "postgres",
        version: String = "12"
    ): KPostgreSQLContainer {
        return KPostgreSQLContainer("$image:$version")
            .withReuse(reUse)
            .withDatabaseName(databaseName)
            .withUsername(username)
            .withPassword(password)
            .withCommand("postgres -c max_connections=1000")
    }
}
