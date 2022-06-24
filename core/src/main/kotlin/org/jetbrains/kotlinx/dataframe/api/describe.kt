package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.api.describeImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import kotlin.reflect.KProperty
import kotlin.reflect.KType

// region DataSchema
@DataSchema
public interface ColumnDescription {
    public val name: String
    public val path: ColumnPath
    public val type: KType
    public val count: Int
    public val unique: Int
    public val nulls: Int
    public val top: Any
    public val freq: Int
    public val mean: Double
    public val std: Double
    public val min: Any
    public val median: Any
    public val max: Any
}

public val ColumnsContainer<ColumnDescription>.count: DataColumn<Int> @JvmName("ColumnDescription_count") get() = this["count"] as DataColumn<Int>
public val DataRow<ColumnDescription>.count: Int @JvmName("ColumnDescription_count") get() = this["count"] as Int
public val ColumnsContainer<ColumnDescription>.freq: DataColumn<Int> @JvmName("ColumnDescription_freq") get() = this["freq"] as DataColumn<Int>
public val DataRow<ColumnDescription>.freq: Int @JvmName("ColumnDescription_freq") get() = this["freq"] as Int
public val ColumnsContainer<ColumnDescription>.max: DataColumn<Any> @JvmName("ColumnDescription_max") get() = this["max"] as DataColumn<Any>
public val DataRow<ColumnDescription>.max: Any @JvmName("ColumnDescription_max") get() = this["max"] as Any
public val ColumnsContainer<ColumnDescription>.mean: DataColumn<Double> @JvmName("ColumnDescription_mean") get() = this["mean"] as DataColumn<Double>
public val DataRow<ColumnDescription>.mean: Double @JvmName("ColumnDescription_mean") get() = this["mean"] as Double
public val ColumnsContainer<ColumnDescription>.median: DataColumn<Any> @JvmName("ColumnDescription_median") get() = this["median"] as DataColumn<Any>
public val DataRow<ColumnDescription>.median: Any @JvmName("ColumnDescription_median") get() = this["median"] as Any
public val ColumnsContainer<ColumnDescription>.min: DataColumn<Any> @JvmName("ColumnDescription_min") get() = this["min"] as DataColumn<Any>
public val DataRow<ColumnDescription>.min: Any @JvmName("ColumnDescription_min") get() = this["min"] as Any
public val ColumnsContainer<ColumnDescription>.name: DataColumn<String> @JvmName("ColumnDescription_name") get() = this["name"] as DataColumn<String>
public val DataRow<ColumnDescription>.name: String @JvmName("ColumnDescription_name") get() = this["name"] as String
public val ColumnsContainer<ColumnDescription>.nulls: DataColumn<Int> @JvmName("ColumnDescription_nulls") get() = this["nulls"] as DataColumn<Int>
public val DataRow<ColumnDescription>.nulls: Int @JvmName("ColumnDescription_nulls") get() = this["nulls"] as Int
public val ColumnsContainer<ColumnDescription>.path: DataColumn<ColumnPath> @JvmName("ColumnDescription_path") get() = this["path"] as DataColumn<ColumnPath>
public val DataRow<ColumnDescription>.path: ColumnPath @JvmName("ColumnDescription_path") get() = this["path"] as ColumnPath
public val ColumnsContainer<ColumnDescription>.std: DataColumn<Double> @JvmName("ColumnDescription_std") get() = this["std"] as DataColumn<Double>
public val DataRow<ColumnDescription>.std: Double @JvmName("ColumnDescription_std") get() = this["std"] as Double
public val ColumnsContainer<ColumnDescription>.top: DataColumn<Any> @JvmName("ColumnDescription_top") get() = this["top"] as DataColumn<Any>
public val DataRow<ColumnDescription>.top: Any @JvmName("ColumnDescription_top") get() = this["top"] as Any
public val ColumnsContainer<ColumnDescription>.type: DataColumn<KType> @JvmName("ColumnDescription_type") get() = this["type"] as DataColumn<KType>
public val DataRow<ColumnDescription>.type: KType @JvmName("ColumnDescription_type") get() = this["type"] as KType
public val ColumnsContainer<ColumnDescription>.unique: DataColumn<Int> @JvmName("ColumnDescription_unique") get() = this["unique"] as DataColumn<Int>
public val DataRow<ColumnDescription>.unique: Int @JvmName("ColumnDescription_unique") get() = this["unique"] as Int

// endregion

// region DataColumn

public fun <T> DataColumn<T>.describe(): DataFrame<ColumnDescription> = describeImpl(listOf(this))

// endregion

// region DataFrame

public fun <T> DataFrame<T>.describe(): DataFrame<ColumnDescription> = describe { allDfs() }
public fun <T> DataFrame<T>.describe(columns: ColumnsSelector<T, *>): DataFrame<ColumnDescription> = describeImpl(getColumnsWithPaths(columns))
public fun <T> DataFrame<T>.describe(vararg columns: String): DataFrame<ColumnDescription> = describe { columns.toColumns() }
public fun <T, C : Number?> DataFrame<T>.describe(vararg columns: ColumnReference<C>): DataFrame<ColumnDescription> = describe { columns.toColumns() }
public fun <T, C : Number?> DataFrame<T>.describe(vararg columns: KProperty<C>): DataFrame<ColumnDescription> = describe { columns.toColumns() }

// endregion
