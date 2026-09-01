package org.jetbrains.kotlinx.dataframe.samples.schemas

import io.kotest.assertions.throwables.shouldThrow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.DateTimeFormat
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.asValueColumn
import org.jetbrains.kotlinx.dataframe.api.cast
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.replace
import org.jetbrains.kotlinx.dataframe.api.with
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.db.Sqlite
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.junit.Ignore
import org.junit.Test
import java.sql.Connection
import kotlin.io.path.writeText
import kotlin.reflect.typeOf

class DataSchemasTroubleshooting {

    val simpleCsvFile = kotlin.io.path.createTempFile(suffix = ".csv").also {
        it.writeText(
            """
            age
            17
            32
            26
            """.trimIndent(),
        )
    }

    // SampleStart
    @DataSchema
    interface Schema {
        val age: String
    }
    // SampleEnd

    @Test
    fun extensionGeneratedWithAnIncompatibleSchema() {
        shouldThrow<IllegalStateException> {
            // SampleStart
            val df = DataFrame.readCsv(simpleCsvFile).cast<Schema>()

            // Compiles correctly but fails on runtime
            df.filter { age > "20" }
            // SampleEnd
        }
    }

    interface ActualType

    @Ignore
    @Test
    fun changeType() {
        val df = dataFrameOf("wrongTypeCol" to listOf())
        // SampleStart
        df.replace { wrongTypeCol }.with { it.asValueColumn().changeType(typeOf<ActualType>()) }
        // SampleEnd
    }

    @Ignore
    @Test
    fun readSqliteCustom() {
        val connectionConfig = DbConnectionConfig("")

        val customFormat = LocalDateTime.Format { year() }

        // SampleStart
        val sqliteCustom = Sqlite.withCustomConverters {
            // SQLite assigns `NUMERIC` affinity to the custom `LONGVARCHAR` type,
            // so the JDBC driver reports the column type as Int.
            // However, the actual stored values are strings, so we explicitly
            // set the resulting Kotlin type to String?.
            forType<String?>("LONGVARCHAR")

            // Convert values from the "time_stamp" column regardless of its SQL type.
            // The raw values are stored as strings and parsed into LocalDateTime values;
            //  the resulting column has LocalDateTime type as well.
            forColumn("time_stamp") { raw: String ->
                LocalDateTime.parse(raw, customFormat)
            }
        }

        val df = DataFrame.readSqlTable(
            connectionConfig,
            "table_name",
            dbType = sqliteCustom,
        )
        // SampleEnd
    }
}
