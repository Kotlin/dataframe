@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.io
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType5>.height: DataColumn<Int> @JvmName("DataFrameType5_height") get() = this["height"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType5>.height: Int @JvmName("DataFrameType5_height") get() = this["height"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType5?>.height: DataColumn<Int?> @JvmName("NullableDataFrameType5_height") get() = this["height"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType5?>.height: Int? @JvmName("NullableDataFrameType5_height") get() = this["height"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType5>.url: DataColumn<String> @JvmName("DataFrameType5_url") get() = this["url"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType5>.url: String @JvmName("DataFrameType5_url") get() = this["url"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType5?>.url: DataColumn<String?> @JvmName("NullableDataFrameType5_url") get() = this["url"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType5?>.url: String? @JvmName("NullableDataFrameType5_url") get() = this["url"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType5>.width: DataColumn<Int> @JvmName("DataFrameType5_width") get() = this["width"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType5>.width: Int @JvmName("DataFrameType5_width") get() = this["width"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType5?>.width: DataColumn<Int?> @JvmName("NullableDataFrameType5_width") get() = this["width"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType5?>.width: Int? @JvmName("NullableDataFrameType5_width") get() = this["width"] as Int?
