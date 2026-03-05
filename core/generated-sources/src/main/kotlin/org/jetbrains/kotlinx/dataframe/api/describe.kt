package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.describeImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataSchema
@DataSchema
public interface ColumnDescription {
    public val name: String
    public val path: ColumnPath
    public val type: String
    public val count: Int
    public val unique: Int
    public val nulls: Int
    public val top: Any
    public val freq: Int
    public val mean: Double
    public val std: Double
    public val min: Any
    public val p25: Any
    public val median: Any
    public val p75: Any
    public val max: Any
}

// endregion

// region DataColumn

/**
 * Computes descriptive statistics for a given [DataColumn], returning a [DataFrame] with key summary metrics.
 *
 * This function provides a statistical summary of a given column, including its type, count, uniqueness,
 * missing values, most frequent values, and statistical measures if applicable.
 *
 * ### Summary Metrics:
 *
 * - **`name`** — The name of the column.
 * - **`path`** — path to the column (for hierarchical `DataFrame`)
 * - **`type`** — The data type of the column (e.g., Int, String, Boolean).
 * - **`count`** — The total number of non-null values in the column.
 * - **`unique`** — The number of unique values in the column.
 * - **`nulls`** — The count of null (missing) values in the column.
 * - **`top`** — The most frequently occurring value in the column.
 * - **`freq`** — The frequency of the most common value.
 * - **`mean`** — The arithmetic mean (only for numeric columns).
 * - **`std`** — The standard deviation (only for numeric columns).
 * - **`min`** — The minimum value in the column.
 * - **`p25`** — The 25th percentile value (first quartile).
 * - **`median`** — The median value (50th percentile / second quartile).
 * - **`p75`** — The 75th percentile value (third quartile).
 * - **`max`** — The maximum value in the column.
 *
 * For non-numeric columns, statistical metrics
 * such as `mean` and `std` will return `null`. If column values are incomparable,
 * percentile values (`min`, `p25`, `median`, `p75`, `max`) will also return `null`.
 *
 * @return A [DataFrame] where each row represents the descriptive statistics of a single column.
 *         The output contains one row per described column with the summary metrics as columns.
 */
public fun <T> DataColumn<T>.describe(): DataFrame<ColumnDescription> = describeImpl(listOf(this))

// endregion

// region DataFrame

/**
 * ## The Describe Operation
 *
 * Computes descriptive statistics for all columns in a given [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], including nested columns,
 * returning a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with key summary metrics for each column (with a [ColumnDescription][org.jetbrains.kotlinx.dataframe.api.ColumnDescription] data schema).
 *
 * This function provides a statistical summary for all columns, including nested ones,
 * providing their type, count, unique and missing values, most frequent values,
 * and statistical measures if applicable.
 *
 * ### Summary Metrics:
 *
 * - **`name`** — The name of the column.
 * - **`path`** — path to the column (for hierarchical `DataFrame`)
 * - **`type`** — The data type of the column (e.g., Int, String, Boolean).
 * - **`count`** — The total number of non-null values in the column.
 * - **`unique`** — The number of unique values in the column.
 * - **`nulls`** — The count of null (missing) values in the column.
 * - **`top`** — The most frequently occurring value in the column.
 * - **`freq`** — The frequency of the most common value.
 * - **`mean`** — The arithmetic mean (only for numeric columns).
 * - **`std`** — The standard deviation (only for numeric columns).
 * - **`min`** — The minimum value in the column.
 * - **`p25`** — The 25th percentile value (first quartile).
 * - **`median`** — The median value (50th percentile / second quartile).
 * - **`p75`** — The 75th percentile value (third quartile).
 * - **`max`** — The maximum value in the column.
 *
 * For non-numeric columns, statistical metrics
 * such as `mean` and `std` will return `null`. If column values are incomparable,
 * percentile values (`min`, `p25`, `median`, `p75`, `max`) will also return `null`.
 *
 * @return A [DataFrame] where each row represents the descriptive statistics of a single column in the input DataFrame.
 *         The output contains one row per described column with the summary metrics as columns.
 */
public fun <T> DataFrame<T>.describe(): DataFrame<ColumnDescription> =
    describe {
        colsAtAnyDepth().filter { !it.isColumnGroup() }
    }

/**
 * ## The Describe Operation
 *
 * Computes descriptive statistics for the selected columns in a given [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], including nested columns,
 * returning a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with key summary metrics for each column (with a [ColumnDescription][org.jetbrains.kotlinx.dataframe.api.ColumnDescription] data schema).
 *
 * This function provides a statistical summary for all columns, including nested ones,
 * providing their type, count, unique and missing values, most frequent values,
 * and statistical measures if applicable.
 *
 * ### Summary Metrics:
 *
 * - **`name`** — The name of the column.
 * - **`path`** — path to the column (for hierarchical `DataFrame`)
 * - **`type`** — The data type of the column (e.g., Int, String, Boolean).
 * - **`count`** — The total number of non-null values in the column.
 * - **`unique`** — The number of unique values in the column.
 * - **`nulls`** — The count of null (missing) values in the column.
 * - **`top`** — The most frequently occurring value in the column.
 * - **`freq`** — The frequency of the most common value.
 * - **`mean`** — The arithmetic mean (only for numeric columns).
 * - **`std`** — The standard deviation (only for numeric columns).
 * - **`min`** — The minimum value in the column.
 * - **`p25`** — The 25th percentile value (first quartile).
 * - **`median`** — The median value (50th percentile / second quartile).
 * - **`p75`** — The 75th percentile value (third quartile).
 * - **`max`** — The maximum value in the column.
 *
 * For non-numeric columns, statistical metrics
 * such as `mean` and `std` will return `null`. If column values are incomparable,
 * percentile values (`min`, `p25`, `median`, `p75`, `max`) will also return `null`.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `describe` on the documentation website.](https://kotlin.github.io/dataframe/describe.html)
 *
 * ### This Describe Overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[describe][org.jetbrains.kotlinx.dataframe.api.describe]` { length `[and][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[describe][org.jetbrains.kotlinx.dataframe.api.describe]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[describe][org.jetbrains.kotlinx.dataframe.api.describe]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 *
 *
 *
 * @param [columns] The [Columns Selector][ColumnsSelector] that specifies which
 * columns of this [DataFrame] should be described.
 */
public fun <T> DataFrame<T>.describe(columns: ColumnsSelector<T, *>): DataFrame<ColumnDescription> =
    describeImpl(getColumnsWithPaths(columns))

/**
 * ## The Describe Operation
 *
 * Computes descriptive statistics for the selected columns in a given [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], including nested columns,
 * returning a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with key summary metrics for each column (with a [ColumnDescription][org.jetbrains.kotlinx.dataframe.api.ColumnDescription] data schema).
 *
 * This function provides a statistical summary for all columns, including nested ones,
 * providing their type, count, unique and missing values, most frequent values,
 * and statistical measures if applicable.
 *
 * ### Summary Metrics:
 *
 * - **`name`** — The name of the column.
 * - **`path`** — path to the column (for hierarchical `DataFrame`)
 * - **`type`** — The data type of the column (e.g., Int, String, Boolean).
 * - **`count`** — The total number of non-null values in the column.
 * - **`unique`** — The number of unique values in the column.
 * - **`nulls`** — The count of null (missing) values in the column.
 * - **`top`** — The most frequently occurring value in the column.
 * - **`freq`** — The frequency of the most common value.
 * - **`mean`** — The arithmetic mean (only for numeric columns).
 * - **`std`** — The standard deviation (only for numeric columns).
 * - **`min`** — The minimum value in the column.
 * - **`p25`** — The 25th percentile value (first quartile).
 * - **`median`** — The median value (50th percentile / second quartile).
 * - **`p75`** — The 75th percentile value (third quartile).
 * - **`max`** — The maximum value in the column.
 *
 * For non-numeric columns, statistical metrics
 * such as `mean` and `std` will return `null`. If column values are incomparable,
 * percentile values (`min`, `p25`, `median`, `p75`, `max`) will also return `null`.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `describe` on the documentation website.](https://kotlin.github.io/dataframe/describe.html)
 *
 * ### This Describe Overload
 * Select single or multiple columns using their names as [String]s.
 * ([String API][`StringAPI`]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[describe][org.jetbrains.kotlinx.dataframe.api.describe]`("length", "age")`
 *
 *
 *
 *
 * @param [columns] The [Column Names][String] that specifies which
 * columns of this [DataFrame] should be described.
 */
public fun <T> DataFrame<T>.describe(vararg columns: String): DataFrame<ColumnDescription> =
    describe { columns.toColumnSet() }

/**
 * ## The Describe Operation
 *
 * Computes descriptive statistics for the selected columns in a given [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], including nested columns,
 * returning a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with key summary metrics for each column (with a [ColumnDescription][org.jetbrains.kotlinx.dataframe.api.ColumnDescription] data schema).
 *
 * This function provides a statistical summary for all columns, including nested ones,
 * providing their type, count, unique and missing values, most frequent values,
 * and statistical measures if applicable.
 *
 * ### Summary Metrics:
 *
 * - **`name`** — The name of the column.
 * - **`path`** — path to the column (for hierarchical `DataFrame`)
 * - **`type`** — The data type of the column (e.g., Int, String, Boolean).
 * - **`count`** — The total number of non-null values in the column.
 * - **`unique`** — The number of unique values in the column.
 * - **`nulls`** — The count of null (missing) values in the column.
 * - **`top`** — The most frequently occurring value in the column.
 * - **`freq`** — The frequency of the most common value.
 * - **`mean`** — The arithmetic mean (only for numeric columns).
 * - **`std`** — The standard deviation (only for numeric columns).
 * - **`min`** — The minimum value in the column.
 * - **`p25`** — The 25th percentile value (first quartile).
 * - **`median`** — The median value (50th percentile / second quartile).
 * - **`p75`** — The 75th percentile value (third quartile).
 * - **`max`** — The maximum value in the column.
 *
 * For non-numeric columns, statistical metrics
 * such as `mean` and `std` will return `null`. If column values are incomparable,
 * percentile values (`min`, `p25`, `median`, `p75`, `max`) will also return `null`.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `describe` on the documentation website.](https://kotlin.github.io/dataframe/describe.html)
 *
 * ### This Describe Overload
 *
 * @param [columns] The [Column Accessors][ColumnReference] that specifies which
 * columns of this [DataFrame] should be described.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.describe(vararg columns: ColumnReference<C>): DataFrame<ColumnDescription> =
    describe { columns.toColumnSet() }

/**
 * ## The Describe Operation
 *
 * Computes descriptive statistics for the selected columns in a given [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], including nested columns,
 * returning a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with key summary metrics for each column (with a [ColumnDescription][org.jetbrains.kotlinx.dataframe.api.ColumnDescription] data schema).
 *
 * This function provides a statistical summary for all columns, including nested ones,
 * providing their type, count, unique and missing values, most frequent values,
 * and statistical measures if applicable.
 *
 * ### Summary Metrics:
 *
 * - **`name`** — The name of the column.
 * - **`path`** — path to the column (for hierarchical `DataFrame`)
 * - **`type`** — The data type of the column (e.g., Int, String, Boolean).
 * - **`count`** — The total number of non-null values in the column.
 * - **`unique`** — The number of unique values in the column.
 * - **`nulls`** — The count of null (missing) values in the column.
 * - **`top`** — The most frequently occurring value in the column.
 * - **`freq`** — The frequency of the most common value.
 * - **`mean`** — The arithmetic mean (only for numeric columns).
 * - **`std`** — The standard deviation (only for numeric columns).
 * - **`min`** — The minimum value in the column.
 * - **`p25`** — The 25th percentile value (first quartile).
 * - **`median`** — The median value (50th percentile / second quartile).
 * - **`p75`** — The 75th percentile value (third quartile).
 * - **`max`** — The maximum value in the column.
 *
 * For non-numeric columns, statistical metrics
 * such as `mean` and `std` will return `null`. If column values are incomparable,
 * percentile values (`min`, `p25`, `median`, `p75`, `max`) will also return `null`.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.Select.SelectSelectingOptions].
 *
 * For more information: [See `describe` on the documentation website.](https://kotlin.github.io/dataframe/describe.html)
 *
 * ### This Describe Overload
 *
 * @param [columns] The [KProperties][KProperty] that specifies which
 * columns of this [DataFrame] should be described.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.describe(vararg columns: KProperty<C>): DataFrame<ColumnDescription> =
    describe { columns.toColumnSet() }

// endregion
