package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.TypeSuggestion
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnGuessingType
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

public fun AnyCol.inferType(): DataColumn<*> =
    createColumnGuessingType(
        name = name,
        values = toList(),
        suggestedType = TypeSuggestion.InferWithUpperbound(type),
    )

// region DataFrame

public fun <T> DataFrame<T>.inferType(): DataFrame<T> =
    inferType {
        colsAtAnyDepth().filter { !it.isColumnGroup() }
    }

public fun <T> DataFrame<T>.inferType(columns: ColumnsSelector<T, *>): DataFrame<T> =
    replace(columns).with { it.inferType() }

public fun <T> DataFrame<T>.inferType(vararg columns: String): DataFrame<T> = inferType { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.inferType(vararg columns: ColumnReference<*>): DataFrame<T> =
    inferType { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.inferType(vararg columns: KProperty<*>): DataFrame<T> = inferType { columns.toColumnSet() }

// endregion
