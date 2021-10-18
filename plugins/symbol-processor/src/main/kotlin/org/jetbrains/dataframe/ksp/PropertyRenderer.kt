package org.jetbrains.dataframe.ksp

import org.jetbrains.kotlinx.dataframe.codeGen.BaseField
import org.jetbrains.kotlinx.dataframe.codeGen.ColumnInfo
import org.jetbrains.kotlinx.dataframe.codeGen.ExtensionsCodeGenerator
import org.jetbrains.kotlinx.dataframe.codeGen.IsolatedMarker
import org.jetbrains.kotlinx.dataframe.codeGen.MarkerVisibility
import org.jetbrains.kotlinx.dataframe.codeGen.ValidFieldName

internal fun renderExtensions(interfaceName: String, visibility: MarkerVisibility, properties: List<Property>): String {
    val generator = ExtensionsCodeGenerator.create()
    return generator.generate(object : IsolatedMarker {
        override val name: String = interfaceName
        override val fields: List<BaseField> = properties.map {
            val columnInfo = when (it.propertyType.fqName) {
                DataFrameNames.DATA_ROW -> ColumnInfo.ColumnGroupInfo
                DataFrameNames.DATA_FRAME -> ColumnInfo.FrameColumnInfo
                else -> ColumnInfo.ValueColumnInfo(it.propertyType.toString())
            }

            BaseFieldImpl(
                fieldName = ValidFieldName.of(it.fieldName),
                columnName = it.columnName,
                markerName = it.propertyType.marker,
                nullable = it.propertyType.isNullable,
                columnInfo = columnInfo
            )
        }
        override val visibility: MarkerVisibility = visibility
    }).declarations
}

internal class Property(val columnName: String, val fieldName: String, val propertyType: RenderedType)

internal class RenderedType(val fqName: String, val marker: String?, val isNullable: Boolean) {
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
    override val markerName: String?,
    override val nullable: Boolean,
    override val columnInfo: ColumnInfo
) : BaseField
