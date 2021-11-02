package org.jetbrains.kotlinx.dataframe.codeGen

import org.jetbrains.kotlinx.dataframe.impl.schema.DataFrameSchemaImpl
import org.jetbrains.kotlinx.dataframe.schema.DataFrameSchema

public enum class MarkerVisibility {
    INTERNAL, IMPLICIT_PUBLIC, EXPLICIT_PUBLIC
}

public interface IsolatedMarker {
    public val name: String
    public val fields: List<BaseField>
    public val visibility: MarkerVisibility
}

public open class Marker(
    override val name: String,
    public val isOpen: Boolean,
    override val fields: List<GeneratedField>,
    base: List<Marker>,
    override val visibility: MarkerVisibility
) : IsolatedMarker {

    public val shortName: String
        get() = name.substringAfterLast(".")

    public val baseMarkers: Map<String, Marker> = base.associateBy { it.name }

    public val allBaseMarkers: Map<String, Marker> by lazy {
        val result = baseMarkers.toMutableMap()
        baseMarkers.forEach {
            result.putAll(it.value.allBaseMarkers)
        }
        result
    }

    public val allFields: List<GeneratedField> by lazy {

        val fieldsMap = mutableMapOf<String, GeneratedField>()
        baseMarkers.values.forEach {
            it.allFields.forEach {
                fieldsMap[it.fieldName.quotedIfNeeded] = it
            }
        }
        fields.forEach {
            fieldsMap[it.fieldName.quotedIfNeeded] = it
        }
        fieldsMap.values.sortedBy { it.fieldName.quotedIfNeeded }
    }

    public val allFieldsByColumn: Map<String, GeneratedField> by lazy {
        allFields.associateBy { it.columnName }
    }

    public fun getField(columnName: String): GeneratedField? = allFieldsByColumn[columnName]

    public fun containsColumn(columnName: String): Boolean = allFieldsByColumn.containsKey(columnName)

    public val columnNames: List<String> get() = allFields.map { it.columnName }

    public val schema: DataFrameSchema by lazy { DataFrameSchemaImpl(allFields.map { it.columnName to it.columnSchema }.toMap()) }

    public fun implements(schema: Marker): Boolean = if (schema.name == name) true else baseMarkers[schema.name]?.let { it === schema } ?: false

    public fun implementsAll(schemas: Iterable<Marker>): Boolean = schemas.all { implements(it) }
}
