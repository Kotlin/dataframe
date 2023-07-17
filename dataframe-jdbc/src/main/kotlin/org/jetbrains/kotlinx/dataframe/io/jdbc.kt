package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.codeGen.AbstractDefaultReadMethod
import org.jetbrains.kotlinx.dataframe.codeGen.DefaultReadDfMethod
import java.io.File
import java.io.InputStream
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.ResultSet

// JDBC is not a file format, we need a hierarchy here
public class JDBC : SupportedDataFrameFormat {
    override fun readDataFrame(stream: InputStream, header: List<String>): AnyFrame = DataFrame.readJDBC(stream)

    override fun readDataFrame(file: File, header: List<String>): AnyFrame = DataFrame.readJDBC(file)

    override fun acceptsExtension(ext: String): Boolean = ext == "jdbc"

    override fun acceptsSample(sample: SupportedFormatSample): Boolean = true // Extension is enough

    override val testOrder: Int = 40000

    override fun createDefaultReadMethod(pathRepresentation: String?): DefaultReadDfMethod {
        return DefaultReadExcelMethod(pathRepresentation)
    }
}

private fun DataFrame.Companion.readJDBC(stream: File): DataFrame<*> {
    TODO("Not yet implemented")
}

private fun DataFrame.Companion.readJDBC(stream: InputStream): DataFrame<*> {
    TODO("Not yet implemented")
}

internal class DefaultReadExcelMethod(path: String?) : AbstractDefaultReadMethod(path, MethodArguments.EMPTY, readJDBC)

private const val readJDBC = "readJDBC"

public data class JDBCColumn(val name: String, val type: String, val size: Int)

public fun DataFrame.Companion.readFromDBViaSQLQuery(connection: Connection, sqlQuery: String): AnyFrame {
    connection.createStatement().use { st ->
        val dbMetaData: DatabaseMetaData = connection.metaData
        val columns: ResultSet = dbMetaData.getColumns("imdb", null, tableName, null)
        val tableColumns = mutableMapOf<String, JDBCColumn>()

        while (columns.next()) {
            val name = columns.getString("COLUMN_NAME")
            val type = columns.getString("TYPE_NAME")
            val size = columns.getInt("COLUMN_SIZE")
            tableColumns += Pair(name, JDBCColumn(name, type, size))
        }

        // map<columnName; columndata>
        val data = mutableMapOf<String, MutableList<Any?>>()
        // init data
        tableColumns.forEach { (columnName, _) ->
            data[columnName] = mutableListOf()
        }

        // TODO: dynamic SQL names - no protection from SQL injection
        // What if just try to match it before to the known SQL table names and if not to reject
        // What if check the name on the SQL commands and ;; commas to reject and throw exception


        // LIMIT 1000 because is very slow to copy into dataframe the whole table (do we need a fetch here? or limit)
        // or progress bar
        var counter = 0
        // ask the COUNT(*) for full table
        st.executeQuery("SELECT * FROM $tableName "
            + "LIMIT 1000" // TODO: work with limits correctly
        ).use { rs ->
            while (rs.next()) {
                tableColumns.forEach { (columnName, jdbcColumn) ->
                    data[columnName] = (data[columnName]!! + getData(rs, jdbcColumn)).toMutableList()
                }
                counter++
                if (counter % 1000 == 0) println("Loaded yet 1000, percentage = $counter")
            }
        }

        return data.toDataFrame()
    }
}


public fun DataFrame.Companion.readFromDB(connection: Connection, tableName: String): AnyFrame {
    connection.createStatement().use { st ->
        val dbMetaData: DatabaseMetaData = connection.metaData
        val columns: ResultSet = dbMetaData.getColumns("imdb", null, tableName, null)
        val tableColumns = mutableMapOf<String, JDBCColumn>()

        while (columns.next()) {
            val name = columns.getString("COLUMN_NAME")
            val type = columns.getString("TYPE_NAME")
            val size = columns.getInt("COLUMN_SIZE")
            tableColumns += Pair(name, JDBCColumn(name, type, size))
        }

        // map<columnName; columndata>
        val data = mutableMapOf<String, MutableList<Any?>>()
        // init data
        tableColumns.forEach { (columnName, _) ->
            data[columnName] = mutableListOf()
        }

        // TODO: dynamic SQL names - no protection from SQL injection
        // What if just try to match it before to the known SQL table names and if not to reject
        // What if check the name on the SQL commands and ;; commas to reject and throw exception


        // LIMIT 1000 because is very slow to copy into dataframe the whole table (do we need a fetch here? or limit)
        // or progress bar
        var counter = 0
        // ask the COUNT(*) for full table
        st.executeQuery("SELECT * FROM $tableName "
             + "LIMIT 1000" // TODO: work with limits correctly
        ).use { rs ->
            while (rs.next()) {
                tableColumns.forEach { (columnName, jdbcColumn) ->
                    data[columnName] = (data[columnName]!! + getData(rs, jdbcColumn)).toMutableList()
                }
                counter++
                if (counter % 1000 == 0) println("Loaded yet 1000, percentage = $counter")
            }
        }

        return data.toDataFrame()
    }
}

// TODO: slow solution could be optimized with batches control and fetching
// also better to manipulate whole row instead of asking by column, need to go to the rowset
// be sure that all the stuff is closed

private fun getData(rs: ResultSet, jdbcColumn: JDBCColumn): Any? {
    return when (jdbcColumn.type) {
        "INT" -> rs.getInt(jdbcColumn.name)
        "VARCHAR" -> rs.getString(jdbcColumn.name)
        else -> null
    }
}

