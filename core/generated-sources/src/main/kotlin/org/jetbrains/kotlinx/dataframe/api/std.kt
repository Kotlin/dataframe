package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.aggregation.ColumnsForAggregateSelector
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.Aggregators
import org.jetbrains.kotlinx.dataframe.impl.aggregation.aggregators.cast2
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateAll
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateFor
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.aggregateOf
import org.jetbrains.kotlinx.dataframe.impl.aggregation.modes.of
import org.jetbrains.kotlinx.dataframe.impl.aggregation.numberColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnsSetOf
import org.jetbrains.kotlinx.dataframe.math.std
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

// region DataColumn

public fun <T : Number> DataColumn<T?>.std(skipNA: Boolean = skipNA_default, ddof: Int = ddof_default): Double = Aggregators.std(skipNA, ddof).aggregate(this) ?: .0

public inline fun <T, reified R : Number> DataColumn<T>.stdOf(
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default,
    noinline expression: (T) -> R?
): Double = Aggregators.std(skipNA, ddof).cast2<R?, Double>().aggregateOf(this, expression) ?: .0

// endregion

// region DataRow

public fun AnyRow.rowStd(
    skipNA: Boolean = org.jetbrains.kotlinx.dataframe.api.skipNA_default,
    ddof: Int = org.jetbrains.kotlinx.dataframe.api.ddof_default
): Double = values().filterIsInstance<Number>().map { it.toDouble() }.std(skipNA, ddof)
public inline fun <reified T : Number> AnyRow.rowStdOf(ddof: Int = org.jetbrains.kotlinx.dataframe.api.ddof_default): Double = values().filterIsInstance<T>().std(
    typeOf<T>(), ddof = ddof
)

// endregion

// region DataFrame

public fun <T> DataFrame<T>.std(skipNA: Boolean = skipNA_default, ddof: Int = ddof_default): DataRow<T> = stdFor(skipNA, ddof, numberColumns())

public fun <T> DataFrame<T>.stdFor(
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default,
    columns: ColumnsForAggregateSelector<T, Number?>
): DataRow<T> = Aggregators.std(skipNA, ddof).aggregateFor(this, columns)
public fun <T> DataFrame<T>.stdFor(vararg columns: String, skipNA: Boolean = skipNA_default, ddof: Int = ddof_default): DataRow<T> = stdFor(skipNA, ddof) { columns.toColumnsSetOf() }
public fun <T, C : Number> DataFrame<T>.stdFor(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataRow<T> = stdFor(skipNA, ddof) { columns.toColumnSet() }
public fun <T, C : Number> DataFrame<T>.stdFor(
    vararg columns: KProperty<C?>,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataRow<T> = stdFor(skipNA, ddof) { columns.toColumnSet() }

public fun <T> DataFrame<T>.std(
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default,
    columns: ColumnsSelector<T, Number?>
): Double = Aggregators.std(skipNA, ddof).aggregateAll(this, columns) ?: .0
public fun <T> DataFrame<T>.std(vararg columns: ColumnReference<Number?>): Double = std { columns.toColumnSet() }
public fun <T> DataFrame<T>.std(vararg columns: String): Double = std { columns.toColumnsSetOf() }
public fun <T> DataFrame<T>.std(vararg columns: KProperty<Number?>): Double = std { columns.toColumnSet() }

public inline fun <T, reified R : Number> DataFrame<T>.stdOf(
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default,
    crossinline expression: RowExpression<T, R?>
): Double = Aggregators.std(skipNA, ddof).of(this, expression) ?: .0

// endregion

// region GroupBy

public fun <T> Grouped<T>.std(skipNA: Boolean = skipNA_default, ddof: Int = ddof_default): DataFrame<T> = stdFor(skipNA, ddof, numberColumns())

public fun <T> Grouped<T>.stdFor(
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default,
    columns: ColumnsForAggregateSelector<T, Number?>
): DataFrame<T> = Aggregators.std(skipNA, ddof).aggregateFor(this, columns)
public fun <T> Grouped<T>.stdFor(vararg columns: String, skipNA: Boolean = skipNA_default, ddof: Int = ddof_default): DataFrame<T> = stdFor(skipNA, ddof) { columns.toColumnsSetOf() }
public fun <T, C : Number> Grouped<T>.stdFor(
    vararg columns: ColumnReference<C?>,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataFrame<T> = stdFor(skipNA, ddof) { columns.toColumnSet() }
public fun <T, C : Number> Grouped<T>.stdFor(
    vararg columns: KProperty<C?>,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataFrame<T> = stdFor(skipNA, ddof) { columns.toColumnSet() }

public fun <T> Grouped<T>.std(
    name: String? = null,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default,
    columns: ColumnsSelector<T, Number?>
): DataFrame<T> = Aggregators.std(skipNA, ddof).aggregateAll(this, name, columns)
public fun <T> Grouped<T>.std(
    vararg columns: ColumnReference<Number?>,
    name: String? = null,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataFrame<T> = std(name, skipNA, ddof) { columns.toColumnSet() }
public fun <T> Grouped<T>.std(
    vararg columns: String,
    name: String? = null,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataFrame<T> = std(name, skipNA, ddof) { columns.toColumnsSetOf() }
public fun <T> Grouped<T>.std(
    vararg columns: KProperty<Number?>,
    name: String? = null,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataFrame<T> = std(name, skipNA, ddof) { columns.toColumnSet() }

public inline fun <T, reified R : Number> Grouped<T>.stdOf(
    name: String? = null,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default,
    crossinline expression: RowExpression<T, R?>
): DataFrame<T> = Aggregators.std(skipNA, ddof).aggregateOf(this, name, expression)

// endregion

// region Pivot

public fun <T> Pivot<T>.std(separate: Boolean = false, skipNA: Boolean = skipNA_default, ddof: Int = ddof_default): DataRow<T> = stdFor(separate, skipNA, ddof, numberColumns())

public fun <T, R : Number> Pivot<T>.stdFor(
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default,
    columns: ColumnsForAggregateSelector<T, R?>
): DataRow<T> = delegate { stdFor(separate, skipNA, ddof, columns) }
public fun <T> Pivot<T>.stdFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataRow<T> = stdFor(separate, skipNA, ddof) { columns.toColumnsSetOf() }
public fun <T, C : Number> Pivot<T>.stdFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataRow<T> = stdFor(separate, skipNA, ddof) { columns.toColumnSet() }
public fun <T, C : Number> Pivot<T>.stdFor(
    vararg columns: KProperty<C?>,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataRow<T> = stdFor(separate, skipNA, ddof) { columns.toColumnSet() }

public fun <T> Pivot<T>.std(
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default,
    columns: ColumnsSelector<T, Number?>
): DataRow<T> = delegate { std(skipNA, ddof, columns) }
public fun <T> Pivot<T>.std(
    vararg columns: ColumnReference<Number?>,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataRow<T> = std(skipNA, ddof) { columns.toColumnSet() }
public fun <T> Pivot<T>.std(vararg columns: String, skipNA: Boolean = skipNA_default, ddof: Int = ddof_default): DataRow<T> = std(skipNA, ddof) { columns.toColumnsSetOf() }
public fun <T> Pivot<T>.std(
    vararg columns: KProperty<Number?>,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataRow<T> = std(skipNA, ddof) { columns.toColumnSet() }

public inline fun <reified T : Number> Pivot<T>.stdOf(
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default,
    crossinline expression: RowExpression<T, T?>
): DataRow<T> = delegate { stdOf(skipNA, ddof, expression) }

// endregion

// region PivotGroupBy

public fun <T> PivotGroupBy<T>.std(
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataFrame<T> = stdFor(separate, skipNA, ddof, numberColumns())

public fun <T, R : Number> PivotGroupBy<T>.stdFor(
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default,
    columns: ColumnsForAggregateSelector<T, R?>
): DataFrame<T> =
    Aggregators.std(skipNA, ddof).aggregateFor(this, separate, columns)
public fun <T> PivotGroupBy<T>.stdFor(
    vararg columns: String,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataFrame<T> = stdFor(separate, skipNA, ddof) { columns.toColumnsSetOf() }
public fun <T, C : Number> PivotGroupBy<T>.stdFor(
    vararg columns: ColumnReference<C?>,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataFrame<T> = stdFor(separate, skipNA, ddof) { columns.toColumnSet() }
public fun <T, C : Number> PivotGroupBy<T>.stdFor(
    vararg columns: KProperty<C?>,
    separate: Boolean = false,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataFrame<T> = stdFor(separate, skipNA, ddof) { columns.toColumnSet() }

public fun <T> PivotGroupBy<T>.std(
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default,
    columns: ColumnsSelector<T, Number?>
): DataFrame<T> = Aggregators.std(skipNA, ddof).aggregateAll(this, columns)
public fun <T> PivotGroupBy<T>.std(
    vararg columns: ColumnReference<Number?>,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataFrame<T> = std(skipNA, ddof) { columns.toColumnSet() }
public fun <T> PivotGroupBy<T>.std(vararg columns: String, skipNA: Boolean = skipNA_default, ddof: Int = ddof_default): DataFrame<T> = std(skipNA, ddof) { columns.toColumnsSetOf() }
public fun <T> PivotGroupBy<T>.std(
    vararg columns: KProperty<Number?>,
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default
): DataFrame<T> = std(skipNA, ddof) { columns.toColumnSet() }

public inline fun <T, reified R : Number> PivotGroupBy<T>.stdOf(
    skipNA: Boolean = skipNA_default,
    ddof: Int = ddof_default,
    crossinline expression: RowExpression<T, R?>
): DataFrame<T> = Aggregators.std(skipNA, ddof).aggregateOf(this, expression)

// endregion
