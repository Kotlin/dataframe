package org.jetbrains.kotlinx.dataframe.samples.schemas

import io.kotest.assertions.throwables.shouldThrow
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
        shouldThrow<ClassCastException> {
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

        // SampleStart
        val sqliteCustom = Sqlite.withCustomTypes(
            mapOf(
                "LONGVARCHAR" to typeOf<String>(),
                "LONGINT" to typeOf<Long>()
            )
        )
        val df = DataFrame.readSqlTable(
            connectionConfig, "table_name", dbType = sqliteCustom
        )
        // SampleEnd
    }
}
