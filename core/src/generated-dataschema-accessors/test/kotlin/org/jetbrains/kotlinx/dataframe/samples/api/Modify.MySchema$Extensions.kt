@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.samples.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MySchema>.a: DataColumn<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MyType> @JvmName("MySchema_a") get() = this["a"] as DataColumn<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MyType>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MySchema>.a: org.jetbrains.kotlinx.dataframe.samples.api.Modify.MyType @JvmName("MySchema_a") get() = this["a"] as org.jetbrains.kotlinx.dataframe.samples.api.Modify.MyType
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MySchema?>.a: DataColumn<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MyType?> @JvmName("NullableMySchema_a") get() = this["a"] as DataColumn<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MyType?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MySchema?>.a: org.jetbrains.kotlinx.dataframe.samples.api.Modify.MyType? @JvmName("NullableMySchema_a") get() = this["a"] as org.jetbrains.kotlinx.dataframe.samples.api.Modify.MyType?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MySchema>.b: DataColumn<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MyType> @JvmName("MySchema_b") get() = this["b"] as DataColumn<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MyType>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MySchema>.b: org.jetbrains.kotlinx.dataframe.samples.api.Modify.MyType @JvmName("MySchema_b") get() = this["b"] as org.jetbrains.kotlinx.dataframe.samples.api.Modify.MyType
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MySchema?>.b: DataColumn<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MyType?> @JvmName("NullableMySchema_b") get() = this["b"] as DataColumn<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MyType?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MySchema?>.b: org.jetbrains.kotlinx.dataframe.samples.api.Modify.MyType? @JvmName("NullableMySchema_b") get() = this["b"] as org.jetbrains.kotlinx.dataframe.samples.api.Modify.MyType?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MySchema>.c: DataColumn<Int> @JvmName("MySchema_c") get() = this["c"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MySchema>.c: Int @JvmName("MySchema_c") get() = this["c"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MySchema?>.c: DataColumn<Int?> @JvmName("NullableMySchema_c") get() = this["c"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Modify.MySchema?>.c: Int? @JvmName("NullableMySchema_c") get() = this["c"] as Int?
