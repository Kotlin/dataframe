@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Location>.gps: ColumnGroup<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Gps?> @JvmName("Location_gps") get() = this["gps"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Gps?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Location>.gps: DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Gps?> @JvmName("Location_gps") get() = this["gps"] as DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Gps?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Location?>.gps: ColumnGroup<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Gps?> @JvmName("NullableLocation_gps") get() = this["gps"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Gps?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Location?>.gps: DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Gps?> @JvmName("NullableLocation_gps") get() = this["gps"] as DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Gps?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Location>.name: DataColumn<String> @JvmName("Location_name") get() = this["name"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Location>.name: String @JvmName("Location_name") get() = this["name"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Location?>.name: DataColumn<String?> @JvmName("NullableLocation_name") get() = this["name"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.ConvertToTests.Location?>.name: String? @JvmName("NullableLocation_name") get() = this["name"] as String?
