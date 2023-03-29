package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.Update.UpdateOperationArg
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.*
import org.jetbrains.kotlinx.dataframe.util.ITERABLE_COLUMNS_DEPRECATION_MESSAGE
import kotlin.reflect.KProperty

// region fillNulls

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [update].
 *
 * Check out the [`fillNulls` Operation Usage][FillNulls.Usage].
 *
 * For more information: [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls)
 */
internal interface FillNulls {

    /** ## [fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls] Operation Usage
     *
     * [fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls] `{ `[columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]` }`
     *
     * - `[.`[where][org.jetbrains.kotlinx.dataframe.api.Update.where]` { `[rowValueCondition][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.RowValueCondition.WithExample]` } ]`
     *
     * - `[.`[at][org.jetbrains.kotlinx.dataframe.api.Update.at]` (`[rowIndices][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]`) ]`
     *
     * - `.`[with][org.jetbrains.kotlinx.dataframe.api.Update.with]` { `[rowExpression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]` }
     *   | .`[notNull][org.jetbrains.kotlinx.dataframe.api.Update.notNull]` { `[rowExpression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]` }
     *   | .`[perCol][org.jetbrains.kotlinx.dataframe.api.Update.perCol]` { `[colExpression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpression.WithExample]` }
     *   | .`[perRowCol][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol]` { `[rowColExpression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn.RowColumnExpression.WithExample]` }
     *   | .`[withValue][org.jetbrains.kotlinx.dataframe.api.Update.withValue]`(value)
     *   | .`[withNull][org.jetbrains.kotlinx.dataframe.api.Update.withNull]`()
     *   | .`[withZero][org.jetbrains.kotlinx.dataframe.api.Update.withZero]`()
     *   | .`[asFrame][org.jetbrains.kotlinx.dataframe.api.Update.asFrame]` { `[dataFrameExpression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpression.WithExample]` }`
     */
    interface Usage
}

private interface SetFillNullsOperationArg

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNulls` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNulls.Usage].
 *
 * For more information: [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 *
 * ## This Fill Nulls Overload
 *
 */
private interface CommonFillNullsFunctionDoc

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNulls` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNulls.Usage].
 *
 * For more information: [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 *
 * ## This Fill Nulls Overload
 *
 * Select or express columns using the Column(s) Selection DSL.
 * (Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL comes in the form of either a [Column Selector][org.jetbrains.kotlinx.dataframe.ColumnSelector]- or [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operate in the [Column Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl] or the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expect you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], respectively.
 *
 * For example:
 *
 * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]` { length `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` age }`
 *
 * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Double][Double]`>() }`
 *  
 * @param columns The [Columns selector DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl.WithExample] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.fillNulls(columns: ColumnsSelector<T, C?>): Update<T, C?> =
    update(columns).where { it == null }

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNulls` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNulls.Usage].
 *
 * For more information: [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 *
 * ## This Fill Nulls Overload
 *
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * For example:
 *
 * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]`("length", "age")`
 *  
 * @param columns The [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample] belonging to this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T> DataFrame<T>.fillNulls(vararg columns: String): Update<T, Any?> =
    fillNulls { columns.toColumnSet() }

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNulls` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNulls.Usage].
 *
 * For more information: [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 *
 * ## This Fill Nulls Overload
 *
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * For example:
 * ```kotlin
 * data class Person(val length: Double, val age: Double)
 * ```
 *
 * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]`(Person::length, Person::age)`
 *  
 * @param columns The [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample] corresponding to columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.fillNulls(vararg columns: KProperty<C>): Update<T, C?> =
    fillNulls { columns.toColumnSet() }

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNulls` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNulls.Usage].
 *
 * For more information: [See `fillNulls` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnulls) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 *
 * ## This Fill Nulls Overload
 *
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * For example:
 *
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]`(length, age)`
 *  
 * @param columns The [Column references][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnAccessors.WithExample] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.fillNulls(vararg columns: ColumnReference<C>): Update<T, C?> =
    fillNulls { columns.toColumnSet() }

@Deprecated(
    message = ITERABLE_COLUMNS_DEPRECATION_MESSAGE,
    replaceWith = ReplaceWith(
        "fillNulls { columns.toColumnSet() }",
        "org.jetbrains.kotlinx.dataframe.columns.toColumnSet",
    ),
    level = DeprecationLevel.ERROR,
)
public fun <T, C> DataFrame<T>.fillNulls(columns: Iterable<ColumnReference<C>>): Update<T, C?> =
    fillNulls { columns.toColumnSet() }

// endregion

internal inline val Any?.isNaN: Boolean get() = (this is Double && isNaN()) || (this is Float && isNaN())

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

internal inline val AnyCol.canHaveNA: Boolean get() = hasNulls() || canHaveNaN || kind != ColumnKind.Value

internal inline val Double?.isNA: Boolean get() = this == null || this.isNaN()

internal inline val Float?.isNA: Boolean get() = this == null || this.isNaN()

// region fillNaNs

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [`NaN`][NaN] values with given value or expression.
 * Specific case of [update].
 *
 * Check out the [`fillNaNs` Operation Usage][FillNaNs.Usage].
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans)
 */
internal interface FillNaNs {

    /** ## [fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs] Operation Usage
     *
     * [fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs] `{ `[columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]` }`
     *
     * - `[.`[where][org.jetbrains.kotlinx.dataframe.api.Update.where]` { `[rowValueCondition][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.RowValueCondition.WithExample]` } ]`
     *
     * - `[.`[at][org.jetbrains.kotlinx.dataframe.api.Update.at]` (`[rowIndices][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]`) ]`
     *
     * - `.`[with][org.jetbrains.kotlinx.dataframe.api.Update.with]` { `[rowExpression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]` }
     *   | .`[notNull][org.jetbrains.kotlinx.dataframe.api.Update.notNull]` { `[rowExpression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]` }
     *   | .`[perCol][org.jetbrains.kotlinx.dataframe.api.Update.perCol]` { `[colExpression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpression.WithExample]` }
     *   | .`[perRowCol][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol]` { `[rowColExpression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn.RowColumnExpression.WithExample]` }
     *   | .`[withValue][org.jetbrains.kotlinx.dataframe.api.Update.withValue]`(value)
     *   | .`[withNull][org.jetbrains.kotlinx.dataframe.api.Update.withNull]`()
     *   | .`[withZero][org.jetbrains.kotlinx.dataframe.api.Update.withZero]`()
     *   | .`[asFrame][org.jetbrains.kotlinx.dataframe.api.Update.asFrame]` { `[dataFrameExpression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpression.WithExample]` }`
     */
    interface Usage
}

internal interface SetFillNaNsOperationArg

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNaNs` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNaNs.Usage].
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Fill NaNs Overload
 */
private interface CommonFillNaNsFunctionDoc

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNaNs` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNaNs.Usage].
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Fill NaNs Overload
 * Select or express columns using the Column(s) Selection DSL.
 * (Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL comes in the form of either a [Column Selector][org.jetbrains.kotlinx.dataframe.ColumnSelector]- or [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operate in the [Column Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl] or the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expect you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], respectively.
 *
 * For example:
 *
 * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]` { length `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` age }`
 *
 * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Double][Double]`>() }`
 *  
 * @param columns The [Columns selector DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl.WithExample] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.fillNaNs(columns: ColumnsSelector<T, C>): Update<T, C> =
    update(columns).where { it.isNaN }

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNaNs` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNaNs.Usage].
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Fill NaNs Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * For example:
 *
 * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`("length", "age")`
 *  
 * @param columns The [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample] belonging to this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T> DataFrame<T>.fillNaNs(vararg columns: String): Update<T, Any?> =
    fillNaNs { columns.toColumnSet() }

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNaNs` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNaNs.Usage].
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Fill NaNs Overload
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * For example:
 * ```kotlin
 * data class Person(val length: Double, val age: Double)
 * ```
 *
 * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`(Person::length, Person::age)`
 *  
 * @param columns The [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample] corresponding to columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.fillNaNs(vararg columns: KProperty<C>): Update<T, C> =
    fillNaNs { columns.toColumnSet() }

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [`NaN`][org.jetbrains.kotlinx.dataframe.documentation.NaN] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNaNs` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNaNs.Usage].
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Fill NaNs Overload
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * For example:
 *
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`(length, age)`
 *  
 * @param columns The [Column references][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnAccessors.WithExample] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.fillNaNs(vararg columns: ColumnReference<C>): Update<T, C> =
    fillNaNs { columns.toColumnSet() }

@Deprecated(
    message = ITERABLE_COLUMNS_DEPRECATION_MESSAGE,
    replaceWith = ReplaceWith(
        "fillNaNs { columns.toColumnSet() }",
        "org.jetbrains.kotlinx.dataframe.columns.toColumnSet",
    ),
    level = DeprecationLevel.ERROR,
)
public fun <T, C> DataFrame<T>.fillNaNs(columns: Iterable<ColumnReference<C>>): Update<T, C> =
    fillNaNs { columns.toColumnSet() }

// endregion

// region fillNA

/**
 * ## The Fill NA Operation
 *
 * Replaces [`NA`][NA] values with given value or expression.
 * Specific case of [update].
 *
 * Check out the [`fillNA` Operation Usage][FillNA.Usage].
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna)
 */
internal interface FillNA {

    /** ## [fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA] Operation Usage
     *
     * [fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA] `{ `[columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]` }`
     *
     * - `[.`[where][org.jetbrains.kotlinx.dataframe.api.Update.where]` { `[rowValueCondition][org.jetbrains.kotlinx.dataframe.documentation.SelectingRows.RowValueCondition.WithExample]` } ]`
     *
     * - `[.`[at][org.jetbrains.kotlinx.dataframe.api.Update.at]` (`[rowIndices][org.jetbrains.kotlinx.dataframe.api.CommonUpdateAtFunctionDoc.RowIndicesParam]`) ]`
     *
     * - `.`[with][org.jetbrains.kotlinx.dataframe.api.Update.with]` { `[rowExpression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]` }
     *   | .`[notNull][org.jetbrains.kotlinx.dataframe.api.Update.notNull]` { `[rowExpression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow.RowValueExpression.WithExample]` }
     *   | .`[perCol][org.jetbrains.kotlinx.dataframe.api.Update.perCol]` { `[colExpression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenColumn.ColumnExpression.WithExample]` }
     *   | .`[perRowCol][org.jetbrains.kotlinx.dataframe.api.Update.perRowCol]` { `[rowColExpression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRowAndColumn.RowColumnExpression.WithExample]` }
     *   | .`[withValue][org.jetbrains.kotlinx.dataframe.api.Update.withValue]`(value)
     *   | .`[withNull][org.jetbrains.kotlinx.dataframe.api.Update.withNull]`()
     *   | .`[withZero][org.jetbrains.kotlinx.dataframe.api.Update.withZero]`()
     *   | .`[asFrame][org.jetbrains.kotlinx.dataframe.api.Update.asFrame]` { `[dataFrameExpression][org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenDataFrame.DataFrameExpression.WithExample]` }`
     */
    interface Usage
}

internal interface SetFillNAOperationArg

/**
 * ## The Fill NA Operation
 *
 * Replaces [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNA` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNA.Usage].
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Fill NA Overload
 */
private interface CommonFillNAFunctionDoc

/**
 * ## The Fill NA Operation
 *
 * Replaces [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNA` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNA.Usage].
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Fill NA Overload
 * Select or express columns using the Column(s) Selection DSL.
 * (Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL comes in the form of either a [Column Selector][org.jetbrains.kotlinx.dataframe.ColumnSelector]- or [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operate in the [Column Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl] or the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expect you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], respectively.
 *
 * For example:
 *
 * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]` { length `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` age }`
 *
 * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Double][Double]`>() }`
 *  
 * @param columns The [Columns selector DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl.WithExample] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.fillNA(columns: ColumnsSelector<T, C?>): Update<T, C?> =
    update(columns).where { it.isNA }

/**
 * ## The Fill NA Operation
 *
 * Replaces [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNA` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNA.Usage].
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Fill NA Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * For example:
 *
 * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]`("length", "age")`
 *  
 * @param columns The [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample] belonging to this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T> DataFrame<T>.fillNA(vararg columns: String): Update<T, Any?> =
    fillNA { columns.toColumnSet() }

/**
 * ## The Fill NA Operation
 *
 * Replaces [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNA` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNA.Usage].
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Fill NA Overload
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * For example:
 * ```kotlin
 * data class Person(val length: Double, val age: Double)
 * ```
 *
 * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]`(Person::length, Person::age)`
 *  
 * @param columns The [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample] corresponding to columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.fillNA(vararg columns: KProperty<C>): Update<T, C?> =
    fillNA { columns.toColumnSet() }

/**
 * ## The Fill NA Operation
 *
 * Replaces [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNA` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNA.Usage].
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Fill NA Overload
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * For example:
 *
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]`(length, age)`
 *  
 * @param columns The [Column references][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnAccessors.WithExample] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.fillNA(vararg columns: ColumnReference<C>): Update<T, C?> =
    fillNA { columns.toColumnSet() }

@Deprecated(
    message = ITERABLE_COLUMNS_DEPRECATION_MESSAGE,
    replaceWith = ReplaceWith(
        "fillNA { columns.toColumnSet() }",
        "org.jetbrains.kotlinx.dataframe.columns.toColumnSet",
    ),
    level = DeprecationLevel.ERROR,
)
public fun <T, C> DataFrame<T>.fillNA(columns: Iterable<ColumnReference<C>>): Update<T, C?> =
    fillNA { columns.toColumnSet() }

// endregion

/** @param columns The [Columns selector DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl.WithExample] used to select the columns of this [DataFrame] to drop rows in. */
private interface DropDslParam

/** @param columns The [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample] used to select the columns of this [DataFrame] to drop rows in. */
private interface DropKPropertiesParam

/** @param columns The [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample] used to select the columns of this [DataFrame] to drop rows in. */
private interface DropColumnNamesParam

/** @param columns The Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]). used to select the columns of this [DataFrame] to drop rows in. */
private interface DropColumnAccessorsParam

// region dropNulls

/**
 * ## The Drop Nulls Operation
 *
 * Removes rows with `null` values. Specific case of [drop][DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls)
 */
internal interface DropNulls {

    /**
     * @param whereAllNull `false` by default.
     *   If `true`, rows are dropped if all selected cells are `null`.
     *   If `false`, rows are dropped if any of the selected cells is `null`.
     */
    interface WhereAllNullParam
}

private interface SetDropNullsOperationArg

/**
 * ## The Drop Nulls Operation
 *
 * Removes rows with `null` values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls) 
 * ## This Drop Nulls Overload
 */
private interface CommonDropNullsFunctionDoc

/**
 * ## The Drop Nulls Operation
 *
 * Removes rows with `null` values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls) 
 * ## This Drop Nulls Overload
 * Select or express columns using the Column(s) Selection DSL.
 * (Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL comes in the form of either a [Column Selector][org.jetbrains.kotlinx.dataframe.ColumnSelector]- or [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operate in the [Column Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl] or the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expect you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], respectively.
 *
 * For example:
 *
 * `df.`[dropNulls][org.jetbrains.kotlinx.dataframe.api.dropNulls]` { length `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` age }`
 *
 * `df.`[dropNulls][org.jetbrains.kotlinx.dataframe.api.dropNulls]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * `df.`[dropNulls][org.jetbrains.kotlinx.dataframe.api.dropNulls]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Double][Double]`>() }`
 *  
 * `df.`[dropNulls][org.jetbrains.kotlinx.dataframe.api.dropNulls]`(whereAllNull = true) { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Double][Double]`>() }`
 * @param whereAllNull `false` by default.
 *   If `true`, rows are dropped if all selected cells are `null`.
 *   If `false`, rows are dropped if any of the selected cells is `null`.
 * @param columns The [Columns selector DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl.WithExample] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNulls(whereAllNull: Boolean = false, columns: ColumnsSelector<T, *>): DataFrame<T> {
    val cols = this[columns]
    return if (whereAllNull) drop { row -> cols.all { col -> col[row] == null } }
    else drop { row -> cols.any { col -> col[row] == null } }
}

/**
 * ## The Drop Nulls Operation
 *
 * Removes rows with `null` values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls) 
 * ## This Drop Nulls Overload
 * This overload operates on all columns in the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param whereAllNull `false` by default.
 *   If `true`, rows are dropped if all selected cells are `null`.
 *   If `false`, rows are dropped if any of the selected cells is `null`.
 */
public fun <T> DataFrame<T>.dropNulls(whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { all() }

/**
 * ## The Drop Nulls Operation
 *
 * Removes rows with `null` values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls) 
 * ## This Drop Nulls Overload
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * For example:
 * ```kotlin
 * data class Person(val length: Double, val age: Double)
 * ```
 *
 * `df.`[dropNulls][org.jetbrains.kotlinx.dataframe.api.dropNulls]`(Person::length, Person::age)`
 *  
 * `df.`[dropNulls][org.jetbrains.kotlinx.dataframe.api.dropNulls]`(Person::length, whereAllNull = true)`
 * @param whereAllNull `false` by default.
 *   If `true`, rows are dropped if all selected cells are `null`.
 *   If `false`, rows are dropped if any of the selected cells is `null`.
 * @param columns The [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNulls(vararg columns: KProperty<*>, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumnSet() }

/**
 * ## The Drop Nulls Operation
 *
 * Removes rows with `null` values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls) 
 * ## This Drop Nulls Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * For example:
 *
 * `df.`[dropNulls][org.jetbrains.kotlinx.dataframe.api.dropNulls]`("length", "age")`
 *  
 * `df.`[dropNulls][org.jetbrains.kotlinx.dataframe.api.dropNulls]`("length", whereAllNull = true)`
 * @param whereAllNull `false` by default.
 *   If `true`, rows are dropped if all selected cells are `null`.
 *   If `false`, rows are dropped if any of the selected cells is `null`.
 * @param columns The [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNulls(vararg columns: String, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumnSet() }

/**
 * ## The Drop Nulls Operation
 *
 * Removes rows with `null` values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNull = true` to only drop rows where all selected cells are `null`. By default,
 * rows are dropped if any of the selected cells are `null`.
 *
 * For more information: [See `dropNulls` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnulls) 
 * ## This Drop Nulls Overload
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * For example:
 *
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `df.`[dropNulls][org.jetbrains.kotlinx.dataframe.api.dropNulls]`(length, age)`
 *  
 * `df.`[dropNulls][org.jetbrains.kotlinx.dataframe.api.dropNulls]`(length, whereAllNull = true)`
 * @param whereAllNull `false` by default.
 *   If `true`, rows are dropped if all selected cells are `null`.
 *   If `false`, rows are dropped if any of the selected cells is `null`.
 * @param columns The Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]). used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNulls(vararg columns: AnyColumnReference, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumnSet() }

@Deprecated(
    message = ITERABLE_COLUMNS_DEPRECATION_MESSAGE,
    replaceWith = ReplaceWith(
        "dropNulls(whereAllNull) { columns.toColumnSet() }",
        "org.jetbrains.kotlinx.dataframe.columns.toColumnSet",
    ),
    level = DeprecationLevel.ERROR,
)
public fun <T> DataFrame<T>.dropNulls(
    columns: Iterable<AnyColumnReference>,
    whereAllNull: Boolean = false,
): DataFrame<T> =
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
 * Removes rows with [`NA`][NA] values. Specific case of [drop][DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [`NA`][NA]. By default,
 * rows are dropped if any of the selected cells are [`NA`][NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna)
 */
internal interface DropNA {

    /**
     * @param whereAllNA `false` by default.
     *   If `true`, rows are dropped if all selected cells are [`NA`][NA].
     *   If `false`, rows are dropped if any of the selected cells is [`NA`][NA].
     */
    interface WhereAllNAParam
}

private interface SetDropNAOperationArg

/**
 * ## The Drop `NA` Operation
 *
 * Removes rows with [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA]. By default,
 * rows are dropped if any of the selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna) 
 * ## This Drop NA Overload
 */
private interface CommonDropNAFunctionDoc

/**
 * ## The Drop `NA` Operation
 *
 * Removes rows with [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA]. By default,
 * rows are dropped if any of the selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna) 
 * ## This Drop NA Overload
 * Select or express columns using the Column(s) Selection DSL.
 * (Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL comes in the form of either a [Column Selector][org.jetbrains.kotlinx.dataframe.ColumnSelector]- or [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operate in the [Column Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl] or the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expect you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], respectively.
 *
 * For example:
 *
 * `df.`[dropNA][org.jetbrains.kotlinx.dataframe.api.dropNA]` { length `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` age }`
 *
 * `df.`[dropNA][org.jetbrains.kotlinx.dataframe.api.dropNA]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * `df.`[dropNA][org.jetbrains.kotlinx.dataframe.api.dropNA]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Double][Double]`>() }`
 *  
 * `df.`[dropNA][org.jetbrains.kotlinx.dataframe.api.dropNA]`(whereAllNA = true) { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Double][Double]`>() }`
 * @param whereAllNA `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *   If `false`, rows are dropped if any of the selected cells is [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 * @param columns The [Columns selector DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl.WithExample] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNA(whereAllNA: Boolean = false, columns: ColumnsSelector<T, *>): DataFrame<T> {
    val cols = this[columns]
    return if (whereAllNA) drop { cols.all { this[it].isNA } }
    else drop { cols.any { this[it].isNA } }
}

/**
 * ## The Drop `NA` Operation
 *
 * Removes rows with [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA]. By default,
 * rows are dropped if any of the selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna) 
 * ## This Drop NA Overload
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * For example:
 * ```kotlin
 * data class Person(val length: Double, val age: Double)
 * ```
 *
 * `df.`[dropNA][org.jetbrains.kotlinx.dataframe.api.dropNA]`(Person::length, Person::age)`
 *  
 * `df.`[dropNA][org.jetbrains.kotlinx.dataframe.api.dropNA]`(Person::length, whereAllNA = true)`
 * @param whereAllNA `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *   If `false`, rows are dropped if any of the selected cells is [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 * @param columns The [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNA(vararg columns: KProperty<*>, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumnSet() }

/**
 * ## The Drop `NA` Operation
 *
 * Removes rows with [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA]. By default,
 * rows are dropped if any of the selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna) 
 * ## This Drop NA Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * For example:
 *
 * `df.`[dropNA][org.jetbrains.kotlinx.dataframe.api.dropNA]`("length", "age")`
 *  
 * `df.`[dropNA][org.jetbrains.kotlinx.dataframe.api.dropNA]`("length", whereAllNA = true)`
 * @param whereAllNA `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *   If `false`, rows are dropped if any of the selected cells is [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 * @param columns The [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNA(vararg columns: String, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumnSet() }

/**
 * ## The Drop `NA` Operation
 *
 * Removes rows with [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA]. By default,
 * rows are dropped if any of the selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna) 
 * ## This Drop NA Overload
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * For example:
 *
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `df.`[dropNA][org.jetbrains.kotlinx.dataframe.api.dropNA]`(length, age)`
 *  
 * `df.`[dropNA][org.jetbrains.kotlinx.dataframe.api.dropNA]`(length, whereAllNA = true)`
 * @param whereAllNA `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *   If `false`, rows are dropped if any of the selected cells is [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 * @param columns The Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]). used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNA(vararg columns: AnyColumnReference, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumnSet() }

@Deprecated(
    message = ITERABLE_COLUMNS_DEPRECATION_MESSAGE,
    replaceWith = ReplaceWith(
        "dropNA(whereAllNA) { columns.toColumnSet() }",
        "org.jetbrains.kotlinx.dataframe.columns.toColumnSet",
    ),
    level = DeprecationLevel.ERROR,
)
public fun <T> DataFrame<T>.dropNA(columns: Iterable<AnyColumnReference>, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumnSet() }

/**
 * ## The Drop `NA` Operation
 *
 * Removes rows with [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNA = true` to only drop rows where all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA]. By default,
 * rows are dropped if any of the selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * For more information: [See `dropNA` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropna) 
 * ## This Drop NA Overload
 * This overload operates on all columns in the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param whereAllNA `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *   If `false`, rows are dropped if any of the selected cells is [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 */
public fun <T> DataFrame<T>.dropNA(whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { all() }

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
 * Removes rows with [`NaN`][Double.isNaN] values. Specific case of [drop][DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [`NaN`][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [`NaN`][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans)
 */
internal interface DropNaNs {

    /**
     * @param whereAllNaN `false` by default.
     *   If `true`, rows are dropped if all selected cells are [`NaN`][Double.isNaN].
     *   If `false`, rows are dropped if any of the selected cells is [`NaN`][Double.isNaN].
     */
    interface WhereAllNaNParam
}

private interface SetDropNaNsOperationArg

/**
 * ## The Drop `NaN` Operation
 *
 * Removes rows with [`NaN`][Double.isNaN] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [`NaN`][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [`NaN`][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans) 
 * ## This Drop NaNs Overload
 */
private interface CommonDropNaNsFunctionDoc

/**
 * ## The Drop `NaN` Operation
 *
 * Removes rows with [`NaN`][Double.isNaN] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [`NaN`][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [`NaN`][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans) 
 * ## This Drop NaNs Overload
 * Select or express columns using the Column(s) Selection DSL.
 * (Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL comes in the form of either a [Column Selector][org.jetbrains.kotlinx.dataframe.ColumnSelector]- or [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operate in the [Column Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnSelectionDsl] or the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expect you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], respectively.
 *
 * For example:
 *
 * `df.`[dropNaNs][org.jetbrains.kotlinx.dataframe.api.dropNaNs]` { length `[and][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.and]` age }`
 *
 * `df.`[dropNaNs][org.jetbrains.kotlinx.dataframe.api.dropNaNs]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * `df.`[dropNaNs][org.jetbrains.kotlinx.dataframe.api.dropNaNs]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Double][Double]`>() }`
 *  
 * `df.`[dropNaNs][org.jetbrains.kotlinx.dataframe.api.dropNaNs]`(whereAllNaN = true) { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Double][Double]`>() }`
 * @param whereAllNaN `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NaN`][Double.isNaN].
 *   If `false`, rows are dropped if any of the selected cells is [`NaN`][Double.isNaN].
 * @param columns The [Columns selector DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl.WithExample] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNaNs(whereAllNaN: Boolean = false, columns: ColumnsSelector<T, *>): DataFrame<T> {
    val cols = this[columns]
    return if (whereAllNaN) drop { cols.all { this[it].isNaN } }
    else drop { cols.any { this[it].isNaN } }
}

/**
 * ## The Drop `NaN` Operation
 *
 * Removes rows with [`NaN`][Double.isNaN] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [`NaN`][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [`NaN`][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans) 
 * ## This Drop NaNs Overload
 * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
 *
 * For example:
 * ```kotlin
 * data class Person(val length: Double, val age: Double)
 * ```
 *
 * `df.`[dropNaNs][org.jetbrains.kotlinx.dataframe.api.dropNaNs]`(Person::length, Person::age)`
 *  
 * `df.`[dropNaNs][org.jetbrains.kotlinx.dataframe.api.dropNaNs]`(Person::length, whereAllNaN = true)`
 * @param whereAllNaN `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NaN`][Double.isNaN].
 *   If `false`, rows are dropped if any of the selected cells is [`NaN`][Double.isNaN].
 * @param columns The [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNaNs(vararg columns: KProperty<*>, whereAllNaN: Boolean = false): DataFrame<T> =
    dropNaNs(whereAllNaN) { columns.toColumnSet() }

/**
 * ## The Drop `NaN` Operation
 *
 * Removes rows with [`NaN`][Double.isNaN] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [`NaN`][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [`NaN`][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans) 
 * ## This Drop NaNs Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * For example:
 *
 * `df.`[dropNaNs][org.jetbrains.kotlinx.dataframe.api.dropNaNs]`("length", "age")`
 *  
 * `df.`[dropNaNs][org.jetbrains.kotlinx.dataframe.api.dropNaNs]`("length", whereAllNaN = true)`
 * @param whereAllNaN `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NaN`][Double.isNaN].
 *   If `false`, rows are dropped if any of the selected cells is [`NaN`][Double.isNaN].
 * @param columns The [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNaNs(vararg columns: String, whereAllNaN: Boolean = false): DataFrame<T> =
    dropNaNs(whereAllNaN) { columns.toColumnSet() }

/**
 * ## The Drop `NaN` Operation
 *
 * Removes rows with [`NaN`][Double.isNaN] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [`NaN`][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [`NaN`][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans) 
 * ## This Drop NaNs Overload
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * For example:
 *
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
 *
 * `df.`[dropNaNs][org.jetbrains.kotlinx.dataframe.api.dropNaNs]`(length, age)`
 *  
 * `df.`[dropNaNs][org.jetbrains.kotlinx.dataframe.api.dropNaNs]`(length, whereAllNaN = true)`
 * @param whereAllNaN `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NaN`][Double.isNaN].
 *   If `false`, rows are dropped if any of the selected cells is [`NaN`][Double.isNaN].
 * @param columns The Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]). used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to drop rows in.
 */
public fun <T> DataFrame<T>.dropNaNs(vararg columns: AnyColumnReference, whereAllNaN: Boolean = false): DataFrame<T> =
    dropNaNs(whereAllNaN) { columns.toColumnSet() }

@Deprecated(
    message = ITERABLE_COLUMNS_DEPRECATION_MESSAGE,
    replaceWith = ReplaceWith(
        "dropNaNs(whereAllNaN) { columns.toColumnSet() }",
        "org.jetbrains.kotlinx.dataframe.columns.toColumnSet",
    ),
    level = DeprecationLevel.ERROR,
)
public fun <T> DataFrame<T>.dropNaNs(
    columns: Iterable<AnyColumnReference>,
    whereAllNaN: Boolean = false,
): DataFrame<T> =
    dropNaNs(whereAllNaN) { columns.toColumnSet() }

/**
 * ## The Drop `NaN` Operation
 *
 * Removes rows with [`NaN`][Double.isNaN] values. Specific case of [drop][org.jetbrains.kotlinx.dataframe.DataFrame.drop].
 *
 * Optionally, you can select which columns to operate on (see [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns]).
 * Also, you can supply `whereAllNaN = true` to only drop rows where all selected cells are [`NaN`][Double.isNaN]. By default,
 * rows are dropped if any of the selected cells are [`NaN`][Double.isNaN].
 *
 * For more information: [See `dropNaNs` on the documentation website.](https://kotlin.github.io/dataframe/drop.html#dropnans) 
 * ## This Drop NaNs Overload
 * This overload operates on all columns in the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 * @param whereAllNaN `false` by default.
 *   If `true`, rows are dropped if all selected cells are [`NaN`][Double.isNaN].
 *   If `false`, rows are dropped if any of the selected cells is [`NaN`][Double.isNaN].
 */
public fun <T> DataFrame<T>.dropNaNs(whereAllNaN: Boolean = false): DataFrame<T> =
    dropNaNs(whereAllNaN) { all() }

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
