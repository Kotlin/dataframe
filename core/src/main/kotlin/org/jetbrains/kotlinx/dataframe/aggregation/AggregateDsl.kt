package org.jetbrains.kotlinx.dataframe.aggregation

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.HasSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl
import org.jetbrains.kotlinx.dataframe.api.pathOf
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.ValueWithDefault
import org.jetbrains.kotlinx.dataframe.impl.aggregation.receivers.internal
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

@HasSchema(schemaArg = 0)
public abstract class AggregateDsl<out T> :
    DataFrame<T>,
    ColumnSelectionDsl<T> {

    @Interpretable("AggregateDslInto")
    public inline infix fun <reified R> R.into(name: String): NamedValue =
        internal().yield(pathOf(name), this, typeOf<R>())

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public inline infix fun <reified R> R.into(column: ColumnAccessor<R>): NamedValue =
        internal().yield(pathOf(column.name()), this, typeOf<R>())

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public inline infix fun <reified R> R.into(column: KProperty<R>): NamedValue =
        internal().yield(pathOf(column.columnName), this, typeOf<R>())

    public infix fun <R> R.default(defaultValue: R): Any =
        when (this) {
            is NamedValue -> this.also { it.default = defaultValue }
            else -> ValueWithDefault(this, defaultValue)
        }
}
