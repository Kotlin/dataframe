package org.jetbrains.dataframe.ksp

import org.jetbrains.kotlinx.dataframe.codeGen.BaseField
import org.jetbrains.kotlinx.dataframe.codeGen.FieldType
import org.jetbrains.kotlinx.dataframe.codeGen.ExtensionsCodeGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.IsolatedMarker
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.ValidFieldName

internal fun renderExtensions(interfaceName: String, visibility: MarkerVisibility, properties: List<Property>): String {
    val generator = ExtensionsCodeGenerator.create()
    return generator.generate(object : IsolatedMarker {
        override val name: String = interfaceName
        override val fields: List<BaseField> = properties.map {
            val fieldType = if (it.propertyType.isDataSchema) {
                if (it.propertyType.fqName == "kotlin.collections.List") {
                    FieldType.FrameFieldType(it.propertyType.marker!!, it.propertyType.isNullable)
                } else {
                    FieldType.GroupFieldType(it.propertyType.toString())
                }
            } else {
                when (it.propertyType.fqName) {
                    DataFrameNames.DATA_ROW -> FieldType.GroupFieldType(it.propertyType.marker!!)
                    DataFrameNames.DATA_FRAME -> FieldType.FrameFieldType(it.propertyType.marker!!, it.propertyType.isNullable)
                    else -> FieldType.ValueFieldType(it.propertyType.toString())
                }
            }


            BaseFieldImpl(
                fieldName = ValidFieldName.of(it.fieldName),
                columnName = it.columnName,
                fieldType = fieldType
            )
        }
        override val visibility: MarkerVisibility = visibility
    }).declarations
}

internal class Property(val columnName: String, val fieldName: String, val propertyType: RenderedType)

internal class RenderedType(val fqName: String, val marker: String?, val isNullable: Boolean, val isDataSchema: Boolean) {
    override fun toString(): String {
        return buildString {
            append(fqName)
            if (marker != null) {
                append("<")
                append(marker)
                append(">")
            }
            if (isNullable) {
                append("?")
            }
        }
    }
}

internal class BaseFieldImpl(
    override val fieldName: ValidFieldName,
    override val columnName: String,
    override val fieldType: FieldType
) : BaseField
