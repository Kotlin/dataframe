package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.fail
import org.jetbrains.kotlinx.dataframe.io.db.DbType
import org.jetbrains.kotlinx.dataframe.io.db.TableColumnMetadata
import java.sql.Connection
import kotlin.reflect.jvm.jvmErasure

/**
 * Checks the invariant behind [#2087](https://github.com/Kotlin/dataframe/issues/2087): for every
 * column, the type the library *declares* must accept the value the driver actually *returns*.
 *
 * When the two disagree, reading the table throws
 * (`Values of ValueColumn 'x' have types '[Y]' which are not compatible given with column type 'Z'`),
 * but only with `-Pkotlin.dataframe.debug=true`, and only for the first offending column — which is
 * how several of these went unnoticed at once. This audit therefore
 *
 * - covers a **wide surface of SQL types** rather than only the columns the other tests happen to use,
 * - reads every column **in a separate query**, so one broken column cannot hide the others,
 * - reports **all** mismatches in a single failure instead of stopping at the first.
 *
 * @param [dbType] the [DbType] under test, used exactly as the reading pipeline would use it
 * @param [ddl] creates the probe table; it must be named [PROBE_TABLE] and hold one column per SQL type
 * @param [insert] inserts a single non-null row into it
 */
internal fun Connection.assertColumnTypesMatchValues(dbType: DbType, ddl: String, insert: String) {
    val mismatches = try {
        createStatement().use { st ->
            st.execute("DROP TABLE IF EXISTS $PROBE_TABLE")
            st.execute(ddl)
            st.execute(insert)
        }

        val columnNames = createStatement().executeQuery("SELECT * FROM $PROBE_TABLE").use { rs ->
            (1..rs.metaData.columnCount).map { rs.metaData.getColumnName(it) }
        }

        columnNames.mapNotNull { name -> describeMismatch(dbType, name) }
    } finally {
        // the probe table must not outlive the test: tests that read *all* tables of the database
        // address the results positionally, so leaving it behind would make them order-dependent
        createStatement().use { st -> st.execute("DROP TABLE IF EXISTS $PROBE_TABLE") }
    }

    if (mismatches.isNotEmpty()) {
        fail(
            "${mismatches.size} column(s) declare a type that does not accept the value the driver returns:\n" +
                mismatches.joinToString("\n") { "  - $it" },
        )
    }
}

private const val PROBE_TABLE = "column_type_audit"

/**
 * Runs the value-reading pipeline for a single column and returns a description of the problem,
 * or `null` when the declared type accepts the value.
 *
 * Note that this stops after [DbType.preprocessValue]; `java.sql.Array` values are turned into Kotlin
 * arrays one step later, in [DbType.buildDataColumn], so array columns are skipped here rather than
 * reported as false mismatches. They are covered end-to-end by the PostgreSQL tests.
 */
private fun Connection.describeMismatch(dbType: DbType, columnName: String): String? {
    val quoted = dbType.quoteIdentifier(columnName)
    return createStatement().executeQuery("SELECT $quoted FROM $PROBE_TABLE").use { rs ->
        val metadata: TableColumnMetadata = dbType.getTableColumnsMetadata(rs).single()
        val expectedJdbcType = dbType.getExpectedJdbcType(metadata)
        val declaredType = dbType.getPreprocessedValueType(metadata, expectedJdbcType)

        rs.next()
        val value: Any? = dbType.preprocessValue(
            dbType.getValueFromResultSet<Any?>(rs, 0, metadata, expectedJdbcType),
            metadata,
            expectedJdbcType,
            declaredType,
        )

        when {
            value == null -> "$columnName: read back as null, the probe row has no nulls"

            // a java.sql.Array is turned into a Kotlin array one step later, in buildDataColumn
            value is java.sql.Array -> null

            // javaObjectType, not java: for a non-nullable Int the latter is the primitive `int`,
            // which no boxed value is ever assignable to
            declaredType.jvmErasure.javaObjectType.isAssignableFrom(value.javaClass) -> null

            else ->
                "$columnName (${metadata.sqlTypeName}, jdbcType=${metadata.jdbcType}, " +
                    "reported as ${metadata.javaClassName}): declared $declaredType, " +
                    "but the value is a ${value.javaClass.kotlin.qualifiedName}"
        }
    }
}
