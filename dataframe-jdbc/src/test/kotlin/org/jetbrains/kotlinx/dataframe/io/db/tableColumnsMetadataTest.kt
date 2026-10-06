package org.jetbrains.kotlinx.dataframe.io.db

import io.kotest.matchers.shouldBe
import org.junit.Test
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLFeatureNotSupportedException

/**
 * How [DbType.getTableColumnsMetadata] reads a column when the driver does not support some
 * [ResultSetMetaData] methods. The driver is simulated: a proxy over an H2 [ResultSet] answers the
 * overridden methods.
 */
class TableColumnsMetadataTest {

    private val unsupported: (Int) -> Any? = { throw SQLFeatureNotSupportedException() }

    @Test
    fun `a column whose label is unsupported is named by its column name`() {
        columns("getColumnLabel" to unsupported).map { it.name } shouldBe listOf("id", "name")
    }

    @Test
    fun `a column with an empty label is named by its column name`() {
        columns("getColumnLabel" to { "" }).map { it.name } shouldBe listOf("id", "name")
    }

    @Test
    fun `a column with a null label is named by its column name`() {
        columns("getColumnLabel" to { null }).map { it.name } shouldBe listOf("id", "name")
    }

    @Test
    fun `a column whose column name is unsupported is named by its label`() {
        columns("getColumnName" to unsupported).map { it.name } shouldBe listOf("customer_id", "customer_name")
    }

    @Test
    fun `a column with neither a label nor a name is named untitled`() {
        columns("getColumnLabel" to unsupported, "getColumnName" to unsupported).map { it.name } shouldBe
            listOf("untitled", "untitled1")
    }

    @Test
    fun `without isNullable the nullability is looked up by the name the column has in its table`() {
        // `id` is NOT NULL, `name` is nullable; the aliases `customer_id`, `customer_name` match no table column
        columns("isNullable" to unsupported).map { it.isNullable } shouldBe listOf(false, true)
    }

    @Test
    fun `without getTableName the table is taken from a table-qualified column name`() {
        // a driver that reports the column name qualified with its table
        val qualified: (Int) -> Any? = { listOf("t.id", "t.name")[it - 1] }
        columns("getTableName" to unsupported, "getColumnName" to qualified, "isNullable" to unsupported)
            .map { it.isNullable } shouldBe listOf(false, true)
    }

    /**
     * Reads the metadata of `SELECT id AS customer_id, name AS customer_name` from H2, with the driver's
     * [ResultSetMetaData] methods named in [overrides] answered by the override instead.
     */
    private fun columns(vararg overrides: Pair<String, (Int) -> Any?>): List<TableColumnMetadata> =
        DriverManager.getConnection("jdbc:h2:mem:;DATABASE_TO_UPPER=false").use { connection ->
            connection.createStatement().use { st ->
                st.execute("CREATE TABLE t (id INT NOT NULL, name VARCHAR(50))")
                st.executeQuery("SELECT id AS customer_id, name AS customer_name FROM t").use { rs ->
                    H2(H2.Mode.Regular).getTableColumnsMetadata(rs.withMetaData(overrides.toMap()))
                }
            }
        }

    private fun ResultSet.withMetaData(overrides: Map<String, (Int) -> Any?>): ResultSet {
        val resultSet = this
        val metaData = resultSet.metaData
        val loader = javaClass.classLoader
        val metaDataProxy = Proxy.newProxyInstance(loader, arrayOf(ResultSetMetaData::class.java)) { _, method, args ->
            val override = overrides[method.name]
            if (override != null) override(args[0] as Int) else method.forward(metaData, args)
        } as ResultSetMetaData
        return Proxy.newProxyInstance(loader, arrayOf(ResultSet::class.java)) { _, method, args ->
            if (method.name == "getMetaData") metaDataProxy else method.forward(resultSet, args)
        } as ResultSet
    }

    private fun Method.forward(target: Any, args: Array<out Any?>?): Any? =
        try {
            invoke(target, *(args ?: emptyArray()))
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }
}
