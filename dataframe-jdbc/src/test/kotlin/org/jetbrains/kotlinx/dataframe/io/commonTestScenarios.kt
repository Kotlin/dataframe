package org.jetbrains.kotlinx.dataframe.io

import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.inferType
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.io.db.MsSql
import org.jetbrains.kotlinx.dataframe.schema.CompareResult
import java.sql.Connection
import java.sql.ResultSet
import kotlin.reflect.typeOf

internal fun inferNullability(connection: Connection) {
    // prepare tables and data
    @Language("SQL")
    val createTestTable1Query = """
                CREATE TABLE TestTable1 (
                    id INT PRIMARY KEY,
                    name VARCHAR(50),
                    surname VARCHAR(50),
                    age INT NOT NULL
                )
            """

    connection.createStatement().execute(createTestTable1Query)

    connection.createStatement()
        .execute("INSERT INTO TestTable1 (id, name, surname, age) VALUES (1, 'John', 'Crawford', 40)")
    connection.createStatement()
        .execute("INSERT INTO TestTable1 (id, name, surname, age) VALUES (2, 'Alice', 'Smith', 25)")
    connection.createStatement()
        .execute("INSERT INTO TestTable1 (id, name, surname, age) VALUES (3, 'Bob', 'Johnson', 47)")
    connection.createStatement()
        .execute("INSERT INTO TestTable1 (id, name, surname, age) VALUES (4, 'Sam', NULL, 15)")

    // start testing `readSqlTable` method

    // with default inferNullability: Boolean = true
    val tableName = "TestTable1"
    val df = DataFrame.readSqlTable(connection, tableName)
    df.schema().columns["id"]!!.type shouldBe typeOf<Int>()
    df.schema().columns["name"]!!.type shouldBe typeOf<String>()
    df.schema().columns["surname"]!!.type shouldBe typeOf<String?>()
    df.schema().columns["age"]!!.type shouldBe typeOf<Int>()

    val dataSchema = DataFrame.getSchemaForSqlTable(connection, tableName)
    dataSchema.columns.size shouldBe 4
    dataSchema.columns["id"]!!.type shouldBe typeOf<Int>()
    dataSchema.columns["name"]!!.type shouldBe typeOf<String?>()
    dataSchema.columns["surname"]!!.type shouldBe typeOf<String?>()
    dataSchema.columns["age"]!!.type shouldBe typeOf<Int>()

    // with inferNullability: Boolean = false
    val df1 = DataFrame.readSqlTable(connection, tableName, inferNullability = false)
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
        SELECT name, surname, age FROM TestTable1
        """.trimIndent()

    val df2 = DataFrame.readSqlQuery(connection, sqlQuery)
    df2.schema().columns["name"]!!.type shouldBe typeOf<String>()
    df2.schema().columns["surname"]!!.type shouldBe typeOf<String?>()
    df2.schema().columns["age"]!!.type shouldBe typeOf<Int>()

    val dataSchema2 = DataFrame.getSchemaForSqlQuery(connection, sqlQuery)
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
        val selectStatement = "SELECT * FROM TestTable1"

        st.executeQuery(selectStatement).use { rs ->
            // ith default inferNullability: Boolean = true
            val df4 = DataFrame.readResultSet(rs, MsSql)
            df4.schema().columns["id"]!!.type shouldBe typeOf<Int>()
            df4.schema().columns["name"]!!.type shouldBe typeOf<String>()
            df4.schema().columns["surname"]!!.type shouldBe typeOf<String?>()
            df4.schema().columns["age"]!!.type shouldBe typeOf<Int>()

            rs.beforeFirst()

            val dataSchema3 = DataFrame.getSchemaForResultSet(rs, MsSql)
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

    connection.createStatement().execute("DROP TABLE TestTable1")
}

/**
 * Helper to check whether the provided schema matches the inferred schema.
 *
 * It must hold that all types in the provided schema are equal or super to
 * the corresponding types in the inferred schema.
 */
@Suppress("INVISIBLE_REFERENCE")
fun AnyFrame.assertInferredTypesMatchSchema() {
    withClue({
        """
        |Inferred schema must be <: Provided schema
        |
        |Inferred Schema: 
        |${inferType().schema().toString().lines().joinToString("\n|")}
        |
        |Provided Schema:
        |${schema().toString().lines().joinToString("\n|")}
        """.trimMargin()
    }) {
        schema().compare(inferType().schema()).isSuperOrEqual() shouldBe true
    }
}
