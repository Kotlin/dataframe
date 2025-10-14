package org.jetbrains.kotlinx.dataframe.io.db

/**
 * Represents a column in a database table to keep all required meta-information.
 *
 * @property [name] the name of the column.
 * @property [sqlTypeName] the SQL data type of the column.
 * @property [jdbcType] the JDBC data type of the column produced from [java.sql.Types].
 * @property [size] the size of the column.
 * @property [javaClassName] the class name in Java.
 * @property [isNullable] true if column could contain nulls.
 */
public data class TableColumnMetadata(
    val name: String,
    val sqlTypeName: String,
    val jdbcType: Int,
    val size: Int,
    val javaClassName: String,
    val isNullable: Boolean = false,
)
