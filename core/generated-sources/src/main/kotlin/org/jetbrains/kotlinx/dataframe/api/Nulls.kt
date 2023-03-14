package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.Update.UpdateOperationArg
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.documentation.*
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumns
import org.jetbrains.kotlinx.dataframe.kind
import org.jetbrains.kotlinx.dataframe.typeClass
import kotlin.reflect.KProperty

// region fillNulls

/**
 * ## The Fill Nulls Operation
 *
 * Replaces `null` values with given value or expression.
 * Specific case of [update].
 *
 * Check out the [`fillNulls` Operation Usage][Usage].
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
 * ## This Fill Nulls Overload
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
 * ## This Fill Nulls Overload
 * Select or express columns using the Column(s) Selection DSL.
 * (Any [Access Api][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
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
 * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<Double>() }`
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
 * ## This Fill Nulls Overload
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
    fillNulls { columns.toColumns() }

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
 * ## This Fill Nulls Overload
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
    fillNulls { columns.toColumns() }

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
 * ## This Fill Nulls Overload
 * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
 * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
 *
 * For example:
 *
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<Double>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<Double>()`
 *
 * `df.`[fillNulls][org.jetbrains.kotlinx.dataframe.api.fillNulls]`(length, age)`
 *  
 * @param columns The [Column references][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnAccessors.WithExample] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.fillNulls(vararg columns: ColumnReference<C>): Update<T, C?> =
    fillNulls { columns.toColumns() }

/**
 * TODO this will be deprecated
 */
public fun <T, C> DataFrame<T>.fillNulls(columns: Iterable<ColumnReference<C>>): Update<T, C?> =
    fillNulls { columns.toColumnSet() }

// endregion

/**
 * [Floats][Float] or [Doubles][Double] can be represented as [Float.NaN] or [Double.NaN], respectively,
 * in cases where a mathematical operation is undefined, such as dividing by zero.
 * In Dataframe we have helper functions to check for `NaNs`, such as [Any?.isNaN][Any.isNaN] and
 * [column.canHaveNaN][DataColumn.canHaveNaN].
 * You can also use [fillNaNs][fillNaNs] to replace `NaNs` in certain columns with a given value or expression.
 *
 * @see NA
 */
internal interface NaN

/**
 * `NA` in Dataframe can be seen as "[NaN] or `null`".
 *
 * [Floats][Float] or [Doubles][Double] can be represented as [Float.NaN] or [Double.NaN], respectively,
 * in cases where a mathematical operation is undefined, such as dividing by zero.
 *
 * In Dataframe we have helper functions to check for `NAs`, such as [Any?.isNA][Any.isNA] and
 * [column.canHaveNA][DataColumn.canHaveNA].
 * You can also use [fillNA][fillNA] to replace `NAs` in certain columns with a given value or expression.
 * @see NaN
 */
internal interface NA

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
 * Check out the [`fillNaNs` Operation Usage][Usage].
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
 * Replaces [`NaN`][org.jetbrains.kotlinx.dataframe.api.NaN] values with given value or expression.
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
 * Replaces [`NaN`][org.jetbrains.kotlinx.dataframe.api.NaN] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNaNs` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNaNs.Usage].
 *
 * For more information: [See `fillNaNs` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillnans) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Fill NaNs Overload
 * Select or express columns using the Column(s) Selection DSL.
 * (Any [Access Api][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
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
 * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<Double>() }`
 *  
 * @param columns The [Columns selector DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl.WithExample] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.fillNaNs(columns: ColumnsSelector<T, C>): Update<T, C> =
    update(columns).where { it.isNaN }

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [`NaN`][org.jetbrains.kotlinx.dataframe.api.NaN] values with given value or expression.
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
    fillNaNs { columns.toColumns() }

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [`NaN`][org.jetbrains.kotlinx.dataframe.api.NaN] values with given value or expression.
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
    fillNaNs { columns.toColumns() }

/**
 * ## The Fill NaNs Operation
 *
 * Replaces [`NaN`][org.jetbrains.kotlinx.dataframe.api.NaN] values with given value or expression.
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
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<Double>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<Double>()`
 *
 * `df.`[fillNaNs][org.jetbrains.kotlinx.dataframe.api.fillNaNs]`(length, age)`
 *  
 * @param columns The [Column references][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnAccessors.WithExample] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.fillNaNs(vararg columns: ColumnReference<C>): Update<T, C> =
    fillNaNs { columns.toColumns() }

/**
 * TODO this will be deprecated
 */
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
 * Check out the [`fillNA` Operation Usage][Usage].
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
 * Replaces [`NA`][org.jetbrains.kotlinx.dataframe.api.NA] values with given value or expression.
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
 * Replaces [`NA`][org.jetbrains.kotlinx.dataframe.api.NA] values with given value or expression.
 * Specific case of [update][org.jetbrains.kotlinx.dataframe.api.update].
 *
 * Check out the [`fillNA` Operation Usage][org.jetbrains.kotlinx.dataframe.api.FillNA.Usage].
 *
 * For more information: [See `fillNA` on the documentation website.](https://kotlin.github.io/dataframe/fill.html#fillna) 
 * ## ‎
 * The columns to update need to be selected. See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns] for all the selecting options. 
 * ## This Fill NA Overload
 * Select or express columns using the Column(s) Selection DSL.
 * (Any [Access Api][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
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
 * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<Double>() }`
 *  
 * @param columns The [Columns selector DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl.WithExample] used to select the columns of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.fillNA(columns: ColumnsSelector<T, C?>): Update<T, C?> =
    update(columns).where { it.isNA }

/**
 * ## The Fill NA Operation
 *
 * Replaces [`NA`][org.jetbrains.kotlinx.dataframe.api.NA] values with given value or expression.
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
    fillNA { columns.toColumns() }

/**
 * ## The Fill NA Operation
 *
 * Replaces [`NA`][org.jetbrains.kotlinx.dataframe.api.NA] values with given value or expression.
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
    fillNA { columns.toColumns() }

/**
 * ## The Fill NA Operation
 *
 * Replaces [`NA`][org.jetbrains.kotlinx.dataframe.api.NA] values with given value or expression.
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
 * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<Double>()`
 *
 * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<Double>()`
 *
 * `df.`[fillNA][org.jetbrains.kotlinx.dataframe.api.fillNA]`(length, age)`
 *  
 * @param columns The [Column references][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnAccessors.WithExample] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] to update.
 */
public fun <T, C> DataFrame<T>.fillNA(vararg columns: ColumnReference<C>): Update<T, C?> =
    fillNA { columns.toColumns() }

/**
 * TODO this will be deprecated
 */
public fun <T, C> DataFrame<T>.fillNA(columns: Iterable<ColumnReference<C>>): Update<T, C?> =
    fillNA { columns.toColumnSet() }

// endregion

// region dropNulls

public fun <T> DataFrame<T>.dropNulls(whereAllNull: Boolean = false, selector: ColumnsSelector<T, *>): DataFrame<T> {
    val columns = this[selector]
    return if (whereAllNull) drop { row -> columns.all { col -> col[row] == null } }
    else drop { row -> columns.any { col -> col[row] == null } }
}

public fun <T> DataFrame<T>.dropNulls(whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { all() }

public fun <T> DataFrame<T>.dropNulls(vararg columns: KProperty<*>, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumns() }

public fun <T> DataFrame<T>.dropNulls(vararg columns: String, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumns() }

public fun <T> DataFrame<T>.dropNulls(vararg columns: AnyColumnReference, whereAllNull: Boolean = false): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumns() }

public fun <T> DataFrame<T>.dropNulls(
    columns: Iterable<AnyColumnReference>,
    whereAllNull: Boolean = false
): DataFrame<T> =
    dropNulls(whereAllNull) { columns.toColumnSet() }

public fun <T> DataColumn<T?>.dropNulls(): DataColumn<T> =
    (if (!hasNulls()) this else filter { it != null }) as DataColumn<T>

// endregion

// region dropNA

public fun <T> DataFrame<T>.dropNA(whereAllNA: Boolean = false, selector: ColumnsSelector<T, *>): DataFrame<T> {
    val columns = this[selector]

    return if (whereAllNA) drop { columns.all { this[it].isNA } }
    else drop { columns.any { this[it].isNA } }
}

public fun <T> DataFrame<T>.dropNA(vararg columns: KProperty<*>, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumns() }

public fun <T> DataFrame<T>.dropNA(vararg columns: String, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumns() }

public fun <T> DataFrame<T>.dropNA(vararg columns: AnyColumnReference, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumns() }

public fun <T> DataFrame<T>.dropNA(columns: Iterable<AnyColumnReference>, whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { columns.toColumnSet() }

public fun <T> DataFrame<T>.dropNA(whereAllNA: Boolean = false): DataFrame<T> =
    dropNA(whereAllNA) { all() }

public fun <T> DataColumn<T?>.dropNA(): DataColumn<T> =
    when (typeClass) {
        Double::class, Float::class -> filter { !it.isNA }.cast()
        else -> (if (!hasNulls()) this else filter { it != null }) as DataColumn<T>
    }

// endregion
