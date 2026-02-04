@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.GroupSchema>.c: DataColumn<Int> @JvmName("GroupSchema_c") get() = this["c"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.GroupSchema>.c: Int @JvmName("GroupSchema_c") get() = this["c"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.GroupSchema?>.c: DataColumn<Int?> @JvmName("NullableGroupSchema_c") get() = this["c"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.GroupSchema?>.c: Int? @JvmName("NullableGroupSchema_c") get() = this["c"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.GroupSchema>.d: DataColumn<String> @JvmName("GroupSchema_d") get() = this["d"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.GroupSchema>.d: String @JvmName("GroupSchema_d") get() = this["d"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.GroupSchema?>.d: DataColumn<String?> @JvmName("NullableGroupSchema_d") get() = this["d"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.GroupSchema?>.d: String? @JvmName("NullableGroupSchema_d") get() = this["d"] as String?
