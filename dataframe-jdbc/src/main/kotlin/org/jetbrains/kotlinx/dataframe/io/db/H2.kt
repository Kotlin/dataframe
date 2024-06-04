package org.jetbrains.kotlinx.dataframe.io.db

import org.jetbrains.kotlinx.dataframe.io.TableColumnMetadata
import org.jetbrains.kotlinx.dataframe.io.TableMetadata
import org.jetbrains.kotlinx.dataframe.schema.ColumnSchema
import java.sql.ResultSet
import java.util.Locale
import kotlin.reflect.KType

/**
 * Represents the H2 database type.
 *
 * This class provides methods to convert data from a ResultSet to the appropriate type for H2
 * and to generate the corresponding column schema.
 *
 * NOTE: All date and timestamp-related types are converted to String to avoid java.sql.* types.
 */
public class H2 (public val dialect: DbType) : DbType("h2") {
    init {
        require(dialect.javaClass.simpleName != "H2kt") { "H2 database could not be specified with H2 dialect!"}
    }

    override val driverClassName: String
        get() = "org.h2.Driver"

    override fun convertSqlTypeToColumnSchemaValue(tableColumnMetadata: TableColumnMetadata): ColumnSchema? {
        return dialect.convertSqlTypeToColumnSchemaValue(tableColumnMetadata)
    }

    override fun isSystemTable(tableMetadata: TableMetadata): Boolean {
        return dialect.isSystemTable(tableMetadata)
    }

    override fun buildTableMetadata(tables: ResultSet): TableMetadata {
        return dialect.buildTableMetadata(tables)
    }

    override fun convertSqlTypeToKType(tableColumnMetadata: TableColumnMetadata): KType? {
        return dialect.convertSqlTypeToKType(tableColumnMetadata)
    }
}
