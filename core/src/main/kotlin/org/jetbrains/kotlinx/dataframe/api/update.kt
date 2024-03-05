package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnExpression
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataFrameExpression
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowColumnExpression
import org.jetbrains.kotlinx.dataframe.RowValueFilter
import org.jetbrains.kotlinx.dataframe.api.Update.Grammar
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.documentation.SelectingRows
import org.jetbrains.kotlinx.dataframe.impl.api.asFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.api.updateImpl
import org.jetbrains.kotlinx.dataframe.impl.api.updateWithValuePerColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.util.UPDATE_AS_NULLABLE_MESSAGE
import org.jetbrains.kotlinx.dataframe.util.UPDATE_AS_NULLABLE_REPLACE
import org.jetbrains.kotlinx.dataframe.util.UPDATE_WITH_VALUE
import org.jetbrains.kotlinx.dataframe.util.UPDATE_WITH_VALUE_REPLACE
import kotlin.reflect.KProperty

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame] with changed values in some cells
 * (column types can not be changed).
 *
 * ### Check out: [Grammar]
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

    /*
     * This argument providing the (clickable) name of the update-like function.
     * Note: If clickable, make sure to [alias][your type].
     */
    internal interface UpdateOperationArg

    /**
     * ## {@get [UpdateOperationArg]} Operation Grammar
     * {@include [LineBreak]}
     * {@include [DslGrammarLink]}
     * {@include [LineBreak]}
     *
     * {@get [UpdateOperationArg]} **`{ `**[columns][SelectingColumns]**` }`**
     *
     * {@include [Indent]}
     * `[ .`[**where**][Update.where]**` { `**[rowValueCondition][SelectingRows.RowValueCondition.WithExample]**` } `**`]`
     *
     * {@include [Indent]}
     * `[ .`[**at**][Update.at]**`(`**[rowIndices][CommonUpdateAtFunctionDoc.RowIndicesParam]**`)`**` ]`
     *
     * {@include [Indent]}
     * `.`[**with**][Update.with]**` { `**[rowExpression][ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * {@include [Indent]}
     * `| .`[**notNull**][Update.notNull]**` { `**[rowExpression][ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * {@include [Indent]}
     * `| .`[**perCol**][Update.perCol]**` { `**[colExpression][ExpressionsGivenColumn.ColumnExpression.WithExample]**` }`**
     *
     * {@include [Indent]}
     * `| .`[**perRowCol**][Update.perRowCol]**` { `**[rowColExpression][ExpressionsGivenRowAndColumn.RowColumnExpression.WithExample]**` }`**
     *
     * {@include [Indent]}
     * `| .`[**withNull**][Update.withNull]**`()`**
     *
     * {@include [Indent]}
     * `| .`[**withZero**][Update.withZero]**`()`**
     *
     * {@include [Indent]}
     * `| .`[**asFrame**][Update.asFrame]**` { `**[dataFrameExpression][ExpressionsGivenDataFrame.DataFrameExpression.WithExample]**` }`**
     *
     * {@set [UpdateOperationArg] [**update**][update]}{@comment The default name of the `update` operation function name.}
     */
    public interface Grammar

    /**
     * The columns to update need to be selected. See {@get [Columns.SelectingColumnsArg]}
     * for all the selecting options. {@set [Columns.SelectingColumnsArg] [Selecting Columns][UpdateSelectingOptions]}
     */
    public interface Columns {

        /* Optional argument that can be set to redirect where the [Selecting Columns] link points to */
        public interface SelectingColumnsArg
    }

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetSelectingColumnsOperationArg]}
     */
    public interface UpdateSelectingOptions

    /** @param [columns\] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame] to update. */
    internal interface DslParam

    /** @param [columns\] The [Column References][ColumnReference] of this [DataFrame] to update. */
    internal interface ColumnAccessorsParam

    /** @param [columns\] The [KProperties][KProperty] corresponding to columns of this [DataFrame] to update. */
    internal interface KPropertiesParam

    /** @param [columns\] The [Strings][String] corresponding to the names of columns belonging to this [DataFrame] to update. */
    internal interface ColumnNamesParam
}

// region update

/** {@set [SelectingColumns.OperationArg] [update][update]} */
private interface SetSelectingColumnsOperationArg

/**
 * @include [Update] {@comment Description of the update operation.}
 * @include [LineBreak]
 * @include [Update.Columns] {@comment Description of what this function expects the user to do: select columns}
 * ### This Update Overload
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
public fun <T> DataFrame<T>.update(vararg columns: String): Update<T, Any?> = update { columns.toColumnSet() }

/**
 * @include [CommonUpdateFunctionDoc]
 * @include [SelectingColumns.KProperties.WithExample] {@include [SetSelectingColumnsOperationArg]}
 * @include [UpdateWithNote]
 * @include [Update.KPropertiesParam]
 */
public fun <T, C> DataFrame<T>.update(vararg columns: KProperty<C>): Update<T, C> = update { columns.toColumnSet() }

/**
 * @include [CommonUpdateFunctionDoc]
 * @include [SelectingColumns.ColumnAccessors.WithExample] {@include [SetSelectingColumnsOperationArg]}
 * @include [UpdateWithNote]
 * @include [Update.ColumnAccessorsParam]
 */
public fun <T, C> DataFrame<T>.update(vararg columns: ColumnReference<C>): Update<T, C> =
    update { columns.toColumnSet() }

// endregion

/** ## Where
 * @include [SelectingRows.RowValueCondition.WithExample]
 * {@set [SelectingRows.FirstOperationArg] [update][update]}
 * {@set [SelectingRows.SecondOperationArg] [where][where]}
 *
 * @param [predicate] The [row value filter][RowValueFilter] to select the rows to update.
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
 * ### This At Overload
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
 * @param [rowIndices] {@include [CommonUpdateAtFunctionDoc.RowIndicesParam]}
 */
public fun <T, C> Update<T, C>.at(rowIndices: Collection<Int>): Update<T, C> = where { index in rowIndices }

/**
 * @include [CommonUpdateAtFunctionDoc]
 *
 * Provide a `vararg` of [Ints][Int] of row indices to update.
 *
 * @param [rowIndices] {@include [CommonUpdateAtFunctionDoc.RowIndicesParam]}
 */
public fun <T, C> Update<T, C>.at(vararg rowIndices: Int): Update<T, C> = at(rowIndices.toSet())

/**
 * @include [CommonUpdateAtFunctionDoc]
 *
 * Provide an [IntRange] of row indices to update.
 *
 * @param [rowRange] {@include [CommonUpdateAtFunctionDoc.RowIndicesParam]}
 */
public fun <T, C> Update<T, C>.at(rowRange: IntRange): Update<T, C> = where { index in rowRange }

/** ## Per Row Col
 * @include [ExpressionsGivenRowAndColumn.RowColumnExpression.WithExample]
 * {@set [ExpressionsGivenRowAndColumn.OperationArg] [update][update]` { age \}.`[perRowCol][perRowCol]}
 *
 * ## See Also
 *  - {@include [SeeAlsoWith]}
 *  - {@include [SeeAlsoPerCol]}
 * @param [expression] The {@include [ExpressionsGivenRowAndColumn.RowColumnExpressionLink]} to provide a new value for every selected cell giving its row and column.
 */
public fun <T, C> Update<T, C>.perRowCol(expression: RowColumnExpression<T, C, C>): DataFrame<T> =
    updateImpl { row, column, _ -> expression(row, column) }

/** [Update per row col][Update.perRowCol] to provide a new value for every selected cell giving its row and column. */
private interface SeeAlsoPerRowCol

/** ## Update Expression
 * @see ExpressionsGivenRow.RowValueExpression.WithExample
 * @see ExpressionsGivenRow.AddDataRowNote
 */ // doc processor plugin does not work with type aliases yet
public typealias UpdateExpression<T, C, R> = AddDataRow<T>.(C) -> R

/** ## With
 * {@include [ExpressionsGivenRow.RowValueExpression.WithExample]}
 * {@set [ExpressionsGivenRow.OperationArg] [update][update]` { city \}.`[with][with]}
 *
 * ## Note
 * @include [ExpressionsGivenRow.AddDataRowNote]
 * ## See Also
 * - {@include [SeeAlsoPerCol]}
 * - {@include [SeeAlsoPerRowCol]}
 * @param [expression] The {@include [ExpressionsGivenRow.RowValueExpressionLink]} to update the rows with.
 */
public fun <T, C> Update<T, C>.with(expression: UpdateExpression<T, C, C?>): DataFrame<T> =
    updateImpl { row, _, value ->
        expression(row, value)
    }

/** [Update with][Update.with] to provide a new value for every selected cell giving its row. */
private interface SeeAlsoWith

/** ## As Frame
 *
 * Updates selected [column group][ColumnGroup] as a [DataFrame] with the given [expression].
 *
 * {@include [ExpressionsGivenDataFrame.DataFrameExpression.WithExample]}
 * {@set [ExpressionsGivenDataFrame.OperationArg] `df.`[update][update]` { name \}.`[asFrame][asFrame]}
 * @param [expression] The {@include [ExpressionsGivenDataFrame.DataFrameExpressionLink]} to replace the selected column group with.
 */
public fun <T, C, R> Update<T, DataRow<C>>.asFrame(expression: DataFrameExpression<C, DataFrame<R>>): DataFrame<T> =
    asFrameImpl(expression)

@Deprecated(
    message = UPDATE_AS_NULLABLE_MESSAGE,
    replaceWith = ReplaceWith(UPDATE_AS_NULLABLE_REPLACE),
    level = DeprecationLevel.ERROR,
)
public fun <T, C> Update<T, C>.asNullable(): Update<T, C?> = this as Update<T, C?>

/** ## Per Col
 *
 * Per Col can be used for two different types of operations:
 *  - {@include [ExpressionsGivenColumn.ColumnExpression]}
 *  - {@include [UpdatePerColMap]}
 *
 * ### See Also
 *  - {@include [SeeAlsoWith]}
 *  - {@include [SeeAlsoPerRowCol]}
 * ### This Per Col Overload
 */
private interface CommonUpdatePerColDoc

/** Provide a new value for every selected cell per column using a [Map][Map]`<`[colName: String][String]`, value: C>`
 *  or [DataRow][DataRow] as Map. */
private interface UpdatePerColMap

/**
 * @include [CommonUpdatePerColDoc]
 * @include [UpdatePerColMap]
 *
 * For example:
 *
 * `val defaults = {@get [CommonUpdatePerColMapDoc]}`
 *
 * `df.`[update][update]` { name and age }.`[where][Update.where]` { ... }.`[perCol][perCol]`(defaults)`
 *
 * @throws [IllegalArgumentException] if a value for a selected cell's column is not defined in [values\].
 */
private interface CommonUpdatePerColMapDoc

/**
 * @include [CommonUpdatePerColMapDoc]
 * {@set [CommonUpdatePerColMapDoc] `[mapOf][mapOf]`("name" to "Empty", "age" to 0)}
 *
 * @param [values] The [Map]<[String], Value> to provide a new value for every selected cell.
 *   For each selected column, there must be a value in the map with the same name.
 */
public fun <T, C> Update<T, C>.perCol(values: Map<String, C>): DataFrame<T> = updateWithValuePerColumnImpl {
    values[it.name()] ?: throw IllegalArgumentException("Update value for column ${it.name()} is not defined")
}

/**
 * {@include [CommonUpdatePerColMapDoc]}
 * {@set [CommonUpdatePerColMapDoc] df.`[getRows][DataFrame.getRows]`(`[listOf][listOf]`(0))`
 *
 *   `.`[update][update]` { name \}.`[with][Update.with]` { "Empty" \}`
 *
 *   `.`[update][update]` { age \}.`[with][Update.with]` { 0 \}`
 *
 *   `.first()}
 *
 * @param [values] The [DataRow] to provide a new value for every selected cell.
 */
public fun <T, C> Update<T, C>.perCol(values: AnyRow): DataFrame<T> = perCol(values.toMap() as Map<String, C>)

/**
 * @include [CommonUpdatePerColDoc]
 * @include [ExpressionsGivenColumn.ColumnExpression.WithExample]
 * {@set [ExpressionsGivenColumn.OperationArg] [update][update]` { age \}.`[perCol][perCol]}
 *
 * @param [valueSelector] The {@include [ExpressionsGivenColumn.ColumnExpressionLink]} to provide a new value for every selected cell giving its column.
 */
public fun <T, C> Update<T, C>.perCol(valueSelector: ColumnExpression<C, C>): DataFrame<T> =
    updateWithValuePerColumnImpl(valueSelector)

/** [Update per col][Update.perCol] to provide a new value for every selected cell giving its column. */
private interface SeeAlsoPerCol

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
 * {@comment No brackets around `expression` because this doc is copied to [Update.notNull]}
 * @param expression Optional {@include [ExpressionsGivenRow.RowExpressionLink]} to update the rows with.
 */
public fun <T, C> Update<T, C?>.notNull(expression: UpdateExpression<T, C, C>): DataFrame<T> =
    notNull().with(expression)

/**
 * @include [CommonUpdateFunctionDoc]
 * This overload is a combination of [update] and [with][Update.with].
 *
 * @include [SelectingColumns.ColumnAccessors]
 *
 * {@include [ExpressionsGivenRow.RowValueExpression.WithExample]}
 * {@set [ExpressionsGivenRow.OperationArg] [update][update]`("city")` }
 *
 * @include [Update.ColumnAccessorsParam]
 * @param [expression] The {@include [ExpressionsGivenRow.RowValueExpressionLink]} to update the rows with.
 */
public fun <T, C> DataFrame<T>.update(
    firstCol: ColumnReference<C>,
    vararg cols: ColumnReference<C>,
    expression: UpdateExpression<T, C, C>,
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).with(expression)

/**
 * @include [CommonUpdateFunctionDoc]
 * This overload is a combination of [update] and [with][Update.with].
 *
 * @include [SelectingColumns.KProperties]
 *
 * {@include [ExpressionsGivenRow.RowValueExpression.WithExample]}
 * {@set [ExpressionsGivenRow.OperationArg] [update][update]`("city")` }
 *
 * @include [Update.KPropertiesParam]
 * @param [expression] The {@include [ExpressionsGivenRow.RowValueExpressionLink]} to update the rows with.
 */
public fun <T, C> DataFrame<T>.update(
    firstCol: KProperty<C>,
    vararg cols: KProperty<C>,
    expression: UpdateExpression<T, C, C>,
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).with(expression)

/**
 * @include [CommonUpdateFunctionDoc]
 * This overload is a combination of [update] and [with][Update.with].
 *
 * @include [SelectingColumns.ColumnNames]
 *
 * {@include [ExpressionsGivenRow.RowValueExpression.WithExample]}
 * {@set [ExpressionsGivenRow.OperationArg] [update][update]`("city")` }
 *
 * @include [Update.ColumnNamesParam]
 * @param [expression] The {@include [ExpressionsGivenRow.RowValueExpressionLink]} to update the rows with.
 */
public fun <T> DataFrame<T>.update(
    firstCol: String,
    vararg cols: String,
    expression: UpdateExpression<T, Any?, Any?>,
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).with(expression)

/**
 * Specific version of [with] that simply sets the value of each selected row to {@get [CommonSpecificWithDocFirstArg]}.
 *
 * For example:
 *
 * `df.`[update][update]` { id }.`[where][Update.where]` { it < 0 }.`{@get [CommonSpecificWithDocSecondArg]}`
 */
private interface CommonSpecificWithDoc

/** Arg for the resulting value */
private interface CommonSpecificWithDocFirstArg

/** Arg for the function call */
private interface CommonSpecificWithDocSecondArg

/**
 * ## With Null
 * @include [CommonSpecificWithDoc]
 * {@set [CommonSpecificWithDocFirstArg] `null`}
 * {@set [CommonSpecificWithDocSecondArg] [withNull][withNull]`()}
 */
public fun <T, C> Update<T, C>.withNull(): DataFrame<T> = with { null }

/**
 * ## With Zero
 * @include [CommonSpecificWithDoc]
 * {@set [CommonSpecificWithDocFirstArg] `0`}
 * {@set [CommonSpecificWithDocSecondArg] [withZero][withZero]`()}
 */
public fun <T, C> Update<T, C>.withZero(): DataFrame<T> = updateWithValuePerColumnImpl { 0 as C }

/**
 * ## With Value (Deprecated)
 * @include [CommonSpecificWithDoc]
 * {@set [CommonSpecificWithDocFirstArg] [value]}
 * {@set [CommonSpecificWithDocSecondArg] [withValue][withValue]`(-1)}
 *
 * @param [value] The value to set the selected rows to. In contrast to [with][Update.with], this must be the same exact type.
 */
@Deprecated(UPDATE_WITH_VALUE, ReplaceWith(UPDATE_WITH_VALUE_REPLACE), DeprecationLevel.ERROR)
public fun <T, C> Update<T, C>.withValue(value: C): DataFrame<T> = with { value }
