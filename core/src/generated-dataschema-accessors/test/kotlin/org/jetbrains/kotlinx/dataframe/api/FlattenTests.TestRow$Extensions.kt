@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow>.a: DataColumn<String> @JvmName("TestRow_a") get() = this["a"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow>.a: String @JvmName("TestRow_a") get() = this["a"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow?>.a: DataColumn<String?> @JvmName("NullableTestRow_a") get() = this["a"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow?>.a: String? @JvmName("NullableTestRow_a") get() = this["a"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow>.b: DataColumn<String> @JvmName("TestRow_b") get() = this["b"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow>.b: String @JvmName("TestRow_b") get() = this["b"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow?>.b: DataColumn<String?> @JvmName("NullableTestRow_b") get() = this["b"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow?>.b: String? @JvmName("NullableTestRow_b") get() = this["b"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow>.c: DataColumn<String> @JvmName("TestRow_c") get() = this["c"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow>.c: String @JvmName("TestRow_c") get() = this["c"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow?>.c: DataColumn<String?> @JvmName("NullableTestRow_c") get() = this["c"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.FlattenTests.TestRow?>.c: String? @JvmName("NullableTestRow_c") get() = this["c"] as String?
