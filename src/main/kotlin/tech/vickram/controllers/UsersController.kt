package tech.vickram.controllers

import org.jetbrains.exposed.sql.selectAll
import tech.vickram.database.User
import tech.vickram.database.Users
import tech.vickram.models.UserRequest
import tech.vickram.models.UserResponse
import tech.vickram.plugins.DatabaseFactory.dbQuery
import tech.vickram.util.customMatch
import tech.vickram.util.hashedPassword
import java.util.*

suspend fun createUser(user: UserRequest): UserResponse = dbQuery {
    val userExists = Users.selectAll().where { Users.email eq user.email }.count() > 0
    if (userExists) {
        throw IllegalArgumentException("User with email ${user.email} already exists")
    }
    return@dbQuery User.new {
        email = user.email
        password = hashedPassword(user.password)
        firstName = user.firstName
        lastName = user.lastName
        tsv = "to_tsvector('english', '$email $firstName $lastName')"
    }.toResponse()
}

suspend fun updateUser(userId: UUID, user: UserRequest): Boolean = dbQuery {
    val userExists = Users.selectAll().where { Users.email eq user.email }.count() > 0
    if (!userExists) {
        throw IllegalArgumentException("User with email ${user.email} does not exist")
    }
    User.findByIdAndUpdate(userId) { update ->
        update.email = user.email
        update.password = hashedPassword(user.password)
        update.firstName = user.firstName
        update.lastName = user.lastName
    }
    true
}

suspend fun deleteUser(userId: UUID): Boolean = dbQuery {
    User.findById(userId)?.delete()
        ?: throw IllegalArgumentException("User with id $userId not found")
    true
}

suspend fun filteredUsers(filter: (User) -> Boolean): List<UserResponse> = dbQuery {
    return@dbQuery try {
        User.all().limit(100, 2L)
            .filter(filter)
            .sortedByDescending { it.createdAt.coerceAtLeast(it.updatedAt) }
            .map { it.toResponse() }
    } catch (e: Exception) {
        throw IllegalArgumentException("No customer(s) found")
    }
}

/**
 * function to perform full-text-search
 * @param search
 * @return List<[UserResponse]>
 * @throws IllegalArgumentException
 */
suspend fun searchUser(search: String): List<UserResponse> = dbQuery {
    return@dbQuery try {
        Users.selectAll()
            .where { Users.tsv.customMatch(search) }
            .map { User.wrapRow(it).toResponse() }
    } catch (e: Exception) {
        throw IllegalArgumentException("No customer(s) found")
    }
}