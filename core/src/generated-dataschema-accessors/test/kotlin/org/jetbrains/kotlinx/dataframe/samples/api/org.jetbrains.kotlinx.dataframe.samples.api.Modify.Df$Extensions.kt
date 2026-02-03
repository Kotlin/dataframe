@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.samples.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Modify.Df>.response: ColumnGroup<org.jetbrains.kotlinx.dataframe.samples.api.Modify.Response> @JvmName("Df_response") get() = this["response"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.samples.api.Modify.Response>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Modify.Df>.response: DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Modify.Response> @JvmName("Df_response") get() = this["response"] as DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Modify.Response>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.samples.api.Modify.Df?>.response: ColumnGroup<org.jetbrains.kotlinx.dataframe.samples.api.Modify.Response?> @JvmName("NullableDf_response") get() = this["response"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.samples.api.Modify.Response?>
val DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Modify.Df?>.response: DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Modify.Response?> @JvmName("NullableDf_response") get() = this["response"] as DataRow<org.jetbrains.kotlinx.dataframe.samples.api.Modify.Response?>
