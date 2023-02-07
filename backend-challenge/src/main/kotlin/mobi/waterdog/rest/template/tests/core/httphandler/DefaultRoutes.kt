package mobi.waterdog.rest.template.tests.core.httphandler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mobi.waterdog.rest.template.pagination.PageResponse
import mobi.waterdog.rest.template.pagination.parsePageRequest
import mobi.waterdog.rest.template.tests.core.model.RepairTypeSaveCommand
import mobi.waterdog.rest.template.tests.core.service.RepairService
import mobi.waterdog.rest.template.tests.core.service.RepairTypeService
import mobi.waterdog.rest.template.tests.core.service.UserService
import mobi.waterdog.rest.template.tests.core.utils.versioning.ApiVersion
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

internal class DefaultRoutesInjector : KoinComponent {
    val userService: UserService by inject()
    val repairService: RepairService by inject()
    val repairTypeService: RepairTypeService by inject()
}

fun Route.defaultRoutes() {
    val apiVersion = ApiVersion.Latest
    val injector = DefaultRoutesInjector()
    val userService = injector.userService
    val repairService = injector.repairService
    val repairTypeService = injector.repairTypeService

    get("/$apiVersion/users/{id}") {
        val userId = UUID.fromString(call.parameters["id"])

        when (val user = userService.getUserById(userId)) {
            null -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(user)
        }
    }

    get("/$apiVersion/repairs") {
        val pageRequest = call.parsePageRequest()
        val totalElements = repairService.count(pageRequest)
        val data = repairService.list(pageRequest)
        call.respond(
            PageResponse.from(
                pageRequest = pageRequest,
                totalElements = totalElements,
                data = data,
                path = call.request.path()
            )
        )
    }

    post("/$apiVersion/repair_types") {
        val newRepairType = call.receive<RepairTypeSaveCommand>()
        newRepairType.validate()

        val insertedCar = repairTypeService.insertNewRepairType(RepairTypeSaveCommand(newRepairType.name, newRepairType.description))
        call.respond(insertedCar)
    }
}
