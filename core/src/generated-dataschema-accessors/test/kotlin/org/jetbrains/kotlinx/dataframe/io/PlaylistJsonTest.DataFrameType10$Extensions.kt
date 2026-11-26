@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.io
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType10>.resultsPerPage: DataColumn<Int> @JvmName("DataFrameType10_resultsPerPage") get() = this["resultsPerPage"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType10>.resultsPerPage: Int @JvmName("DataFrameType10_resultsPerPage") get() = this["resultsPerPage"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType10?>.resultsPerPage: DataColumn<Int?> @JvmName("NullableDataFrameType10_resultsPerPage") get() = this["resultsPerPage"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType10?>.resultsPerPage: Int? @JvmName("NullableDataFrameType10_resultsPerPage") get() = this["resultsPerPage"] as Int?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType10>.totalResults: DataColumn<Int> @JvmName("DataFrameType10_totalResults") get() = this["totalResults"] as DataColumn<Int>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType10>.totalResults: Int @JvmName("DataFrameType10_totalResults") get() = this["totalResults"] as Int
val ColumnsScope<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType10?>.totalResults: DataColumn<Int?> @JvmName("NullableDataFrameType10_totalResults") get() = this["totalResults"] as DataColumn<Int?>
val DataRow<org.jetbrains.kotlinx.dataframe.io.PlaylistJsonTest.DataFrameType10?>.totalResults: Int? @JvmName("NullableDataFrameType10_totalResults") get() = this["totalResults"] as Int?
