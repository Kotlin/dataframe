@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.io
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1>.etag: DataColumn<String> @JvmName("DataFrameType1_etag") get() = this["etag"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1>.etag: String @JvmName("DataFrameType1_etag") get() = this["etag"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1?>.etag: DataColumn<String?> @JvmName("NullableDataFrameType1_etag") get() = this["etag"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1?>.etag: String? @JvmName("NullableDataFrameType1_etag") get() = this["etag"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1>.id: DataColumn<String> @JvmName("DataFrameType1_id") get() = this["id"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1>.id: String @JvmName("DataFrameType1_id") get() = this["id"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1?>.id: DataColumn<String?> @JvmName("NullableDataFrameType1_id") get() = this["id"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1?>.id: String? @JvmName("NullableDataFrameType1_id") get() = this["id"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1>.kind: DataColumn<String> @JvmName("DataFrameType1_kind") get() = this["kind"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1>.kind: String @JvmName("DataFrameType1_kind") get() = this["kind"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1?>.kind: DataColumn<String?> @JvmName("NullableDataFrameType1_kind") get() = this["kind"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1?>.kind: String? @JvmName("NullableDataFrameType1_kind") get() = this["kind"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1>.snippet: ColumnGroup<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType2> @JvmName("DataFrameType1_snippet") get() = this["snippet"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType2>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1>.snippet: DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType2> @JvmName("DataFrameType1_snippet") get() = this["snippet"] as DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType2>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1?>.snippet: ColumnGroup<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType2?> @JvmName("NullableDataFrameType1_snippet") get() = this["snippet"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType2?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1?>.snippet: DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType2?> @JvmName("NullableDataFrameType1_snippet") get() = this["snippet"] as DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType2?>
