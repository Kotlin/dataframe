package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import kotlin.reflect.KProperty

// region DataFrame

public fun <T> DataFrame<T>.select(columns: ColumnsSelector<T, *>): DataFrame<T> =
    get(columns).toDataFrame().cast()

public fun <T> DataFrame<T>.select(vararg columns: KProperty<*>): DataFrame<T> = select { columns.toColumnSet() }

public fun <T> DataFrame<T>.select(vararg columns: String): DataFrame<T> = select { columns.toColumnSet() }

public fun <T> DataFrame<T>.select(vararg columns: AnyColumnReference): DataFrame<T> = select { columns.toColumnSet() }

// endregion

// region ColumnsSelectionDsl
// TODO probably provide all overloads, similar to DataFrame.select in this file
// TODO explore parallels with except {}
// TODO cannot be moved out ColumnsSelectionDsl due to String.invoke conflict
public interface SelectColumnsSelectionDsl
// endregion
