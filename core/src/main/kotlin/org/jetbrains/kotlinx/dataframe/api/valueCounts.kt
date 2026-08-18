package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.annotations.RequiredByIntellijPlugin
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.NALink
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.nameGenerator
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

// region docs

/**
 * @param [sort\] If `true` (default), the result is sorted by count.
 * Otherwise, the counted values keep the order of their first occurrence.
 * @param [ascending\] The sorting direction. If `false` (default), the most frequent values come first.
 * Only used when [sort\] is `true`.
 * @param [dropNA\] If `true` (default), {@include [NALink]} values (`null`s and `NaN`s) are not counted
 * and are excluded from the result.
 * @param [resultColumn\] The name of the resulting count column. Default — `"count"`.
 * If a column with this name is already present, the name is made unique by appending
 * a number to it (for example, `"count"` becomes `"count1"`).
 */
@ExcludeFromSources
private typealias ValueCountsParams = Nothing

/**
 * Returns a [DataFrame] containing the counts of unique rows (or combinations of selected values) in this [DataFrame].
 *
 * Rows are compared by the values in the selected [columns\]. If no columns are selected, values from
 * all columns are used.
 *
 * {@include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]}
 *
 * The resulting [DataFrame] contains:
 * - the distinct combinations of these values,
 * - a new [Int] column (named [resultColumn\], default is `"count"`) with the number of occurrences of each combination.
 *
 * By default, the result is sorted by count in descending order (the most frequent combination first),
 * and a row is not counted at all if any of its selected values is {@include [NALink]}.
 *
 * See also:
 * - [`valueCounts`][DataColumn.valueCounts] — counts of unique values in a single [DataColumn].
 * - [`countDistinct`][DataFrame.countDistinct] — the number of distinct rows or selected value combinations,
 * without their counts.
 * - [`distinct`][DataFrame.distinct] — the distinct rows themselves, without their counts.
 * - [`count`][DataFrame.count] — the total number of rows in this [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.ValueCounts]}
 *
 * All summary statistics: {@include [DocumentationUrls.Statistics]}
 *
 * ### This `valueCounts` Overload
 */
@ExcludeFromSources
private typealias CommonValueCountsDocs = Nothing

// endregion

// region DataSchema

/**
 * A [DataSchema] of the [valueCounts] result. It declares the [count] column
 * with the number of occurrences of each counted value.
 *
 * For more information: {@include [DocumentationUrls.ValueCounts]}
 */
@DataSchema
public interface ValueCount {

    /** The number of occurrences of the value (or combination of values) in this row. */
    public val count: Int
}

// endregion

// region DataColumn

internal val defaultCountColumnName: String = ValueCount::count.name

/**
 * Returns a [DataFrame] containing counts of unique values in this [DataColumn].
 *
 * The resulting [DataFrame] contains:
 * - the column with the distinct values of the original [DataColumn]
 * - a new [Int] column ([resultColumn]) with the number of occurrences of each value.
 *
 * By default, the result is sorted by count in descending order (the most frequent value first),
 * and {@include [NALink]} values (`null`s and `NaN`s) are not counted.
 *
 * See also:
 * - [`valueCounts`][DataFrame.valueCounts] — counts of unique rows in a [DataFrame].
 * - [`countDistinct`][DataFrame.countDistinct] — the number of distinct rows
 * or selected value combinations in a [DataFrame].
 * - [`distinct`][DataColumn.distinct] — the distinct values themselves, without their counts.
 * - [`count`][DataColumn.count] — the total number of elements in this [DataColumn].
 *
 * For more information: {@include [DocumentationUrls.ValueCounts]}
 *
 * All summary statistics: {@include [DocumentationUrls.Statistics]}
 *
 * ### Example
 *
 * ```kotlin
 * // Counts the unique values in the "city" column,
 * // starting with the most frequent one
 * df.city.valueCounts()
 *
 * // Counts the unique values in the "city" column, including `null`s,
 * // in the order of their first occurrence, in a column named "quantity"
 * df.city.valueCounts(sort = false, dropNA = false, resultColumn = "quantity")
 * ```
 *
 * @include [ValueCountsParams]
 * @return A [DataFrame] with the distinct values of this [DataColumn] and their counts.
 */
@Refine
@Interpretable("DataColumnValueCounts")
@RequiredByIntellijPlugin
public fun <T> DataColumn<T>.valueCounts(
    sort: Boolean = true,
    ascending: Boolean = false,
    dropNA: Boolean = true,
    resultColumn: String = defaultCountColumnName,
): DataFrame<ValueCount> {
    var grouped = toList().groupBy { it }.map { it.key to it.value.size }
    if (sort) {
        grouped = if (ascending) {
            grouped.sortedBy { it.second }
        } else {
            grouped.sortedByDescending { it.second }
        }
    }
    if (dropNA) grouped = grouped.filter { !it.first.isNA }
    val nulls = if (dropNA) false else hasNulls()
    val values = DataColumn.createByType(name(), grouped.map { it.first }, type().withNullability(nulls))
    val countName = if (resultColumn == name()) resultColumn + "1" else resultColumn
    val counts = DataColumn.createByType(countName, grouped.map { it.second }, typeOf<Int>())
    return dataFrameOf(values, counts).cast()
}

// endregion

// region DataFrame

/**
 * {@include [CommonValueCountsDocs]}
 * {@include [SelectingColumns.ColumnsSelectionDsl]}
 *
 * #### Example
 *
 * ```kotlin
 * // Counts the unique combinations of the values in the "name" and "city" columns,
 * // starting with the most frequent one
 * df.valueCounts { name and city }
 *
 * // Counts the unique rows of the whole dataframe,
 * // including rows with `NA` values, in the order of their first occurrence
 * df.valueCounts(sort = false, dropNA = false)
 * ```
 *
 * @include [ValueCountsParams]
 * @param [columns] The optional [ColumnsSelector] that selects the columns whose distinct value combinations
 * are counted. If `null` or omitted, all columns are used.
 * @return A [DataFrame] with the distinct value combinations and their counts.
 */
@Refine
@Interpretable("ValueCounts")
public fun <T> DataFrame<T>.valueCounts(
    sort: Boolean = true,
    ascending: Boolean = false,
    dropNA: Boolean = true,
    resultColumn: String = defaultCountColumnName,
    columns: ColumnsSelector<T, *>? = null,
): DataFrame<T> {
    var df = if (columns != null) select(columns) else this
    if (dropNA) df = df.dropNA()

    val rows by columnGroup()
    val countName = nameGenerator().addUnique(resultColumn)
    return df
        .asColumnGroup(rows)
        .asDataColumn()
        .valueCounts(sort, ascending, dropNA, countName)
        .ungroup { rows }
        .cast()
}

/**
 * {@include [CommonValueCountsDocs]}
 * {@include [SelectingColumns.ColumnNamesApi]}
 *
 * #### Example
 *
 * ```kotlin
 * // Counts the unique combinations of the values in the "name" and "city" columns,
 * // starting with the most frequent one
 * df.valueCounts("name", "city")
 * ```
 *
 * @param [columns] (optional) The names of the columns whose distinct value combinations are counted. If not supplied,
 * all columns are selected.
 * @include [ValueCountsParams]
 * @return A [DataFrame] with the distinct value combinations and their counts.
 */
public fun <T> DataFrame<T>.valueCounts(
    vararg columns: String,
    sort: Boolean = true,
    ascending: Boolean = false,
    dropNA: Boolean = true,
    resultColumn: String = defaultCountColumnName,
): DataFrame<T> = valueCounts(sort, ascending, dropNA, resultColumn) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.valueCounts(
    vararg columns: AnyColumnReference,
    sort: Boolean = true,
    ascending: Boolean = false,
    dropNA: Boolean = true,
    resultColumn: String = defaultCountColumnName,
): DataFrame<T> = valueCounts(sort, ascending, dropNA, resultColumn) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.valueCounts(
    vararg columns: KProperty<*>,
    sort: Boolean = true,
    ascending: Boolean = false,
    dropNA: Boolean = true,
    resultColumn: String = defaultCountColumnName,
): DataFrame<T> = valueCounts(sort, ascending, dropNA, resultColumn) { columns.toColumnSet() }

// endregion
