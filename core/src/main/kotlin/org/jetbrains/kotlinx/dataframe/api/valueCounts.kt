package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.Column
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.nameGenerator
import kotlin.reflect.KProperty
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

// region DataSchema

@DataSchema
public interface ValueCount {
    public val count: Int
}

public val ColumnsContainer<ValueCount>.count: DataColumn<Int> @JvmName("ValueCount_count") get() = this["count"] as DataColumn<Int>
public val DataRow<ValueCount>.count: Int @JvmName("ValueCount_count") get() = this["count"] as Int

// endregion

// region DataColumn

internal val defaultCountColumnName: String = ValueCount::count.name

public fun <T> DataColumn<T>.valueCounts(
    sort: Boolean = true,
    ascending: Boolean = false,
    dropNA: Boolean = true,
    resultColumn: String = defaultCountColumnName
): DataFrame<ValueCount> {
    var grouped = toList().groupBy { it }.map { it.key to it.value.size }
    if (sort) {
        grouped = if (ascending) grouped.sortedBy { it.second }
        else grouped.sortedByDescending { it.second }
    }
    if (dropNA) grouped = grouped.filter { !it.first.isNA }
    val nulls = if (dropNA) false else hasNulls()
    val values = DataColumn.create(name(), grouped.map { it.first }, type().withNullability(nulls))
    val countName = if (resultColumn == name()) resultColumn + "1" else resultColumn
    val counts = DataColumn.create(countName, grouped.map { it.second }, typeOf<Int>())
    return dataFrameOf(values, counts).cast()
}

// endregion

// region DataFrame

public fun <T> DataFrame<T>.valueCounts(
    sort: Boolean = true,
    ascending: Boolean = false,
    dropNA: Boolean = true,
    resultColumn: String = defaultCountColumnName,
    columns: ColumnsSelector<T, *>? = null
): DataFrame<T> {
    var df = if (columns != null) select(columns) else this
    if (dropNA) df = df.dropNA()

    val rows by columnGroup()
    val countName = nameGenerator().addUnique(resultColumn)
    return df.asColumnGroup(rows).asDataColumn().valueCounts(sort, ascending, dropNA, countName).ungroup(rows).cast()
}

public fun <T> DataFrame<T>.valueCounts(
    vararg columns: String,
    sort: Boolean = true,
    ascending: Boolean = false,
    dropNA: Boolean = true,
    resultColumn: String = defaultCountColumnName
): DataFrame<T> = valueCounts(sort, ascending, dropNA, resultColumn) { columns.toColumns() }
public fun <T> DataFrame<T>.valueCounts(
    vararg columns: Column,
    sort: Boolean = true,
    ascending: Boolean = false,
    dropNA: Boolean = true,
    resultColumn: String = defaultCountColumnName
): DataFrame<T> = valueCounts(sort, ascending, dropNA, resultColumn) { columns.toColumns() }
public fun <T> DataFrame<T>.valueCounts(
    vararg columns: KProperty<*>,
    sort: Boolean = true,
    ascending: Boolean = false,
    dropNA: Boolean = true,
    resultColumn: String = defaultCountColumnName
): DataFrame<T> = valueCounts(sort, ascending, dropNA, resultColumn) { columns.toColumns() }

// endregion
