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
import org.jetbrains.kotlinx.dataframe.annotations.StringApiInterpretable
import org.jetbrains.kotlinx.dataframe.api.Update.UPDATE_OPERATION
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.NA
import org.jetbrains.kotlinx.dataframe.documentation.`NaN`
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
 * Specific case of [<code>update</code>][update].
 *
 * ### Check out: [<code>Grammar</code>][FillNulls.Grammar]
 *
 * For more information: [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls)
 */
internal interface FillNulls {

    /** ## [<code>**fillNulls**</code>][fillNulls] Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [<code>**fillNulls**</code>][fillNulls]**`  {  `**[<code>`columns`</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`where`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.where]**`  {  `**[<code>`rowValueCondition`</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.RowValueCondition.WithExample]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`at`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.at]**`(`**[<code>`rowIndices`</code>][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]**`)`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`notNull`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.notNull]**`()`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code>**`with`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.with]**`  {  `**[<code>`rowExpression`</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`notNull`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.notNull]**`  {  `**[<code>`rowExpression`</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`perCol`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.perCol]**`  {  `**[<code>`colExpression`</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`perRowCol`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol]**`  {  `**[<code>`rowColExpression`</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn.RowColumnExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`withNull`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.withNull]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`withZero`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.withZero]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`asFrame`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.asFrame]**`  {  `**[<code>`dataFrameExpression`</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpression.WithExample]**` }`**
     *
     */
    typealias Grammar = Nothing

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
     * <code>`df`</code>`.`[<code>fillNulls</code>][org.jetbrains.kotlinx.dataframe.api.fillNulls]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>fillNulls</code>][org.jetbrains.kotlinx.dataframe.api.fillNulls]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>fillNulls</code>][org.jetbrains.kotlinx.dataframe.api.fillNulls]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>fillNulls</code>][org.jetbrains.kotlinx.dataframe.api.fillNulls]`("length", "age")`
     *
     *
     *
     */
    typealias FillNullsSelectingOptions = Nothing
}

private typealias SetFillNullsOperationArg = Nothing

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FillNulls.Grammar]
 *
 * For more information: [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FillNulls.FillNullsSelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill Nulls Overload
 *
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
 * <code>`df`</code>`.`[<code>fillNulls</code>][org.jetbrains.kotlinx.dataframe.api.fillNulls]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>fillNulls</code>][org.jetbrains.kotlinx.dataframe.api.fillNulls]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>fillNulls</code>][org.jetbrains.kotlinx.dataframe.api.fillNulls]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 * @param [columns] The [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Interpretable("FillNulls0")
public fun <T, C> DataFrame<T>.fillNulls(columns: ColumnsSelector<T, C?>): Update<T, C?> =
    update(columns).where { it == null }

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FillNulls.Grammar]
 *
 * For more information: [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FillNulls.FillNullsSelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill Nulls Overload
 *
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>fillNulls</code>][org.jetbrains.kotlinx.dataframe.api.fillNulls]`("length", "age")`
 *
 *
 *
 * @param [columns] The [<code>Strings</code>][String] corresponding to the names of columns belonging to this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@StringApiInterpretable(interpreter = "FillNulls0", stringArgument = "columns", targetArgument = "columns")
public fun <T> DataFrame<T>.fillNulls(vararg columns: String): Update<T, Any?> = fillNulls { columns.toColumnSet() }

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FillNulls.Grammar]
 *
 * For more information: [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FillNulls.FillNullsSelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill Nulls Overload
 *
 *
 * @param [columns] The [<code>KProperties</code>][KProperty] corresponding to columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.fillNulls(vararg columns: KProperty<C>): Update<T, C?> =
    fillNulls { columns.toColumnSet() }

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FillNulls.Grammar]
 *
 * For more information: [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FillNulls.FillNullsSelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill Nulls Overload
 *
 *
 * @param [columns] The [<code>Column References</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.fillNulls(vararg columns: ColumnReference<C>): Update<T, C?> =
    fillNulls { columns.toColumnSet() }

// endregion

/** Is only `true` if [<code>this</code>][this] is [<code>Double.NaN</code>][Double.NaN] or [<code>Float.NaN</code>][Float.NaN]. */
internal inline val Any?.isNaN: Boolean get() = (this is Double && isNaN()) || (this is Float && isNaN())

/**
 * Returns `true` if [<code>this</code>][this] is considered NA.
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
 * Is `true` if [<code>this</code>][this] is considered NA.
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
 * Is `true` when [<code>this</code>][this] column can have [<code>`NA`</code>][`NA`] values.
 * @see NA
 */
internal inline val AnyCol.canHaveNA: Boolean get() = hasNulls() || canHaveNaN || kind() != ColumnKind.Value

/**
 * Is `true` when [<code>this</code>][this] is `null` or [<code>Double.NaN</code>][Double.NaN].
 * @see NA
 */
internal inline val Double?.isNA: Boolean get() = this == null || this.isNaN()

/**
 * Is `true` when [<code>this</code>][this] is `null` or [<code>Float.NaN</code>][Float.NaN].
 * @see NA
 */
internal inline val Float?.isNA: Boolean get() = this == null || this.isNaN()

// region fillNaNs

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [<code>`NaN`</code>][NaN] values with given value or expression.
 * Specific case of [<code>update</code>][update].
 *
 * ### Check out: [<code>Grammar</code>][FillNaNs.Grammar]
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans)
 */
internal interface FillNaNs {

    /** ## [<code>fillNaNs</code>][fillNaNs] Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [<code>fillNaNs</code>][fillNaNs]**`  {  `**[<code>`columns`</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`where`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.where]**`  {  `**[<code>`rowValueCondition`</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.RowValueCondition.WithExample]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`at`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.at]**`(`**[<code>`rowIndices`</code>][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]**`)`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`notNull`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.notNull]**`()`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code>**`with`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.with]**`  {  `**[<code>`rowExpression`</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`notNull`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.notNull]**`  {  `**[<code>`rowExpression`</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`perCol`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.perCol]**`  {  `**[<code>`colExpression`</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`perRowCol`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol]**`  {  `**[<code>`rowColExpression`</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn.RowColumnExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`withNull`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.withNull]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`withZero`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.withZero]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`asFrame`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.asFrame]**`  {  `**[<code>`dataFrameExpression`</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpression.WithExample]**` }`**
     *
     */
    typealias Grammar = Nothing

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
     * <code>`df`</code>`.`[<code>fillNaNs</code>][org.jetbrains.kotlinx.dataframe.api.fillNaNs]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>fillNaNs</code>][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>fillNaNs</code>][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>fillNaNs</code>][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`("length", "age")`
     *
     *
     *
     */
    typealias FillNaNsSelectingOptions = Nothing
}

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values with given value or expression.
 * Specific case of [<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FillNaNs.Grammar]
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FillNaNs.FillNaNsSelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill NaNs Overload
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
 * <code>`df`</code>`.`[<code>fillNaNs</code>][org.jetbrains.kotlinx.dataframe.api.fillNaNs]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>fillNaNs</code>][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>fillNaNs</code>][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 * @param [columns] The [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Interpretable("FillNaNs0")
public fun <T, C> DataFrame<T>.fillNaNs(columns: ColumnsSelector<T, C>): Update<T, C> =
    update(columns).where { it.isNaN }

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values with given value or expression.
 * Specific case of [<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FillNaNs.Grammar]
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FillNaNs.FillNaNsSelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill NaNs Overload
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>fillNaNs</code>][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`("length", "age")`
 *
 *
 *
 * @param [columns] The [<code>Strings</code>][String] corresponding to the names of columns belonging to this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@StringApiInterpretable("FillNaNs0", stringArgument = "columns", targetArgument = "columns")
public fun <T> DataFrame<T>.fillNaNs(vararg columns: String): Update<T, Any?> = fillNaNs { columns.toColumnSet() }

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values with given value or expression.
 * Specific case of [<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FillNaNs.Grammar]
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FillNaNs.FillNaNsSelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill NaNs Overload
 *
 * @param [columns] The [<code>KProperties</code>][KProperty] corresponding to columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.fillNaNs(vararg columns: KProperty<C>): Update<T, C> = fillNaNs { columns.toColumnSet() }

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [<code>`NaN`</code>][org.jetbrains.kotlinx.dataframe.documentation.NaN] values with given value or expression.
 * Specific case of [<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FillNaNs.Grammar]
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FillNaNs.FillNaNsSelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill NaNs Overload
 *
 * @param [columns] The [<code>Column References</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
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
 * Replaces [<code>`NA`</code>][NA] values with given value or expression.
 * Specific case of [<code>update</code>][update].
 *
 * ### Check out: [<code>Grammar</code>][FillNA.Grammar]
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna)
 */
internal interface FillNA {

    /** ## [<code>fillNA</code>][fillNA] Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [<code>fillNA</code>][fillNA]**`  {  `**[<code>`columns`</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`where`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.where]**`  {  `**[<code>`rowValueCondition`</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.RowValueCondition.WithExample]**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`at`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.at]**`(`**[<code>`rowIndices`</code>][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]**`)`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `[ `__`.`__[<code>**`notNull`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.notNull]**`()`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code>**`with`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.with]**`  {  `**[<code>`rowExpression`</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`notNull`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.notNull]**`  {  `**[<code>`rowExpression`</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`perCol`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.perCol]**`  {  `**[<code>`colExpression`</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`perRowCol`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol]**`  {  `**[<code>`rowColExpression`</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn.RowColumnExpression.WithExample]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`withNull`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.withNull]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`withZero`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.withZero]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`asFrame`**</code>][org.jetbrains.kotlinx.dataframe.api.Update.asFrame]**`  {  `**[<code>`dataFrameExpression`</code>][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpression.WithExample]**` }`**
     *
     */
    typealias Grammar = Nothing

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
     * <code>`df`</code>`.`[<code>fillNA</code>][org.jetbrains.kotlinx.dataframe.api.fillNA]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>fillNA</code>][org.jetbrains.kotlinx.dataframe.api.fillNA]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>fillNA</code>][org.jetbrains.kotlinx.dataframe.api.fillNA]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>fillNA</code>][org.jetbrains.kotlinx.dataframe.api.fillNA]`("length", "age")`
     *
     *
     *
     */
    typealias FillNASelectingOptions = Nothing
}

/**
 * ## The Fill NA Operation
 *
 * Replaces [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values with given value or expression.
 * Specific case of [<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FillNA.Grammar]
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FillNA.FillNASelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill NA Overload
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
 * <code>`df`</code>`.`[<code>fillNA</code>][org.jetbrains.kotlinx.dataframe.api.fillNA]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>fillNA</code>][org.jetbrains.kotlinx.dataframe.api.fillNA]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>fillNA</code>][org.jetbrains.kotlinx.dataframe.api.fillNA]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 * @param [columns] The [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Interpretable("FillNulls0") // fillNA changes schema same as fillNulls
public fun <T, C> DataFrame<T>.fillNA(columns: ColumnsSelector<T, C?>): Update<T, C?> =
    update(columns).where { it.isNA }

/**
 * ## The Fill NA Operation
 *
 * Replaces [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values with given value or expression.
 * Specific case of [<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FillNA.Grammar]
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FillNA.FillNASelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill NA Overload
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>fillNA</code>][org.jetbrains.kotlinx.dataframe.api.fillNA]`("length", "age")`
 *
 *
 *
 * @param [columns] The [<code>Strings</code>][String] corresponding to the names of columns belonging to this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@StringApiInterpretable(interpreter = "FillNulls0", stringArgument = "columns", targetArgument = "columns")
public fun <T> DataFrame<T>.fillNA(vararg columns: String): Update<T, Any?> = fillNA { columns.toColumnSet() }

/**
 * ## The Fill NA Operation
 *
 * Replaces [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values with given value or expression.
 * Specific case of [<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FillNA.Grammar]
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FillNA.FillNASelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill NA Overload
 *
 * @param [columns] The [<code>KProperties</code>][KProperty] corresponding to columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.fillNA(vararg columns: KProperty<C>): Update<T, C?> = fillNA { columns.toColumnSet() }

/**
 * ## The Fill NA Operation
 *
 * Replaces [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values with given value or expression.
 * Specific case of [<code>update</code>][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FillNA.Grammar]
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna)
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * The columns to update need to be selected. See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.FillNA.FillNASelectingOptions]
 * for all the selecting options.
 *
 * ### This Fill NA Overload
 *
 * @param [columns] The [<code>Column References</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
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
 * Removes rows with `null` values. Specific case of [<code>drop</code>][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.DropNulls.DropNullsSelectingOptions]).
 *
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls)
 * ### This Drop Nulls Overload
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
 * <code>`df`</code>`.`[<code>dropNulls</code>][org.jetbrains.kotlinx.dataframe.api.dropNulls]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>dropNulls</code>][org.jetbrains.kotlinx.dataframe.api.dropNulls]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>dropNulls</code>][org.jetbrains.kotlinx.dataframe.api.dropNulls]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 * `df.`[<code>dropNulls</code>][dropNulls]`(whereAllNull = true) { `[<code>colsOf</code>][colsOf]`<`[<code>Double</code>][Double]`>() }`
 * @param whereAllNull `false` by default.
 *   If `true`, rows are dropped if all selected cells are `null`.
 *   If `false`, rows are dropped if any of the selected cells is `null`.
 * @param columns The [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
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

@Refine
@Interpretable("DropNulls0")
@Deprecated(
    "DataFrame conventional name for filterNot* functions is drop*",
    ReplaceWith("dropNulls(columns = columns)"),
    DeprecationLevel.ERROR,
)
public fun <T> DataFrame<T>.filterNotNull(columns: ColumnsSelector<T, *>): DataFrame<T> = dropNulls(columns = columns)

/**
 * ## The Drop Nulls Operation
 *
 * Removes rows with `null` values. Specific case of [<code>drop</code>][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.DropNulls.DropNullsSelectingOptions]).
 *
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls)
 * ### This Drop Nulls Overload
 * This overload operates on all columns in the [<code>DataFrame</code>][DataFrame].
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
 * Removes rows with `null` values. Specific case of [<code>drop</code>][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.DropNulls.DropNullsSelectingOptions]).
 *
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls)
 * ### This Drop Nulls Overload
 *
 * `df.`[<code>dropNulls</code>][dropNulls]`(Person::length, whereAllNull = true)`
 * @param whereAllNull `false` by default.
 *   If `true`, rows are dropped if all selected cells are `null`.
 *   If `false`, rows are dropped if any of the selected cells is `null`.
 * @param columns The [<code>KProperties</code>][KProperty] used to select the columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNulls(vararg columns: KProperty<*>, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumnSet() }

/**
 * ## The Drop Nulls Operation
 *
 * Removes rows with `null` values. Specific case of [<code>drop</code>][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.DropNulls.DropNullsSelectingOptions]).
 *
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls)
 * ### This Drop Nulls Overload
 *
 * `df.`[<code>dropNulls</code>][dropNulls]`("length", whereAllNull = true)`
 * @param whereAllNull `false` by default.
 *   If `true`, rows are dropped if all selected cells are `null`.
 *   If `false`, rows are dropped if any of the selected cells is `null`.
 * @param columns The [<code>Strings</code>][String] corresponding to the names of columns in this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
@Refine
@StringApiInterpretable(interpreter = "DropNulls0", stringArgument = "columns", targetArgument = "columns")
public fun <T> DataFrame<T>.dropNulls(vararg columns: String, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumnSet() }

/**
 * ## The Drop Nulls Operation
 *
 * Removes rows with `null` values. Specific case of [<code>drop</code>][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.DropNulls.DropNullsSelectingOptions]).
 *
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls)
 * ### This Drop Nulls Overload
 *
 * `df.`[<code>dropNulls</code>][dropNulls]`(length, whereAllNull = true)`
 * @param whereAllNull `false` by default.
 *   If `true`, rows are dropped if all selected cells are `null`.
 *   If `false`, rows are dropped if any of the selected cells is `null`.
 * @param columns The [<code>Column References</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] used to select the columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNulls(vararg columns: AnyColumnReference, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumnSet() }

/**
 * ## The Drop Nulls Operation
 *
 * Removes `null` values from this [<code>DataColumn</code>][DataColumn], adjusting the type accordingly.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls)
 */
public fun <T> DataColumn<T?>.dropNulls(): DataColumn<T> =
    (if (!hasNulls()) this else filter { it != null }) as DataColumn<T>

// endregion

// region dropNA

/**
 * ## The Drop `NA` Operation
 *
 * Removes rows with [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values. Specific case of [<code>drop</code>][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.DropNA.DropNASelectingOptions]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA]. By default,
 * rows are dropped if any of the selected cells are [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna)
 * ### This Drop NA Overload
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
 * <code>`df`</code>`.`[<code>dropNA</code>][org.jetbrains.kotlinx.dataframe.api.dropNA]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>dropNA</code>][org.jetbrains.kotlinx.dataframe.api.dropNA]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>dropNA</code>][org.jetbrains.kotlinx.dataframe.api.dropNA]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 * `df.`[<code>dropNA</code>][dropNA]`(whereAllNA = true) { `[<code>colsOf</code>][colsOf]`<`[<code>Double</code>][Double]`>() }`
 * @param whereAllNA `false` by default.
 *   If `true`, rows are dropped if all selected cells are [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *   If `false`, rows are dropped if any of the selected cells is [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 * @param columns The [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
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
 * Removes rows with [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values. Specific case of [<code>drop</code>][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.DropNA.DropNASelectingOptions]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA]. By default,
 * rows are dropped if any of the selected cells are [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna)
 * ### This Drop NA Overload
 *
 * `df.`[<code>dropNA</code>][dropNA]`(Person::length, whereAllNA = true)`
 * @param whereAllNA `false` by default.
 *   If `true`, rows are dropped if all selected cells are [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *   If `false`, rows are dropped if any of the selected cells is [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 * @param columns The [<code>KProperties</code>][KProperty] used to select the columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNA(vararg columns: KProperty<*>, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumnSet() }

/**
 * ## The Drop `NA` Operation
 *
 * Removes rows with [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values. Specific case of [<code>drop</code>][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.DropNA.DropNASelectingOptions]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA]. By default,
 * rows are dropped if any of the selected cells are [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna)
 * ### This Drop NA Overload
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>dropNA</code>][org.jetbrains.kotlinx.dataframe.api.dropNA]`("length", "age")`
 *
 *
 *
 * `df.`[<code>dropNA</code>][dropNA]`("length", whereAllNA = true)`
 * @param whereAllNA `false` by default.
 *   If `true`, rows are dropped if all selected cells are [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *   If `false`, rows are dropped if any of the selected cells is [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 * @param columns The [<code>Strings</code>][String] corresponding to the names of columns in this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
@Refine
@StringApiInterpretable(interpreter = "DropNa0", stringArgument = "columns", targetArgument = "columns")
public fun <T> DataFrame<T>.dropNA(vararg columns: String, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumnSet() }

/**
 * ## The Drop `NA` Operation
 *
 * Removes rows with [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values. Specific case of [<code>drop</code>][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.DropNA.DropNASelectingOptions]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA]. By default,
 * rows are dropped if any of the selected cells are [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna)
 * ### This Drop NA Overload
 *
 * `df.`[<code>dropNA</code>][dropNA]`(length, whereAllNA = true)`
 * @param whereAllNA `false` by default.
 *   If `true`, rows are dropped if all selected cells are [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *   If `false`, rows are dropped if any of the selected cells is [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 * @param columns The [<code>Column References</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] used to select the columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNA(vararg columns: AnyColumnReference, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumnSet() }

/**
 * ## The Drop `NA` Operation
 *
 * Removes rows with [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values. Specific case of [<code>drop</code>][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.DropNA.DropNASelectingOptions]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA]. By default,
 * rows are dropped if any of the selected cells are [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna)
 * ### This Drop NA Overload
 * This overload operates on all columns in the [<code>DataFrame</code>][DataFrame].
 * @param whereAllNA `false` by default.
 *   If `true`, rows are dropped if all selected cells are [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *   If `false`, rows are dropped if any of the selected cells is [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 */
@Refine
@Interpretable("DropNa1")
public fun <T> DataFrame<T>.dropNA(whereAllNA: Boolean = false): DataFrame<T> = dropNA(whereAllNA) { all() }

/**
 * ## The Drop `NA` Operation
 *
 * Removes [<code>`NA`</code>][NA] values from this [<code>DataColumn</code>][DataColumn], adjusting the type accordingly.
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna)
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
 * Removes rows with [<code>`NaN`</code>][Double.isNaN] values. Specific case of [<code>drop</code>][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.DropNaNs.DropNaNsSelectingOptions]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [<code>`NaN`</code>][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [<code>`NaN`</code>][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans)
 * ### This Drop NaNs Overload
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
 * <code>`df`</code>`.`[<code>dropNaNs</code>][org.jetbrains.kotlinx.dataframe.api.dropNaNs]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>dropNaNs</code>][org.jetbrains.kotlinx.dataframe.api.dropNaNs]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>dropNaNs</code>][org.jetbrains.kotlinx.dataframe.api.dropNaNs]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 * `df.`[<code>dropNaNs</code>][dropNaNs]`(whereAllNaN = true) { `[<code>colsOf</code>][colsOf]`<`[<code>Double</code>][Double]`>() }`
 * @param whereAllNaN `false` by default.
 *   If `true`, rows are dropped if all selected cells are [<code>`NaN`</code>][Double.isNaN].
 *   If `false`, rows are dropped if any of the selected cells is [<code>`NaN`</code>][Double.isNaN].
 * @param columns The [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
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
 * Removes rows with [<code>`NaN`</code>][Double.isNaN] values. Specific case of [<code>drop</code>][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.DropNaNs.DropNaNsSelectingOptions]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [<code>`NaN`</code>][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [<code>`NaN`</code>][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans)
 * ### This Drop NaNs Overload
 *
 * `df.`[<code>dropNaNs</code>][dropNaNs]`(Person::length, whereAllNaN = true)`
 * @param whereAllNaN `false` by default.
 *   If `true`, rows are dropped if all selected cells are [<code>`NaN`</code>][Double.isNaN].
 *   If `false`, rows are dropped if any of the selected cells is [<code>`NaN`</code>][Double.isNaN].
 * @param columns The [<code>KProperties</code>][KProperty] used to select the columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNaNs(vararg columns: KProperty<*>, whereAllNaN: Boolean = false): DataFrame<T> =
    dropNaNs(whereAllNaN) { columns.toColumnSet() }

/**
 * ## The Drop `NaN` Operation
 *
 * Removes rows with [<code>`NaN`</code>][Double.isNaN] values. Specific case of [<code>drop</code>][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.DropNaNs.DropNaNsSelectingOptions]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [<code>`NaN`</code>][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [<code>`NaN`</code>][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans)
 * ### This Drop NaNs Overload
 *
 * `df.`[<code>dropNaNs</code>][dropNaNs]`("length", whereAllNaN = true)`
 * @param whereAllNaN `false` by default.
 *   If `true`, rows are dropped if all selected cells are [<code>`NaN`</code>][Double.isNaN].
 *   If `false`, rows are dropped if any of the selected cells is [<code>`NaN`</code>][Double.isNaN].
 * @param columns The [<code>Strings</code>][String] corresponding to the names of columns in this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNaNs(vararg columns: String, whereAllNaN: Boolean = false): DataFrame<T> =
    dropNaNs(whereAllNaN) { columns.toColumnSet() }

/**
 * ## The Drop `NaN` Operation
 *
 * Removes rows with [<code>`NaN`</code>][Double.isNaN] values. Specific case of [<code>drop</code>][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.DropNaNs.DropNaNsSelectingOptions]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [<code>`NaN`</code>][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [<code>`NaN`</code>][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans)
 * ### This Drop NaNs Overload
 *
 * `df.`[<code>dropNaNs</code>][dropNaNs]`(length, whereAllNaN = true)`
 * @param whereAllNaN `false` by default.
 *   If `true`, rows are dropped if all selected cells are [<code>`NaN`</code>][Double.isNaN].
 *   If `false`, rows are dropped if any of the selected cells is [<code>`NaN`</code>][Double.isNaN].
 * @param columns The [<code>Column References</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] used to select the columns of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.dropNaNs(vararg columns: AnyColumnReference, whereAllNaN: Boolean = false): DataFrame<T> =
    dropNaNs(whereAllNaN) { columns.toColumnSet() }

/**
 * ## The Drop `NaN` Operation
 *
 * Removes rows with [<code>`NaN`</code>][Double.isNaN] values. Specific case of [<code>drop</code>][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.DropNaNs.DropNaNsSelectingOptions]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [<code>`NaN`</code>][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [<code>`NaN`</code>][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans)
 * ### This Drop NaNs Overload
 * This overload operates on all columns in the [<code>DataFrame</code>][DataFrame].
 * @param whereAllNaN `false` by default.
 *   If `true`, rows are dropped if all selected cells are [<code>`NaN`</code>][Double.isNaN].
 *   If `false`, rows are dropped if any of the selected cells is [<code>`NaN`</code>][Double.isNaN].
 */
public fun <T> DataFrame<T>.dropNaNs(whereAllNaN: Boolean = false): DataFrame<T> = dropNaNs(whereAllNaN) { all() }

/**
 * ## The Drop `NaN` Operation
 *
 * Removes [<code>`NaN`</code>][NaN] values from this [<code>DataColumn</code>][DataColumn], adjusting the type accordingly.
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans)
 */
public fun <T> DataColumn<T>.dropNaNs(): DataColumn<T> =
    when (typeClass) {
        Double::class, Float::class -> filter { !it.isNaN }.cast()
        else -> this
    }

// endregion
