@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.UpdateTests.DataPart>.a: DataColumn<Int> @JvmName("DataPart_a") get() = this["a"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.api.UpdateTests.DataPart>.a: Int @JvmName("DataPart_a") get() = this["a"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.UpdateTests.DataPart?>.a: DataColumn<Int?> @JvmName("NullableDataPart_a") get() = this["a"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.UpdateTests.DataPart?>.a: Int? @JvmName("NullableDataPart_a") get() = this["a"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.UpdateTests.DataPart>.b: DataColumn<String> @JvmName("DataPart_b") get() = this["b"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.api.UpdateTests.DataPart>.b: String @JvmName("DataPart_b") get() = this["b"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.UpdateTests.DataPart?>.b: DataColumn<String?> @JvmName("NullableDataPart_b") get() = this["b"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.UpdateTests.DataPart?>.b: String? @JvmName("NullableDataPart_b") get() = this["b"] as String?
