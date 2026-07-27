package org.jetbrains.kotlinx.dataframe.io

import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.db.Sqlite
import org.jetbrains.kotlinx.dataframe.io.db.SqliteCustomTypeConverter
import org.jetbrains.kotlinx.dataframe.type
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.nio.file.Files
import java.sql.Connection
import java.sql.DriverManager
import kotlin.reflect.typeOf
import kotlin.time.Instant

class SqliteTestCustomTypes {

    companion object {
        private lateinit var connection: Connection

        // Dataset from https://github.com/Kotlin/dataframe/issues/964
        private val dbUrl =
            "jdbc:sqlite:${(this::class as Any).javaClass.classLoader
                .getResource("safe_moz_places_sample.sqlite")!!.path}"

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            connection = DriverManager.getConnection(dbUrl)
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            try {
                connection.close()
            } catch (e: Exception) {
                // Log, but not fail
                println("Warning: Could not clean up test database file: ${e.message}")
            }
        }
    }

    @Test
    fun `LONGVARCHAR columns should be read as String via custom mapping`() {
        // Identity overload — pin the declared LONGVARCHAR to `String?` without any conversion.
        // This is the intended usage when SQLite's type affinity misclassifies the declared type
        // and we just want to fix the resulting Kotlin type.
        val sqliteCustomTypes = Sqlite.withCustomConverters {
            forType<String?>("LONGVARCHAR")
        }

        val df = DataFrame.readSqlTable(connection, "moz_places", dbType = sqliteCustomTypes)

        df["url"].type shouldBe typeOf<String>()
        df["title"].type shouldBe typeOf<String?>()

        df["url"][0] shouldBe "https://support.example.org/products/browser"
        df["title"][0] shouldBe null
        df["title"][4] shouldBe "Column selectors | Sample Docs"
    }

    @Test
    fun `identity forType and forColumn overloads pin the declared type without conversion`() {
        val tempFile = Files.createTempFile("dataframe_sqlite_identity_", ".db").toFile()
        tempFile.deleteOnExit()
        val url = "jdbc:sqlite:${tempFile.absolutePath}"

        DriverManager.getConnection(url).use { conn ->
            // `MY_ID` has NUMERIC affinity by default — SQLite would coerce '42' into an
            // integer on insert. `forType<String>("MY_ID")` pins the resulting DataFrame column
            // to `String`. `forColumn<String>("token")` does the same for a single column.
            @Language("SQL")
            val create =
                """
                CREATE TABLE items (
                    id INTEGER PRIMARY KEY,
                    shortId MY_ID NOT NULL,
                    token MY_ID NOT NULL
                )
                """.trimIndent()
            conn.createStatement().execute(create)
            conn.createStatement().execute(
                """
                INSERT INTO items (shortId, token) VALUES
                    ('A42', 'tok-1'),
                    ('B17', 'tok-2')
                """.trimIndent(),
            )

            val sqlite = Sqlite.withCustomConverters {
                forType<String>("MY_ID")
                forColumn<String>("token")
            }

            val df = DataFrame.readSqlTable(conn, "items", dbType = sqlite)

            df.rowsCount() shouldBe 2
            df["shortId"].type shouldBe typeOf<String>()
            df["token"].type shouldBe typeOf<String>()
            df["shortId"][0] shouldBe "A42"
            df["shortId"][1] shouldBe "B17"
            df["token"][0] shouldBe "tok-1"
            df["token"][1] shouldBe "tok-2"
        }
    }

    @Test
    fun `customColumnsMap overrides customTypesMap for the named column`() {
        val tempFile = Files.createTempFile("dataframe_sqlite_col_override_", ".db").toFile()
        tempFile.deleteOnExit()
        val url = "jdbc:sqlite:${tempFile.absolutePath}"

        DriverManager.getConnection(url).use { conn ->
            // Both columns share the declared type `MYSTR_TEXT` (TEXT affinity — values are
            // stored as-is). The by-type mapping reads them as String; the `ratio` column has a
            // by-name override that parses the same string into a Double instead.
            @Language("SQL")
            val create =
                """
                CREATE TABLE metrics (
                    id INTEGER PRIMARY KEY,
                    label MYSTR_TEXT NOT NULL,
                    ratio MYSTR_TEXT NOT NULL
                )
                """.trimIndent()
            conn.createStatement().execute(create)
            conn.createStatement().execute(
                """
                INSERT INTO metrics (label, ratio) VALUES
                    ('accuracy', '0.87'),
                    ('recall',   '0.61')
                """.trimIndent(),
            )

            val asString: SqliteCustomTypeConverter<String?> = typeOf<String>() to { raw -> raw }
            val asDouble: SqliteCustomTypeConverter<String?> = typeOf<Double>() to { raw -> raw?.toDouble() }

            val sqlite = Sqlite(
                customTypesMap = mapOf("MYSTR_TEXT" to asString),
                customColumnsMap = mapOf("ratio" to asDouble),
            )

            val df = DataFrame.readSqlTable(conn, "metrics", dbType = sqlite)

            df.rowsCount() shouldBe 2

            // `label` is covered only by the by-type mapping → String.
            df["label"].type shouldBe typeOf<String>()
            df["label"][0] shouldBe "accuracy"
            df["label"][1] shouldBe "recall"

            // `ratio` shares the declared type but the by-name override wins → Double.
            df["ratio"].type shouldBe typeOf<Double>()
            df["ratio"][0] shouldBe 0.87
            df["ratio"][1] shouldBe 0.61
        }
    }

    @Test
    fun `withCustomConverters DSL builds Sqlite with type and column mappings`() {
        val tempFile = Files.createTempFile("dataframe_sqlite_dsl_", ".db").toFile()
        tempFile.deleteOnExit()
        val url = "jdbc:sqlite:${tempFile.absolutePath}"

        DriverManager.getConnection(url).use { conn ->
            @Language("SQL")
            val create =
                """
                CREATE TABLE events (
                    id INTEGER PRIMARY KEY,
                    occurredAt MY_DATETIME NOT NULL,
                    note MY_DATETIME,
                    score MYSTR_TEXT NOT NULL,
                    ratio MYSTR_TEXT NOT NULL
                )
                """.trimIndent()
            conn.createStatement().execute(create)
            conn.createStatement().execute(
                """
                INSERT INTO events (occurredAt, note, score, ratio) VALUES
                    ('2023-07-21 10:30:00 UTC', '2023-07-21 10:30:00 UTC', 'A+', '0.87'),
                    ('2023-08-15 18:45:30 UTC', NULL,                     'B',  '0.61')
                """.trimIndent(),
            )

            val format = LocalDateTime.Format {
                year()
                char('-')
                monthNumber()
                char('-')
                dayOfMonth()
                char(' ')
                hour()
                char(':')
                minute()
                char(':')
                second()
                chars(" UTC")
            }
            val sqlite = Sqlite.withCustomConverters {
                // Every MY_DATETIME column is parsed from the custom text format into Instant.
                // Declare T as `String?` so null rows (`note` column) don't blow up in the lambda.
                forType("MY_DATETIME") { raw: String? ->
                    raw?.let { LocalDateTime.parse(it, format).toInstant(TimeZone.UTC) }
                }
                // `score` uses the declared type MYSTR_TEXT — no converter registered, so the
                // default text mapping applies → String.
                // The `ratio` column shares the declared type but the column-name override
                // parses it into a Double.
                forColumn("ratio") { raw: String -> raw.toDouble() }
            }

            val df = DataFrame.readSqlTable(conn, "events", dbType = sqlite)

            df.rowsCount() shouldBe 2

            df["occurredAt"].type shouldBe typeOf<Instant>()
            df["note"].type shouldBe typeOf<Instant?>()
            df["ratio"].type shouldBe typeOf<Double>()

            df["occurredAt"][0] shouldBe Instant.parse("2023-07-21T10:30:00Z")
            df["occurredAt"][1] shouldBe Instant.parse("2023-08-15T18:45:30Z")
            df["note"][0] shouldBe Instant.parse("2023-07-21T10:30:00Z")
            df["note"][1] shouldBe null
            df["ratio"][0] shouldBe 0.87
            df["ratio"][1] shouldBe 0.61
        }
    }

    @Test
    fun `custom mapping converts a stored String value into a domain type`() {
        // Set up a fresh SQLite database with a table that stores timestamps in an
        // application-specific format that neither `Instant.parse` nor `LocalDateTime.parse`
        // accepts by default.
        val tempFile = Files.createTempFile("dataframe_sqlite_custom_", ".db").toFile()
        tempFile.deleteOnExit()
        val url = "jdbc:sqlite:${tempFile.absolutePath}"

        DriverManager.getConnection(url).use { conn ->
            @Language("SQL")
            val create =
                "CREATE TABLE events (id INTEGER PRIMARY KEY, occurredAt MY_DATETIME NOT NULL, note MY_DATETIME)"
            conn.createStatement().execute(create)
            conn.createStatement().execute(
                """
                INSERT INTO events (occurredAt, note) VALUES
                    ('2023-07-21 10:30:00 UTC', '2023-07-21 10:30:00 UTC'),
                    ('2023-08-15 18:45:30 UTC', NULL)
                """.trimIndent(),
            )

            val format = LocalDateTime.Format {
                year()
                char('-')
                monthNumber()
                char('-')
                day()
                char(' ')
                hour()
                char(':')
                minute()
                char(':')
                second()
                chars(" UTC")
            }

            val sqlite = Sqlite.withCustomConverters {
                forType("MY_DATETIME") { raw: String? ->
                    raw?.let { LocalDateTime.parse(it, format).toInstant(TimeZone.UTC) }
                }
            }

            val eventsDf = DataFrame.readSqlTable(conn, "events", dbType = sqlite)

            eventsDf.rowsCount() shouldBe 2

            // Column types come from the KType half of the pair. Infer.Nulls narrows the NOT NULL
            // column to non-nullable.
            eventsDf["occurredAt"].type shouldBe typeOf<Instant>()
            eventsDf["note"].type shouldBe typeOf<Instant?>()

            eventsDf["occurredAt"][0] shouldBe Instant.parse("2023-07-21T10:30:00Z")
            eventsDf["occurredAt"][1] shouldBe Instant.parse("2023-08-15T18:45:30Z")
            eventsDf["note"][0] shouldBe Instant.parse("2023-07-21T10:30:00Z")
            eventsDf["note"][1] shouldBe null
        }
    }
}
