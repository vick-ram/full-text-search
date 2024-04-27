package tech.vickram.endpoints

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import tech.vickram.controllers.*
import tech.vickram.models.UserRequest
import tech.vickram.util.UUIDSerializer
import java.util.*

@Resource("/api/v1/users")
class User(
    val email: String? = null,
    val search: String? = null
) {
    @Resource("{id}")
    class Id(
        val parent: User = User(),
        @Serializable(with = UUIDSerializer::class)
        val id: UUID
    )
}

fun Route.userRoutes() {
    get<User> { query ->
        try {
            when {
                query.email != null -> call.respond(
                    filteredUsers { it.email == query.email }.firstOrNull()
                        ?: return@get call.respondText("No such customer")
                )

                query.search != null -> call.respond(
                    searchUser(query.search)
                )

                else -> call.respond(
                    filteredUsers { true }
                )
            }
        } catch (e: Exception) {
            when (e) {
                is IllegalArgumentException -> call.respond(HttpStatusCode.BadRequest, e.message.toString())
                else -> call.respond(HttpStatusCode.InternalServerError, e.message.toString())
            }
        }
    }

    post<User, UserRequest> { _, userRequest ->
        try {
            call.respond(
                HttpStatusCode.Created,
                createUser(userRequest)
            )
        } catch (e: Exception) {
            /* Exceptions here */
        }
    }

    put<User.Id, UserRequest> { param, userRequest ->
        try {
            updateUser(param.id, userRequest)
            call.respond(HttpStatusCode.Accepted, "user updated successfully")
        } catch (e: Exception) {
            /*Nothing much, just catching exceptions*/
        }
    }

    delete<User.Id> { param ->
        try {
            deleteUser(param.id)
            call.respond(HttpStatusCode.NoContent, "user deleted")
        } catch (e: Exception) {
            /*Exceptions*/
        }
    }
}