@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.samples.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Modify.Response>.`data`: DataColumn<Any> @JvmName("Response_data") get() = this["data"] as DataColumn<Any>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Modify.Response>.`data`: Any @JvmName("Response_data") get() = this["data"] as Any
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Modify.Response?>.`data`: DataColumn<Any?> @JvmName("NullableResponse_data") get() = this["data"] as DataColumn<Any?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Modify.Response?>.`data`: Any? @JvmName("NullableResponse_data") get() = this["data"] as Any?
