package org.jetbrains.kotlinx.dataframe.io

import org.jetbrains.kotlinx.dataframe.codeGen.FieldType
import org.jetbrains.kotlinx.dataframe.codeGen.Marker
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.ValidFieldName

internal fun AdditionalProperty.Companion.getMarker(typeArguments: List<String>) =
    Marker(
        name = AdditionalProperty::class.qualifiedName!!,
        isOpen = false,
        fields = listOf(
            generatedFieldOf(
                fieldName = ValidFieldName.of(AdditionalProperty<*>::name.name),
                columnName = AdditionalProperty<*>::name.name,
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
