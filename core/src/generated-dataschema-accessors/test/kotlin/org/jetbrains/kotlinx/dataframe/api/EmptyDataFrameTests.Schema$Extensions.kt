@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.Schema>.a: DataColumn<Int> @JvmName("Schema_a") get() = this["a"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.Schema>.a: Int @JvmName("Schema_a") get() = this["a"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.Schema?>.a: DataColumn<Int?> @JvmName("NullableSchema_a") get() = this["a"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.Schema?>.a: Int? @JvmName("NullableSchema_a") get() = this["a"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.Schema>.frame: DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.FrameSchema>> @JvmName("Schema_frame") get() = this["frame"] as DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.FrameSchema>>
val DataRow<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.Schema>.frame: DataFrame<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.FrameSchema> @JvmName("Schema_frame") get() = this["frame"] as DataFrame<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.FrameSchema>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.Schema?>.frame: DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.FrameSchema?>> @JvmName("NullableSchema_frame") get() = this["frame"] as DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.FrameSchema?>>
val DataRow<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.Schema?>.frame: DataFrame<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.FrameSchema?> @JvmName("NullableSchema_frame") get() = this["frame"] as DataFrame<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.FrameSchema?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.Schema>.group: ColumnGroup<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.GroupSchema> @JvmName("Schema_group") get() = this["group"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.GroupSchema>
val DataRow<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.Schema>.group: DataRow<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.GroupSchema> @JvmName("Schema_group") get() = this["group"] as DataRow<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.GroupSchema>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.Schema?>.group: ColumnGroup<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.GroupSchema?> @JvmName("NullableSchema_group") get() = this["group"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.GroupSchema?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.Schema?>.group: DataRow<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.GroupSchema?> @JvmName("NullableSchema_group") get() = this["group"] as DataRow<org.jetbrains.kotlinx.dataframe.api.EmptyDataFrameTests.GroupSchema?>
