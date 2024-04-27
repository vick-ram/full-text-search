package tech.vickram.plugins

import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import tech.vickram.database.Users

object DatabaseFactory {
    fun init() {
        Database.connect(hikari())

        transaction {
            SchemaUtils.create(Users)

            exec(
                stmt = """
               UPDATE ${Users.tableName}
               SET tsv = to_tsvector('english', email || ' ' || first_name || ' ' || last_name)
            """.trimIndent()
            )

            exec(
                """
                    DO $$ BEGIN
                    IF NOT EXISTS (
                        SELECT 1
                        FROM pg_class c
                        JOIN pg_namespace n on n.oid = c.relnamespace
                        WHERE c.relname = 'user_tsv_idx'
                        AND n.nspname = 'public'
                    ) THEN
                        EXECUTE 'CREATE INDEX user_tsv_idx ON ${Users.tableName} USING gin(tsv)';
                    END IF;
                    END $$;
                """.trimIndent()
            )
            exec(
                stmt = """
                CREATE OR REPLACE FUNCTION user_tsv_trigger() RETURNS trigger AS $$
                BEGIN
                NEW.tsv :=
                to_tsvector('english', COALESCE(NEW.email, '') || ' ' || COALESCE(NEW.first_name, '') || ' ' || COALESCE(NEW.last_name, ''));
                RETURN NEW;
                END
                $$ LANGUAGE plpgsql;
                """.trimIndent()
            )
            exec(
                """
                DROP TRIGGER IF EXISTS user_tsv_update ON ${Users.tableName};
                """.trimIndent()
            )
            exec(
                stmt = """
                CREATE TRIGGER user_tsv_update BEFORE INSERT OR UPDATE
                ON ${Users.tableName} FOR EACH ROW EXECUTE FUNCTION User_tsv_trigger();
                """.trimIndent()
            )

        }
    }

    suspend fun <T> dbQuery(block: () -> T): T {
        return newSuspendedTransaction(Dispatchers.IO) { block() }
    }

}

fun hikari() = HikariDataSource().apply {
    jdbcUrl = System.getenv("URL")
    username = System.getenv("USER")
    password = System.getenv("DB_PASSWORD")
    driverClassName = System.getenv("DRIVER")
    maximumPoolSize = 3
    isAutoCommit = false
    transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    validate()
}
