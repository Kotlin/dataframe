package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.Update.Usage
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.documentation.*
import org.jetbrains.kotlinx.dataframe.impl.api.updateImpl
import org.jetbrains.kotlinx.dataframe.impl.api.updateWithValuePerColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import kotlin.reflect.KProperty

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame] with changed values in some cells
 * (column types can not be changed).
 *
 * Check out the [`update` Operation Usage][Usage].
 *
 * For more information: {@include [DocumentationUrls.Update]}
 */
public data class Update<T, C>(
    val df: DataFrame<T>,
    val filter: RowValueFilter<T, C>?,
    val columns: ColumnsSelector<T, C>,
) {
    public fun <R : C> cast(): Update<T, R> =
        Update(df, filter as RowValueFilter<T, R>?, columns as ColumnsSelector<T, R>)

    /** This argument providing the (clickable) name of the update-like function.
     * Note: If clickable, make sure to [alias][your type].
     */
    internal interface UpdateOperationArg

    /**
     * ## {@includeArg [UpdateOperationArg]} Operation Usage
     *
     * {@includeArg [UpdateOperationArg]} `{ `[columns][SelectingColumns]` }`
     *
     * - `[.`[where][Update.where]` { `[rowValueCondition][RowCondition.RowValueCondition.WithExample]` } ]`
     *
     * - `[.`[at][Update.at]` (`[rowIndices][CommonUpdateAtFunctionDoc.RowIndicesParam]`) ]`
     *
     * - `.`[with][Update.with]` { `[rowExpression][ExpressingRows.RowValueExpression.WithExample]` }
     *   | .`[notNull][Update.notNull]` { `[rowExpression][ExpressingRows.RowValueExpression.WithExample]` }
     *   | .`[perCol][Update.perCol]` { `[colExpression][ExpressingColumns.ColumnExpression.WithExample]` }
     *   | .`[perRowCol][Update.perRowCol]` { `[rowColExpression][ExpressingColumns.RowColumnExpression.WithExample]` }
     *   | .`[withValue][Update.withValue]`(value)
     *   | .`[withNull][Update.withNull]`()
     *   | .`[withZero][Update.withZero]`()
     *   | .`[asFrame][Update.asFrame]` { `[dataFrameExpression][ExpressingDataFrames.DataFrameExpression.WithExample]` }`
     * {@arg [UpdateOperationArg] [update][update]}{@comment The default name of the `update` operation function name.}
     */
    public interface Usage

    /** The columns to update need to be selected. See {@include [SelectingColumnsLink]} for all the selecting options. */
    public interface Columns

    /** @param columns The [ColumnsSelector] used to select the columns of this [DataFrame] to update. */
    internal interface DslParam

    /** @param columns The [ColumnReference]s of this [DataFrame] to update. */
    internal interface ColumnAccessorsParam

    /** @param columns The [KProperty] values corresponding to columns of this [DataFrame] to update. */
    internal interface KPropertiesParam

    /** @param columns The column names belonging to this [DataFrame] to update. */
    internal interface ColumnNamesParam
}

// region update

/** {@arg [SelectingColumns.OperationArg] [update][update]} */
private interface SetSelectingColumnsOperationArg

/**
 * @include [Update] {@comment Description of the update operation.}
 * @include [LineBreak]
 * @include [Update.Columns] {@comment Description of what this function expects the user to do: select columns}
 * ## This Update Overload
 */
private interface CommonUpdateFunctionDoc

/**
 * ## Optional
 * Combine `df.`[update][update]`(...).`[with][Update.with]` { ... }`
 * into `df.`[update][update]`(...) { ... }`
 */
private interface UpdateWithNote

/**
 * @include [CommonUpdateFunctionDoc]
 * @include [SelectingColumns.Dsl.WithExample] {@include [SetSelectingColumnsOperationArg]}
 * @include [Update.DslParam]
 */
public fun <T, C> DataFrame<T>.update(columns: ColumnsSelector<T, C>): Update<T, C> =
    Update(this, null, columns)

/**
 * @include [CommonUpdateFunctionDoc]
 * @include [SelectingColumns.ColumnNames.WithExample] {@include [SetSelectingColumnsOperationArg]}
 * @include [UpdateWithNote]
 * @include [Update.ColumnNamesParam]
 */
public fun <T> DataFrame<T>.update(vararg columns: String): Update<T, Any?> = update { columns.toColumns() }

/**
 * @include [CommonUpdateFunctionDoc]
 * @include [SelectingColumns.KProperties.WithExample] {@include [SetSelectingColumnsOperationArg]}
 * @include [UpdateWithNote]
 * @include [Update.KPropertiesParam]
 */
public fun <T, C> DataFrame<T>.update(vararg columns: KProperty<C>): Update<T, C> = update { columns.toColumns() }

/**
 * @include [CommonUpdateFunctionDoc]
 * @include [SelectingColumns.ColumnAccessors.WithExample] {@include [SetSelectingColumnsOperationArg]}
 * @include [UpdateWithNote]
 * @include [Update.ColumnAccessorsParam]
 */
public fun <T, C> DataFrame<T>.update(vararg columns: ColumnReference<C>): Update<T, C> =
    update { columns.toColumns() }

/**
 * TODO this will be deprecated
 */
public fun <T, C> DataFrame<T>.update(columns: Iterable<ColumnReference<C>>): Update<T, C> =
    update { columns.toColumnSet() }

// endregion

/** ## Where
 * @include [RowCondition.RowValueCondition.WithExample]
 * {@arg [RowCondition.FirstOperationArg] [update][update]}
 * {@arg [RowCondition.SecondOperationArg] [where][where]}
 *
 * @param predicate The [row value filter][RowValueFilter] to select the rows to update.
 */
public fun <T, C> Update<T, C>.where(predicate: RowValueFilter<T, C>): Update<T, C> =
    copy(filter = filter and predicate)

/** ## At
 * Only update the columns at certain given [row indices][CommonUpdateAtFunctionDoc.RowIndicesParam]:
 *
 * Either a [Collection]<[Int]>, an [IntRange], or just `vararg` indices.
 *
 * For example:
 *
 * `df.`[update][update]` { city }.`[at][at]`(5..10).`[with][with]` { "Paris" }`
 *
 * `df.`[update][update]` { name }.`[at][at]`(1, 2, 3, 4).`[with][with]` { "Empty" }`
 *
 * ## This At Overload
 */
private interface CommonUpdateAtFunctionDoc {

    /** The indices of the rows to update. Either a [Collection]<[Int]>, an [IntRange], or just `vararg` indices. */
    interface RowIndicesParam
}

/**
 * @include [CommonUpdateAtFunctionDoc]
 *
 * Provide a [Collection]<[Int]> of row indices to update.
 *
 * @param rowIndices {@include [CommonUpdateAtFunctionDoc.RowIndicesParam]}
 */
public fun <T, C> Update<T, C>.at(rowIndices: Collection<Int>): Update<T, C> = where { index in rowIndices }

/**
 * @include [CommonUpdateAtFunctionDoc]
 *
 * Provide a `vararg` of [Ints][Int] of row indices to update.
 *
 * @param rowIndices {@include [CommonUpdateAtFunctionDoc.RowIndicesParam]}
 */
public fun <T, C> Update<T, C>.at(vararg rowIndices: Int): Update<T, C> = at(rowIndices.toSet())

/**
 * @include [CommonUpdateAtFunctionDoc]
 *
 * Provide an [IntRange] of row indices to update.
 *
 * @param rowRange {@include [CommonUpdateAtFunctionDoc.RowIndicesParam]}
 */
public fun <T, C> Update<T, C>.at(rowRange: IntRange): Update<T, C> = where { index in rowRange }

/** TODO */
public infix fun <T, C> Update<T, C>.perRowCol(expression: RowColumnExpression<T, C, C>): DataFrame<T> =
    updateImpl { row, column, _ -> expression(row, column) }

/** ## Update Expression
 * @see ExpressingRows.RowValueExpression.WithExample
 * @see ExpressingRows.AddDataRowNote
 */ // doc processor plugin does not work with type aliases yet
public typealias UpdateExpression<T, C, R> = AddDataRow<T>.(C) -> R

/** ## With
 * {@include [ExpressingRows.RowValueExpression.WithExample]}
 * {@arg [ExpressingRows.OperationArg] [update][update]` { city \}.`[with][with]}
 *
 * ## Note
 * @include [ExpressingRows.AddDataRowNote]
 * @param expression The {@include [ExpressingRows.RowValueExpressionLink]} to update the rows with.
 */
public infix fun <T, C> Update<T, C>.with(expression: UpdateExpression<T, C, C?>): DataFrame<T> =
    updateImpl { row, _, value ->
        expression(row, value)
    }

/** ## As Frame
 *
 * Updates selected [column group][ColumnGroup] as a [DataFrame] with the given [expression].
 *
 * {@include [ExpressingDataFrames.DataFrameExpression.WithExample]}
 * {@arg [ExpressingDataFrames.OperationArg] `df.`[update][update]` { name \}.`[asFrame][asFrame]}
 * @param expression The [DataFrameExpression] to replace the selected column group with.
 */
public infix fun <T, C, R> Update<T, DataRow<C>>.asFrame(expression: DataFrameExpression<C, DataFrame<R>>): DataFrame<T> =
    df.replace(columns).with { it.asColumnGroup().let { expression(it, it) }.asColumnGroup(it.name()) }

@Deprecated(
    "Useless unless in combination with `withValue(null)`, but then users can just use `with { null }`...",
    ReplaceWith("this as Update<T, C?>")
)
public fun <T, C> Update<T, C>.asNullable(): Update<T, C?> = this as Update<T, C?>

/** TODO */
public fun <T, C> Update<T, C>.perCol(values: Map<String, C>): DataFrame<T> = updateWithValuePerColumnImpl {
    values[it.name()] ?: throw IllegalArgumentException("Update value for column ${it.name()} is not defined")
}

/** TODO */
public fun <T, C> Update<T, C>.perCol(values: AnyRow): DataFrame<T> = perCol(values.toMap() as Map<String, C>)

/** TODO */
public fun <T, C> Update<T, C>.perCol(valueSelector: ColumnExpression<C, C>): DataFrame<T> =
    updateWithValuePerColumnImpl(valueSelector)

/** Chains up two row value filters together. */
internal infix fun <T, C> RowValueFilter<T, C>?.and(other: RowValueFilter<T, C>): RowValueFilter<T, C> {
    if (this == null) return other
    val thisExp = this
    return { thisExp(this, it) && other(this, it) }
}

/** @include [Update.notNull] */
public fun <T, C> Update<T, C?>.notNull(): Update<T, C> =
    where { it != null } as Update<T, C>

/**
 * ## Not Null
 *
 * Selects only the rows where the values in the selected columns are not null.
 *
 * Shorthand for: [update][update]` { ... }.`[where][Update.where]` { it != null }`
 *
 * For example:
 *
 * `df.`[update][update]` { `[colsOf][colsOf]`<`[Number][Number]`?>() }.`[notNull][notNull]`()`.[perCol][Update.perCol] `{ `[mean][mean]`() }`
 *
 * ### Optional
 * Provide an [expression] to update the rows with.
 * This combines [with][Update.with] with [notNull].
 *
 * For example:
 *
 * `df.`[update][update]` { city }.`[notNull][Update.notNull]` { it.`[toUpperCase][String.toUpperCase]`() }`
 *
 * @param expression Optional {@include [ExpressingRows.RowExpressionLink]} to update the rows with.
 */
public fun <T, C> Update<T, C?>.notNull(expression: UpdateExpression<T, C, C>): DataFrame<T> =
    notNull().with(expression)

/**
 * @include [CommonUpdateFunctionDoc]
 * ### This overload is a combination of [update] and [with][Update.with].
 *
 * @include [SelectingColumns.ColumnAccessors]
 *
 * {@include [ExpressingRows.RowValueExpression.WithExample]}
 * {@arg [ExpressingRows.OperationArg] [update][update]`("city")` }
 *
 * @include [Update.ColumnAccessorsParam]
 * @param expression The {@include [ExpressingRows.RowValueExpressionLink]} to update the rows with.
 */
public fun <T, C> DataFrame<T>.update(
    firstCol: ColumnReference<C>,
    vararg cols: ColumnReference<C>,
    expression: UpdateExpression<T, C, C>
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).with(expression)

/**
 * @include [CommonUpdateFunctionDoc]
 * ### This overload is a combination of [update] and [with][Update.with].
 *
 * @include [SelectingColumns.KProperties]
 *
 * {@include [ExpressingRows.RowValueExpression.WithExample]}
 * {@arg [ExpressingRows.OperationArg] [update][update]`("city")` }
 *
 * @include [Update.KPropertiesParam]
 * @param expression The {@include [ExpressingRows.RowValueExpressionLink]} to update the rows with.
 */
public fun <T, C> DataFrame<T>.update(
    firstCol: KProperty<C>,
    vararg cols: KProperty<C>,
    expression: UpdateExpression<T, C, C>
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).with(expression)

/**
 * @include [CommonUpdateFunctionDoc]
 * ### This overload is a combination of [update] and [with][Update.with].
 *
 * @include [SelectingColumns.ColumnNames]
 *
 * {@include [ExpressingRows.RowValueExpression.WithExample]}
 * {@arg [ExpressingRows.OperationArg] [update][update]`("city")` }
 *
 * @include [Update.ColumnNamesParam]
 * @param expression The {@include [ExpressingRows.RowValueExpressionLink]} to update the rows with.
 */
public fun <T> DataFrame<T>.update(
    firstCol: String,
    vararg cols: String,
    expression: UpdateExpression<T, Any?, Any?>
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).with(expression)

/**
 * Specific version of [with] that simply sets the value of each selected row to {@includeArg [CommonSpecificWithDocFirstArg]}.
 *
 * For example:
 *
 * `df.`[update][update]` { id }.`[where][Update.where]` { it < 0 }.`{@includeArg [CommonSpecificWithDocSecondArg]}`
 */
private interface CommonSpecificWithDoc

/** Arg for the resulting value */
private interface CommonSpecificWithDocFirstArg

/** Arg for the function call */
private interface CommonSpecificWithDocSecondArg

/**
 * ## With Null
 * @include [CommonSpecificWithDoc]
 * {@arg [CommonSpecificWithDocFirstArg] `null`}
 * {@arg [CommonSpecificWithDocSecondArg] [withNull][withNull]`()}
 */
public fun <T, C> Update<T, C>.withNull(): DataFrame<T> = with { null }

/**
 * ## With Zero
 * @include [CommonSpecificWithDoc]
 * {@arg [CommonSpecificWithDocFirstArg] `0`}
 * {@arg [CommonSpecificWithDocSecondArg] [withZero][withZero]`()}
 */
public fun <T, C> Update<T, C>.withZero(): DataFrame<T> = updateWithValuePerColumnImpl { 0 as C }

/**
 * ## With Value
 * @include [CommonSpecificWithDoc]
 * {@arg [CommonSpecificWithDocFirstArg] [value]}
 * {@arg [CommonSpecificWithDocSecondArg] [withValue][withValue]`(-1)}
 *
 * @param value The value to set the selected rows to. In contrast to [with][Update.with], this must be the same exact type.
 */
public infix fun <T, C> Update<T, C>.withValue(value: C): DataFrame<T> = with { value }
