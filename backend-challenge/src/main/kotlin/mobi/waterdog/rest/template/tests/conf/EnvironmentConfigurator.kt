package mobi.waterdog.rest.template.tests.conf

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.MapApplicationConfig
import mobi.waterdog.rest.template.database.DatabaseConnection
import mobi.waterdog.rest.template.tests.core.persistance.RepairRepository
import mobi.waterdog.rest.template.tests.core.persistance.RepairTypeRepository
import mobi.waterdog.rest.template.tests.core.persistance.UserRepository
import mobi.waterdog.rest.template.tests.core.persistance.sql.RepairRepositoryImpl
import mobi.waterdog.rest.template.tests.core.persistance.sql.RepairTypeRepositoryImpl
import mobi.waterdog.rest.template.tests.core.persistance.sql.UserRepositoryImpl
import mobi.waterdog.rest.template.tests.core.service.RepairService
import mobi.waterdog.rest.template.tests.core.service.RepairServiceImpl
import mobi.waterdog.rest.template.tests.core.service.RepairTypeService
import mobi.waterdog.rest.template.tests.core.service.RepairTypeServiceImpl
import mobi.waterdog.rest.template.tests.core.service.UserService
import mobi.waterdog.rest.template.tests.core.service.UserServiceImpl
import org.flywaydb.core.Flyway
import org.koin.core.module.Module
import org.koin.dsl.module
import javax.sql.DataSource

class EnvironmentConfigurator(baseConfig: ApplicationConfig, configOverrides: ApplicationConfig? = null) {

    private val mergedConfig: ApplicationConfig
    private val pathsToMerge = listOf(
        "datasource.driver",
        "datasource.jdbcUrl",
        "datasource.username",
        "datasource.password",
        "datasource.pool.defaultIsolation",
        "datasource.pool.maxPoolSize"
    )

    init {
        mergedConfig = mergeConfigurations(baseConfig, configOverrides)
        runMigrations()
    }

    fun getDependencyInjectionModules(): List<Module> {
        return listOf(
            initDbCore(),
            initServicesAndRepos()
        )
    }

    private fun initServicesAndRepos() = module {
        single<UserRepository> { UserRepositoryImpl() }
        single<UserService> { UserServiceImpl(get(), get()) }
        single<RepairRepository> { RepairRepositoryImpl() }
        single<RepairService> { RepairServiceImpl(get(), get()) }
        single<RepairTypeRepository> { RepairTypeRepositoryImpl() }
        single<RepairTypeService> { RepairTypeServiceImpl(get(), get()) }
    }

    private fun initDbCore() = module {
        val className = mergedConfig.property("datasource.driver").getString()
        val jdbcUrl = mergedConfig.property("datasource.jdbcUrl").getString()
        val username = mergedConfig.property("datasource.username").getString()
        val password = mergedConfig.property("datasource.password").getString()
        val isolation = mergedConfig.property("datasource.pool.defaultIsolation").getString()
        val maxPoolSize = mergedConfig.property("datasource.pool.maxPoolSize").getString().toInt()
        val dataSource: DataSource = HikariDataSource(
            HikariConfig().apply {
                this.driverClassName = className
                this.jdbcUrl = jdbcUrl
                this.username = username
                this.password = password
                this.maximumPoolSize = maxPoolSize
                this.isAutoCommit = false
                this.transactionIsolation = isolation
                this.leakDetectionThreshold = 10000
                this.poolName = "hikari-cp"
                this.validate()
            }
        )

        single { DatabaseConnection(dataSource) }
    }

    private fun runMigrations() {
        val jdbcUrl = mergedConfig.property("datasource.jdbcUrl").getString()
        val username = mergedConfig.property("datasource.username").getString()
        val password = mergedConfig.property("datasource.password").getString()

        val flyway = Flyway.configure().dataSource(jdbcUrl, username, password).load()
        flyway.migrate()
    }

    private fun mergeConfigurations(base: ApplicationConfig, overrides: ApplicationConfig?): ApplicationConfig {
        return if (overrides == null) {
            base
        } else {
            val mergedConfig = MapApplicationConfig()
            pathsToMerge.forEach { path ->
                mergedConfig.put(path, mergeProp(base, overrides, path))
            }
            mergedConfig
        }
    }

    private fun mergeProp(base: ApplicationConfig, overrides: ApplicationConfig, path: String): String {
        return overrides.propertyOrNull(path)?.getString() ?: base.property(path).getString()
    }
}
