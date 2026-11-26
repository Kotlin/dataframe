@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertTests.Schema>.time: DataColumn<kotlinx.datetime.Instant> @JvmName("Schema_time") get() = this["time"] as DataColumn<kotlinx.datetime.Instant>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertTests.Schema>.time: kotlinx.datetime.Instant @JvmName("Schema_time") get() = this["time"] as kotlinx.datetime.Instant
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertTests.Schema?>.time: DataColumn<kotlinx.datetime.Instant?> @JvmName("NullableSchema_time") get() = this["time"] as DataColumn<kotlinx.datetime.Instant?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertTests.Schema?>.time: kotlinx.datetime.Instant? @JvmName("NullableSchema_time") get() = this["time"] as kotlinx.datetime.Instant?
