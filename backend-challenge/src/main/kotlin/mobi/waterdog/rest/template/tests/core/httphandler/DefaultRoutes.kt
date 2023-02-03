package mobi.waterdog.rest.template.tests.core.httphandler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mobi.waterdog.rest.template.tests.core.service.UserService
import mobi.waterdog.rest.template.tests.core.utils.versioning.ApiVersion
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

internal class DefaultRoutesInjector : KoinComponent {
    val userService: UserService by inject()
}

fun Route.defaultRoutes() {
    val apiVersion = ApiVersion.Latest
    val injector = DefaultRoutesInjector()
    val userService = injector.userService

    get("/$apiVersion/users/{id}") {
        val userId: UUID

        try {
            userId = UUID.fromString(call.parameters["id"])
        } catch(e: IllegalArgumentException) {
            return@get call.respond(HttpStatusCode.BadRequest)
        }

        when (val user = userService.getUserById(userId)) {
            null -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(user)
        }
    }
}
