package tech.vickram.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.jetbrains.exposed.sql.*
import org.postgresql.util.PGobject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Local date time serializer
 */
object LocalDateSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("LocalDateTime" ,PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), formatter)
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(formatter))
    }
}

object UUIDSerializer : KSerializer<UUID> {
    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("UUID" ,PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

/**
 * Custom column type for PostgreSQL TSVECTOR
 * @constructor Create empty Ts vector column type
 *
 */
class TsVectorColumnType : ColumnType() {
    override fun sqlType() = "TSVECTOR"

    override fun valueFromDB(value: Any): Any {
        return when (value) {
            is PGobject -> value.value ?: ""
            else -> value
        }
    }

    override fun notNullValueToDB(value: Any): Any {
        return when (value) {
            is String -> PGobject().also {
                it.type = "tsvector"
                it.value = value
            }
            else -> value
        }
    }
}

/**
 * Create a new column with the type TSVECTOR
 * @param name The name of the column
 * @return Column<String>
 */
fun Table.tsVector(name: String): Column<String> = registerColumn(name, TsVectorColumnType())

/**
 * Custom match function for PostgreSQL full text search
 * @param query The query to match
 * @return Op<Boolean>
 */
fun Column<String>.customMatch(query: String): Op<Boolean> = object : Op<Boolean>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) {
        queryBuilder {
            append(this@customMatch, " @@ to_tsquery('", query, ":*')")
        }
    }
}

/**
 * Hash a password using HmacSHA256
 * @param password The password to hash
 * @return String?
 */
fun hashedPassword(password: String) : String {
    val secret = System.getenv("SECRET")
    val secretKey = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(secretKey)
    val hash = mac.doFinal(password.toByteArray())
    return Base64.getEncoder().encodeToString(hash)
}

/**
 * Compare a password with a hashed password
 * @param password The password to compare
 * @param hashedPassword The hashed password to compare
 * @return Boolean
 */
fun comparePassword(password: String, hashedPassword: String): Boolean {
    return hashedPassword(password) == hashedPassword
}