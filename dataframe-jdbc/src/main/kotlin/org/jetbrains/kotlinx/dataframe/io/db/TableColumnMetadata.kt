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
public class TableColumnMetadata(
    public val name: String,
    public val sqlTypeName: String,
    public val jdbcType: Int,
    public val size: Int,
    public val javaClassName: String,
    public val isNullable: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TableColumnMetadata) return false

        if (name != other.name) return false
        if (sqlTypeName != other.sqlTypeName) return false
        if (jdbcType != other.jdbcType) return false
        if (size != other.size) return false
        if (javaClassName != other.javaClassName) return false
        if (isNullable != other.isNullable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + sqlTypeName.hashCode()
        result = 31 * result + jdbcType
        result = 31 * result + size
        result = 31 * result + javaClassName.hashCode()
        result = 31 * result + isNullable.hashCode()
        return result
    }

    override fun toString(): String {
        return "TableColumnMetadata(name='$name', sqlTypeName='$sqlTypeName', jdbcType=$jdbcType, " +
            "size=$size, javaClassName='$javaClassName', isNullable=$isNullable)"
    }
}
