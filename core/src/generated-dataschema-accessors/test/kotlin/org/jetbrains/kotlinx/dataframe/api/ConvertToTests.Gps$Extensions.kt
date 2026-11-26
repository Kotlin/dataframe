@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Gps>.latitude: DataColumn<Double> @JvmName("Gps_latitude") get() = this["latitude"] as DataColumn<Double>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Gps>.latitude: Double @JvmName("Gps_latitude") get() = this["latitude"] as Double
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Gps?>.latitude: DataColumn<Double?> @JvmName("NullableGps_latitude") get() = this["latitude"] as DataColumn<Double?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Gps?>.latitude: Double? @JvmName("NullableGps_latitude") get() = this["latitude"] as Double?
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Gps>.longitude: DataColumn<Double> @JvmName("Gps_longitude") get() = this["longitude"] as DataColumn<Double>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Gps>.longitude: Double @JvmName("Gps_longitude") get() = this["longitude"] as Double
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Gps?>.longitude: DataColumn<Double?> @JvmName("NullableGps_longitude") get() = this["longitude"] as DataColumn<Double?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Gps?>.longitude: Double? @JvmName("NullableGps_longitude") get() = this["longitude"] as Double?
