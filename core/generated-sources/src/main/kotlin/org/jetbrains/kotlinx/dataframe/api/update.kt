package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.Update.Usage
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.documentation.*
import org.jetbrains.kotlinx.dataframe.impl.api.asFrameImpl
import org.jetbrains.kotlinx.dataframe.impl.api.updateImpl
import org.jetbrains.kotlinx.dataframe.impl.api.updateWithValuePerColumnImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import org.jetbrains.kotlinx.dataframe.util.ITERABLE_COLUMNS_DEPRECATION_MESSAGE
import kotlin.reflect.KProperty

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame] with changed values in some cells
 * (column types can not be changed).
 *
 * Check out the [`update` Operation Usage][Usage].
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
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
     * ## [update][update] Operation Usage
     *
     * [update][update] `{ `[columns][SelectingColumns]` }`
     *
     * - `[.`[where][Update.where]` { `[rowValueCondition][SelectingRows.RowValueCondition.WithExample]` } ]`
     *
     * - `[.`[at][Update.at]` (`[rowIndices][CommonUpdateAtFunctionDoc.RowIndicesParam]`) ]`
     *
     * - `.`[with][Update.with]` { `[rowExpression][ExpressionsGivenRow.RowValueExpression.WithExample]` }
     *   | .`[notNull][Update.notNull]` { `[rowExpression][ExpressionsGivenRow.RowValueExpression.WithExample]` }
     *   | .`[perCol][Update.perCol]` { `[colExpression][ExpressionsGivenColumn.ColumnExpression.WithExample]` }
     *   | .`[perRowCol][Update.perRowCol]` { `[rowColExpression][ExpressionsGivenRowAndColumn.RowColumnExpression.WithExample]` }
     *   | .`[withValue][Update.withValue]`(value)
     *   | .`[withNull][Update.withNull]`()
     *   | .`[withZero][Update.withZero]`()
     *   | .`[asFrame][Update.asFrame]` { `[dataFrameExpression][ExpressionsGivenDataFrame.DataFrameExpression.WithExample]` }`
     *
     */
    public interface Usage

    /** The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. */
    public interface Columns

    /** @param columns The [Columns selector DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl.WithExample] used to select the columns of this [DataFrame] to update. */
    internal interface DslParam

    /** @param columns The [Column references][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnAccessors.WithExample] of this [DataFrame] to update. */
    internal interface ColumnAccessorsParam

    /** @param columns The [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample] corresponding to columns of this [DataFrame] to update. */
    internal interface KPropertiesParam

    /** @param columns The [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample] belonging to this [DataFrame] to update. */
    internal interface ColumnNamesParam
}

// region update

private interface SetSelectingColumnsOperationArg

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types can not be changed).
 *
 * Check out the [`update` Operation Usage][org.jetbrains.kotlinx.dataframe.api.Update.Usage].
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
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
 * ## The Update Operation
 *
 * Returns the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types can not be changed).
 *
 * Check out the [`update` Operation Usage][org.jetbrains.kotlinx.dataframe.api.Update.Usage].
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Update Overload
 * Select or express columns using the Column(s) Selection DSL.
 * (Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL comes in the form of either a [Column Selector][org.jetbrains.kotlinx.dataframe.ColumnSelector]- or [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operate in the [Column Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl] or the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expect you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], respectively.
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { length `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` age }`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Double][Double]`>() }`
 *  
 * @param columns The [Columns selector DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl.WithExample] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.update(columns: ColumnsSelector<T, C>): Update<T, C> =
    Update(this, null, columns)

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types can not be changed).
 *
 * Check out the [`update` Operation Usage][org.jetbrains.kotlinx.dataframe.api.Update.Usage].
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Update Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`("length", "age")`
 *  
 * ## Optional
 * Combine `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`(...).`[with][org.jetbrains.kotlinx.dataframe.api.Update.with]` { ... }`
 * into `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`(...) { ... }`
 * @param columns The [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample] belonging to this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T> DataFrame<T>.update(vararg columns: String): Update<T, Any?> = update { columns.toColumnSet() }

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types can not be changed).
 *
 * Check out the [`update` Operation Usage][org.jetbrains.kotlinx.dataframe.api.Update.Usage].
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Update Overload
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * For example:
 * ```kotlin
 * data class Person(val length: Double, val age: Double)
 * ```
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`(Person::length, Person::age)`
 *  
 * ## Optional
 * Combine `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`(...).`[with][org.jetbrains.kotlinx.dataframe.api.Update.with]` { ... }`
 * into `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`(...) { ... }`
 * @param columns The [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample] corresponding to columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.update(vararg columns: KProperty<C>): Update<T, C> = update { columns.toColumnSet() }

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types can not be changed).
 *
 * Check out the [`update` Operation Usage][org.jetbrains.kotlinx.dataframe.api.Update.Usage].
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Update Overload
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * For example:
 *
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`(length, age)`
 *  
 * ## Optional
 * Combine `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`(...).`[with][org.jetbrains.kotlinx.dataframe.api.Update.with]` { ... }`
 * into `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`(...) { ... }`
 * @param columns The [Column references][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnAccessors.WithExample] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.update(vararg columns: ColumnReference<C>): Update<T, C> =
    update { columns.toColumnSet() }

@Deprecated(
    message = ITERABLE_COLUMNS_DEPRECATION_MESSAGE,
    replaceWith = ReplaceWith(
        "update { columns.toColumnSet() }",
        "org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet"
    ),
    level = DeprecationLevel.ERROR
)
public fun <T, C> DataFrame<T>.update(columns: Iterable<ColumnReference<C>>): Update<T, C> =
    update { columns.toColumnSet() }

// endregion

/** ## Where
 * Filter or find rows to operate on after [selecting columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] using a
 * [row value filter][org.jetbrains.kotlinx.dataframe.RowValueFilter].
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { length }.`[where][org.jetbrains.kotlinx.dataframe.api.where]` { it > 10.0 }`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }.`[where][org.jetbrains.kotlinx.dataframe.api.where]` { `[index][org.jetbrains.kotlinx.dataframe.index]`() > 4 && city != "Paris" }`
 *
 *
 *
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
 * ## At
 * Only update the columns at certain given [row indices][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]:
 *
 * Either a [Collection][Collection]<[Int][Int]>, an [IntRange][IntRange], or just `vararg` indices.
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { city }.`[at][org.jetbrains.kotlinx.dataframe.api.at]`(5..10).`[with][org.jetbrains.kotlinx.dataframe.api.with]` { "Paris" }`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { name }.`[at][org.jetbrains.kotlinx.dataframe.api.at]`(1, 2, 3, 4).`[with][org.jetbrains.kotlinx.dataframe.api.with]` { "Empty" }`
 *
 * ## This At Overload
 *
 * Provide a [Collection][Collection]<[Int][Int]> of row indices to update.
 *
 * @param rowIndices The indices of the rows to update. Either a [Collection][Collection]<[Int][Int]>, an [IntRange][IntRange], or just `vararg` indices.
 */
public fun <T, C> Update<T, C>.at(rowIndices: Collection<Int>): Update<T, C> = where { index in rowIndices }

/**
 * ## At
 * Only update the columns at certain given [row indices][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]:
 *
 * Either a [Collection][Collection]<[Int][Int]>, an [IntRange][IntRange], or just `vararg` indices.
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { city }.`[at][org.jetbrains.kotlinx.dataframe.api.at]`(5..10).`[with][org.jetbrains.kotlinx.dataframe.api.with]` { "Paris" }`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { name }.`[at][org.jetbrains.kotlinx.dataframe.api.at]`(1, 2, 3, 4).`[with][org.jetbrains.kotlinx.dataframe.api.with]` { "Empty" }`
 *
 * ## This At Overload
 *
 * Provide a `vararg` of [Ints][Int] of row indices to update.
 *
 * @param rowIndices The indices of the rows to update. Either a [Collection][Collection]<[Int][Int]>, an [IntRange][IntRange], or just `vararg` indices.
 */
public fun <T, C> Update<T, C>.at(vararg rowIndices: Int): Update<T, C> = at(rowIndices.toSet())

/**
 * ## At
 * Only update the columns at certain given [row indices][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]:
 *
 * Either a [Collection][Collection]<[Int][Int]>, an [IntRange][IntRange], or just `vararg` indices.
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { city }.`[at][org.jetbrains.kotlinx.dataframe.api.at]`(5..10).`[with][org.jetbrains.kotlinx.dataframe.api.with]` { "Paris" }`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { name }.`[at][org.jetbrains.kotlinx.dataframe.api.at]`(1, 2, 3, 4).`[with][org.jetbrains.kotlinx.dataframe.api.with]` { "Empty" }`
 *
 * ## This At Overload
 *
 * Provide an [IntRange][IntRange] of row indices to update.
 *
 * @param rowRange The indices of the rows to update. Either a [Collection][Collection]<[Int][Int]>, an [IntRange][IntRange], or just `vararg` indices.
 */
public fun <T, C> Update<T, C>.at(rowRange: IntRange): Update<T, C> = where { index in rowRange }

/** ## Per Row Col
 * Provide a new value for every selected cell given both its row and column using a [row-column expression][org.jetbrains.kotlinx.dataframe.RowColumnExpression].
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { age ` { row, col ->`
 *
 * `row.age / col.`[mean][org.jetbrains.kotlinx.dataframe.DataColumn.mean]`(skipNA = true)`
 *
 * `}`
 *
 *
 * .`[perRowCol][org.jetbrains.kotlinx.dataframe.api.perRowCol]}
 *
 * ## See Also
 *  - [Update with][org.jetbrains.kotlinx.dataframe.api.Update.with] to provide a new value for every selected cell giving its row.
 *  - [Update per col][org.jetbrains.kotlinx.dataframe.api.Update.perCol] to provide a new value for every selected cell giving its column.
 * @param expression The [Row Column Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn.RowColumnExpression] to provide a new value for every selected cell giving its row and column.
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
 * Provide a new value for every selected cell given its row and its previous value using a
 * [row value expression][org.jetbrains.kotlinx.dataframe.RowValueExpression].
 *
 * For example:
 *
 * `df.`[update][update]` { city }.`[with][with]` { name.firstName + " from " + it }`
 *
 * `df.`[update][update]` { city }.`[with][with]` { it.uppercase() }`
 *
 *
 *
 * ## Note
 * [update with][org.jetbrains.kotlinx.dataframe.api.Update.with]- and [add][org.jetbrains.kotlinx.dataframe.api.add]-like expressions use [AddDataRow][org.jetbrains.kotlinx.dataframe.api.AddDataRow] instead of [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as the DSL's receiver type.
 * This is an extension to [RowValueExpression][org.jetbrains.kotlinx.dataframe.RowValueExpression] and
 * [RowExpression][org.jetbrains.kotlinx.dataframe.RowExpression] that provides access to
 * the modified/generated value of the preceding row ([AddDataRow.newValue][org.jetbrains.kotlinx.dataframe.api.AddDataRow.newValue]).
 * ## See Also
 * - [Update per col][org.jetbrains.kotlinx.dataframe.api.Update.perCol] to provide a new value for every selected cell giving its column.
 * - [Update per row col][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol] to provide a new value for every selected cell giving its row and column.
 * @param expression The [Row Value Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample] to update the rows with.
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
 * Provide a new value for every selected data frame using a [dataframe expression][org.jetbrains.kotlinx.dataframe.DataFrameExpression].
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { name ` { `[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { lastName } }`
 * .`[asFrame][org.jetbrains.kotlinx.dataframe.api.asFrame]}
 * @param expression The [Data Frame Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpression] to replace the selected column group with.
 */
public fun <T, C, R> Update<T, DataRow<C>>.asFrame(expression: DataFrameExpression<C, DataFrame<R>>): DataFrame<T> =
    asFrameImpl(expression)

@Deprecated(
    "Useless unless in combination with `withValue(null)`, but then users can just use `with { null }`...",
    ReplaceWith("this as Update<T, C?>")
)
public fun <T, C> Update<T, C>.asNullable(): Update<T, C?> = this as Update<T, C?>

/** ## Per Col
 *
 * Per Col can be used for two different types of operations:
 *  - Provide a new value for every selected cell given its column using a [column expression][org.jetbrains.kotlinx.dataframe.ColumnExpression].
 *  - Provide a new value for every selected cell per column using a [Map][Map]`<`[colName: String][String]`, value: C>`
 *  or [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * ## See Also
 *  - [Update with][org.jetbrains.kotlinx.dataframe.api.Update.with] to provide a new value for every selected cell giving its row.
 *  - [Update per row col][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol] to provide a new value for every selected cell giving its row and column.
 * ## This Per Col Overload
 */
private interface CommonUpdatePerColDoc

/** Provide a new value for every selected cell per column using a [Map][Map]`<`[colName: String][String]`, value: C>`
 *  or [DataRow][DataRow] as Map. */
private interface UpdatePerColMap

/**
 * ## Per Col
 *
 * Per Col can be used for two different types of operations:
 *  - Provide a new value for every selected cell given its column using a [column expression][org.jetbrains.kotlinx.dataframe.ColumnExpression].
 *  - Provide a new value for every selected cell per column using a [Map][Map]`<`[colName: String][String]`, value: C>`
 *  or [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * ## See Also
 *  - [Update with][org.jetbrains.kotlinx.dataframe.api.Update.with] to provide a new value for every selected cell giving its row.
 *  - [Update per row col][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol] to provide a new value for every selected cell giving its row and column.
 * ## This Per Col Overload
 * Provide a new value for every selected cell per column using a [Map][Map]`<`[colName: String][String]`, value: C>`
 *  or [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * For example:
 *
 * `val defaults = {@includeArg [CommonUpdatePerColMapDoc][org.jetbrains.kotlinx.dataframe.api.CommonUpdatePerColMapDoc]}`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { name and age }.`[where][org.jetbrains.kotlinx.dataframe.api.Update.where]` { ... }.`[perCol][org.jetbrains.kotlinx.dataframe.api.perCol]`(defaults)`
 *
 * @throws IllegalArgumentException if a value for a selected cell's column is not defined in [values\].
 */
private interface CommonUpdatePerColMapDoc

/**
 * ## Per Col
 *
 * Per Col can be used for two different types of operations:
 *  - Provide a new value for every selected cell given its column using a [column expression][org.jetbrains.kotlinx.dataframe.ColumnExpression].
 *  - Provide a new value for every selected cell per column using a [Map][Map]`<`[colName: String][String]`, value: C>`
 *  or [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * ## See Also
 *  - [Update with][org.jetbrains.kotlinx.dataframe.api.Update.with] to provide a new value for every selected cell giving its row.
 *  - [Update per row col][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol] to provide a new value for every selected cell giving its row and column.
 * ## This Per Col Overload
 * Provide a new value for every selected cell per column using a [Map][Map]`<`[colName: String][String]`, value: C>`
 *  or [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * For example:
 *
 * `val defaults = `[mapOf][mapOf]`("name" to "Empty", "age" to 0)`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { name and age }.`[where][org.jetbrains.kotlinx.dataframe.api.Update.where]` { ... }.`[perCol][org.jetbrains.kotlinx.dataframe.api.perCol]`(defaults)`
 *
 * @throws IllegalArgumentException if a value for a selected cell's column is not defined in [values][values].
 *
 *
 * @param values The [Map]<[String], Value> to provide a new value for every selected cell.
 *   For each selected column, there must be a value in the map with the same name.
 */
public fun <T, C> Update<T, C>.perCol(values: Map<String, C>): DataFrame<T> = updateWithValuePerColumnImpl {
    values[it.name()] ?: throw IllegalArgumentException("Update value for column ${it.name()} is not defined")
}

/**
 * ## Per Col
 *
 * Per Col can be used for two different types of operations:
 *  - Provide a new value for every selected cell given its column using a [column expression][org.jetbrains.kotlinx.dataframe.ColumnExpression].
 *  - Provide a new value for every selected cell per column using a [Map][Map]`<`[colName: String][String]`, value: C>`
 *  or [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * ## See Also
 *  - [Update with][org.jetbrains.kotlinx.dataframe.api.Update.with] to provide a new value for every selected cell giving its row.
 *  - [Update per row col][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol] to provide a new value for every selected cell giving its row and column.
 * ## This Per Col Overload
 * Provide a new value for every selected cell per column using a [Map][Map]`<`[colName: String][String]`, value: C>`
 *  or [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * For example:
 *
 * `val defaults = df.`[getRows][org.jetbrains.kotlinx.dataframe.DataFrame.getRows]`(`[listOf][listOf]`(0))`
 *
 *   `.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { name `
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { name and age }.`[where][org.jetbrains.kotlinx.dataframe.api.Update.where]` { ... }.`[perCol][org.jetbrains.kotlinx.dataframe.api.perCol]`(defaults)`
 *
 * @throws IllegalArgumentException if a value for a selected cell's column is not defined in [values][values].
 * .`[with][org.jetbrains.kotlinx.dataframe.api.Update.with]` { "Empty" }`
 *
 *   `.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { age }.`[with][org.jetbrains.kotlinx.dataframe.api.Update.with]` { 0 }`
 *
 *   `.first()}
 *
 * @param values The [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] to provide a new value for every selected cell.
 */
public fun <T, C> Update<T, C>.perCol(values: AnyRow): DataFrame<T> = perCol(values.toMap() as Map<String, C>)

/**
 * ## Per Col
 *
 * Per Col can be used for two different types of operations:
 *  - Provide a new value for every selected cell given its column using a [column expression][org.jetbrains.kotlinx.dataframe.ColumnExpression].
 *  - Provide a new value for every selected cell per column using a [Map][Map]`<`[colName: String][String]`, value: C>`
 *  or [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * ## See Also
 *  - [Update with][org.jetbrains.kotlinx.dataframe.api.Update.with] to provide a new value for every selected cell giving its row.
 *  - [Update per row col][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol] to provide a new value for every selected cell giving its row and column.
 * ## This Per Col Overload
 * Provide a new value for every selected cell given its column using a [column expression][org.jetbrains.kotlinx.dataframe.ColumnExpression].
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { age ` { `[mean][org.jetbrains.kotlinx.dataframe.DataColumn.mean]`(skipNA = true) }`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { age ` { `[count][org.jetbrains.kotlinx.dataframe.DataColumn.count]` { it > 10 } }`
 *
 * .`[perCol][org.jetbrains.kotlinx.dataframe.api.perCol]}
 *
 * @param valueSelector The [Column Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpression] to provide a new value for every selected cell giving its column.
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

/** ## Not Null
 *
 * Selects only the rows where the values in the selected columns are not null.
 *
 * Shorthand for: [update][org.jetbrains.kotlinx.dataframe.api.update]` { ... }.`[where][org.jetbrains.kotlinx.dataframe.api.Update.where]` { it != null }`
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Number][Number]`?>() }.`[notNull][org.jetbrains.kotlinx.dataframe.api.notNull]`()`.[perCol][org.jetbrains.kotlinx.dataframe.api.Update.perCol] `{ `[mean][org.jetbrains.kotlinx.dataframe.api.mean]`() }`
 *
 * ### Optional
 * Provide an [expression][expression] to update the rows with.
 * This combines [with][org.jetbrains.kotlinx.dataframe.api.Update.with] with [notNull][org.jetbrains.kotlinx.dataframe.api.notNull].
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { city }.`[notNull][org.jetbrains.kotlinx.dataframe.api.Update.notNull]` { it.`[toUpperCase][String.toUpperCase]`() }`
 *
 * @param expression Optional [Row Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowExpression.WithExample] to update the rows with. */
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
 * @param expression Optional [Row Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowExpression.WithExample] to update the rows with.
 */
public fun <T, C> Update<T, C?>.notNull(expression: UpdateExpression<T, C, C>): DataFrame<T> =
    notNull().with(expression)

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types can not be changed).
 *
 * Check out the [`update` Operation Usage][org.jetbrains.kotlinx.dataframe.api.Update.Usage].
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Update Overload
 * ### This overload is a combination of [update][org.jetbrains.kotlinx.dataframe.api.update] and [with][org.jetbrains.kotlinx.dataframe.api.Update.with].
 *
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * Provide a new value for every selected cell given its row and its previous value using a
 * [row value expression][org.jetbrains.kotlinx.dataframe.RowValueExpression].
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`("city")` ` { name.firstName + " from " + it }`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`("city")` ` { it.uppercase() }`
 *
 *
 *
 * @param columns The [Column references][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnAccessors.WithExample] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 * @param expression The [Row Value Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample] to update the rows with.
 */
public fun <T, C> DataFrame<T>.update(
    firstCol: ColumnReference<C>,
    vararg cols: ColumnReference<C>,
    expression: UpdateExpression<T, C, C>
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).with(expression)

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types can not be changed).
 *
 * Check out the [`update` Operation Usage][org.jetbrains.kotlinx.dataframe.api.Update.Usage].
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Update Overload
 * ### This overload is a combination of [update][org.jetbrains.kotlinx.dataframe.api.update] and [with][org.jetbrains.kotlinx.dataframe.api.Update.with].
 *
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * Provide a new value for every selected cell given its row and its previous value using a
 * [row value expression][org.jetbrains.kotlinx.dataframe.RowValueExpression].
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`("city")` ` { name.firstName + " from " + it }`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`("city")` ` { it.uppercase() }`
 *
 *
 *
 * @param columns The [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample] corresponding to columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 * @param expression The [Row Value Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample] to update the rows with.
 */
public fun <T, C> DataFrame<T>.update(
    firstCol: KProperty<C>,
    vararg cols: KProperty<C>,
    expression: UpdateExpression<T, C, C>
): DataFrame<T> =
    update(*headPlusArray(firstCol, cols)).with(expression)

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types can not be changed).
 *
 * Check out the [`update` Operation Usage][org.jetbrains.kotlinx.dataframe.api.Update.Usage].
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Update Overload
 * ### This overload is a combination of [update][org.jetbrains.kotlinx.dataframe.api.update] and [with][org.jetbrains.kotlinx.dataframe.api.Update.with].
 *
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * Provide a new value for every selected cell given its row and its previous value using a
 * [row value expression][org.jetbrains.kotlinx.dataframe.RowValueExpression].
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`("city")` ` { name.firstName + " from " + it }`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`("city")` ` { it.uppercase() }`
 *
 *
 *
 * @param columns The [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample] belonging to this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 * @param expression The [Row Value Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample] to update the rows with.
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
 * Specific version of [with][org.jetbrains.kotlinx.dataframe.api.with] that simply sets the value of each selected row to `null`.
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { id }.`[where][org.jetbrains.kotlinx.dataframe.api.Update.where]` { it < 0 }.`[withNull][org.jetbrains.kotlinx.dataframe.api.withNull]`()`
 *
 *
 */
public fun <T, C> Update<T, C>.withNull(): DataFrame<T> = with { null }

/**
 * ## With Zero
 * Specific version of [with][org.jetbrains.kotlinx.dataframe.api.with] that simply sets the value of each selected row to `0`.
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { id }.`[where][org.jetbrains.kotlinx.dataframe.api.Update.where]` { it < 0 }.`[withZero][org.jetbrains.kotlinx.dataframe.api.withZero]`()`
 *
 *
 */
public fun <T, C> Update<T, C>.withZero(): DataFrame<T> = updateWithValuePerColumnImpl { 0 as C }

/**
 * ## With Value
 * Specific version of [with][org.jetbrains.kotlinx.dataframe.api.with] that simply sets the value of each selected row to [value][org.jetbrains.kotlinx.dataframe.api.value].
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { id }.`[where][org.jetbrains.kotlinx.dataframe.api.Update.where]` { it < 0 }.`[withValue][org.jetbrains.kotlinx.dataframe.api.withValue]`(-1)`
 *
 *
 *
 * @param value The value to set the selected rows to. In contrast to [with][Update.with], this must be the same exact type.
 */
public fun <T, C> Update<T, C>.withValue(value: C): DataFrame<T> = with { value }
