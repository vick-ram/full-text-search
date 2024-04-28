package tech.vickram.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import tech.vickram.endpoints.userRoutes

fun Application.configureRouting() {
    install(Resources)
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when(cause) {
                is IllegalArgumentException -> call.respond(
                    HttpStatusCode.BadRequest,
                    cause.message ?: "Not found"
                )
                else -> call.respond(
                    HttpStatusCode.InternalServerError,
                    cause.message ?: "Something went wrong"
                )
            }
        }
    }
    routing { userRoutes() }
}
