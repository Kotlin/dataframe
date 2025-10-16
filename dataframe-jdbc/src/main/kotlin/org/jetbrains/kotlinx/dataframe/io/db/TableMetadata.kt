package org.jetbrains.kotlinx.dataframe.io.db

/**
 * Represents a table metadata to store information about a database table,
 * including its name, schema name, and catalogue name.
 *
 * NOTE: we need to extract both, [schemaName] and [catalogue]
 * because the different databases have different implementations of metadata.
 *
 * @property [name] the name of the table.
 * @property [schemaName] the name of the schema the table belongs to (optional).
 * @property [catalogue] the name of the catalogue the table belongs to (optional).
 */
public class TableMetadata {
    public val name: String
    public val schemaName: String?
    public val catalogue: String?

    public constructor(name: String, schemaName: String?, catalogue: String?) {
        this.name = name
        this.schemaName = schemaName
        this.catalogue = catalogue
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TableMetadata) return false

        if (name != other.name) return false
        if (schemaName != other.schemaName) return false
        if (catalogue != other.catalogue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (schemaName?.hashCode() ?: 0)
        result = 31 * result + (catalogue?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String = "TableMetadata(name='$name', schemaName=$schemaName, catalogue=$catalogue)"
}
