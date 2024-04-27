package tech.vickram.database

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import tech.vickram.util.tsVector
import java.time.LocalDateTime
import java.util.*

object Users : UUIDTable("users") {
    val email = varchar("email", 100).uniqueIndex()
    val password = varchar("password", 100)
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val active = bool("active").default(true)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
    val tsv = tsVector("tsv")
}

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users)

    var email by Users.email
    var password by Users.password
    var firstName by Users.firstName
    var lastName by Users.lastName
    var active by Users.active
    var createdAt by Users.createdAt
    var updatedAt by Users.updatedAt
    var tsv by Users.tsv

    fun toResponse() =
        tech.vickram.models.UserResponse(id.value, email, password, firstName, lastName, active, createdAt, updatedAt)
}