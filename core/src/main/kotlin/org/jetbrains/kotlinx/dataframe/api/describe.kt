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
import org.jetbrains.kotlinx.dataframe.documentation.`Selecting Columns`
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

/**
 * ### Summary Metrics:

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
 */
@ExcludeFromSources
internal typealias SummaryMetrics = Nothing

/**
 * ## The Describe Operation
 *
 * Computes descriptive statistics for {@get COLUMNS all} columns in a given [DataFrame], including nested columns,
 * returning a [DataFrame] with key summary metrics for each column (with a [ColumnDescription] data schema).
 *
 * This function provides a statistical summary for all columns, including nested ones,
 * providing their type, count, unique and missing values, most frequent values,
 * and statistical measures if applicable.
 *
 * {@include [SummaryMetrics]}
 */
@ExcludeFromSources
internal typealias Describe = Nothing

/**
 * {@include [Describe]} {@set COLUMNS the selected}
 *
 * See [Selecting Columns][Select.SelectSelectingOptions].
 *
 * For more information: {@include [DocumentationUrls.Describe]}
 *
 * ### This Describe Overload
 */
@ExcludeFromSources
internal typealias DescribeWithSelection = Nothing

/** {@set [`Selecting Columns`.OPERATION] [describe][describe]} */
@ExcludeFromSources
private typealias SetDescribeOperationArg = Nothing
// endregion

// region DataColumn

/**
 * Computes descriptive statistics for a given [DataColumn], returning a [DataFrame] with key summary metrics.
 *
 * This function provides a statistical summary of a given column, including its type, count, uniqueness,
 * missing values, most frequent values, and statistical measures if applicable.
 *
 * {@include [SummaryMetrics]}
 *
 * @return A [DataFrame] where each row represents the descriptive statistics of a single column.
 *         The output contains one row per described column with the summary metrics as columns.
 */
public fun <T> DataColumn<T>.describe(): DataFrame<ColumnDescription> = describeImpl(listOf(this))

// endregion

// region DataFrame

/**
 * {@include [Describe]}
 *
 * @return A [DataFrame] where each row represents the descriptive statistics of a single column in the input DataFrame.
 *         The output contains one row per described column with the summary metrics as columns.
 */
public fun <T> DataFrame<T>.describe(): DataFrame<ColumnDescription> =
    describe {
        colsAtAnyDepth().filter { !it.isColumnGroup() }
    }

/**
 * @include [DescribeWithSelection]
 * @include [`Selecting Columns`.`Columns Selection DSL`.`Columns Selection DSL with Example`] {@include [SetDescribeOperationArg]}
 * @param [columns] The [Columns Selector][ColumnsSelector] that specifies which
 * columns of this [DataFrame] should be described.
 */
public fun <T> DataFrame<T>.describe(columns: ColumnsSelector<T, *>): DataFrame<ColumnDescription> =
    describeImpl(getColumnsWithPaths(columns))

/**
 * @include [DescribeWithSelection]
 * @include [`Selecting Columns`.`Column Names API`.`Column Names API with Example`] {@include [SetDescribeOperationArg]}
 * @param [columns] The [Column Names][String] that specifies which
 * columns of this [DataFrame] should be described.
 */
public fun <T> DataFrame<T>.describe(vararg columns: String): DataFrame<ColumnDescription> =
    describe { columns.toColumnSet() }

/**
 * @include [DescribeWithSelection]
 * @include [`Selecting Columns`.ColumnAccessors.WithExample] {@include [SetDescribeOperationArg]}
 * @param [columns] The [Column Accessors][ColumnReference] that specifies which
 * columns of this [DataFrame] should be described.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.describe(vararg columns: ColumnReference<C>): DataFrame<ColumnDescription> =
    describe { columns.toColumnSet() }

/**
 * @include [DescribeWithSelection]
 * @include [`Selecting Columns`.KProperties.WithExample] {@include [SetDescribeOperationArg]}
 * @param [columns] The [KProperties][KProperty] that specifies which
 * columns of this [DataFrame] should be described.
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C : Number?> DataFrame<T>.describe(vararg columns: KProperty<C>): DataFrame<ColumnDescription> =
    describe { columns.toColumnSet() }

// endregion
