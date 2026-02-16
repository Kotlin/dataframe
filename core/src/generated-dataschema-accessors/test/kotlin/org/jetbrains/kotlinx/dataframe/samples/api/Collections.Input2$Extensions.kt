@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.samples.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Collections.Input2>.a: DataColumn<Int> @JvmName("Input2_a") get() = this["a"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Collections.Input2>.a: Int @JvmName("Input2_a") get() = this["a"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Collections.Input2?>.a: DataColumn<Int?> @JvmName("NullableInput2_a") get() = this["a"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Collections.Input2?>.a: Int? @JvmName("NullableInput2_a") get() = this["a"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Collections.Input2>.b: DataColumn<Int> @JvmName("Input2_b") get() = this["b"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Collections.Input2>.b: Int @JvmName("Input2_b") get() = this["b"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Collections.Input2?>.b: DataColumn<Int?> @JvmName("NullableInput2_b") get() = this["b"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Collections.Input2?>.b: Int? @JvmName("NullableInput2_b") get() = this["b"] as Int?
