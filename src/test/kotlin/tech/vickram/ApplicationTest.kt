package tech.vickram

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.EmptyContent.status
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.selectAll
import tech.vickram.database.Users
import tech.vickram.models.UserRequest
import tech.vickram.plugins.DatabaseFactory
import tech.vickram.plugins.configureRouting
import tech.vickram.plugins.hikari
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(InternalAPI::class)
class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        routing {
            get("/api/v1/users") {
                call.respondText("Hello World!")
            }
        }
    }

    /**
     * Test cases are not working as expected
     * Need to fix the test cases so that they pass soon
     */

    @Test
    fun testDataBaseInit() = testApplication {
        application {
            DatabaseFactory.init()
        }
        //Add assert to check if the database is initialized correctly
        val hikari = hikari()
        val connection = hikari.isRunning
        assertTrue(connection, "Database connection should be established")

        val userTableExists = DatabaseFactory.dbQuery {
            Users.selectAll().count()
        }
        assertEquals(0, userTableExists, "User table should be empty")
    }

    @Test
    fun testGetUsers() = testApplication {
        val client = createClient {
            this@testApplication.install(ContentNegotiation) {
                json()
            }
        }
        application {
            configureRouting()
        }
        client.get("/api/v1/users").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("[]", bodyAsText())
        }
    }

    @Test
    fun testCreateUser() = testApplication {
        application {
            configureRouting()
        }
        val client = createClient {
            this@testApplication.install(ContentNegotiation){
                json(Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
        }
        val userRequest = UserRequest(
            email = "test@gmail.com",
            password = "password",
            firstName = "Test",
            lastName = "User"
        )
        client.post("/api/v1/users") {
            contentType(ContentType.Application.Json)
            setBody(userRequest)
        }.apply {
            assertEquals(HttpStatusCode.Created, this.status)
            assertEquals("User stored correctly", this.bodyAsText())
        }
    }

    @Test
    fun getUser() = testApplication {
        application {
            configureRouting()
        }
        client.get("/api/v1/users/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("User retrieved correctly", bodyAsText())
        }
    }

    @Test
    fun testUpdateUser() = testApplication {
        application {
            configureRouting()
        }
        val userUpdate = UserRequest(
            email = "test@test.com",
            password = "password1",
            firstName = "Test1",
            lastName = "User1"
        )
        client.put("/api/v1/users/") {
            body = Json.encodeToString(UserRequest.serializer(), userUpdate)
            contentType(ContentType.Application.Json)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("User updated correctly", bodyAsText())
        }
    }

    @Test
    fun testDeleteUser() = testApplication {
        application {
            configureRouting()
        }
        client.delete("/api/v1/users/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("User deleted correctly", bodyAsText())
        }
    }
}
