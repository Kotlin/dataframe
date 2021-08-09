package org.jetbrains.dataframe.ksp

interface PropertyRenderer {
    fun render(interfaceName: String, columnName: String, propertyName: String, propertyType: String): String
}

internal class PropertyRendererImpl : PropertyRenderer {
    override fun render(interfaceName: String, columnName: String, propertyName: String, propertyType: String): String {
        val jvmName = "${interfaceName}_${columnName}"
        return """
            val $fqnDataFrameBase<$interfaceName>.`$propertyName`: $fqnDataColumn<${propertyType}> @JvmName("$jvmName") get() = this["$columnName"] as $fqnDataColumn<${propertyType}>
            val $fqnDataRowBase<$interfaceName>.`$propertyName`: $propertyType @JvmName("$jvmName") get() = this["$columnName"] as $propertyType
            """.trimIndent()
    }
}

private const val fqnDataFrameBase = "org.jetbrains.dataframe.DataFrameBase"
private const val fqnDataRowBase = "org.jetbrains.dataframe.DataRowBase"
private const val fqnDataColumn = "org.jetbrains.dataframe.columns.DataColumn"
