package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.fail
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.db.DbType
import org.jetbrains.kotlinx.dataframe.io.db.TableColumnMetadata
import java.sql.Connection
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
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
 * This says nothing about *which* type a column gets, only that the declared one is not contradicted
 * by the value — a column declared [Any] passes it trivially. The types themselves are pinned by
 * [assertColumnTypes].
 *
 * @param [dbType] the [DbType] under test, used exactly as the reading pipeline would use it
 * @param [ddl] creates the probe table; it must be named [PROBE_TABLE] and hold one column per SQL type
 * @param [insert] inserts a single non-null row into it
 */
internal fun Connection.assertColumnTypesMatchValues(dbType: DbType, ddl: String, insert: String) {
    val mismatches = withProbeTable(ddl, insert) {
        probeColumnNames().mapNotNull { name -> describeMismatch(dbType, name) }
    }

    if (mismatches.isNotEmpty()) {
        fail(
            "${mismatches.size} column(s) declare a type that does not accept the value the driver returns:\n" +
                mismatches.joinToString("\n") { "  - $it" },
        )
    }
}

/**
 * Pins the column type every SQL type of the probe table is read as — the claim the type-mapping
 * pages under `docs/StardustDocs/topics/io/` publish, asserted against a real read of a real column
 * rather than against synthetic metadata like [assertMappings] does.
 *
 * Complements [assertColumnTypesMatchValues]: that one proves the declared type is not *wrong*, this
 * one proves it is the *documented* one. Without it a mapping may silently widen to [Any] — which
 * accepts every value and so passes the invariant — or a page may drift from the code, which is
 * exactly what happened before #2087.
 *
 * The table is read with `inferNullability = false`, so the types come from the driver's metadata and
 * not from the single probe row, and compared with nullability stripped: every probe column is
 * nullable, while the pages document the non-nullable type. So [expected] is written exactly as the
 * pages' "DataFrame column type" column reads.
 *
 * @param [expected] the expected column type per column name; must list **every** column of the probe
 *   table, so that a newly added column cannot go unpinned
 */
internal fun Connection.assertColumnTypes(
    dbType: DbType,
    ddl: String,
    insert: String,
    expected: Map<String, KType>,
) {
    val actual = withProbeTable(ddl, insert) {
        val df = DataFrame.readSqlTable(this, PROBE_TABLE, inferNullability = false, dbType = dbType)
        df.columns().associate { it.name() to it.type().withNullability(false) }
    }

    val problems = (actual.keys + expected.keys).sorted().mapNotNull { name ->
        val actualType = actual[name]
        val expectedType = expected[name]
        when {
            actualType == null -> "$name: pinned as $expectedType, but the probe table has no such column"
            expectedType == null -> "$name: read as $actualType, but is not pinned — add it to the expected types"
            actualType != expectedType -> "$name: pinned as $expectedType, but read as $actualType"
            else -> null
        }
    }

    if (problems.isNotEmpty()) {
        fail(
            "${problems.size} column(s) are not read as the type-mapping page documents:\n" +
                problems.joinToString("\n") { "  - $it" },
        )
    }
}

private const val PROBE_TABLE = "column_type_audit"

/**
 * Creates the probe table, runs [body] against it and drops it again.
 *
 * The table must not outlive the test: tests that read *all* tables of the database address the
 * results positionally, so leaving it behind would make them order-dependent.
 */
private fun <R> Connection.withProbeTable(ddl: String, insert: String, body: Connection.() -> R): R =
    try {
        createStatement().use { st ->
            st.execute("DROP TABLE IF EXISTS $PROBE_TABLE")
            st.execute(ddl)
            st.execute(insert)
        }
        body()
    } finally {
        createStatement().use { st -> st.execute("DROP TABLE IF EXISTS $PROBE_TABLE") }
    }

private fun Connection.probeColumnNames(): List<String> =
    createStatement().use { st ->
        st.executeQuery("SELECT * FROM $PROBE_TABLE").use { rs ->
            (1..rs.metaData.columnCount).map { rs.metaData.getColumnName(it) }
        }
    }

/**
 * Runs the value-reading pipeline for a single column and returns a description of the problem,
 * or `null` when the declared type accepts the value.
 *
 * Note that this stops after [DbType.preprocessValue]: a `java.sql.Array` value is turned into a
 * Kotlin array one step later, in [DbType.buildDataColumn], so for an array column the check is that
 * the declared type is an array type at all. What it is an array *of* is pinned by
 * [assertColumnTypes], which reads the column end-to-end.
 */
private fun Connection.describeMismatch(dbType: DbType, columnName: String): String? {
    val quoted = dbType.quoteIdentifier(columnName)
    return createStatement().use { st ->
        st.executeQuery("SELECT $quoted FROM $PROBE_TABLE").use { rs ->
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

            val describe = { problem: String ->
                "$columnName (${metadata.sqlTypeName}, jdbcType=${metadata.jdbcType}, " +
                    "reported as ${metadata.javaClassName}): declared $declaredType, $problem"
            }

            when {
                value == null -> "$columnName: read back as null, the probe row has no nulls"

                value is java.sql.Array ->
                    if (declaredType.jvmErasure.java.isArray) {
                        null
                    } else {
                        describe("but the value is a java.sql.Array, which buildDataColumn turns into an array")
                    }

                // javaObjectType, not java: for a non-nullable Int the latter is the primitive `int`,
                // which no boxed value is ever assignable to
                declaredType.jvmErasure.javaObjectType.isAssignableFrom(value.javaClass) -> null

                else -> describe("but the value is a ${value.javaClass.kotlin.qualifiedName}")
            }
        }
    }
}
