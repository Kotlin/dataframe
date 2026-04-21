package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionFailedError
import io.kotest.assertions.Exceptions
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.print.printed
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.inferType
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.io.db.MsSql
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema
import java.sql.Connection
import java.sql.ResultSet
import kotlin.reflect.typeOf

private const val TEST_TABLE_NAME = "testtable123"

internal fun inferNullability(connection: Connection) {
    connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS $TEST_TABLE_NAME") }

    // prepare tables and data
    @Language("SQL")
    val createTestTable1Query = """
                CREATE TABLE $TEST_TABLE_NAME (
                    id INT PRIMARY KEY,
                    name VARCHAR(50),
                    surname VARCHAR(50),
                    age INT NOT NULL
                )
            """

    connection.createStatement().use { st -> st.execute(createTestTable1Query) }

    connection.createStatement()
        .execute("INSERT INTO $TEST_TABLE_NAME (id, name, surname, age) VALUES (1, 'John', 'Crawford', 40)")
    connection.createStatement()
        .execute("INSERT INTO $TEST_TABLE_NAME (id, name, surname, age) VALUES (2, 'Alice', 'Smith', 25)")
    connection.createStatement()
        .execute("INSERT INTO $TEST_TABLE_NAME (id, name, surname, age) VALUES (3, 'Bob', 'Johnson', 47)")
    connection.createStatement()
        .execute("INSERT INTO $TEST_TABLE_NAME (id, name, surname, age) VALUES (4, 'Sam', NULL, 15)")

    // start testing `readSqlTable` method

    // with default inferNullability: Boolean = true
    val df = DataFrame.readSqlTable(connection, TEST_TABLE_NAME)
    df.schema().columns["id"]!!.type shouldBe typeOf<Int>()
    df.schema().columns["name"]!!.type shouldBe typeOf<String>()
    df.schema().columns["surname"]!!.type shouldBe typeOf<String?>()
    df.schema().columns["age"]!!.type shouldBe typeOf<Int>()

    val dataSchema = DataFrameSchema.readSqlTable(connection, TEST_TABLE_NAME)
    dataSchema.columns.size shouldBe 4
    dataSchema.columns["id"]!!.type shouldBe typeOf<Int>()
    dataSchema.columns["name"]!!.type shouldBe typeOf<String?>()
    dataSchema.columns["surname"]!!.type shouldBe typeOf<String?>()
    dataSchema.columns["age"]!!.type shouldBe typeOf<Int>()

    // with inferNullability: Boolean = false
    val df1 = DataFrame.readSqlTable(connection, TEST_TABLE_NAME, inferNullability = false)
    df1.schema().columns["id"]!!.type shouldBe typeOf<Int>()

    // this column changed a type because it doesn't contain nulls
    df1.schema().columns["name"]!!.type shouldBe typeOf<String?>()
    df1.schema().columns["surname"]!!.type shouldBe typeOf<String?>()
    df1.schema().columns["age"]!!.type shouldBe typeOf<Int>()

    // end testing `readSqlTable` method

    // start testing `readSQLQuery` method

    // ith default inferNullability: Boolean = true
    @Language("SQL")
    val sqlQuery =
        """
        SELECT name, surname, age FROM $TEST_TABLE_NAME
        """.trimIndent()

    val df2 = DataFrame.readSqlQuery(connection, sqlQuery)
    df2.schema().columns["name"]!!.type shouldBe typeOf<String>()
    df2.schema().columns["surname"]!!.type shouldBe typeOf<String?>()
    df2.schema().columns["age"]!!.type shouldBe typeOf<Int>()

    val dataSchema2 = DataFrameSchema.readSqlQuery(connection, sqlQuery)
    dataSchema2.columns.size shouldBe 3
    dataSchema2.columns["name"]!!.type shouldBe typeOf<String?>()
    dataSchema2.columns["surname"]!!.type shouldBe typeOf<String?>()
    dataSchema2.columns["age"]!!.type shouldBe typeOf<Int>()

    // with inferNullability: Boolean = false
    val df3 = DataFrame.readSqlQuery(connection, sqlQuery, inferNullability = false)
    // this column changed a type because it doesn't contain nulls
    df3.schema().columns["name"]!!.type shouldBe typeOf<String?>()
    df3.schema().columns["surname"]!!.type shouldBe typeOf<String?>()
    df3.schema().columns["age"]!!.type shouldBe typeOf<Int>()

    // end testing `readSQLQuery` method

    // start testing `readResultSet` method

    connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).use { st ->
        @Language("SQL")
        val selectStatement = "SELECT * FROM $TEST_TABLE_NAME"

        st.executeQuery(selectStatement).use { rs ->
            // ith default inferNullability: Boolean = true
            val df4 = DataFrame.readResultSet(rs, MsSql)
            df4.schema().columns["id"]!!.type shouldBe typeOf<Int>()
            df4.schema().columns["name"]!!.type shouldBe typeOf<String>()
            df4.schema().columns["surname"]!!.type shouldBe typeOf<String?>()
            df4.schema().columns["age"]!!.type shouldBe typeOf<Int>()

            rs.beforeFirst()

            val dataSchema3 = DataFrameSchema.readResultSet(rs, MsSql)
            dataSchema3.columns.size shouldBe 4
            dataSchema3.columns["id"]!!.type shouldBe typeOf<Int>()
            dataSchema3.columns["name"]!!.type shouldBe typeOf<String?>()
            dataSchema3.columns["surname"]!!.type shouldBe typeOf<String?>()
            dataSchema3.columns["age"]!!.type shouldBe typeOf<Int>()

            // with inferNullability: Boolean = false
            rs.beforeFirst()

            val df5 = DataFrame.readResultSet(rs, MsSql, inferNullability = false)
            df5.schema().columns["id"]!!.type shouldBe typeOf<Int>()

            // this column changed a type because it doesn't contain nulls
            df5.schema().columns["name"]!!.type shouldBe typeOf<String?>()
            df5.schema().columns["surname"]!!.type shouldBe typeOf<String?>()
            df5.schema().columns["age"]!!.type shouldBe typeOf<Int>()
        }
    }
    // end testing `readResultSet` method

    connection.createStatement().use { st -> st.execute("DROP TABLE IF EXISTS $TEST_TABLE_NAME") }
}

/**
 * Helper to check whether the provided schema matches the inferred schema.
 *
 * It must hold that all types in the provided schema are equal or super to
 * the corresponding types in the inferred schema.
 */
@Suppress("INVISIBLE_REFERENCE")
fun AnyFrame.assertInferredTypesMatchSchema() {
    if (!schema().compare(inferType().schema()).isSuperOrMatches()) {
        throw failure(
            expected = Expected(inferType().schema().toString().lines().sorted().joinToString("\n").printed()),
            actual = Actual(schema().toString().lines().sorted().joinToString("\n").printed()),
            prependMessage = "Inferred schema must be <: Provided schema",
        )
    }
}

fun DataFrameSchema.assertMatches(other: DataFrameSchema) {
    if (!this.compare(other).isSuperOrMatches()) {
        throw failure(
            expected = Expected(other.toString().lines().sorted().joinToString("\n").printed()),
            actual = Actual(this.toString().lines().sorted().joinToString("\n").printed()),
            prependMessage = "Schemas must be <:",
        )
    }
}
