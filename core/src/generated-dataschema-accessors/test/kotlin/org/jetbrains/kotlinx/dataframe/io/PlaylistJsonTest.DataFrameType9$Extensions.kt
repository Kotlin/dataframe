@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.io
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType9>.kind: DataColumn<String> @JvmName("DataFrameType9_kind") get() = this["kind"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType9>.kind: String @JvmName("DataFrameType9_kind") get() = this["kind"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType9?>.kind: DataColumn<String?> @JvmName("NullableDataFrameType9_kind") get() = this["kind"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType9?>.kind: String? @JvmName("NullableDataFrameType9_kind") get() = this["kind"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType9>.videoId: DataColumn<String> @JvmName("DataFrameType9_videoId") get() = this["videoId"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType9>.videoId: String @JvmName("DataFrameType9_videoId") get() = this["videoId"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType9?>.videoId: DataColumn<String?> @JvmName("NullableDataFrameType9_videoId") get() = this["videoId"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType9?>.videoId: String? @JvmName("NullableDataFrameType9_videoId") get() = this["videoId"] as String?
