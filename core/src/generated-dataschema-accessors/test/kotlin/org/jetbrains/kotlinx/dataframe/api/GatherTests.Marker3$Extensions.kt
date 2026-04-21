@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker3>.c1: DataColumn<String> @JvmName("Marker3_c1") get() = this["c1"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker3>.c1: String @JvmName("Marker3_c1") get() = this["c1"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker3?>.c1: DataColumn<String?> @JvmName("NullableMarker3_c1") get() = this["c1"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker3?>.c1: String? @JvmName("NullableMarker3_c1") get() = this["c1"] as String?
