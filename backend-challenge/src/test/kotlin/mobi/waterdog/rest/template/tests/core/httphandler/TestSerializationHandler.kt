package mobi.waterdog.rest.template.tests.core.httphandler

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import mobi.waterdog.rest.template.tests.core.model.Const
import mobi.waterdog.rest.template.tests.core.model.Expr
import mobi.waterdog.rest.template.tests.core.model.NotANumber
import mobi.waterdog.rest.template.tests.core.model.Sum
import mobi.waterdog.rest.template.tests.core.model.TestInstantLongSerialization
import mobi.waterdog.rest.template.tests.core.model.TestInstantStringSerialization
import mobi.waterdog.rest.template.tests.core.model.TestSealedClass
import java.time.Instant

fun Route.testSerializationRoutes() {

    get("/sealed") {
        val expr: Expr = when (call.request.queryParameters["type"]) {
            "const" -> Const(call.request.queryParameters["value"]!!.toDouble())
            "sum" -> Sum(
                Const(call.request.queryParameters["value"]!!.toDouble()),
                Const(call.request.queryParameters["value"]!!.toDouble())
            )
            else -> NotANumber
        }

        call.respond(TestSealedClass(expr))
    }

    get("/instant") {
        val type = call.request.queryParameters["type"]
        requireNotNull(type)

        when (type) {
            "string" -> {
                val time = Instant.parse(call.request.queryParameters["time"]) ?: Instant.now()
                call.respond(TestInstantStringSerialization(time))
            }
            "long" -> {
                val time = Instant.ofEpochMilli(call.request.queryParameters["time"]?.toLong() ?: Instant.now().toEpochMilli())
                call.respond(TestInstantLongSerialization(time))
            }
            else -> {
                val time = Instant.now()
                call.respond(TestInstantStringSerialization(time))
            }
        }
    }
}
