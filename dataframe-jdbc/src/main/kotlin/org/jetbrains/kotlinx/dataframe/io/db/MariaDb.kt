package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import java.sql.Types
import org.jetbrains.kotlinx.dataframe.io.TableMetadata
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Represents the MariaDb database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for MariaDb,
 * and to generate the corresponding column schema.
 */
public object MariaDb : DbType("mariadb") {
    override val driverClassName: String
        get() = "org.mariadb.jdbc.Driver"

    override fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema? {
        if(tableColumnMetadata.jdbcType == Types.TINYINT) // because of https://github.com/pgjdbc/pgjdbc/blob/246b759cdc264c2732717dbd6ff9f8f472024196/pgjdbc/src/main/java/org/postgresql/jdbc/PgResultSet.java#L182
            return ColumnSchema.Value(typeOf<Int>())
        if(tableColumnMetadata.jdbcType == Types.SMALLINT) // because of https://github.com/pgjdbc/pgjdbc/blob/246b759cdc264c2732717dbd6ff9f8f472024196/pgjdbc/src/main/java/org/postgresql/jdbc/PgResultSet.java#L182
            return ColumnSchema.Value(typeOf<Int>())
        return null
    }

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean {
        return MySql.isSystemTable(tableMetadata)
    }

    override fun buildTableMetadata(tables: ResultSet): TableMetadata {
        return TableMetadata(
            tables.getString("table_name"),
            tables.getString("table_schem"),
            tables.getString("table_cat"))
    }

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? {
        if(tableColumnMetadata.jdbcType == Types.TINYINT)
            return typeOf<Int>()
        if(tableColumnMetadata.jdbcType == Types.SMALLINT)
            return typeOf<Int>()
        return null
    }
}
