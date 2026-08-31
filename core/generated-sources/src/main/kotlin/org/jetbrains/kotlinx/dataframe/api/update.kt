package org.jetbrains.kotlinx.dataframe.api

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
 * Returns the [<code>DataFrame</code>][DataFrame] with changed values in some cells
 * (column types cannot be changed).
 *
 * ### Check out: [<code>Grammar</code>][Grammar]
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
     * ## [<code>**`update`**</code>][update] Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [<code>**`update`**</code>][update]**`  {  `**[<code>`columns`</code>][SelectingColumns]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`where`**</code>][Update.where]**`  {  `**[<code>`rowValueCondition`</code>][SelectingRows.RowValueCondition.WithExample]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`at`**</code>][Update.at]**`(`**[<code>`rowIndices`</code>][CommonUpdateAtFunctionDoc.RowIndicesParam]**`)`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`notNull`**</code>][Update.notNull]**`()`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code>**`with`**</code>][Update.with]**`  {  `**[<code>`rowExpression`</code>][ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`notNull`**</code>][Update.notNull]**`  {  `**[<code>`rowExpression`</code>][ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`perCol`**</code>][Update.perCol]**`  {  `**[<code>`colExpression`</code>][ExpressionsGivenColumn.ColumnExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`perRowCol`**</code>][Update.perRowCol]**`  {  `**[<code>`rowColExpression`</code>][ExpressionsGivenRowAndColumn.RowColumnExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`withNull`**</code>][Update.withNull]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`withZero`**</code>][Update.withZero]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`asFrame`**</code>][Update.asFrame]**`  {  `**[<code>`dataFrameExpression`</code>][ExpressionsGivenDataFrame.DataFrameExpression.WithExample]**` }`**
     *
     *
     */
    public typealias Grammar = Nothing

    /**
     * The columns to update need to be selected. See [<code>Selecting Columns</code>][UpdateSelectingOptions]
     * for all the selecting options.
     */
    public interface Columns {

        // Optional argument that can be set to redirect where the [Selecting Columns] link points to
        @Suppress("ClassName")
        public typealias SELECTING_COLUMNS = Nothing
    }

    /**
     *
     *
     *
     * ## Selecting Columns
     *
     * Selecting columns for various [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] operations
     * can be done in the following ways:
     * ### 1. [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
     *
     *
     *
     *
     * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
     * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
     * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     *
     * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
     * for specifying columns type- and name-safe.
     *
     * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     *
     *
     * > There's also a 'single column' variant used sometimes: [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
     * ### 2. [<code>Column names</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
     *
     *
     *
     *
     * Select single or multiple columns using their names as [<code>String</code>][String]s.
     * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]`("length", "age")`
     *
     *
     *
     */
    public typealias UpdateSelectingOptions = Nothing

    /** @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to update. */
    internal typealias DslParam = Nothing

    /** @param [columns] The [<code>Column References</code>][ColumnReference] of this [<code>DataFrame</code>][DataFrame] to update. */
    internal typealias ColumnAccessorsParam = Nothing

    /** @param [columns] The [<code>KProperties</code>][KProperty] corresponding to columns of this [<code>DataFrame</code>][DataFrame] to update. */
    internal typealias KPropertiesParam = Nothing

    /** @param [columns] The [<code>Strings</code>][String] corresponding to the names of columns belonging to this [<code>DataFrame</code>][DataFrame] to update. */
    internal typealias ColumnNamesParam = Nothing
    // endregion
}

// region update

/**
 * ## The Update Operation
 *
 * Returns the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types cannot be changed).
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.Update.Grammar]
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Update.UpdateSelectingOptions]
 * for all the selecting options.
 * ### This Update Overload
 *
 *
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 * @param [columns] The [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Interpretable("Update0")
public fun <T, C> DataFrame<T>.update(columns: ColumnsSelector<T, C>): Update<T, C> = Update(this, null, columns)

/**
 * ## The Update Operation
 *
 * Returns the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types cannot be changed).
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.Update.Grammar]
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Update.UpdateSelectingOptions]
 * for all the selecting options.
 * ### This Update Overload
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]`("length", "age")`
 *
 *
 *
 * ## Optional
 * Combine `df.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]`(...).`[<code>with</code>][org.jetbrains.kotlinx.dataframe.api.Update.with]` { ... }`
 * into `df.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]`(...) { ... }`
 * @param [columns] The [<code>Strings</code>][String] corresponding to the names of columns belonging to this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T> DataFrame<T>.update(vararg columns: String): Update<T, Any?> = update { columns.toColumnSet() }

/**
 * ## The Update Operation
 *
 * Returns the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types cannot be changed).
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.Update.Grammar]
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Update.UpdateSelectingOptions]
 * for all the selecting options.
 * ### This Update Overload
 *
 * ## Optional
 * Combine `df.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]`(...).`[<code>with</code>][org.jetbrains.kotlinx.dataframe.api.Update.with]` { ... }`
 * into `df.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]`(...) { ... }`
 * @param [columns] The [<code>KProperties</code>][KProperty] corresponding to columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.update(vararg columns: KProperty<C>): Update<T, C> = update { columns.toColumnSet() }

/**
 * ## The Update Operation
 *
 * Returns the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types cannot be changed).
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.Update.Grammar]
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Update.UpdateSelectingOptions]
 * for all the selecting options.
 * ### This Update Overload
 *
 * ## Optional
 * Combine `df.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]`(...).`[<code>with</code>][org.jetbrains.kotlinx.dataframe.api.Update.with]` { ... }`
 * into `df.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]`(...) { ... }`
 * @param [columns] The [<code>Column References</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.update(vararg columns: ColumnReference<C>): Update<T, C> =
    update { columns.toColumnSet() }

// endregion

/** ## Where
 * Filter or find rows to operate on after [<code>selecting columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] using a
 * [<code>row value filter</code>][org.jetbrains.kotlinx.dataframe.RowValueFilter].
 *
 * For example:
 *
 * `df.`[<code>update</code>][update]` { length }.`[<code>where</code>][where]` { it > 10.0 }`
 *
 * `df.`[<code>update</code>][update]` { `[<code>cols</code>][ColumnsSelectionDsl.cols]`(1..5) }.`[<code>where</code>][where]` { `[<code>index</code>][org.jetbrains.kotlinx.dataframe.index]`() > 4 && city != "Paris" }`
 *
 *
 *
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * @param [predicate] The [<code>row value filter</code>][RowValueFilter] to select the rows to update.
 */
@Interpretable("UpdateWhere")
public fun <T, C> Update<T, C>.where(predicate: RowValueFilter<T, C>): Update<T, C> =
    Update(df = df, filter = filter and predicate, columns = columns)

/**
 * ## At
 * Only update the columns at certain given [<code>row indices</code>][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]:
 *
 * Either a [<code>Collection</code>][Collection]<[<code>Int</code>][Int]>, an [<code>IntRange</code>][IntRange], or just `vararg` indices.
 *
 * For example:
 *
 * `df.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]` { city }.`[<code>at</code>][org.jetbrains.kotlinx.dataframe.api.at]`(5..10).`[<code>with</code>][org.jetbrains.kotlinx.dataframe.api.with]` { "Paris" }`
 *
 * `df.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]` { name }.`[<code>at</code>][org.jetbrains.kotlinx.dataframe.api.at]`(1, 2, 3, 4).`[<code>with</code>][org.jetbrains.kotlinx.dataframe.api.with]` { "Empty" }`
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * ### This At Overload
 *
 * Provide a [<code>Collection</code>][Collection]<[<code>Int</code>][Int]> of row indices to update.
 *
 * @param [rowIndices] The indices of the rows to update. Either a [<code>Collection</code>][Collection]<[<code>Int</code>][Int]>, an [<code>IntRange</code>][IntRange], or just `vararg` indices.
 */
@Interpretable("UpdateAt")
public fun <T, C> Update<T, C>.at(rowIndices: Collection<Int>): Update<T, C> = where { index in rowIndices }

/**
 * ## At
 * Only update the columns at certain given [<code>row indices</code>][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]:
 *
 * Either a [<code>Collection</code>][Collection]<[<code>Int</code>][Int]>, an [<code>IntRange</code>][IntRange], or just `vararg` indices.
 *
 * For example:
 *
 * `df.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]` { city }.`[<code>at</code>][org.jetbrains.kotlinx.dataframe.api.at]`(5..10).`[<code>with</code>][org.jetbrains.kotlinx.dataframe.api.with]` { "Paris" }`
 *
 * `df.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]` { name }.`[<code>at</code>][org.jetbrains.kotlinx.dataframe.api.at]`(1, 2, 3, 4).`[<code>with</code>][org.jetbrains.kotlinx.dataframe.api.with]` { "Empty" }`
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * ### This At Overload
 *
 * Provide a `vararg` of [<code>Ints</code>][Int] of row indices to update.
 *
 * @param [rowIndices] The indices of the rows to update. Either a [<code>Collection</code>][Collection]<[<code>Int</code>][Int]>, an [<code>IntRange</code>][IntRange], or just `vararg` indices.
 */
@Interpretable("UpdateAt")
public fun <T, C> Update<T, C>.at(vararg rowIndices: Int): Update<T, C> = at(rowIndices.toSet())

/**
 * ## At
 * Only update the columns at certain given [<code>row indices</code>][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]:
 *
 * Either a [<code>Collection</code>][Collection]<[<code>Int</code>][Int]>, an [<code>IntRange</code>][IntRange], or just `vararg` indices.
 *
 * For example:
 *
 * `df.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]` { city }.`[<code>at</code>][org.jetbrains.kotlinx.dataframe.api.at]`(5..10).`[<code>with</code>][org.jetbrains.kotlinx.dataframe.api.with]` { "Paris" }`
 *
 * `df.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]` { name }.`[<code>at</code>][org.jetbrains.kotlinx.dataframe.api.at]`(1, 2, 3, 4).`[<code>with</code>][org.jetbrains.kotlinx.dataframe.api.with]` { "Empty" }`
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * ### This At Overload
 *
 * Provide an [<code>IntRange</code>][IntRange] of row indices to update.
 *
 * @param [rowRange] The indices of the rows to update. Either a [<code>Collection</code>][Collection]<[<code>Int</code>][Int]>, an [<code>IntRange</code>][IntRange], or just `vararg` indices.
 */
@Interpretable("UpdateAt")
public fun <T, C> Update<T, C>.at(rowRange: IntRange): Update<T, C> = where { index in rowRange }

/** ## Per Row Col
 * Provide a new value for every selected cell given both its row and column using a [<code>row-column expression</code>][org.jetbrains.kotlinx.dataframe.RowColumnExpression].
 *
 * For example:
 *
 * `df.`[<code>update</code>][update]` { age }.`[<code>perRowCol</code>][perRowCol]` { row, col ->`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`row.age / col.`[<code>mean</code>][org.jetbrains.kotlinx.dataframe.DataColumn.mean]`(skipNA = true)`
 *
 * `}`
 *
 *
 *
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * ## See Also
 *  - [<code>Update with</code>][org.jetbrains.kotlinx.dataframe.api.Update.with] to provide a new value for every selected cell giving its row
 * and its previous value.
 *  - [<code>Update per col</code>][org.jetbrains.kotlinx.dataframe.api.Update.perCol] to provide a new value for every selected cell giving its column.
 * @param [expression] The [<code>Row Column Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn.RowColumnExpression] to provide a new value for every selected cell giving its row and column.
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
 * [<code>row value expression</code>][org.jetbrains.kotlinx.dataframe.RowValueExpression].
 *
 * Fore more information, [See RowValueExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowvalueexpression)
 *
 * For example:
 *
 * `df.`[<code>update</code>][update]` { city }.`[<code>with</code>][with]` { name.firstName + " from " + it }`
 *
 * `df.`[<code>update</code>][update]` { city }.`[<code>with</code>][with]` { it.uppercase() }`
 *
 *
 *
 * ## Note
 * [<code>update with</code>][org.jetbrains.kotlinx.dataframe.api.Update.with]-,
 * [<code>convert with</code>][org.jetbrains.kotlinx.dataframe.api.Convert.with]-
 * and [<code>add</code>][org.jetbrains.kotlinx.dataframe.api.add]-like expressions use [<code>AddDataRow</code>][org.jetbrains.kotlinx.dataframe.api.AddDataRow] instead of [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as the DSL's receiver type.
 * This is an extension to [<code>RowValueExpression</code>][org.jetbrains.kotlinx.dataframe.RowValueExpression] and
 * [<code>RowExpression</code>][org.jetbrains.kotlinx.dataframe.RowExpression] that provides access to
 * the modified/generated value of the preceding row ([<code>AddDataRow.newValue</code>][org.jetbrains.kotlinx.dataframe.api.AddDataRow.newValue]).
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * ## See Also
 * - [<code>Update per col</code>][org.jetbrains.kotlinx.dataframe.api.Update.perCol] to provide a new value for every selected cell giving its column.
 * - [<code>Update per row col</code>][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol] to provide a new value for every selected cell giving its row and column.
 * @param [expression] The [<code>Row Value Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample] to update the rows with.
 */
@Refine
@Interpretable("UpdateWith0")
public inline fun <T, C, R : C?> Update<T, C>.with(crossinline expression: UpdateExpression<T, C, R>): DataFrame<T> =
    updateImpl { row, _, value ->
        expression(row, value)
    }

/** ## As Frame
 *
 * Updates selected [<code>column group</code>][ColumnGroup] as a [<code>DataFrame</code>][DataFrame] with the given [<code>expression</code>][expression].
 *
 * Provide a new value for every selected dataframe using a [<code>dataframe expression</code>][org.jetbrains.kotlinx.dataframe.DataFrameExpression].
 *
 * For example:
 *
 * `df.`[<code>update</code>][update]` { name }.`[<code>asFrame</code>][asFrame]` { `[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { lastName } }`
 *
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * @param [expression] The [<code>DataFrame Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpression] to replace the selected column group with.
 */
public fun <T, C, R> Update<T, DataRow<C>>.asFrame(expression: DataFrameExpression<C, DataFrame<R>>): DataFrame<T> =
    asFrameImpl(expression)

/**
 * ## Per Col
 *
 * Per Col can be used for two different types of operations:
 *  - Provide a new value for every selected cell given its column using a [<code>column expression</code>][org.jetbrains.kotlinx.dataframe.ColumnExpression].
 *  - Provide a new value for every selected cell per column using a [<code>Map</code>][Map]`<`[<code>colName: String</code>][String]`, value: C>`
 *  or [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * ### See Also
 *  - [<code>Update with</code>][org.jetbrains.kotlinx.dataframe.api.Update.with] to provide a new value for every selected cell giving its row
 * and its previous value.
 *  - [<code>Update per row col</code>][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol] to provide a new value for every selected cell giving its row and column.
 * ### This Per Col Overload
 * Provide a new value for every selected cell per column using a [<code>Map</code>][Map]`<`[<code>colName: String</code>][String]`, value: C>`
 *  or [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * For example:
 *
 * `val defaults = `[<code>mapOf</code>][mapOf]`("name" to "Empty", "age" to 0)`
 *
 * `df.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]` { name and age }.`[<code>where</code>][org.jetbrains.kotlinx.dataframe.api.Update.where]` { ... }.`[<code>perCol</code>][org.jetbrains.kotlinx.dataframe.api.perCol]`(defaults)`
 *
 * @throws [IllegalArgumentException] if a value for a selected cell's column is not defined in [values].
 *
 *
 * @param [values] The [<code>Map</code>][Map]<[<code>String</code>][String], Value> to provide a new value for every selected cell.
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
 *  - Provide a new value for every selected cell given its column using a [<code>column expression</code>][org.jetbrains.kotlinx.dataframe.ColumnExpression].
 *  - Provide a new value for every selected cell per column using a [<code>Map</code>][Map]`<`[<code>colName: String</code>][String]`, value: C>`
 *  or [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * ### See Also
 *  - [<code>Update with</code>][org.jetbrains.kotlinx.dataframe.api.Update.with] to provide a new value for every selected cell giving its row
 * and its previous value.
 *  - [<code>Update per row col</code>][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol] to provide a new value for every selected cell giving its row and column.
 * ### This Per Col Overload
 * Provide a new value for every selected cell per column using a [<code>Map</code>][Map]`<`[<code>colName: String</code>][String]`, value: C>`
 *  or [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * For example:
 *
 * `val defaults = df.`[<code>getRows</code>][DataFrame.getRows]`(`[<code>listOf</code>][listOf]`(0))`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`.`[<code>update</code>][update]` { name }.`[<code>with</code>][Update.with]` { "Empty" }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`.`[<code>update</code>][update]` { age }.`[<code>with</code>][Update.with]` { 0 }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`.first()`
 *
 * `df.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]` { name and age }.`[<code>where</code>][org.jetbrains.kotlinx.dataframe.api.Update.where]` { ... }.`[<code>perCol</code>][org.jetbrains.kotlinx.dataframe.api.perCol]`(defaults)`
 *
 * @throws [IllegalArgumentException] if a value for a selected cell's column is not defined in [values].
 *
 *
 * @param [values] The [<code>DataRow</code>][DataRow] to provide a new value for every selected cell.
 */
@Refine
@Interpretable("UpdatePerColRow")
public fun <T, C> Update<T, C>.perCol(values: DataRow<*>): DataFrame<T> = perCol(values.toMap() as Map<String, C>)

/**
 * ## Per Col
 *
 * Per Col can be used for two different types of operations:
 *  - Provide a new value for every selected cell given its column using a [<code>column expression</code>][org.jetbrains.kotlinx.dataframe.ColumnExpression].
 *  - Provide a new value for every selected cell per column using a [<code>Map</code>][Map]`<`[<code>colName: String</code>][String]`, value: C>`
 *  or [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as Map.
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * ### See Also
 *  - [<code>Update with</code>][org.jetbrains.kotlinx.dataframe.api.Update.with] to provide a new value for every selected cell giving its row
 * and its previous value.
 *  - [<code>Update per row col</code>][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol] to provide a new value for every selected cell giving its row and column.
 * ### This Per Col Overload
 * Provide a new value for every selected cell given its column using a [<code>column expression</code>][org.jetbrains.kotlinx.dataframe.ColumnExpression].
 *
 * For example:
 *
 * `df.`[<code>update</code>][update]` { age }.`[<code>perCol</code>][perCol]` { `[<code>mean</code>][org.jetbrains.kotlinx.dataframe.DataColumn.mean]`(skipNA = true) }`
 *
 * `df.`[<code>update</code>][update]` { age }.`[<code>perCol</code>][perCol]` { `[<code>count</code>][org.jetbrains.kotlinx.dataframe.DataColumn.count]` { it > 10 } }`
 *
 *
 *
 * @param [valueSelector] The [<code>Column Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpression] to provide a new value for every selected cell giving its column.
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
 * This is shorthand for `.`[<code>where</code>][Update.where]` { it != null }`.
 *
 * For example:
 *
 * `df.`[<code>update</code>][update]` { `[<code>colsOf</code>][colsOf]`<`[<code>Int</code>][Int]`?>() }.`[<code>notNull</code>][notNull]`().`[<code>perRowCol</code>][Update.perRowCol]` { row, col ->`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`row[col] / col.`[<code>mean</code>][DataColumn.mean]`(skipNA = true)`
 *
 * `}`
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 */
@Suppress("UNCHECKED_CAST")
@Interpretable("UpdateNotNullDefault")
public fun <T, C> Update<T, C?>.notNull(): Update<T, C> = where { it != null } as Update<T, C>

/**
 * ## Not Null
 *
 * Selects only the rows where the values in the selected columns are not null.
 *
 * Shorthand for: [<code>update</code>][update]` { ... }.`[<code>where</code>][Update.where]` { it != null }`
 *
 * For example:
 *
 * `df.`[<code>update</code>][update]`  {  `[<code>colsOf</code>][colsOf]`<`[<code>Number</code>][Number]`?>() }.`[<code>notNull</code>][notNull]`().`[<code>perCol</code>][Update.perCol]`  {  `[<code>mean</code>][mean]`() }`
 *
 * ### Optional
 * Provide an [<code>expression</code>][expression] to update the rows with.
 * This combines [<code>with</code>][Update.with] with [<code>notNull</code>][notNull].
 *
 * For example:
 *
 * `df.`[<code>update</code>][update]` { city }.`[<code>notNull</code>][Update.notNull]` { it.`[<code>toUpperCase</code>][String.toUpperCase]`() }`
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * @param expression Optional [<code>Row Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowExpression.WithExample] to update the rows with.
 */
@Refine
@Interpretable("UpdateNotNull")
public fun <T, C> Update<T, C?>.notNull(expression: UpdateExpression<T, C, C>): DataFrame<T> =
    notNull().with(expression)

/**
 * ## The Update Operation
 *
 * Returns the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types cannot be changed).
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.Update.Grammar]
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Update.UpdateSelectingOptions]
 * for all the selecting options.
 * ### This Update Overload
 * This overload is a combination of [<code>update</code>][update] and [<code>with</code>][Update.with].
 *
 * Provide a new value for every selected cell given its row and its previous value using a
 * [<code>row value expression</code>][org.jetbrains.kotlinx.dataframe.RowValueExpression].
 *
 * Fore more information, [See RowValueExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowvalueexpression)
 *
 * For example:
 *
 * `df.`[<code>update</code>][update]<code>`("city")`</code>` { name.firstName + " from " + it }`
 *
 * `df.`[<code>update</code>][update]<code>`("city")`</code>` { it.uppercase() }`
 *
 *
 *
 * @param [columns] The [<code>Column References</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 * @param [expression] The [<code>Row Value Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample] to update the rows with.
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
 * Returns the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types cannot be changed).
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.Update.Grammar]
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Update.UpdateSelectingOptions]
 * for all the selecting options.
 * ### This Update Overload
 * This overload is a combination of [<code>update</code>][update] and [<code>with</code>][Update.with].
 *
 * Provide a new value for every selected cell given its row and its previous value using a
 * [<code>row value expression</code>][org.jetbrains.kotlinx.dataframe.RowValueExpression].
 *
 * Fore more information, [See RowValueExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowvalueexpression)
 *
 * For example:
 *
 * `df.`[<code>update</code>][update]<code>`("city")`</code>` { name.firstName + " from " + it }`
 *
 * `df.`[<code>update</code>][update]<code>`("city")`</code>` { it.uppercase() }`
 *
 *
 *
 * @param [columns] The [<code>KProperties</code>][KProperty] corresponding to columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 * @param [expression] The [<code>Row Value Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample] to update the rows with.
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
 * Returns the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with changed values in some cells
 * (column types cannot be changed).
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.Update.Grammar]
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Update.UpdateSelectingOptions]
 * for all the selecting options.
 * ### This Update Overload
 * This overload is a combination of [<code>update</code>][update] and [<code>with</code>][Update.with].
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * Provide a new value for every selected cell given its row and its previous value using a
 * [<code>row value expression</code>][org.jetbrains.kotlinx.dataframe.RowValueExpression].
 *
 * Fore more information, [See RowValueExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowvalueexpression)
 *
 * For example:
 *
 * `df.`[<code>update</code>][update]<code>`("city")`</code>` { name.firstName + " from " + it }`
 *
 * `df.`[<code>update</code>][update]<code>`("city")`</code>` { it.uppercase() }`
 *
 *
 *
 * @param [columns] The [<code>Strings</code>][String] corresponding to the names of columns belonging to this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 * @param [expression] The [<code>Row Value Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample] to update the rows with.
 */
public fun <T> DataFrame<T>.update(
    firstCol: String,
    vararg cols: String,
    expression: UpdateExpression<T, Any?, Any?>,
): DataFrame<T> = update(*headPlusArray(firstCol, cols)).with(expression)

/**
 * ## With Null
 * Specific version of [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.with] that simply sets the value of each selected row to `null`.
 *
 * For example:
 *
 * `df.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]` { id }.`[<code>where</code>][org.jetbrains.kotlinx.dataframe.api.Update.where]` { it < 0 }.`[<code>withNull</code>][withNull]`()`
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 *
 */
@Refine
@Interpretable("UpdateWithNull")
public fun <T, C> Update<T, C>.withNull(): DataFrame<T> = with { null }

/**
 * ## With Zero
 * Specific version of [<code>with</code>][org.jetbrains.kotlinx.dataframe.api.with] that simply sets the value of each selected row to `0`.
 *
 * For example:
 *
 * `df.`[<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update]` { id }.`[<code>where</code>][org.jetbrains.kotlinx.dataframe.api.Update.where]` { it < 0 }.`[<code>withZero</code>][withZero]`()`
 *
 * For more information: [See `update` on the documentation website.](https://kotlin.github.io/dataframe/update.html)
 *
 *
 */
@Refine
@Interpretable("UpdateWithZero")
public fun <T, C> Update<T, C>.withZero(): DataFrame<T> = updateWithValuePerColumnImpl { 0 as C }
