package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.api.KeyValueProperty
import org.jetbrains.kotlinx.dataframe.codeGen.FieldType
import org.jetbrains.kotlinx.dataframe.codeGen.Marker
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.ValidFieldName

/**
 * A [DataSchema] interface can implement this if it represents a map-like data schema (so key: value).
 * Used in OpenAPI to represent objects with 'just' additionalProperties of a certain type.
 */
public interface AdditionalProperty<T> : KeyValueProperty<T> {

    /** Key of the property. */
    override val key: String

    /** Value of the property. */
    override val value: T

    public companion object {
        internal fun getMarker(typeArguments: List<String>) = Marker(
            name = AdditionalProperty::class.qualifiedName!!,
            isOpen = false,
            fields = listOf(
                generatedFieldOf(
                    fieldName = ValidFieldName.of(AdditionalProperty<*>::key.name),
                    columnName = AdditionalProperty<*>::key.name,
                    overrides = false,
                    fieldType = FieldType.ValueFieldType(String::class.qualifiedName!!),
                ),
                generatedFieldOf(
                    fieldName = ValidFieldName.of(AdditionalProperty<*>::`value`.name),
                    columnName = AdditionalProperty<*>::`value`.name,
                    overrides = false,
                    fieldType = FieldType.ValueFieldType(Any::class.qualifiedName!! + "?"),
                ),
            ),
            superMarkers = emptyList(),
            visibility = MarkerVisibility.EXPLICIT_PUBLIC,
            typeParameters = emptyList(),
            typeArguments = typeArguments,
        )
    }
}
