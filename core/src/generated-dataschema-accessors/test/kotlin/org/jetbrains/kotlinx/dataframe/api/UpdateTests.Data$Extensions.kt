@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.UpdateTests.Data>.a: DataColumn<Int> @JvmName("Data_a") get() = this["a"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.api.UpdateTests.Data>.a: Int @JvmName("Data_a") get() = this["a"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.UpdateTests.Data?>.a: DataColumn<Int?> @JvmName("NullableData_a") get() = this["a"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.UpdateTests.Data?>.a: Int? @JvmName("NullableData_a") get() = this["a"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.UpdateTests.Data>.b: DataColumn<String> @JvmName("Data_b") get() = this["b"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.api.UpdateTests.Data>.b: String @JvmName("Data_b") get() = this["b"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.UpdateTests.Data?>.b: DataColumn<String?> @JvmName("NullableData_b") get() = this["b"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.UpdateTests.Data?>.b: String? @JvmName("NullableData_b") get() = this["b"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.UpdateTests.Data>.c: DataColumn<Boolean> @JvmName("Data_c") get() = this["c"] as DataColumn<Boolean>
val DataRow<org.jetbrains.kotlinx.dataframe.api.UpdateTests.Data>.c: Boolean @JvmName("Data_c") get() = this["c"] as Boolean
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.UpdateTests.Data?>.c: DataColumn<Boolean?> @JvmName("NullableData_c") get() = this["c"] as DataColumn<Boolean?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.UpdateTests.Data?>.c: Boolean? @JvmName("NullableData_c") get() = this["c"] as Boolean?
