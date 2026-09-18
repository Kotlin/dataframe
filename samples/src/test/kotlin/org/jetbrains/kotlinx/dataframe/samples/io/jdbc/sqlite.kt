@file:Suppress("UNUSED_VARIABLE", "unused", "UNCHECKED_CAST", "ktlint", "ClassName")

package org.jetbrains.kotlinx.dataframe.samples.io.jdbc

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.DbConnectionConfig
import org.jetbrains.kotlinx.dataframe.io.db.Sqlite
import org.jetbrains.kotlinx.dataframe.io.readSqlTable
import org.junit.Ignore
import org.junit.Test
import kotlin.time.Instant

class SqliteSamples {

    /*
       TODO: Create an actual db for these examples and remove @Ignore
     */

    @Ignore
    @Test
    fun forTypeWithConverter() {
        val customFormat = LocalDateTime.Format {
            year(); char('-'); monthNumber(); char('-'); day()
            char(' ')
            hour(); char(':'); minute(); char(':'); second()
            chars(" UTC")
        }
        // SampleStart
        val sqlite = Sqlite.withCustomConverters {
            // Convert DATETIME values stored as strings in a non-standard format.
            forType("DATETIME") { raw: String ->
                LocalDateTime.parse(raw, customFormat)
            }
        }
        // SampleEnd
    }

    @Ignore
    @Test
    fun forColumnSpecifyType() {
        // SampleStart
        val sqlite = Sqlite.withCustomConverters {
            forColumn<Number?>("mixed_values")
        }
        // SampleEnd
    }

    @Ignore
    @Test
    fun forColumnWithConverter() {
        // SampleStart
        val sqlite = Sqlite.withCustomConverters {
            forColumn("mixed_values") { raw: Number? -> raw?.toLong() }
        }
        // SampleEnd
    }

    @Ignore
    @Test
    fun complexSqlite() {
        val connection = DbConnectionConfig("jdbc:sqlite:todo.db")
        // SampleStart
        val format = LocalDateTime.Format {
            year(); char('-'); monthNumber(); char('-'); day()
            char(' ')
            hour(); char(':'); minute(); char(':'); second()
            chars(" UTC")
        }

        val sqlite = Sqlite.withCustomConverters {
            // Parse a custom text representation into Instant.
            forType("MY_DATETIME") { raw: String? ->
                raw?.let { LocalDateTime.parse(it, format).toInstant(TimeZone.UTC) }
            }

            // Explicitly treat values columns with of `LONGVARCHAR` type as nullable strings.
            // This is necessary because SQLite type affinity assigns those columns the `NUMERIC`
            // base type, causing DataFrame to expect integer values.
            // No conversion is performed; only the expected value type is specified.
            forType<String?>("LONGVARCHAR")

            // Override conversion for a specific "time" column.
            // Column-specific converters take precedence over type-specific converters.
            forColumn("time") { raw: String -> raw.toDouble() }
        }

        val df = DataFrame.readSqlTable(connection, "events", dbType = sqlite)
        // SampleEnd
    }
}
