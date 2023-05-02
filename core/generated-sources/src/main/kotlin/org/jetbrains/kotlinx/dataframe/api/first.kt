package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.nrow

// region DataColumn

public fun <T> DataColumn<T>.first(): T = get(0)
public fun <T> DataColumn<T>.firstOrNull(): T? = if (size > 0) first() else null
public fun <T> DataColumn<T>.first(predicate: (T) -> Boolean): T = values.first(predicate)
public fun <T> DataColumn<T>.firstOrNull(predicate: (T) -> Boolean): T? = values.firstOrNull(predicate)

// endregion

// region DataFrame

public fun <T> DataFrame<T>.first(): DataRow<T> {
    if (nrow == 0) {
        throw NoSuchElementException("DataFrame has no rows. Use `firstOrNull`.")
    }
    return get(0)
}
public fun <T> DataFrame<T>.firstOrNull(): DataRow<T>? = if (nrow > 0) first() else null

public fun <T> DataFrame<T>.first(predicate: RowFilter<T>): DataRow<T> = rows().first { predicate(it, it) }
public fun <T> DataFrame<T>.firstOrNull(predicate: RowFilter<T>): DataRow<T>? = rows().firstOrNull { predicate(it, it) }

// endregion

// region GroupBy

public fun <T, G> GroupBy<T, G>.first(): ReducedGroupBy<T, G> = reduce { firstOrNull() }

public fun <T, G> GroupBy<T, G>.first(predicate: RowFilter<G>): ReducedGroupBy<T, G> = reduce { firstOrNull(predicate) }

// endregion

// region Pivot

public fun <T> Pivot<T>.first(): ReducedPivot<T> = reduce { firstOrNull() }

public fun <T> Pivot<T>.first(predicate: RowFilter<T>): ReducedPivot<T> = reduce { firstOrNull(predicate) }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.first(): ReducedPivotGroupBy<T> = reduce { firstOrNull() }

public fun <T> PivotGroupBy<T>.first(predicate: RowFilter<T>): ReducedPivotGroupBy<T> = reduce { firstOrNull(predicate) }

// endregion
