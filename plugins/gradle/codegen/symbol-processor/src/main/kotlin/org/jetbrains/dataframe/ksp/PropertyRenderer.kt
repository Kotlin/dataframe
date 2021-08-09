package org.jetbrains.dataframe.ksp

interface PropertyRenderer {
    fun render(interfaceName: String, columnName: String, propertyName: String, propertyType: RenderedType): String
}

class RenderedType(val fqName: String, val marker: String?, val isNullable: Boolean) {
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

internal class PropertyRendererImpl : PropertyRenderer {
    override fun render(interfaceName: String, columnName: String, propertyName: String, propertyType: RenderedType): String {
        val jvmName = "${interfaceName}_${columnName}"
        val columnType = when (propertyType.fqName) {
            fqnDataRow -> "$fqnColumnGroup<${propertyType.marker}>"
            fqnDataFrame -> "$fqnDataColumn<$fqnDataFrame<${propertyType.marker}>>"
            else -> "$fqnDataColumn<$propertyType>"
        }
        return """
            val $fqnDataFrameBase<$interfaceName>.`$propertyName`: $columnType @JvmName("$jvmName") get() = this["$columnName"] as $columnType
            val $fqnDataRowBase<$interfaceName>.`$propertyName`: $propertyType @JvmName("$jvmName") get() = this["$columnName"] as $propertyType
            """.trimIndent()
    }
}

private const val fqnDataFrameBase = "org.jetbrains.dataframe.DataFrameBase"
private const val fqnDataRowBase = "org.jetbrains.dataframe.DataRowBase"
private const val fqnDataColumn = "org.jetbrains.dataframe.columns.DataColumn"
private const val fqnColumnGroup = "org.jetbrains.dataframe.columns.ColumnGroup"
private const val fqnDataFrame = "org.jetbrains.dataframe.DataFrame"
private const val fqnDataRow = "org.jetbrains.dataframe.DataRow"
