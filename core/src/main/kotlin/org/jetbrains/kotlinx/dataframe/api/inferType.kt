package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnGuessingType
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.type
import kotlin.reflect.KProperty

public fun AnyCol.inferType(): DataColumn<*> = createColumnGuessingType(name, toList(), type, true)

public fun <T> DataFrame<T>.inferType(): DataFrame<T> = inferType { allDfs() }
public fun <T> DataFrame<T>.inferType(columns: ColumnsSelector<T, *>): DataFrame<T> =
    replace(columns).with { it.inferType() }

public fun <T> DataFrame<T>.inferType(vararg columns: String): DataFrame<T> = inferType { columns.toColumns() }
public fun <T> DataFrame<T>.inferType(vararg columns: ColumnReference<*>): DataFrame<T> =
    inferType { columns.toColumns() }

public fun <T> DataFrame<T>.inferType(vararg columns: KProperty<*>): DataFrame<T> = inferType { columns.toColumns() }
