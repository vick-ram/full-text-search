package tech.vickram.models

import kotlinx.serialization.Serializable
import tech.vickram.util.LocalDateSerializer
import tech.vickram.util.UUIDSerializer
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class UserRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

@Serializable
data class UserResponse(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val active: Boolean,
    @Serializable(with = LocalDateSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateSerializer::class)
    val updatedAt: LocalDateTime
)