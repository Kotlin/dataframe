@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker>.first: ColumnGroup<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker3> @JvmName("Marker_first") get() = this["first"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker3>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker>.first: DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker3> @JvmName("Marker_first") get() = this["first"] as DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker3>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker?>.first: ColumnGroup<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker3?> @JvmName("NullableMarker_first") get() = this["first"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker3?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker?>.first: DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker3?> @JvmName("NullableMarker_first") get() = this["first"] as DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker3?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker>.name: DataColumn<String> @JvmName("Marker_name") get() = this["name"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker>.name: String @JvmName("Marker_name") get() = this["name"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker?>.name: DataColumn<String?> @JvmName("NullableMarker_name") get() = this["name"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker?>.name: String? @JvmName("NullableMarker_name") get() = this["name"] as String?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker>.normal: ColumnGroup<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1> @JvmName("Marker_normal") get() = this["normal"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker>.normal: DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1> @JvmName("Marker_normal") get() = this["normal"] as DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker?>.normal: ColumnGroup<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1?> @JvmName("NullableMarker_normal") get() = this["normal"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker?>.normal: DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1?> @JvmName("NullableMarker_normal") get() = this["normal"] as DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker1?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker>.reversed: ColumnGroup<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker2> @JvmName("Marker_reversed") get() = this["reversed"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker2>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker>.reversed: DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker2> @JvmName("Marker_reversed") get() = this["reversed"] as DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker2>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker?>.reversed: ColumnGroup<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker2?> @JvmName("NullableMarker_reversed") get() = this["reversed"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker2?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker?>.reversed: DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker2?> @JvmName("NullableMarker_reversed") get() = this["reversed"] as DataRow<org.jetbrains.kotlinx.dataframe.api.GatherTests.Marker2?>
