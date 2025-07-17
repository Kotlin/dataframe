package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.NA
import org.jetbrains.kotlinx.dataframe.documentation.NaN
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.get
import org.jetbrains.kotlinx.dataframe.typeClass
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KProperty

// region fillNulls

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [update].
 *
 * ### Check out: [Grammar][FillNulls.Grammar]
 *
 * For more information: [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls)
 */
internal interface FillNulls {

    /** ## [**fillNulls**][fillNulls] Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [**fillNulls**][fillNulls]**`  {  `**[`columns`][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[**`where`**][org.jetbrains.kotlinx.dataframe.api.Update.where]**`  {  `**[`rowValueCondition`][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.RowValueCondition.WithExample]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[**`at`**][org.jetbrains.kotlinx.dataframe.api.Update.at]**`(`**[`rowIndices`][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]**`)`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[**`with`**][org.jetbrains.kotlinx.dataframe.api.Update.with]**`  {  `**[`rowExpression`][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`notNull`**][org.jetbrains.kotlinx.dataframe.api.Update.notNull]**`  {  `**[`rowExpression`][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`perCol`**][org.jetbrains.kotlinx.dataframe.api.Update.perCol]**`  {  `**[`colExpression`][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`perRowCol`**][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol]**`  {  `**[`rowColExpression`][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn.RowColumnExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`withNull`**][org.jetbrains.kotlinx.dataframe.api.Update.withNull]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`withZero`**][org.jetbrains.kotlinx.dataframe.api.Update.withZero]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`asFrame`**][org.jetbrains.kotlinx.dataframe.api.Update.asFrame]**`  {  `**[`dataFrameExpression`][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpression.WithExample]**` }`**
     *
     */
    interface Grammar

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
     * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
     *
     * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
     *
     *
     * #### NOTE: There's also a 'single column' variant used sometimes: [Column Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.DslSingle.WithExample].
     * ### 2. [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample]
     * Select columns using their [column names][String]
     * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
     *
     * #### For example:
     *
     * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]`("length", "age")`
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
     * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]`(length, age)`
     *
     * ### 4. [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample]
     * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
     *
     * #### For example:
     * ```kotlin
     * data class Person(val length: Double, val age: Double)
     * ```
     *
     * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]`(Person::length, Person::age)`
     *
     */
    interface FillNullsSelectingOptions
}

private interface SetFillNullsOperationArg

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FillNulls.Grammar]
 *
 * For more information: [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FillNulls.FillNullsSelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill Nulls Overload
 *
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
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
 * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
 *
 * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 *
 * @param [columns] The [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Interpretable("FillNulls0")
public fun <T, C> DataFrame<T>.fillNulls(columns: ColumnsSelector<T, C?>): Update<T, C?> =
    update(columns).where { it == null }

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FillNulls.Grammar]
 *
 * For more information: [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FillNulls.FillNullsSelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill Nulls Overload
 *
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * #### For example:
 *
 * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]`("length", "age")`
 *
 * @param [columns] The [Strings][String] corresponding to the names of columns belonging to this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T> DataFrame<T>.fillNulls(vararg columns: String): Update<T, Any?> = fillNulls { columns.toColumnSet() }

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FillNulls.Grammar]
 *
 * For more information: [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FillNulls.FillNullsSelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill Nulls Overload
 *
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * #### For example:
 * ```kotlin
 * data class Person(val length: Double, val age: Double)
 * ```
 *
 * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]`(Person::length, Person::age)`
 *
 * @param [columns] The [KProperties][KProperty] corresponding to columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.fillNulls(vararg columns: KProperty<C>): Update<T, C?> =
    fillNulls { columns.toColumnSet() }

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FillNulls.Grammar]
 *
 * For more information: [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FillNulls.FillNullsSelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill Nulls Overload
 *
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * #### For example:
 *
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]`(length, age)`
 *
 * @param [columns] The [Column References][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.fillNulls(vararg columns: ColumnReference<C>): Update<T, C?> =
    fillNulls { columns.toColumnSet() }

// endregion

/** Is only `true` if [this] is [Double.NaN] or [Float.NaN]. */
internal inline val Any?.isNaN: Boolean get() = (this is Double && isNaN()) || (this is Float && isNaN())

/**
 * Returns `true` if [this] is considered NA.
 * "NA", in DataFrame, roughly means `null` or `NaN`.
 *
 * Overload of `isNA` with contract support.
 *
 * @see NA
 */
@JvmName("isNaWithContract")
@Suppress("NOTHING_TO_INLINE")
@OptIn(ExperimentalContracts::class)
internal inline fun <T : Any?> T.isNA(): Boolean {
    contract { returns(false) implies (this@isNA != null) }
    return isNA
}

/**
 * Is `true` if [this] is considered NA.
 * "NA", in DataFrame, roughly means `null` or `NaN`.
 * @see NA
 */
internal inline val Any?.isNA: Boolean
    get() = when (this) {
        null -> true
        is Double -> isNaN()
        is Float -> isNaN()
        is AnyRow -> allNA()
        is AnyFrame -> isEmpty()
        else -> false
    }

internal inline val AnyCol.canHaveNaN: Boolean get() = typeClass.let { it == Double::class || it == Float::class }

/**
 * Is `true` when [this] column can have [NA] values.
 * @see NA
 */
internal inline val AnyCol.canHaveNA: Boolean get() = hasNulls() || canHaveNaN || kind() != ColumnKind.Value

/**
 * Is `true` when [this] is `null` or [Double.NaN].
 * @see NA
 */
internal inline val Double?.isNA: Boolean get() = this == null || this.isNaN()

/**
 * Is `true` when [this] is `null` or [Float.NaN].
 * @see NA
 */
internal inline val Float?.isNA: Boolean get() = this == null || this.isNaN()

// region fillNaNs

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [`NaN`][NaN] values with given value or expression.
 * Specific case of [update].
 *
 * ### Check out: [Grammar][FillNaNs.Grammar]
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans)
 */
internal interface FillNaNs {

    /** ## [fillNaNs][fillNaNs] Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [fillNaNs][fillNaNs]**`  {  `**[`columns`][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[**`where`**][org.jetbrains.kotlinx.dataframe.api.Update.where]**`  {  `**[`rowValueCondition`][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.RowValueCondition.WithExample]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[**`at`**][org.jetbrains.kotlinx.dataframe.api.Update.at]**`(`**[`rowIndices`][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]**`)`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[**`with`**][org.jetbrains.kotlinx.dataframe.api.Update.with]**`  {  `**[`rowExpression`][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`notNull`**][org.jetbrains.kotlinx.dataframe.api.Update.notNull]**`  {  `**[`rowExpression`][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`perCol`**][org.jetbrains.kotlinx.dataframe.api.Update.perCol]**`  {  `**[`colExpression`][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`perRowCol`**][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol]**`  {  `**[`rowColExpression`][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn.RowColumnExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`withNull`**][org.jetbrains.kotlinx.dataframe.api.Update.withNull]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`withZero`**][org.jetbrains.kotlinx.dataframe.api.Update.withZero]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`asFrame`**][org.jetbrains.kotlinx.dataframe.api.Update.asFrame]**`  {  `**[`dataFrameExpression`][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpression.WithExample]**` }`**
     *
     */
    interface Grammar

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
     * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
     *
     * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
     *
     *
     * #### NOTE: There's also a 'single column' variant used sometimes: [Column Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.DslSingle.WithExample].
     * ### 2. [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample]
     * Select columns using their [column names][String]
     * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
     *
     * #### For example:
     *
     * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`("length", "age")`
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
     * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`(length, age)`
     *
     * ### 4. [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample]
     * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
     *
     * #### For example:
     * ```kotlin
     * data class Person(val length: Double, val age: Double)
     * ```
     *
     * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`(Person::length, Person::age)`
     *
     */
    interface FillNaNsSelectingOptions
}

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FillNaNs.Grammar]
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FillNaNs.FillNaNsSelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill NaNs Overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
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
 * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
 *
 * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 *
 * @param [columns] The [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Interpretable("FillNaNs0")
public fun <T, C> DataFrame<T>.fillNaNs(columns: ColumnsSelector<T, C>): Update<T, C> =
    update(columns).where { it.isNaN }

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FillNaNs.Grammar]
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FillNaNs.FillNaNsSelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill NaNs Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * #### For example:
 *
 * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`("length", "age")`
 *
 * @param [columns] The [Strings][String] corresponding to the names of columns belonging to this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T> DataFrame<T>.fillNaNs(vararg columns: String): Update<T, Any?> = fillNaNs { columns.toColumnSet() }

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FillNaNs.Grammar]
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FillNaNs.FillNaNsSelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill NaNs Overload
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * #### For example:
 * ```kotlin
 * data class Person(val length: Double, val age: Double)
 * ```
 *
 * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`(Person::length, Person::age)`
 *
 * @param [columns] The [KProperties][KProperty] corresponding to columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.fillNaNs(vararg columns: KProperty<C>): Update<T, C> = fillNaNs { columns.toColumnSet() }

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FillNaNs.Grammar]
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FillNaNs.FillNaNsSelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill NaNs Overload
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * #### For example:
 *
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`(length, age)`
 *
 * @param [columns] The [Column References][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.fillNaNs(vararg columns: ColumnReference<C>): Update<T, C> =
    fillNaNs { columns.toColumnSet() }

// endregion

// region fillNA

/**
 * ## The Fill NA Operation
 *
 * Replaces [`NA`][NA] values with given value or expression.
 * Specific case of [update].
 *
 * ### Check out: [Grammar][FillNA.Grammar]
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna)
 */
internal interface FillNA {

    /** ## [fillNA][fillNA] Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [fillNA][fillNA]**`  {  `**[`columns`][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[**`where`**][org.jetbrains.kotlinx.dataframe.api.Update.where]**`  {  `**[`rowValueCondition`][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.RowValueCondition.WithExample]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[**`at`**][org.jetbrains.kotlinx.dataframe.api.Update.at]**`(`**[`rowIndices`][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]**`)`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[**`with`**][org.jetbrains.kotlinx.dataframe.api.Update.with]**`  {  `**[`rowExpression`][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`notNull`**][org.jetbrains.kotlinx.dataframe.api.Update.notNull]**`  {  `**[`rowExpression`][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`perCol`**][org.jetbrains.kotlinx.dataframe.api.Update.perCol]**`  {  `**[`colExpression`][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`perRowCol`**][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol]**`  {  `**[`rowColExpression`][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn.RowColumnExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`withNull`**][org.jetbrains.kotlinx.dataframe.api.Update.withNull]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`withZero`**][org.jetbrains.kotlinx.dataframe.api.Update.withZero]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`asFrame`**][org.jetbrains.kotlinx.dataframe.api.Update.asFrame]**`  {  `**[`dataFrameExpression`][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpression.WithExample]**` }`**
     *
     */
    interface Grammar

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
     * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
     *
     * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
     *
     *
     * #### NOTE: There's also a 'single column' variant used sometimes: [Column Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.DslSingle.WithExample].
     * ### 2. [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample]
     * Select columns using their [column names][String]
     * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
     *
     * #### For example:
     *
     * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]`("length", "age")`
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
     * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]`(length, age)`
     *
     * ### 4. [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample]
     * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
     *
     * #### For example:
     * ```kotlin
     * data class Person(val length: Double, val age: Double)
     * ```
     *
     * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]`(Person::length, Person::age)`
     *
     */
    interface FillNASelectingOptions
}

/**
 * ## The Fill NA Operation
 *
 * Replaces [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FillNA.Grammar]
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FillNA.FillNASelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill NA Overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
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
 * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
 *
 * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 *
 * @param [columns] The [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Interpretable("FillNulls0") // fillNA changes schema same as fillNulls
public fun <T, C> DataFrame<T>.fillNA(columns: ColumnsSelector<T, C?>): Update<T, C?> =
    update(columns).where { it.isNA }

/**
 * ## The Fill NA Operation
 *
 * Replaces [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FillNA.Grammar]
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FillNA.FillNASelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill NA Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * #### For example:
 *
 * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]`("length", "age")`
 *
 * @param [columns] The [Strings][String] corresponding to the names of columns belonging to this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T> DataFrame<T>.fillNA(vararg columns: String): Update<T, Any?> = fillNA { columns.toColumnSet() }

/**
 * ## The Fill NA Operation
 *
 * Replaces [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FillNA.Grammar]
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FillNA.FillNASelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill NA Overload
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * #### For example:
 * ```kotlin
 * data class Person(val length: Double, val age: Double)
 * ```
 *
 * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]`(Person::length, Person::age)`
 *
 * @param [columns] The [KProperties][KProperty] corresponding to columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.fillNA(vararg columns: KProperty<C>): Update<T, C?> = fillNA { columns.toColumnSet() }

/**
 * ## The Fill NA Operation
 *
 * Replaces [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FillNA.Grammar]
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.FillNA.FillNASelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill NA Overload
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * #### For example:
 *
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]`(length, age)`
 *
 * @param [columns] The [Column References][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.fillNA(vararg columns: ColumnReference<C>): Update<T, C?> =
    fillNA { columns.toColumnSet() }

// endregion

// region dropNulls

/**
 * ## The Drop Nulls Operation
 *
 * Removes rows with `null` values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.DropNulls.DropNullsSelectingOptions]).
 *
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls)
 * ### This Drop Nulls Overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
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
 * `df.`[dropNulls][org.jetbrains.kotlinx.dataframe.api.dropNulls]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
 *
 * `df.`[dropNulls][org.jetbrains.kotlinx.dataframe.api.dropNulls]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * `df.`[dropNulls][org.jetbrains.kotlinx.dataframe.api.dropNulls]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 *
 * `df.`[dropNulls][dropNulls]`(whereAllNull = true) { `[colsOf][colsOf]`<`[Double][Double]`>() }`
 * @param whereAllNull `false` by default.
 *   If `true`, rows are dropped if all selected cells are `null`.
 *   If `false`, rows are dropped if any of the selected cells is `null`.
 * @param columns The [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
@Refine
@Interpretable("DropNulls0")
public fun <T> DataFrame<T>.dropNulls(whereAllNull: Boolean = false, columns: ColumnsSelector<T, *>): DataFrame<T> {
    val cols = this[columns]
    return if (whereAllNull) {
        drop { row -> cols.all { col -> col[row] == null } }
    } else {
        drop { row -> cols.any { col -> col[row] == null } }
    }
}

/**
 * ## The Drop Nulls Operation
 *
 * Removes rows with `null` values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.DropNulls.DropNullsSelectingOptions]).
 *
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls)
 * ### This Drop Nulls Overload
 * This overload operates on all columns in the [DataFrame].
 * @param whereAllNull `false` by default.
 *   If `true`, rows are dropped if all selected cells are `null`.
 *   If `false`, rows are dropped if any of the selected cells is `null`.
 */
@Refine
@Interpretable("DropNulls1")
public fun <T> DataFrame<T>.dropNulls(whereAllNull: Boolean = false): DataFrame<T> = dropNulls(whereAllNull) { all() }

/**
 * ## The Drop Nulls Operation
 *
 * Removes rows with `null` values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.DropNulls.DropNullsSelectingOptions]).
 *
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls)
 * ### This Drop Nulls Overload
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * #### For example:
 * ```kotlin
 * data class Person(val length: Double, val age: Double)
 * ```
 *
 * `df.`[dropNulls][org.jetbrains.kotlinx.dataframe.api.dropNulls]`(Person::length, Person::age)`
 *
 * `df.`[dropNulls][dropNulls]`(Person::length, whereAllNull = true)`
 * @param whereAllNull `false` by default.
 *   If `true`, rows are dropped if all selected cells are `null`.
 *   If `false`, rows are dropped if any of the selected cells is `null`.
 * @param columns The [KProperties][KProperty] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNulls(vararg columns: KProperty<*>, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumnSet() }

/**
 * ## The Drop Nulls Operation
 *
 * Removes rows with `null` values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.DropNulls.DropNullsSelectingOptions]).
 *
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls)
 * ### This Drop Nulls Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * #### For example:
 *
 * `df.`[dropNulls][org.jetbrains.kotlinx.dataframe.api.dropNulls]`("length", "age")`
 *
 * `df.`[dropNulls][dropNulls]`("length", whereAllNull = true)`
 * @param whereAllNull `false` by default.
 *   If `true`, rows are dropped if all selected cells are `null`.
 *   If `false`, rows are dropped if any of the selected cells is `null`.
 * @param columns The [Strings][String] corresponding to the names of columns in this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNulls(vararg columns: String, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumnSet() }

/**
 * ## The Drop Nulls Operation
 *
 * Removes rows with `null` values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.DropNulls.DropNullsSelectingOptions]).
 *
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls)
 * ### This Drop Nulls Overload
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * #### For example:
 *
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `df.`[dropNulls][org.jetbrains.kotlinx.dataframe.api.dropNulls]`(length, age)`
 *
 * `df.`[dropNulls][dropNulls]`(length, whereAllNull = true)`
 * @param whereAllNull `false` by default.
 *   If `true`, rows are dropped if all selected cells are `null`.
 *   If `false`, rows are dropped if any of the selected cells is `null`.
 * @param columns The [Column References][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNulls(vararg columns: AnyColumnReference, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumnSet() }

/**
 * ## The Drop Nulls Operation
 *
 * Removes `null` values from this [DataColumn], adjusting the type accordingly.
 */
public fun <T> DataColumn<T?>.dropNulls(): DataColumn<T> =
    (if (!hasNulls()) this else filter { it != null }) as DataColumn<T>

// endregion

// region dropNA

/**
 * ## The Drop `NA` Operation
 *
 * Removes rows with [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.DropNA.DropNASelectingOptions]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA]. By default,
 * rows are dropped if any of the selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna)
 * ### This Drop NA Overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
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
 * `df.`[dropNA][org.jetbrains.kotlinx.dataframe.api.dropNA]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
 *
 * `df.`[dropNA][org.jetbrains.kotlinx.dataframe.api.dropNA]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * `df.`[dropNA][org.jetbrains.kotlinx.dataframe.api.dropNA]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 *
 * `df.`[dropNA][dropNA]`(whereAllNA = true) { `[colsOf][colsOf]`<`[Double][Double]`>() }`
 * @param whereAllNA `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *   If `false`, rows are dropped if any of the selected cells is [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 * @param columns The [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
@Refine
@Interpretable("DropNa0")
public fun <T> DataFrame<T>.dropNA(whereAllNA: Boolean = false, columns: ColumnsSelector<T, *>): DataFrame<T> {
    val cols = this[columns]
    return if (whereAllNA) {
        drop { cols.all { this[it].isNA } }
    } else {
        drop { cols.any { this[it].isNA } }
    }
}

/**
 * ## The Drop `NA` Operation
 *
 * Removes rows with [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.DropNA.DropNASelectingOptions]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA]. By default,
 * rows are dropped if any of the selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna)
 * ### This Drop NA Overload
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * #### For example:
 * ```kotlin
 * data class Person(val length: Double, val age: Double)
 * ```
 *
 * `df.`[dropNA][org.jetbrains.kotlinx.dataframe.api.dropNA]`(Person::length, Person::age)`
 *
 * `df.`[dropNA][dropNA]`(Person::length, whereAllNA = true)`
 * @param whereAllNA `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *   If `false`, rows are dropped if any of the selected cells is [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 * @param columns The [KProperties][KProperty] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNA(vararg columns: KProperty<*>, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumnSet() }

/**
 * ## The Drop `NA` Operation
 *
 * Removes rows with [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.DropNA.DropNASelectingOptions]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA]. By default,
 * rows are dropped if any of the selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna)
 * ### This Drop NA Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * #### For example:
 *
 * `df.`[dropNA][org.jetbrains.kotlinx.dataframe.api.dropNA]`("length", "age")`
 *
 * `df.`[dropNA][dropNA]`("length", whereAllNA = true)`
 * @param whereAllNA `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *   If `false`, rows are dropped if any of the selected cells is [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 * @param columns The [Strings][String] corresponding to the names of columns in this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNA(vararg columns: String, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumnSet() }

/**
 * ## The Drop `NA` Operation
 *
 * Removes rows with [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.DropNA.DropNASelectingOptions]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA]. By default,
 * rows are dropped if any of the selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna)
 * ### This Drop NA Overload
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * #### For example:
 *
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `df.`[dropNA][org.jetbrains.kotlinx.dataframe.api.dropNA]`(length, age)`
 *
 * `df.`[dropNA][dropNA]`(length, whereAllNA = true)`
 * @param whereAllNA `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *   If `false`, rows are dropped if any of the selected cells is [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 * @param columns The [Column References][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNA(vararg columns: AnyColumnReference, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumnSet() }

/**
 * ## The Drop `NA` Operation
 *
 * Removes rows with [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.DropNA.DropNASelectingOptions]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA]. By default,
 * rows are dropped if any of the selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna)
 * ### This Drop NA Overload
 * This overload operates on all columns in the [DataFrame].
 * @param whereAllNA `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *   If `false`, rows are dropped if any of the selected cells is [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 */
@Refine
@Interpretable("DropNa1")
public fun <T> DataFrame<T>.dropNA(whereAllNA: Boolean = false): DataFrame<T> = dropNA(whereAllNA) { all() }

/**
 * ## The Drop `NA` Operation
 *
 * Removes [`NA`][NA] values from this [DataColumn], adjusting the type accordingly.
 */
public fun <T> DataColumn<T?>.dropNA(): DataColumn<T> =
    when (typeClass) {
        Double::class, Float::class -> filter { !it.isNA }.cast()
        else -> (if (!hasNulls()) this else filter { it != null }) as DataColumn<T>
    }

// endregion

// region dropNaNs

/**
 * ## The Drop `NaN` Operation
 *
 * Removes rows with [`NaN`][Double.isNaN] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.DropNaNs.DropNaNsSelectingOptions]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [`NaN`][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [`NaN`][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans)
 * ### This Drop NaNs Overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
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
 * `df.`[dropNaNs][org.jetbrains.kotlinx.dataframe.api.dropNaNs]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
 *
 * `df.`[dropNaNs][org.jetbrains.kotlinx.dataframe.api.dropNaNs]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * `df.`[dropNaNs][org.jetbrains.kotlinx.dataframe.api.dropNaNs]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 *
 * `df.`[dropNaNs][dropNaNs]`(whereAllNaN = true) { `[colsOf][colsOf]`<`[Double][Double]`>() }`
 * @param whereAllNaN `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NaN`][Double.isNaN].
 *   If `false`, rows are dropped if any of the selected cells is [`NaN`][Double.isNaN].
 * @param columns The [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNaNs(whereAllNaN: Boolean = false, columns: ColumnsSelector<T, *>): DataFrame<T> {
    val cols = this[columns]
    return if (whereAllNaN) {
        drop { cols.all { this[it].isNaN } }
    } else {
        drop { cols.any { this[it].isNaN } }
    }
}

/**
 * ## The Drop `NaN` Operation
 *
 * Removes rows with [`NaN`][Double.isNaN] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.DropNaNs.DropNaNsSelectingOptions]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [`NaN`][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [`NaN`][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans)
 * ### This Drop NaNs Overload
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * #### For example:
 * ```kotlin
 * data class Person(val length: Double, val age: Double)
 * ```
 *
 * `df.`[dropNaNs][org.jetbrains.kotlinx.dataframe.api.dropNaNs]`(Person::length, Person::age)`
 *
 * `df.`[dropNaNs][dropNaNs]`(Person::length, whereAllNaN = true)`
 * @param whereAllNaN `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NaN`][Double.isNaN].
 *   If `false`, rows are dropped if any of the selected cells is [`NaN`][Double.isNaN].
 * @param columns The [KProperties][KProperty] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNaNs(vararg columns: KProperty<*>, whereAllNaN: Boolean = false): DataFrame<T> =
    dropNaNs(whereAllNaN) { columns.toColumnSet() }

/**
 * ## The Drop `NaN` Operation
 *
 * Removes rows with [`NaN`][Double.isNaN] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.DropNaNs.DropNaNsSelectingOptions]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [`NaN`][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [`NaN`][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans)
 * ### This Drop NaNs Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * #### For example:
 *
 * `df.`[dropNaNs][org.jetbrains.kotlinx.dataframe.api.dropNaNs]`("length", "age")`
 *
 * `df.`[dropNaNs][dropNaNs]`("length", whereAllNaN = true)`
 * @param whereAllNaN `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NaN`][Double.isNaN].
 *   If `false`, rows are dropped if any of the selected cells is [`NaN`][Double.isNaN].
 * @param columns The [Strings][String] corresponding to the names of columns in this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNaNs(vararg columns: String, whereAllNaN: Boolean = false): DataFrame<T> =
    dropNaNs(whereAllNaN) { columns.toColumnSet() }

/**
 * ## The Drop `NaN` Operation
 *
 * Removes rows with [`NaN`][Double.isNaN] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.DropNaNs.DropNaNsSelectingOptions]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [`NaN`][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [`NaN`][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans)
 * ### This Drop NaNs Overload
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * #### For example:
 *
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `df.`[dropNaNs][org.jetbrains.kotlinx.dataframe.api.dropNaNs]`(length, age)`
 *
 * `df.`[dropNaNs][dropNaNs]`(length, whereAllNaN = true)`
 * @param whereAllNaN `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NaN`][Double.isNaN].
 *   If `false`, rows are dropped if any of the selected cells is [`NaN`][Double.isNaN].
 * @param columns The [Column References][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNaNs(vararg columns: AnyColumnReference, whereAllNaN: Boolean = false): DataFrame<T> =
    dropNaNs(whereAllNaN) { columns.toColumnSet() }

/**
 * ## The Drop `NaN` Operation
 *
 * Removes rows with [`NaN`][Double.isNaN] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.DropNaNs.DropNaNsSelectingOptions]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [`NaN`][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [`NaN`][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans)
 * ### This Drop NaNs Overload
 * This overload operates on all columns in the [DataFrame].
 * @param whereAllNaN `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NaN`][Double.isNaN].
 *   If `false`, rows are dropped if any of the selected cells is [`NaN`][Double.isNaN].
 */
public fun <T> DataFrame<T>.dropNaNs(whereAllNaN: Boolean = false): DataFrame<T> = dropNaNs(whereAllNaN) { all() }

/**
 * ## The Drop `NaN` Operation
 *
 * Removes [`NaN`][NaN] values from this [DataColumn], adjusting the type accordingly.
 */
public fun <T> DataColumn<T>.dropNaNs(): DataColumn<T> =
    when (typeClass) {
        Double::class, Float::class -> filter { !it.isNaN }.cast()
        else -> this
    }

// endregion
