package org.jetbrains.dataframe.ksp

import org.jetbrains.dataframe.internal.codeGen.*

internal fun renderExtensions(interfaceName: String, properties: List<Property>): String {
    val generator = ExtensionsCodeGenerator.create()
    return generator.generate(object : IsolatedMarker {
        override val name: String = interfaceName
        override val fields: List<BaseField> = properties.map {
            val columnInfo = when (it.propertyType.fqName) {
                fqnDataRow -> ColumnInfo.ColumnGroupInfo
                fqnDataFrame -> ColumnInfo.FrameColumnInfo
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
        override val visibility: MarkerVisibility = MarkerVisibility.IMPLICIT_PUBLIC
    }).declarations
}

internal class Property(val columnName: String, val fieldName: String, val propertyType: RenderedType)

private const val fqnDataFrame = "org.jetbrains.dataframe.DataFrame"
private const val fqnDataRow = "org.jetbrains.dataframe.DataRow"

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
