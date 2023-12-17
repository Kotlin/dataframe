package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateValue

// region DataColumn

public fun <T> DataColumn<T>.count(predicate: Predicate<T>? = null): Int = if (predicate == null) size() else values().count(predicate)

// endregion

// region DataRow

public fun AnyRow.count(): Int = columnsCount()

public fun AnyRow.count(predicate: Predicate<Any?>): Int = values().count(predicate)

// endregion

// region DataFrame

public fun <T> DataFrame<T>.count(): Int = rowsCount()

public fun <T> DataFrame<T>.count(predicate: RowFilter<T>): Int = rows().count { predicate(it, it) }

// endregion

// region GroupBy

public fun <T> Grouped<T>.count(resultName: String = "count"): DataFrame<T> =
    aggregateValue(resultName) { count() default 0 }

public fun <T> Grouped<T>.count(resultName: String = "count", predicate: RowFilter<T>): DataFrame<T> =
    aggregateValue(resultName) { count(predicate) default 0 }

// endregion

// region Pivot

public fun <T> Pivot<T>.count(): DataRow<T> = delegate { count() }

public fun <T> Pivot<T>.count(predicate: RowFilter<T>): DataRow<T> = delegate { count(predicate) }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.count(): DataFrame<T> = aggregate { count() default 0 }

public fun <T> PivotGroupBy<T>.count(predicate: RowFilter<T>): DataFrame<T> = aggregate { count(predicate) default 0 }

// endregion
