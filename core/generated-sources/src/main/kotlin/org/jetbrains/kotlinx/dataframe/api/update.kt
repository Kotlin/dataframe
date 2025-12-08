package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnExpression
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataFrameExpression
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowColumnExpression
import org.jetbrains.kotlinx.dataframe.RowValueFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.api.mean
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
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
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame] with changed values in some cells
 * (column types cannot be changed).
 *
 * ### Check out: [Grammar]
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 */
public class Update<T, C>(
    internal val df: DataFrame<T>,
    internal val filter: RowValueFilter<T, C>?,
    internal val columns: ColumnsSelector<T, C>,
) {
    public fun <R : C> cast(): Update<T, R> =
        Update(df, filter as RowValueFilter<T, R>?, columns as ColumnsSelector<T, R>)

    override fun toString(): String = "Update(df=$df, filter=$filter, columns=$columns)"

    // region KDoc declarations

    /**
     * ## [**`update`**][update] Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [**`update`**][update]**`  {  `**[`columns`][SelectingColumns]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[**`where`**][Update.where]**`  {  `**[`rowValueCondition`][SelectingRows.RowValueCondition.WithExample]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[**`at`**][Update.at]**`(`**[`rowIndices`][CommonUpdateAtFunctionDoc.RowIndicesParam]**`)`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[**`notNull`**][Update.notNull]**`()`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[**`with`**][Update.with]**`  {  `**[`rowExpression`][ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`notNull`**][Update.notNull]**`  {  `**[`rowExpression`][ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`perCol`**][Update.perCol]**`  {  `**[`colExpression`][ExpressionsGivenColumn.ColumnExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`perRowCol`**][Update.perRowCol]**`  {  `**[`rowColExpression`][ExpressionsGivenRowAndColumn.RowColumnExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`withNull`**][Update.withNull]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`withZero`**][Update.withZero]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`asFrame`**][Update.asFrame]**`  {  `**[`dataFrameExpression`][ExpressionsGivenDataFrame.DataFrameExpression.WithExample]**` }`**
     *
     *
     */
    public interface Grammar

    /**
     * The columns to update need to be selected. See [Selecting Columns][UpdateSelectingOptions]
     * for all the selecting options.
     */
    public interface Columns {

        // Optional argument that can be set to redirect where the [Selecting Columns] link points to
        @Suppress("ClassName")
        public interface SELECTING_COLUMNS
    }

    /**
     *
     * ## Selecting Columns
     * Selecting columns for various operations (including but not limited to
     * [DataFrame.select][org.jetbrains.kotlinx.dataframe.DataFrame.select], [DataFrame.update][org.jetbrains.kotlinx.dataframe.DataFrame.update], [DataFrame.gather][org.jetbrains.kotlinx.dataframe.DataFrame.gather], and [DataFrame.fillNulls][org.jetbrains.kotlinx.dataframe.DataFrame.fillNulls])
     * can be done in the following ways:
     * ### 1. [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl.WithExample]
     * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
     * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
     * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     * This also allows you to use [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs]
     * for type- and name-safe columns selection.
     *
     * #### NOTE:
     * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
     * in this DSL directly with any function, they are NOT valid return types for the
     * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
     * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
     *
     * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[update][org.jetbrains.kotlinx.dataframe.api.update]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[update][org.jetbrains.kotlinx.dataframe.api.update]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
     *
     *
     *
     * #### NOTE: There's also a 'single column' variant used sometimes: [Column Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.DslSingle.WithExample].
     * ### 2. [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample]
     * Select columns using their [column names][String]
     * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
     *
     * #### For example:
     *
     * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`("length", "age")`
     *
     * ### 3. [Column references][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnAccessors.WithExample]
     * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
     * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
     *
     * #### For example:
     *
     * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
     *
     * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
     *
     * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`(length, age)`
     *
     * ### 4. [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample]
     * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
     *
     * #### For example:
     * ```kotlin
     * data class Person(val length: Double, val age: Double)
     * ```
     *
     * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`(Person::length, Person::age)`
     *
     */
    public interface UpdateSelectingOptions

    /** @param [columns] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame] to update. */
    internal interface DslParam

    /** @param [columns] The [Column References][ColumnReference] of this [DataFrame] to update. */
    internal interface ColumnAccessorsParam

    /** @param [columns] The [KProperties][KProperty] corresponding to columns of this [DataFrame] to update. */
    internal interface KPropertiesParam

    /** @param [columns] The [Strings][String] corresponding to the names of columns belonging to this [DataFrame] to update. */
    internal interface ColumnNamesParam

    // endregion
}

// region update

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types cannot be changed).
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.Update.Grammar]
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Update.UpdateSelectingOptions]
 * for all the selecting options.
 * ### This Update Overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 * This also allows you to use [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs]
 * for type- and name-safe columns selection.
 *
 * #### NOTE:
 * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * in this DSL directly with any function, they are NOT valid return types for the
 * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
 * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
 *
 * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[update][org.jetbrains.kotlinx.dataframe.api.update]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[update][org.jetbrains.kotlinx.dataframe.api.update]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 *
 *
 * @param [columns] The [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Interpretable("Update0")
public fun <T, C> DataFrame<T>.update(columns: ColumnsSelector<T, C>): Update<T, C> = Update(this, null, columns)

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types cannot be changed).
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.Update.Grammar]
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Update.UpdateSelectingOptions]
 * for all the selecting options.
 * ### This Update Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * #### For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`("length", "age")`
 *
 * ## Optional
 * Combine `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`(...).`[with][org.jetbrains.kotlinx.dataframe.api.Update.with]` { ... }`
 * into `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`(...) { ... }`
 * @param [columns] The [Strings][String] corresponding to the names of columns belonging to this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T> DataFrame<T>.update(vararg columns: String): Update<T, Any?> = update { columns.toColumnSet() }

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types cannot be changed).
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.Update.Grammar]
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Update.UpdateSelectingOptions]
 * for all the selecting options.
 * ### This Update Overload
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * #### For example:
 * ```kotlin
 * data class Person(val length: Double, val age: Double)
 * ```
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`(Person::length, Person::age)`
 *
 * ## Optional
 * Combine `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`(...).`[with][org.jetbrains.kotlinx.dataframe.api.Update.with]` { ... }`
 * into `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]`(...) { ... }`
 * @param [columns] The [KProperties][KProperty] corresponding to columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.update(vararg columns: KProperty<C>): Update<T, C> = update { columns.toColumnSet() }

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types cannot be changed).
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.Update.Grammar]
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Update.UpdateSelectingOptions]
 * for all the selecting options.
 * ### This Update Overload
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * #### For example:
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
 * @param [columns] The [Column References][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.update(vararg columns: ColumnReference<C>): Update<T, C> =
    update { columns.toColumnSet() }

// endregion

/** ## Where
 * Filter or find rows to operate on after [selecting columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] using a
 * [row value filter][org.jetbrains.kotlinx.dataframe.RowValueFilter].
 *
 * For example:
 *
 * `df.`[update][update]` { length }.`[where][where]` { it > 10.0 }`
 *
 * `df.`[update][update]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }.`[where][where]` { `[index][org.jetbrains.kotlinx.dataframe.index]`() > 4 && city != "Paris" }`
 *
 *
 *
 *
 * @param [predicate] The [row value filter][RowValueFilter] to select the rows to update.
 */
@Interpretable("UpdateWhere")
public fun <T, C> Update<T, C>.where(predicate: RowValueFilter<T, C>): Update<T, C> =
    Update(df = df, filter = filter and predicate, columns = columns)

/**
 * ## At
 * Only update the columns at certain given [row indices][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]:
 *
 * Either a [Collection]<[Int]>, an [IntRange], or just `vararg` indices.
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { city }.`[at][org.jetbrains.kotlinx.dataframe.api.at]`(5..10).`[with][org.jetbrains.kotlinx.dataframe.api.with]` { "Paris" }`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { name }.`[at][org.jetbrains.kotlinx.dataframe.api.at]`(1, 2, 3, 4).`[with][org.jetbrains.kotlinx.dataframe.api.with]` { "Empty" }`
 *
 * ### This At Overload
 *
 * Provide a [Collection]<[Int]> of row indices to update.
 *
 * @param [rowIndices] The indices of the rows to update. Either a [Collection]<[Int]>, an [IntRange], or just `vararg` indices.
 */
@Interpretable("UpdateAt")
public fun <T, C> Update<T, C>.at(rowIndices: Collection<Int>): Update<T, C> = where { index in rowIndices }

/**
 * ## At
 * Only update the columns at certain given [row indices][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]:
 *
 * Either a [Collection]<[Int]>, an [IntRange], or just `vararg` indices.
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { city }.`[at][org.jetbrains.kotlinx.dataframe.api.at]`(5..10).`[with][org.jetbrains.kotlinx.dataframe.api.with]` { "Paris" }`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { name }.`[at][org.jetbrains.kotlinx.dataframe.api.at]`(1, 2, 3, 4).`[with][org.jetbrains.kotlinx.dataframe.api.with]` { "Empty" }`
 *
 * ### This At Overload
 *
 * Provide a `vararg` of [Ints][Int] of row indices to update.
 *
 * @param [rowIndices] The indices of the rows to update. Either a [Collection]<[Int]>, an [IntRange], or just `vararg` indices.
 */
@Interpretable("UpdateAt")
public fun <T, C> Update<T, C>.at(vararg rowIndices: Int): Update<T, C> = at(rowIndices.toSet())

/**
 * ## At
 * Only update the columns at certain given [row indices][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]:
 *
 * Either a [Collection]<[Int]>, an [IntRange], or just `vararg` indices.
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { city }.`[at][org.jetbrains.kotlinx.dataframe.api.at]`(5..10).`[with][org.jetbrains.kotlinx.dataframe.api.with]` { "Paris" }`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { name }.`[at][org.jetbrains.kotlinx.dataframe.api.at]`(1, 2, 3, 4).`[with][org.jetbrains.kotlinx.dataframe.api.with]` { "Empty" }`
 *
 * ### This At Overload
 *
 * Provide an [IntRange] of row indices to update.
 *
 * @param [rowRange] The indices of the rows to update. Either a [Collection]<[Int]>, an [IntRange], or just `vararg` indices.
 */
@Interpretable("UpdateAt")
public fun <T, C> Update<T, C>.at(rowRange: IntRange): Update<T, C> = where { index in rowRange }

/** ## Per Row Col
 * Provide a new value for every selected cell given both its row and column using a [row-column expression][org.jetbrains.kotlinx.dataframe.RowColumnExpression].
 *
 * For example:
 *
 * `df.`[update][update]` { age }.`[perRowCol][perRowCol]` { row, col ->`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`row.age / col.`[mean][org.jetbrains.kotlinx.dataframe.DataColumn.mean]`(skipNA = true)`
 *
 * `}`
 *
 *
 *
 *
 * ## See Also
 *  - [Update with][org.jetbrains.kotlinx.dataframe.api.Update.with] to provide a new value for every selected cell giving its row
 * and its previous value.
 *  - [Update per col][org.jetbrains.kotlinx.dataframe.api.Update.perCol] to provide a new value for every selected cell giving its column.
 * @param [expression] The [Row Column Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn.RowColumnExpression] to provide a new value for every selected cell giving its row and column.
 */
@Refine
@Interpretable("UpdatePerRowCol")
public inline fun <T, C> Update<T, C>.perRowCol(crossinline expression: RowColumnExpression<T, C, C>): DataFrame<T> =
    updateImpl { row, column, _ -> expression(row, column) }

/**
 * ## Update Expression
 * @see ExpressionsGivenRow.RowValueExpression.WithExample
 * @see ExpressionsGivenRow.AddDataRowNote
 */
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
 * [update with][org.jetbrains.kotlinx.dataframe.api.Update.with]-,
 * [convert with][org.jetbrains.kotlinx.dataframe.api.Convert.with]-
 * and [add][org.jetbrains.kotlinx.dataframe.api.add]-like expressions use [AddDataRow][org.jetbrains.kotlinx.dataframe.api.AddDataRow] instead of [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as the DSL's receiver type.
 * This is an extension to [RowValueExpression][org.jetbrains.kotlinx.dataframe.RowValueExpression] and
 * [RowExpression][org.jetbrains.kotlinx.dataframe.RowExpression] that provides access to
 * the modified/generated value of the preceding row ([AddDataRow.newValue][org.jetbrains.kotlinx.dataframe.api.AddDataRow.newValue]).
 * ## See Also
 * - [Update per col][org.jetbrains.kotlinx.dataframe.api.Update.perCol] to provide a new value for every selected cell giving its column.
 * - [Update per row col][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol] to provide a new value for every selected cell giving its row and column.
 * @param [expression] The [Row Value Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample] to update the rows with.
 */
@Refine
@Interpretable("UpdateWith0")
public inline fun <T, C, R : C?> Update<T, C>.with(crossinline expression: UpdateExpression<T, C, R>): DataFrame<T> =
    updateImpl { row, _, value ->
        expression(row, value)
    }

/** ## As Frame
 *
 * Updates selected [column group][ColumnGroup] as a [DataFrame] with the given [expression].
 *
 * Provide a new value for every selected dataframe using a [dataframe expression][org.jetbrains.kotlinx.dataframe.DataFrameExpression].
 *
 * For example:
 *
 * `df.`[update][update]` { name }.`[asFrame][asFrame]` { `[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { lastName } }`
 *
 * @param [expression] The [DataFrame Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpression] to replace the selected column group with.
 */
public fun <T, C, R> Update<T, DataRow<C>>.asFrame(expression: DataFrameExpression<C, DataFrame<R>>): DataFrame<T> =
    asFrameImpl(expression)

/**
 * ## Per Col
 *
 * Per Col can be used for two different types of operations:
 *  - Provide a new value for every selected cell given its column using a [column expression][org.jetbrains.kotlinx.dataframe.ColumnExpression].
 *  - Provide a new value for every selected cell per column using a [Map][Map]`<`[colName: String][String]`, value: C>`
 *  or [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * ### See Also
 *  - [Update with][org.jetbrains.kotlinx.dataframe.api.Update.with] to provide a new value for every selected cell giving its row
 * and its previous value.
 *  - [Update per row col][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol] to provide a new value for every selected cell giving its row and column.
 * ### This Per Col Overload
 * Provide a new value for every selected cell per column using a [Map][Map]`<`[colName: String][String]`, value: C>`
 *  or [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * For example:
 *
 * `val defaults = `[mapOf][mapOf]`("name" to "Empty", "age" to 0)`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { name and age }.`[where][org.jetbrains.kotlinx.dataframe.api.Update.where]` { ... }.`[perCol][org.jetbrains.kotlinx.dataframe.api.perCol]`(defaults)`
 *
 * @throws [IllegalArgumentException] if a value for a selected cell's column is not defined in [values].
 *
 *
 * @param [values] The [Map]<[String], Value> to provide a new value for every selected cell.
 *   For each selected column, there must be a value in the map with the same name.
 */
@Refine
@Interpretable("UpdatePerColMap")
public fun <T, C> Update<T, C>.perCol(values: Map<String, C>): DataFrame<T> =
    updateWithValuePerColumnImpl {
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
 * ### See Also
 *  - [Update with][org.jetbrains.kotlinx.dataframe.api.Update.with] to provide a new value for every selected cell giving its row
 * and its previous value.
 *  - [Update per row col][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol] to provide a new value for every selected cell giving its row and column.
 * ### This Per Col Overload
 * Provide a new value for every selected cell per column using a [Map][Map]`<`[colName: String][String]`, value: C>`
 *  or [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * For example:
 *
 * `val defaults = df.`[getRows][DataFrame.getRows]`(`[listOf][listOf]`(0))`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`.`[update][update]` { name }.`[with][Update.with]` { "Empty" }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`.`[update][update]` { age }.`[with][Update.with]` { 0 }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`.first()`
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { name and age }.`[where][org.jetbrains.kotlinx.dataframe.api.Update.where]` { ... }.`[perCol][org.jetbrains.kotlinx.dataframe.api.perCol]`(defaults)`
 *
 * @throws [IllegalArgumentException] if a value for a selected cell's column is not defined in [values].
 *
 *
 * @param [values] The [DataRow] to provide a new value for every selected cell.
 */
@Refine
@Interpretable("UpdatePerColRow")
public fun <T, C> Update<T, C>.perCol(values: AnyRow): DataFrame<T> = perCol(values.toMap() as Map<String, C>)

/**
 * ## Per Col
 *
 * Per Col can be used for two different types of operations:
 *  - Provide a new value for every selected cell given its column using a [column expression][org.jetbrains.kotlinx.dataframe.ColumnExpression].
 *  - Provide a new value for every selected cell per column using a [Map][Map]`<`[colName: String][String]`, value: C>`
 *  or [DataRow][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * ### See Also
 *  - [Update with][org.jetbrains.kotlinx.dataframe.api.Update.with] to provide a new value for every selected cell giving its row
 * and its previous value.
 *  - [Update per row col][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol] to provide a new value for every selected cell giving its row and column.
 * ### This Per Col Overload
 * Provide a new value for every selected cell given its column using a [column expression][org.jetbrains.kotlinx.dataframe.ColumnExpression].
 *
 * For example:
 *
 * `df.`[update][update]` { age }.`[perCol][perCol]` { `[mean][org.jetbrains.kotlinx.dataframe.DataColumn.mean]`(skipNA = true) }`
 *
 * `df.`[update][update]` { age }.`[perCol][perCol]` { `[count][org.jetbrains.kotlinx.dataframe.DataColumn.count]` { it > 10 } }`
 *
 *
 *
 * @param [valueSelector] The [Column Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpression] to provide a new value for every selected cell giving its column.
 */
@Refine
@Interpretable("UpdatePerCol")
public fun <T, C> Update<T, C>.perCol(valueSelector: ColumnExpression<C, C>): DataFrame<T> =
    updateWithValuePerColumnImpl(valueSelector)

/** Chains up two row value filters together. */
internal infix fun <T, C> RowValueFilter<T, C>?.and(other: RowValueFilter<T, C>): RowValueFilter<T, C> {
    if (this == null) return other
    val thisExp = this
    return { thisExp(this, it) && other(this, it) }
}

/**
 * ## Not Null
 * Filters the update-selection to only include cells where the value is not null.
 *
 * This is shorthand for `.`[where][Update.where]` { it != null }`.
 *
 * For example:
 *
 * `df.`[update][update]` { `[colsOf][colsOf]`<`[Int][Int]`?>() }.`[notNull][notNull]`().`[perRowCol][Update.perRowCol]` { row, col ->`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`row[col] / col.`[mean][DataColumn.mean]`(skipNA = true)`
 *
 * `}`
 */
@Suppress("UNCHECKED_CAST")
@Interpretable("UpdateNotNullDefault")
public fun <T, C> Update<T, C?>.notNull(): Update<T, C> = where { it != null } as Update<T, C>

/**
 * ## Not Null
 *
 * Selects only the rows where the values in the selected columns are not null.
 *
 * Shorthand for: [update][update]` { ... }.`[where][Update.where]` { it != null }`
 *
 * For example:
 *
 * `df.`[update][update]`  {  `[colsOf][colsOf]`<`[Number][Number]`?>() }.`[notNull][notNull]`().`[perCol][Update.perCol]`  {  `[mean][mean]`() }`
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
@Refine
@Interpretable("UpdateNotNull")
public fun <T, C> Update<T, C?>.notNull(expression: UpdateExpression<T, C, C>): DataFrame<T> =
    notNull().with(expression)

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types cannot be changed).
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.Update.Grammar]
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Update.UpdateSelectingOptions]
 * for all the selecting options.
 * ### This Update Overload
 * This overload is a combination of [update] and [with][Update.with].
 *
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * Provide a new value for every selected cell given its row and its previous value using a
 * [row value expression][org.jetbrains.kotlinx.dataframe.RowValueExpression].
 *
 * For example:
 *
 * `df.`[update][update]<code>`("city")`</code>` { name.firstName + " from " + it }`
 *
 * `df.`[update][update]<code>`("city")`</code>` { it.uppercase() }`
 *
 *
 *
 * @param [columns] The [Column References][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 * @param [expression] The [Row Value Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample] to update the rows with.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.update(
    firstCol: ColumnReference<C>,
    vararg cols: ColumnReference<C>,
    expression: UpdateExpression<T, C, C>,
): DataFrame<T> = update(*headPlusArray(firstCol, cols)).with(expression)

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types cannot be changed).
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.Update.Grammar]
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Update.UpdateSelectingOptions]
 * for all the selecting options.
 * ### This Update Overload
 * This overload is a combination of [update] and [with][Update.with].
 *
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * Provide a new value for every selected cell given its row and its previous value using a
 * [row value expression][org.jetbrains.kotlinx.dataframe.RowValueExpression].
 *
 * For example:
 *
 * `df.`[update][update]<code>`("city")`</code>` { name.firstName + " from " + it }`
 *
 * `df.`[update][update]<code>`("city")`</code>` { it.uppercase() }`
 *
 *
 *
 * @param [columns] The [KProperties][KProperty] corresponding to columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 * @param [expression] The [Row Value Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample] to update the rows with.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.update(
    firstCol: KProperty<C>,
    vararg cols: KProperty<C>,
    expression: UpdateExpression<T, C, C>,
): DataFrame<T> = update(*headPlusArray(firstCol, cols)).with(expression)

/**
 * ## The Update Operation
 *
 * Returns the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types cannot be changed).
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.Update.Grammar]
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Update.UpdateSelectingOptions]
 * for all the selecting options.
 * ### This Update Overload
 * This overload is a combination of [update] and [with][Update.with].
 *
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * Provide a new value for every selected cell given its row and its previous value using a
 * [row value expression][org.jetbrains.kotlinx.dataframe.RowValueExpression].
 *
 * For example:
 *
 * `df.`[update][update]<code>`("city")`</code>` { name.firstName + " from " + it }`
 *
 * `df.`[update][update]<code>`("city")`</code>` { it.uppercase() }`
 *
 *
 *
 * @param [columns] The [Strings][String] corresponding to the names of columns belonging to this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 * @param [expression] The [Row Value Expression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample] to update the rows with.
 */
public fun <T> DataFrame<T>.update(
    firstCol: String,
    vararg cols: String,
    expression: UpdateExpression<T, Any?, Any?>,
): DataFrame<T> = update(*headPlusArray(firstCol, cols)).with(expression)

/**
 * ## With Null
 * Specific version of [with][org.jetbrains.kotlinx.dataframe.api.with] that simply sets the value of each selected row to `null`.
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { id }.`[where][org.jetbrains.kotlinx.dataframe.api.Update.where]` { it < 0 }.`[withNull][withNull]`()`
 *
 *
 */
@Refine
@Interpretable("UpdateWithNull")
public fun <T, C> Update<T, C>.withNull(): DataFrame<T> = with { null }

/**
 * ## With Zero
 * Specific version of [with][org.jetbrains.kotlinx.dataframe.api.with] that simply sets the value of each selected row to `0`.
 *
 * For example:
 *
 * `df.`[update][org.jetbrains.kotlinx.dataframe.api.update]` { id }.`[where][org.jetbrains.kotlinx.dataframe.api.Update.where]` { it < 0 }.`[withZero][withZero]`()`
 *
 *
 */
@Refine
@Interpretable("UpdateWithZero")
public fun <T, C> Update<T, C>.withZero(): DataFrame<T> = updateWithValuePerColumnImpl { 0 as C }
