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
import org.jetbrains.kotlinx.dataframe.documentation.NA
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.nameGenerator
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

// region docs

// endregion

// region DataSchema

/**
 * A [DataSchema] of the [valueCounts] result. It declares the [count] column
 * with the number of occurrences of each counted value.
 *
 * For more information: [See `valueCounts` on the documentation website.](https://kotlin.github.io/dataframe/valuecounts.html)
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
 * and [`NA`][NA] values (`null`s and `NaN`s) are not counted.
 *
 * See also:
 * - [valueCounts][DataFrame.valueCounts] — counts of unique rows in a [DataFrame].
 * - [countDistinct][DataFrame.countDistinct] — the number of distinct values, without their counts.
 * - [distinct][DataColumn.distinct] — the distinct values themselves, without their counts.
 * - [count][DataColumn.count] — the total number of elements in this [DataColumn].
 *
 * For more information: [See `valueCounts` on the documentation website.](https://kotlin.github.io/dataframe/valuecounts.html)
 *
 * All summary statistics: [See "Summary statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html)
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
 * @param [sort] If `true` (default), the result is sorted by count.
 * Otherwise, the counted values keep the order of their first occurrence.
 * @param [ascending] The sorting direction. If `false` (default), the most frequent values come first.
 * Only used when [sort] is `true`.
 * @param [dropNA] If `true` (default), [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values (`null`s and `NaN`s) are not counted
 * and are excluded from the result.
 * @param [resultColumn] The name of the resulting count column. Default — `"count"`.
 * If a column with this name is already present, the name is made unique by appending
 * a number to it (for example, `"count"` becomes `"count1"`).
 * @return A [DataFrame] with the distinct values of this [DataColumn] and their counts.
 */
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
 * Returns a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] containing counts of unique rows (or combinations of selected values) in this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Rows are compared by the values in the selected [columns]. If no columns are selected, values from
 * all columns are used.
 *
 *
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * The resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] contains:
 * - the distinct combinations of these values,
 * - a new [Int] column (named [resultColumn], default is `"count"`) with the number of occurrences of each combination.
 *
 * By default, the result is sorted by count in descending order (the most frequent combination first),
 * and a row is not counted at all if any of its selected values is [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * See also:
 * - [valueCounts][org.jetbrains.kotlinx.dataframe.DataColumn.valueCounts] — counts of unique values in a single [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 * - [countDistinct][org.jetbrains.kotlinx.dataframe.DataFrame.countDistinct] — the number of distinct rows, without their counts.
 * - [distinct][org.jetbrains.kotlinx.dataframe.DataFrame.distinct] — the distinct rows themselves, without their counts.
 * - [count][org.jetbrains.kotlinx.dataframe.DataFrame.count] — the total number of rows in this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * For more information: [See `valueCounts` on the documentation website.](https://kotlin.github.io/dataframe/valuecounts.html)
 *
 * All summary statistics: [See "Summary statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html)
 *
 * ### This ValueCounts Overload
 *
 *
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
 * @param [sort] If `true` (default), the result is sorted by count.
 * Otherwise, the counted values keep the order of their first occurrence.
 * @param [ascending] The sorting direction. If `false` (default), the most frequent values come first.
 * Only used when [sort] is `true`.
 * @param [dropNA] If `true` (default), [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values (`null`s and `NaN`s) are not counted
 * and are excluded from the result.
 * @param [resultColumn] The name of the resulting count column. Default — `"count"`.
 * If a column with this name is already present, the name is made unique by appending
 * a number to it (for example, `"count"` becomes `"count1"`).
 * @param [columns] The [ColumnsSelector] used to select the columns whose distinct value combinations
 * are counted.
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
 * Returns a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] containing counts of unique rows (or combinations of selected values) in this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Rows are compared by the values in the selected [columns]. If no columns are selected, values from
 * all columns are used.
 *
 *
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * The resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] contains:
 * - the distinct combinations of these values,
 * - a new [Int] column (named [resultColumn], default is `"count"`) with the number of occurrences of each combination.
 *
 * By default, the result is sorted by count in descending order (the most frequent combination first),
 * and a row is not counted at all if any of its selected values is [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * See also:
 * - [valueCounts][org.jetbrains.kotlinx.dataframe.DataColumn.valueCounts] — counts of unique values in a single [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn].
 * - [countDistinct][org.jetbrains.kotlinx.dataframe.DataFrame.countDistinct] — the number of distinct rows, without their counts.
 * - [distinct][org.jetbrains.kotlinx.dataframe.DataFrame.distinct] — the distinct rows themselves, without their counts.
 * - [count][org.jetbrains.kotlinx.dataframe.DataFrame.count] — the total number of rows in this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * For more information: [See `valueCounts` on the documentation website.](https://kotlin.github.io/dataframe/valuecounts.html)
 *
 * All summary statistics: [See "Summary statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html)
 *
 * ### This ValueCounts Overload
 *
 *
 * Select single or multiple columns using their names as [String]s.
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### Example
 *
 * ```kotlin
 * // Counts the unique combinations of the values in the "name" and "city" columns,
 * // starting with the most frequent one
 * df.valueCounts("name", "city")
 * ```
 *
 * @param [sort] If `true` (default), the result is sorted by count.
 * Otherwise, the counted values keep the order of their first occurrence.
 * @param [ascending] The sorting direction. If `false` (default), the most frequent values come first.
 * Only used when [sort] is `true`.
 * @param [dropNA] If `true` (default), [`NA`][org.jetbrains.kotlinx.dataframe.documentation.NA] values (`null`s and `NaN`s) are not counted
 * and are excluded from the result.
 * @param [resultColumn] The name of the resulting count column. Default — `"count"`.
 * If a column with this name is already present, the name is made unique by appending
 * a number to it (for example, `"count"` becomes `"count1"`).
 * @param [columns] The names of the columns whose distinct value combinations are counted.
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
