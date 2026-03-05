@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1>.c1: DataColumn<String> @JvmName("Marker1_c1") get() = this["c1"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1>.c1: String @JvmName("Marker1_c1") get() = this["c1"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1?>.c1: DataColumn<String?> @JvmName("NullableMarker1_c1") get() = this["c1"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1?>.c1: String? @JvmName("NullableMarker1_c1") get() = this["c1"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1>.c2: DataColumn<String> @JvmName("Marker1_c2") get() = this["c2"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1>.c2: String @JvmName("Marker1_c2") get() = this["c2"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1?>.c2: DataColumn<String?> @JvmName("NullableMarker1_c2") get() = this["c2"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1?>.c2: String? @JvmName("NullableMarker1_c2") get() = this["c2"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1>.c3: DataColumn<String?> @JvmName("Marker1_c3") get() = this["c3"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1>.c3: String? @JvmName("Marker1_c3") get() = this["c3"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1?>.c3: DataColumn<String?> @JvmName("NullableMarker1_c3") get() = this["c3"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1?>.c3: String? @JvmName("NullableMarker1_c3") get() = this["c3"] as String?
