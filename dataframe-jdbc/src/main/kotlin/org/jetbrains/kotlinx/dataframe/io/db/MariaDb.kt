package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.db.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.io.db.TableMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import kotlin.reflect.KType
import kotlin.reflect.full.createType

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
        // Force BIGINT to always be Long, regardless of javaClassName
        // MariaDB JDBC driver may report Integer for small BIGINT values
        if (tableColumnMetadata.jdbcType == java.sql.Types.BIGINT) {
            val kType = Long::class.createType(nullable = tableColumnMetadata.isNullable)
            return ColumnSchema.Value(kType)
        }

        if (tableColumnMetadata.sqlTypeName == "INTEGER UNSIGNED" ||
            tableColumnMetadata.sqlTypeName == "INT UNSIGNED"
        ) {
            val kType = Long::class.createType(nullable = tableColumnMetadata.isNullable)
            return ColumnSchema.Value(kType)
        }

        if (tableColumnMetadata.sqlTypeName == "SMALLINT" && tableColumnMetadata.javaClassName == "java.lang.Short") {
            val kType = Short::class.createType(nullable = tableColumnMetadata.isNullable)
            return ColumnSchema.Value(kType)
        }
        return null
    }

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean = MySql.isSystemTable(tableMetadata)

    override fun buildTableMetadata(tables: ResultSet): TableMetadata =
        TableMetadata(
            tables.getString("table_name"),
            tables.getString("table_schem"),
            tables.getString("table_cat"),
        )

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? {
        // Force BIGINT to always be Long, regardless of javaClassName
        // MariaDB JDBC driver may report Integer for small BIGINT values
        if (tableColumnMetadata.jdbcType == java.sql.Types.BIGINT) {
            return Long::class.createType(nullable = tableColumnMetadata.isNullable)
        }

        if (tableColumnMetadata.sqlTypeName == "INTEGER UNSIGNED" ||
            tableColumnMetadata.sqlTypeName == "INT UNSIGNED"
        ) {
            return Long::class.createType(nullable = tableColumnMetadata.isNullable)
        }

        if (tableColumnMetadata.sqlTypeName == "SMALLINT" && tableColumnMetadata.javaClassName == "java.lang.Short") {
            return Short::class.createType(nullable = tableColumnMetadata.isNullable)
        }
        return null
    }

    override fun quoteIdentifier(name: String): String {
        // schema.table -> `schema`.`table`
        return name.split(".").joinToString(".") { "`$it`" }
    }
}
