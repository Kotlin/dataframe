@file:Suppress("UNCHECKED_CAST", "USELESS_CAST")
package org.jetbrains.kotlinx.dataframe.api
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.ColumnsScope
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup

public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.count: DataColumn<Int> @JvmName("ColumnDescription_count") get() = this["count"] as DataColumn<Int>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.count: Int @JvmName("ColumnDescription_count") get() = this["count"] as Int
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.count: DataColumn<Int?> @JvmName("NullableColumnDescription_count") get() = this["count"] as DataColumn<Int?>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.count: Int? @JvmName("NullableColumnDescription_count") get() = this["count"] as Int?
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.freq: DataColumn<Int> @JvmName("ColumnDescription_freq") get() = this["freq"] as DataColumn<Int>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.freq: Int @JvmName("ColumnDescription_freq") get() = this["freq"] as Int
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.freq: DataColumn<Int?> @JvmName("NullableColumnDescription_freq") get() = this["freq"] as DataColumn<Int?>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.freq: Int? @JvmName("NullableColumnDescription_freq") get() = this["freq"] as Int?
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.max: DataColumn<Any> @JvmName("ColumnDescription_max") get() = this["max"] as DataColumn<Any>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.max: Any @JvmName("ColumnDescription_max") get() = this["max"] as Any
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.max: DataColumn<Any?> @JvmName("NullableColumnDescription_max") get() = this["max"] as DataColumn<Any?>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.max: Any? @JvmName("NullableColumnDescription_max") get() = this["max"] as Any?
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.mean: DataColumn<Double> @JvmName("ColumnDescription_mean") get() = this["mean"] as DataColumn<Double>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.mean: Double @JvmName("ColumnDescription_mean") get() = this["mean"] as Double
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.mean: DataColumn<Double?> @JvmName("NullableColumnDescription_mean") get() = this["mean"] as DataColumn<Double?>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.mean: Double? @JvmName("NullableColumnDescription_mean") get() = this["mean"] as Double?
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.median: DataColumn<Any> @JvmName("ColumnDescription_median") get() = this["median"] as DataColumn<Any>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.median: Any @JvmName("ColumnDescription_median") get() = this["median"] as Any
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.median: DataColumn<Any?> @JvmName("NullableColumnDescription_median") get() = this["median"] as DataColumn<Any?>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.median: Any? @JvmName("NullableColumnDescription_median") get() = this["median"] as Any?
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.min: DataColumn<Any> @JvmName("ColumnDescription_min") get() = this["min"] as DataColumn<Any>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.min: Any @JvmName("ColumnDescription_min") get() = this["min"] as Any
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.min: DataColumn<Any?> @JvmName("NullableColumnDescription_min") get() = this["min"] as DataColumn<Any?>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.min: Any? @JvmName("NullableColumnDescription_min") get() = this["min"] as Any?
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.name: DataColumn<String> @JvmName("ColumnDescription_name") get() = this["name"] as DataColumn<String>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.name: String @JvmName("ColumnDescription_name") get() = this["name"] as String
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.name: DataColumn<String?> @JvmName("NullableColumnDescription_name") get() = this["name"] as DataColumn<String?>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.name: String? @JvmName("NullableColumnDescription_name") get() = this["name"] as String?
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.nulls: DataColumn<Int> @JvmName("ColumnDescription_nulls") get() = this["nulls"] as DataColumn<Int>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.nulls: Int @JvmName("ColumnDescription_nulls") get() = this["nulls"] as Int
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.nulls: DataColumn<Int?> @JvmName("NullableColumnDescription_nulls") get() = this["nulls"] as DataColumn<Int?>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.nulls: Int? @JvmName("NullableColumnDescription_nulls") get() = this["nulls"] as Int?
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.p25: DataColumn<Any> @JvmName("ColumnDescription_p25") get() = this["p25"] as DataColumn<Any>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.p25: Any @JvmName("ColumnDescription_p25") get() = this["p25"] as Any
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.p25: DataColumn<Any?> @JvmName("NullableColumnDescription_p25") get() = this["p25"] as DataColumn<Any?>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.p25: Any? @JvmName("NullableColumnDescription_p25") get() = this["p25"] as Any?
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.p75: DataColumn<Any> @JvmName("ColumnDescription_p75") get() = this["p75"] as DataColumn<Any>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.p75: Any @JvmName("ColumnDescription_p75") get() = this["p75"] as Any
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.p75: DataColumn<Any?> @JvmName("NullableColumnDescription_p75") get() = this["p75"] as DataColumn<Any?>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.p75: Any? @JvmName("NullableColumnDescription_p75") get() = this["p75"] as Any?
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.path: DataColumn<org.jetbrains.kotlinx.dataframe.columns.ColumnPath> @JvmName("ColumnDescription_path") get() = this["path"] as DataColumn<org.jetbrains.kotlinx.dataframe.columns.ColumnPath>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.path: org.jetbrains.kotlinx.dataframe.columns.ColumnPath @JvmName("ColumnDescription_path") get() = this["path"] as org.jetbrains.kotlinx.dataframe.columns.ColumnPath
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.path: DataColumn<org.jetbrains.kotlinx.dataframe.columns.ColumnPath?> @JvmName("NullableColumnDescription_path") get() = this["path"] as DataColumn<org.jetbrains.kotlinx.dataframe.columns.ColumnPath?>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.path: org.jetbrains.kotlinx.dataframe.columns.ColumnPath? @JvmName("NullableColumnDescription_path") get() = this["path"] as org.jetbrains.kotlinx.dataframe.columns.ColumnPath?
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.std: DataColumn<Double> @JvmName("ColumnDescription_std") get() = this["std"] as DataColumn<Double>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.std: Double @JvmName("ColumnDescription_std") get() = this["std"] as Double
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.std: DataColumn<Double?> @JvmName("NullableColumnDescription_std") get() = this["std"] as DataColumn<Double?>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.std: Double? @JvmName("NullableColumnDescription_std") get() = this["std"] as Double?
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.top: DataColumn<Any> @JvmName("ColumnDescription_top") get() = this["top"] as DataColumn<Any>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.top: Any @JvmName("ColumnDescription_top") get() = this["top"] as Any
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.top: DataColumn<Any?> @JvmName("NullableColumnDescription_top") get() = this["top"] as DataColumn<Any?>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.top: Any? @JvmName("NullableColumnDescription_top") get() = this["top"] as Any?
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.type: DataColumn<String> @JvmName("ColumnDescription_type") get() = this["type"] as DataColumn<String>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.type: String @JvmName("ColumnDescription_type") get() = this["type"] as String
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.type: DataColumn<String?> @JvmName("NullableColumnDescription_type") get() = this["type"] as DataColumn<String?>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.type: String? @JvmName("NullableColumnDescription_type") get() = this["type"] as String?
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.unique: DataColumn<Int> @JvmName("ColumnDescription_unique") get() = this["unique"] as DataColumn<Int>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription>.unique: Int @JvmName("ColumnDescription_unique") get() = this["unique"] as Int
public val ColumnsScope<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.unique: DataColumn<Int?> @JvmName("NullableColumnDescription_unique") get() = this["unique"] as DataColumn<Int?>
public val DataRow<org.jetbrains.kotlinx.dataframe.api.ColumnDescription?>.unique: Int? @JvmName("NullableColumnDescription_unique") get() = this["unique"] as Int?
