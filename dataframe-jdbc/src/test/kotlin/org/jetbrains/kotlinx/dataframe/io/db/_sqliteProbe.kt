package org.jetbrains.kotlinx.dataframe.io.db

import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.sql.DriverManager
import java.sql.Types

/**
 * Probe used to figure out what the Xerial SQLite JDBC driver reports in the metadata
 * for common declared column types (SQLite type affinity is applied at storage level, but
 * the driver preserves the declared type name in `sqlTypeName`).
 *
 * Not part of the regular suite; enable ad-hoc if you need to refresh the mapping.
 */
@Ignore("Investigation probe — not a real test")
class SqliteMetadataProbe {

    @Test
    fun `dump getObject class vs metadata jdbcType for problematic types`() {
        val file = Files.createTempFile("dataframe_sqlite_probe2_", ".db").toFile()
        file.deleteOnExit()
        val url = "jdbc:sqlite:${file.absolutePath}"

        // (declaredType, sql-literal, description)
        val cases = listOf(
            Triple("BOOLEAN", "1", "boolean-as-int"),
            Triple("BOOLEAN", "0", "boolean-as-int-false"),
            Triple("DATE", "'2020-01-15'", "date-as-iso-text"),
            Triple("DATE", "1579046400", "date-as-unix-int"),
            Triple("DATETIME", "'2020-01-15 10:30:00'", "datetime-as-iso-text"),
            Triple("TIMESTAMP", "'2020-01-15T10:30:00Z'", "timestamp-as-iso-text"),
            Triple("TIMESTAMP", "1579083000", "timestamp-as-unix-int"),
            Triple("NUMERIC", "1.5", "numeric-as-real"),
            Triple("NUMERIC", "42", "numeric-as-int"),
            Triple("DECIMAL(10,5)", "1.5", "decimal-as-real"),
            Triple("DATE", "2460146.5", "date-as-julian-real"),
            Triple("TIMESTAMP", "2460146.5", "timestamp-as-julian-real"),
        )

        DriverManager.getConnection(url).use { conn ->
            cases.forEachIndexed { i, (decl, literal, _) ->
                conn.createStatement().execute("CREATE TABLE t$i (col $decl)")
                conn.createStatement().execute("INSERT INTO t$i (col) VALUES ($literal)")
            }
            for ((i, case) in cases.withIndex()) {
                val (decl, literal, desc) = case
                conn.prepareStatement("SELECT col FROM t$i").executeQuery().use { rs ->
                    val md = rs.metaData
                    val jdbc = md.getColumnType(1)
                    val jdbcName = jdbcName(jdbc)
                    val cls = md.getColumnClassName(1)
                    rs.next()
                    val got = rs.getObject(1)
                    val gotClass = got?.javaClass?.name
                    println(
                        "%-25s declared=%-14s literal=%-24s meta.jdbc=%-9s meta.class=%-22s getObject.class=%s value=%s".format(
                            desc, decl, literal, jdbcName, cls, gotClass, got,
                        ),
                    )
                }
            }
        }
        file.delete()
    }

    @Test
    fun `dump metadata for common declared types`() {
        val file = Files.createTempFile("dataframe_sqlite_probe_", ".db").toFile()
        file.deleteOnExit()
        val url = "jdbc:sqlite:${file.absolutePath}"

        val declaredTypes = listOf(
            "INTEGER", "INT", "TINYINT", "SMALLINT", "MEDIUMINT", "BIGINT", "UNSIGNED BIG INT",
            "INT2", "INT8",
            "REAL", "DOUBLE", "DOUBLE PRECISION", "FLOAT",
            "NUMERIC", "DECIMAL(10,5)",
            "BOOLEAN",
            "DATE", "DATETIME", "TIMESTAMP",
            "TEXT", "VARCHAR(255)", "CHAR(10)", "CLOB", "NVARCHAR(10)", "NCHAR(10)",
            "BLOB",
            "",           // no declared type
            "CUSTOM_TYPE", // unknown → NUMERIC affinity
        )

        DriverManager.getConnection(url).use { conn ->
            declaredTypes.forEachIndexed { i, decl ->
                val col = "col$i ${if (decl.isEmpty()) "" else decl}".trim()
                conn.createStatement().execute("CREATE TABLE t$i ($col)")
                // Insert one representative value so the driver can infer the actual storage class.
                conn.createStatement().execute("INSERT INTO t$i (col$i) VALUES (${sampleValueFor(decl)})")
            }
            for ((i, decl) in declaredTypes.withIndex()) {
                conn.prepareStatement("SELECT col$i FROM t$i").executeQuery().use { rs ->
                    val md = rs.metaData
                    val jdbc = md.getColumnType(1)
                    val jdbcName = jdbcName(jdbc)
                    val typeName = md.getColumnTypeName(1)
                    val cls = md.getColumnClassName(1)
                    println(
                        "declared=%-24s -> sqlTypeName=%-16s jdbcType=%-8s (%s) classNm=%s".format(
                            "\"$decl\"", typeName, jdbc.toString(), jdbcName, cls,
                        ),
                    )
                }
            }
        }
        file.delete()
    }

    private fun jdbcName(t: Int): String =
        Types::class.java.fields.firstOrNull { it.getInt(null) == t }?.name ?: "?"

    private fun sampleValueFor(decl: String): String {
        val u = decl.uppercase()
        return when {
            u.contains("BLOB") -> "x'00'"
            u.contains("BOOLEAN") -> "1"
            u.contains("DATE") || u.contains("TIMESTAMP") || u.contains("TIME") -> "'2020-01-01'"
            u.contains("CHAR") || u.contains("TEXT") || u.contains("CLOB") -> "'x'"
            u.contains("REAL") || u.contains("DOUB") || u.contains("FLOA") ||
                u.contains("NUMERIC") || u.contains("DECIMAL") -> "1.5"
            u.contains("INT") -> "1"
            u.isEmpty() -> "1"
            else -> "'x'"
        }
    }
}
