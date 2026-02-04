@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.samples.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Collections.Input>.a: DataColumn<Int> @JvmName("Input_a") get() = this["a"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Collections.Input>.a: Int @JvmName("Input_a") get() = this["a"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Collections.Input?>.a: DataColumn<Int?> @JvmName("NullableInput_a") get() = this["a"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Collections.Input?>.a: Int? @JvmName("NullableInput_a") get() = this["a"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Collections.Input>.b: DataColumn<Int> @JvmName("Input_b") get() = this["b"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Collections.Input>.b: Int @JvmName("Input_b") get() = this["b"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Collections.Input?>.b: DataColumn<Int?> @JvmName("NullableInput_b") get() = this["b"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Collections.Input?>.b: Int? @JvmName("NullableInput_b") get() = this["b"] as Int?
