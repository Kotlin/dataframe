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
import org.jetbrains.kotlinx.dataframe.annotations.StringApiInterpretable
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

// endregion

// region DataSchema

/**
 * A [<code>DataSchema</code>][DataSchema] of the [<code>valueCounts</code>][valueCounts] result. It declares the [<code>count</code>][count] column
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
 * Returns a [<code>DataFrame</code>][DataFrame] containing counts of unique values in this [<code>DataColumn</code>][DataColumn].
 *
 * The resulting [<code>DataFrame</code>][DataFrame] contains:
 * - the column with the distinct values of the original [<code>DataColumn</code>][DataColumn]
 * - a new [<code>Int</code>][Int] column with the number of occurrences of each value.
 * This column is called `"count"` by default unless it is overridden by [<code>resultColumn</code>][resultColumn].
 *
 * By default, the result is sorted by count in descending order (the most frequent value first),
 * and [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values (`null`s and `NaN`s) are not counted.
 *
 * See also:
 * - [<code>`valueCounts`</code>][DataFrame.valueCounts] — counts of unique rows in a [<code>DataFrame</code>][DataFrame].
 * - [<code>`countDistinct`</code>][DataFrame.countDistinct] — the number of distinct rows
 * or selected value combinations in a [<code>DataFrame</code>][DataFrame].
 * - [<code>`distinct`</code>][DataColumn.distinct] — the distinct values themselves, without their counts.
 * - [<code>`count`</code>][DataColumn.count] — the total number of elements in this [<code>DataColumn</code>][DataColumn].
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
 * @param [dropNA] If `true` (default), [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values (`null`s and `NaN`s) are not counted
 * and are excluded from the result.
 * @param [resultColumn] The name of the resulting count column. Default — `"count"`.
 * If a column with this name is already present, the name is made unique by appending
 * a number to it (for example, `"count"` becomes `"count1"`).
 * @return A [<code>DataFrame</code>][DataFrame] with the distinct values of this [<code>DataColumn</code>][DataColumn] and their counts.
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
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] containing the counts of unique rows (or combinations of selected values) in this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Rows are compared by the values in the selected [columns]. If no columns are selected, values from
 * all columns are used.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * The resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] contains:
 * - the distinct combinations of these values,
 * - a new [<code>Int</code>][Int] column with the number of occurrences of each combination.
 * This column is called `"count"` by default unless it is overridden by [resultColumn].
 *
 * By default, the result is sorted by count in descending order (the most frequent combination first),
 * and a row is not counted at all if any of its selected values is [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * See also:
 * - [<code>`valueCounts`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.valueCounts] — counts of unique values in a single [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 * - [<code>`countDistinct`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.countDistinct] — the number of distinct rows or selected value combinations,
 * without their counts.
 * - [<code>`distinct`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.distinct] — the distinct rows themselves, without their counts.
 * - [<code>`count`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.count] — the total number of rows in this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * For more information: [See `valueCounts` on the documentation website.](https://kotlin.github.io/dataframe/valuecounts.html)
 *
 * All summary statistics: [See "Summary statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html)
 *
 * ### This `valueCounts` Overload
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
 * @param [dropNA] If `true` (default), [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values (`null`s and `NaN`s) are not counted
 * and are excluded from the result.
 * @param [resultColumn] The name of the resulting count column. Default — `"count"`.
 * If a column with this name is already present, the name is made unique by appending
 * a number to it (for example, `"count"` becomes `"count1"`).
 * @param [columns] The optional [<code>ColumnsSelector</code>][ColumnsSelector] that selects the columns whose distinct value combinations
 * are counted. If `null` or omitted, all columns are used.
 * @return A [<code>DataFrame</code>][DataFrame] with the distinct value combinations and their counts.
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
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] containing the counts of unique rows (or combinations of selected values) in this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Rows are compared by the values in the selected [columns]. If no columns are selected, values from
 * all columns are used.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * The resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] contains:
 * - the distinct combinations of these values,
 * - a new [<code>Int</code>][Int] column with the number of occurrences of each combination.
 * This column is called `"count"` by default unless it is overridden by [resultColumn].
 *
 * By default, the result is sorted by count in descending order (the most frequent combination first),
 * and a row is not counted at all if any of its selected values is [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA].
 *
 * See also:
 * - [<code>`valueCounts`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.valueCounts] — counts of unique values in a single [<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn].
 * - [<code>`countDistinct`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.countDistinct] — the number of distinct rows or selected value combinations,
 * without their counts.
 * - [<code>`distinct`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.distinct] — the distinct rows themselves, without their counts.
 * - [<code>`count`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.count] — the total number of rows in this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * For more information: [See `valueCounts` on the documentation website.](https://kotlin.github.io/dataframe/valuecounts.html)
 *
 * All summary statistics: [See "Summary statistics" on the documentation website.](https://kotlin.github.io/dataframe/summarystatistics.html)
 *
 * ### This `valueCounts` Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
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
 * @param [sort] If `true` (default), the result is sorted by count.
 * Otherwise, the counted values keep the order of their first occurrence.
 * @param [ascending] The sorting direction. If `false` (default), the most frequent values come first.
 * Only used when [sort] is `true`.
 * @param [dropNA] If `true` (default), [<code>`NA`</code>][org.jetbrains.kotlinx.dataframe.documentation.NA] values (`null`s and `NaN`s) are not counted
 * and are excluded from the result.
 * @param [resultColumn] The name of the resulting count column. Default — `"count"`.
 * If a column with this name is already present, the name is made unique by appending
 * a number to it (for example, `"count"` becomes `"count1"`).
 * @return A [<code>DataFrame</code>][DataFrame] with the distinct value combinations and their counts.
 */
@Refine
@StringApiInterpretable(interpreter = "ValueCounts", stringArgument = "columns", targetArgument = "columns")
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
