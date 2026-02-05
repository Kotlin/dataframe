@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.io
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType7>.height: DataColumn<Int?> @JvmName("DataFrameType7_height") get() = this["height"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType7>.height: Int? @JvmName("DataFrameType7_height") get() = this["height"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType7?>.height: DataColumn<Int?> @JvmName("NullableDataFrameType7_height") get() = this["height"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType7?>.height: Int? @JvmName("NullableDataFrameType7_height") get() = this["height"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType7>.url: DataColumn<String?> @JvmName("DataFrameType7_url") get() = this["url"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType7>.url: String? @JvmName("DataFrameType7_url") get() = this["url"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType7?>.url: DataColumn<String?> @JvmName("NullableDataFrameType7_url") get() = this["url"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType7?>.url: String? @JvmName("NullableDataFrameType7_url") get() = this["url"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType7>.width: DataColumn<Int?> @JvmName("DataFrameType7_width") get() = this["width"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType7>.width: Int? @JvmName("DataFrameType7_width") get() = this["width"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType7?>.width: DataColumn<Int?> @JvmName("NullableDataFrameType7_width") get() = this["width"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType7?>.width: Int? @JvmName("NullableDataFrameType7_width") get() = this["width"] as Int?
