package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.api.CorrDocs.Grammar
import org.jetbrains.kotlinx.dataframe.api.CorrDocs.SelectingOptions
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.api.corrImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

/**
 * Calculates the Pearson pairwise correlation between values in the specified [columns].
 *
 * This function does not compute the correlation immediately.
 * Instead, it defines the primary set of columns
 * and returns a [Corr] instance that allows configuring how the correlation should be computed.
 *
 * The function is available for numeric- and [Boolean] columns.
 * [Boolean] values are converted into 1 for true and 0 for false.
 * All other columns are ignored.
 * If a [ColumnGroup] instance is passed as the target column for correlation,
 * it will be unpacked into suitable nested columns.
 *
 * The [Corr] object provides two methods to perform correlation calculations:
 * - [with][Corr.with] — computes correlations between the initially selected columns and a second set of columns.
 * - [withItself][Corr.withItself] — computes pairwise correlations within the initially selected columns.
 *
 * Each method returns a square or rectangular correlation matrix represented by a [DataFrame],
 * where rows and columns correspond to the selected column sets,
 * and each cell contains the Pearson correlation coefficient between the corresponding pair of columns.
 *
 * To compute correlations between all suitable columns in the [DataFrame], use [DataFrame.corr()][DataFrame.corr].
 *
 * Check out [Grammar].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also: [Selecting Columns][SelectingOptions].
 *
 * For more information, see: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/corr.html)
 */
internal interface CorrDocs {

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
     * <code>`df`</code>`.`[corr][org.jetbrains.kotlinx.dataframe.api.corr]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[corr][org.jetbrains.kotlinx.dataframe.api.corr]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[corr][org.jetbrains.kotlinx.dataframe.api.corr]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
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
     * `df.`[corr][org.jetbrains.kotlinx.dataframe.api.corr]`("length", "age")`
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
     * `df.`[corr][org.jetbrains.kotlinx.dataframe.api.corr]`(length, age)`
     *
     * ### 4. [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample]
     * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
     *
     * #### For example:
     * ```kotlin
     * data class Person(val length: Double, val age: Double)
     * ```
     *
     * `df.`[corr][org.jetbrains.kotlinx.dataframe.api.corr]`(Person::length, Person::age)`
     *
     */
    interface SelectingOptions

    /**
     * ## Corr Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * **[`corr`][convert]**`  { columnsSelector: `[`ColumnsSelector`][ColumnsSelector]`  }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[**`with`**][Corr.with]` { columnsSelector: `[`ColumnsSelector`][ColumnsSelector]`  }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`withItself`**][Corr.withItself]`()`
     */
    interface Grammar
}

internal fun AnyCol.isSuitableForCorr() = isSubtypeOf<Number>() || type() == typeOf<Boolean>()

// region DataFrame

/**
 * An intermediate class used in the [corr] operation.
 *
 * This class does not perform any computation by itself — it serves as a transitional step
 * before specifying how the correlation should be calculated.
 * It must be followed by one of the computation methods to produce a correlation [DataFrame].
 *
 * The resulting [DataFrame] is a correlation matrix where rows correspond to one set of columns,
 * columns to the other set, and each cell contains the Pearson correlation coefficient
 * between the respective pair of columns.
 *
 * Use the following methods to perform the computation:
 * - [with] — selects a second set of columns and computes correlations between
 *   the initially selected columns and this second set.
 * - [withItself] — computes pairwise correlations within the initially selected columns.
 *
 * See [Grammar][CorrDocs.Grammar] for more details.
 */
public data class Corr<T, C>(internal val df: DataFrame<T>, internal val columns: ColumnsSelector<T, C>)

/**
 * Computes the pearson correlation between all suitable columns in this [DataFrame],
 * including nested columns at any depth.
 *
 * The result is a square correlation matrix represented by a [DataFrame],
 * where both rows and columns correspond to the original columns,
 * and each cell contains the Pearson correlation coefficient between the respective pair of columns.
 *
 * The function is available for numeric- and [Boolean] columns.
 * [Boolean] values are converted into 1 for true and 0 for false.
 * All other columns are ignored.
 *
 * For more information, see: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/corr.html)
 *
 * @return A square correlation matrix as a [DataFrame], where both rows and columns correspond to the original columns.
 */
@Refine
@Interpretable("DataFrameCorr")
public fun <T> DataFrame<T>.corr(): DataFrame<T> =
    corr {
        colsAtAnyDepth().filter { it.isSuitableForCorr() }
    }.withItself()

/**
 * Calculates the Pearson pairwise correlation between values in the specified [columns].
 *
 * This function does not compute the correlation immediately.
 * Instead, it defines the primary set of columns
 * and returns a [Corr][org.jetbrains.kotlinx.dataframe.api.Corr] instance that allows configuring how the correlation should be computed.
 *
 * The function is available for numeric- and [Boolean] columns.
 * [Boolean] values are converted into 1 for true and 0 for false.
 * All other columns are ignored.
 * If a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] instance is passed as the target column for correlation,
 * it will be unpacked into suitable nested columns.
 *
 * The [Corr][org.jetbrains.kotlinx.dataframe.api.Corr] object provides two methods to perform correlation calculations:
 * - [with][org.jetbrains.kotlinx.dataframe.api.Corr.with] — computes correlations between the initially selected columns and a second set of columns.
 * - [withItself][org.jetbrains.kotlinx.dataframe.api.Corr.withItself] — computes pairwise correlations within the initially selected columns.
 *
 * Each method returns a square or rectangular correlation matrix represented by a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
 * where rows and columns correspond to the selected column sets,
 * and each cell contains the Pearson correlation coefficient between the corresponding pair of columns.
 *
 * To compute correlations between all suitable columns in the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], use [DataFrame.corr()][org.jetbrains.kotlinx.dataframe.DataFrame.corr].
 *
 * Check out [Grammar][org.jetbrains.kotlinx.dataframe.api.CorrDocs.Grammar].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also: [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.CorrDocs.SelectingOptions].
 *
 * For more information, see: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/corr.html)
 * ### This Corr Overload
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
 * The function is available for numeric- and [Boolean] columns.
 * [Boolean] values are converted into 1 for true and 0 for false.
 * All other columns are ignored.
 * If a [ColumnGroup] instance is passed as the target column for correlation,
 * it will be unpacked into suitable nested columns.
 *
 * ### Examples
 * ```kotlin
 * // Compute correlations between the "age" column and the "weight" and "height" columns
 * df.corr { age }.with { weight and height }
 *
 * // Compute pairwise correlations between all columns of type `Number`
 * df.corr { colsOf<Number>() }.withItself()
 * ```
 * @param [columns] The [Columns Selector][ColumnsSelector] used to select the columns
 * of this [DataFrame] to compute a correlation.
 * @return A [Corr] intermediate object with the selected columns.
 */
public fun <T, C> DataFrame<T>.corr(columns: ColumnsSelector<T, C>): Corr<T, C> = Corr(this, columns)

/**
 * Calculates the Pearson pairwise correlation between values in the specified [columns].
 *
 * This function does not compute the correlation immediately.
 * Instead, it defines the primary set of columns
 * and returns a [Corr][org.jetbrains.kotlinx.dataframe.api.Corr] instance that allows configuring how the correlation should be computed.
 *
 * The function is available for numeric- and [Boolean] columns.
 * [Boolean] values are converted into 1 for true and 0 for false.
 * All other columns are ignored.
 * If a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] instance is passed as the target column for correlation,
 * it will be unpacked into suitable nested columns.
 *
 * The [Corr][org.jetbrains.kotlinx.dataframe.api.Corr] object provides two methods to perform correlation calculations:
 * - [with][org.jetbrains.kotlinx.dataframe.api.Corr.with] — computes correlations between the initially selected columns and a second set of columns.
 * - [withItself][org.jetbrains.kotlinx.dataframe.api.Corr.withItself] — computes pairwise correlations within the initially selected columns.
 *
 * Each method returns a square or rectangular correlation matrix represented by a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
 * where rows and columns correspond to the selected column sets,
 * and each cell contains the Pearson correlation coefficient between the corresponding pair of columns.
 *
 * To compute correlations between all suitable columns in the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], use [DataFrame.corr()][org.jetbrains.kotlinx.dataframe.DataFrame.corr].
 *
 * Check out [Grammar][org.jetbrains.kotlinx.dataframe.api.CorrDocs.Grammar].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also: [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.CorrDocs.SelectingOptions].
 *
 * For more information, see: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/corr.html)
 * ### This Corr Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * The function is available for numeric- and [Boolean] columns.
 * [Boolean] values are converted into 1 for true and 0 for false.
 * All other columns are ignored.
 * If a [ColumnGroup] instance is passed as the target column for correlation,
 * it will be unpacked into suitable nested columns.
 *
 * ### Examples
 * ```kotlin
 * // Compute correlations between the "age" column and the "weight" and "height" columns
 * df.corr { age }.with { weight and height }
 *
 * // Compute pairwise correlations between all columns of type `Number`
 * df.corr { colsOf<Number>() }.withItself()
 * ```
 * @param [columns] The [Column Names][String] used to select the columns
 * of this [DataFrame] to compute a correlation.
 * @return A [Corr] intermediate object with the selected columns.
 */
public fun <T> DataFrame<T>.corr(vararg columns: String): Corr<T, Any?> = corr { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.corr(vararg columns: KProperty<C>): Corr<T, C> = corr { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.corr(vararg columns: ColumnReference<C>): Corr<T, C> = corr { columns.toColumnSet() }

/**
 * Calculates the correlation of specified [columns][otherColumns]
 * with values in the columns previously selected with [corr].
 *
 * Returns a correlation matrix represented by a [DataFrame],
 * where rows and columns correspond to the selected column sets,
 * and each cell contains the Pearson correlation coefficient between the corresponding pair of columns.
 *
 * Check out [Grammar].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also: [Selecting Columns][SelectingOptions].
 *
 * For more information, see: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/corr.html)
 */
internal interface CorrWithDocs

/**
 * Calculates the correlation of specified [columns][otherColumns]
 * with values in the columns previously selected with [corr][org.jetbrains.kotlinx.dataframe.api.corr].
 *
 * Returns a correlation matrix represented by a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
 * where rows and columns correspond to the selected column sets,
 * and each cell contains the Pearson correlation coefficient between the corresponding pair of columns.
 *
 * Check out [Grammar][org.jetbrains.kotlinx.dataframe.api.CorrDocs.Grammar].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also: [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.CorrDocs.SelectingOptions].
 *
 * For more information, see: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/corr.html)
 * ### This Corr With Overload
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
 * ### Examples
 * ```kotlin
 * // Compute correlations between the "age" column and the "weight" and "height" columns
 * df.corr { age }.with { weight and height }
 *
 * // Compute correlations between the "speed" column and all columns of type `Double` (excluding itself)
 * df.corr { speed }.with { colsOf<Double>() except speed }
 * ```
 *
 * @param otherColumns The [ColumnsSelector] used to select the second set of columns
 * from this [DataFrame] to compute correlations against the initially selected columns.
 * @return A [DataFrame] containing the resulting correlation matrix.
 */
public fun <T, C, R> Corr<T, C>.with(otherColumns: ColumnsSelector<T, R>): DataFrame<T> = corrImpl(otherColumns)

/**
 * Calculates the correlation of specified [columns][otherColumns]
 * with values in the columns previously selected with [corr][org.jetbrains.kotlinx.dataframe.api.corr].
 *
 * Returns a correlation matrix represented by a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
 * where rows and columns correspond to the selected column sets,
 * and each cell contains the Pearson correlation coefficient between the corresponding pair of columns.
 *
 * Check out [Grammar][org.jetbrains.kotlinx.dataframe.api.CorrDocs.Grammar].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See also: [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.CorrDocs.SelectingOptions].
 *
 * For more information, see: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/corr.html)
 * ### This Corr With Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * ### Examples
 * ```kotlin
 * // Compute correlations between the "age" column and the "weight" and "height" columns
 * df.corr("age").with("weight", "height")
 *
 * // Compute correlations between the "speed" column and all columns of type `Number`
 * df.corr { colsOf<Number>() }.with("speed")
 * ```
 *
 * @param otherColumns The [Column Names][String] used to select the second set of columns
 * from this [DataFrame] to compute correlations against the initially selected columns.
 * @return A [DataFrame] containing the resulting correlation matrix.
 */
public fun <T, C> Corr<T, C>.with(vararg otherColumns: String): DataFrame<T> = with { otherColumns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, R> Corr<T, C>.with(vararg otherColumns: KProperty<R>): DataFrame<T> =
    with { otherColumns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C, R> Corr<T, C>.with(vararg otherColumns: ColumnReference<R>): DataFrame<T> =
    with { otherColumns.toColumnSet() }

/**
 * Calculates Pearson pairwise correlations between the columns
 * previously selected with [corr].
 *
 * Returns a square correlation matrix represented by a [DataFrame],
 * where both rows and columns correspond to the selected columns,
 * and each cell contains the Pearson correlation coefficient between the respective pair of columns.
 *
 * Check out [Grammar].
 *
 * For more information, see: [See `convert` on the documentation website.](https://kotlin.github.io/dataframe/corr.html)
 *
 * @return A [DataFrame] containing the pairwise correlation matrix.
 */
public fun <T, C> Corr<T, C>.withItself(): DataFrame<T> = with(columns)

// endregion
