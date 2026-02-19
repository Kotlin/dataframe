@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.io
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord>.etag: DataColumn<String> @JvmName("DataRecord_etag") get() = this["etag"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord>.etag: String @JvmName("DataRecord_etag") get() = this["etag"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord?>.etag: DataColumn<String?> @JvmName("NullableDataRecord_etag") get() = this["etag"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord?>.etag: String? @JvmName("NullableDataRecord_etag") get() = this["etag"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord>.items: DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1>> @JvmName("DataRecord_items") get() = this["items"] as DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1>>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord>.items: DataFrame<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1> @JvmName("DataRecord_items") get() = this["items"] as DataFrame<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord?>.items: DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1?>> @JvmName("NullableDataRecord_items") get() = this["items"] as DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1?>>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord?>.items: DataFrame<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1?> @JvmName("NullableDataRecord_items") get() = this["items"] as DataFrame<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType1?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord>.kind: DataColumn<String> @JvmName("DataRecord_kind") get() = this["kind"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord>.kind: String @JvmName("DataRecord_kind") get() = this["kind"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord?>.kind: DataColumn<String?> @JvmName("NullableDataRecord_kind") get() = this["kind"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord?>.kind: String? @JvmName("NullableDataRecord_kind") get() = this["kind"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord>.nextPageToken: DataColumn<String> @JvmName("DataRecord_nextPageToken") get() = this["nextPageToken"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord>.nextPageToken: String @JvmName("DataRecord_nextPageToken") get() = this["nextPageToken"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord?>.nextPageToken: DataColumn<String?> @JvmName("NullableDataRecord_nextPageToken") get() = this["nextPageToken"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord?>.nextPageToken: String? @JvmName("NullableDataRecord_nextPageToken") get() = this["nextPageToken"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord>.pageInfo: ColumnGroup<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType10> @JvmName("DataRecord_pageInfo") get() = this["pageInfo"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType10>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord>.pageInfo: DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType10> @JvmName("DataRecord_pageInfo") get() = this["pageInfo"] as DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType10>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord?>.pageInfo: ColumnGroup<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType10?> @JvmName("NullableDataRecord_pageInfo") get() = this["pageInfo"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType10?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataRecord?>.pageInfo: DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType10?> @JvmName("NullableDataRecord_pageInfo") get() = this["pageInfo"] as DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType10?>
