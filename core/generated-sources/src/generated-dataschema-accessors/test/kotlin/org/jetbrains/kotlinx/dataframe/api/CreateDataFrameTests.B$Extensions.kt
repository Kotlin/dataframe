@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B>.a: ColumnGroup<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A> @JvmName("B_a") get() = this["a"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B>.a: DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A> @JvmName("B_a") get() = this["a"] as DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B?>.a: ColumnGroup<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?> @JvmName("NullableB_a") get() = this["a"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B?>.a: DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?> @JvmName("NullableB_a") get() = this["a"] as DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B>.frame: DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A>> @JvmName("B_frame") get() = this["frame"] as DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A>>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B>.frame: DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A> @JvmName("B_frame") get() = this["frame"] as DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B?>.frame: DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?>> @JvmName("NullableB_frame") get() = this["frame"] as DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?>>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B?>.frame: DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?> @JvmName("NullableB_frame") get() = this["frame"] as DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B>.list: DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A>> @JvmName("B_list") get() = this["list"] as DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A>>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B>.list: DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A> @JvmName("B_list") get() = this["list"] as DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B?>.list: DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?>> @JvmName("NullableB_list") get() = this["list"] as DataColumn<DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?>>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B?>.list: DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?> @JvmName("NullableB_list") get() = this["list"] as DataFrame<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B>.row: ColumnGroup<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A> @JvmName("B_row") get() = this["row"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B>.row: DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A> @JvmName("B_row") get() = this["row"] as DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B?>.row: ColumnGroup<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?> @JvmName("NullableB_row") get() = this["row"] as ColumnGroup<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B?>.row: DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?> @JvmName("NullableB_row") get() = this["row"] as DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.A?>
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B>.str: DataColumn<String> @JvmName("B_str") get() = this["str"] as DataColumn<String>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B>.str: String @JvmName("B_str") get() = this["str"] as String
val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B?>.str: DataColumn<String?> @JvmName("NullableB_str") get() = this["str"] as DataColumn<String?>
val DataRow<org.jetbrains.kotlinx.dataframe.api.CreateDataFrameTests.B?>.str: String? @JvmName("NullableB_str") get() = this["str"] as String?
