package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.api.concatImpl
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.type

@ExcludeFromSources
private interface ConcatDocs {

    /**
     * The result contains the union of the input schemas. Columns are ordered by their first appearance in the
     * inputs. When two or more input schemas are combined, columns with the same name form one result column.
     */
    typealias SchemaUnification = Nothing

    /**
     * When two or more columns contribute to one result column, the result keeps their shared
     * [runtime type][DataColumn.type], except for its nullability. If their runtime types differ, the result runtime
     * type is inferred from the concatenated values. The result runtime type is nullable only if the resulting values
     * contain `null`.
     */
    typealias RuntimeTypeUnification = Nothing

    /**
     * If all input columns are empty, their runtime types determine the result runtime type.
     */
    typealias EmptyColumnTypeUnification = Nothing

    /**
     * Missing values in a [List] column are represented by empty lists, and missing values in a [FrameColumn] by empty
     * [DataFrame]s; these columns do not become nullable. A missing [ColumnGroup] remains a column group, and its
     * nested columns are filled according to these same rules recursively.
     */
    typealias MissingColumnValues = Nothing

    /**
     * @include [SchemaUnification]
     *
     * @include [RuntimeTypeUnification]
     *
     * @include [EmptyColumnTypeUnification]
     *
     * When a value column is missing from an input that contributes rows, its values for those rows are filled with
     * `null`, and its result runtime type becomes nullable.
     *
     * @include [MissingColumnValues]
     */
    typealias DataFrameSchemaUnification = Nothing

    /**
     * @include [SchemaUnification]
     *
     * @include [RuntimeTypeUnification]
     *
     * If a row does not contain a result value column, the corresponding value is `null`, and the result runtime type
     * of that column becomes nullable.
     *
     * @include [MissingColumnValues]
     */
    typealias DataRowSchemaUnification = Nothing

    /**
     * The result keeps the name of the first column.
     * If there is only one input column, its [runtime type][DataColumn.type] is preserved.
     *
     * @include [RuntimeTypeUnification]
     *
     * @include [EmptyColumnTypeUnification]
     */
    typealias DataColumnUnification = Nothing
}

// region DataColumn

/**
 * Returns a [DataColumn] containing the values of this column followed by the values of [other].
 *
 * The columns in [other] are processed in argument order. The order within each column is preserved.
 *
 * @include [ConcatDocs.DataColumnUnification]
 *
 * Empty columns add no values.
 *
 * For more information: {@include [DocumentationUrls.Concat]}
 *
 * See also [Iterable.concat], which concatenates an iterable of columns.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ConcatSamples.concatDataColumns]
 *
 * @param [T] The type of values in this [DataColumn] and the columns in [other].
 * @param [other] The columns whose values are appended to this column.
 * @return A [DataColumn] with the same name as this column. It contains the values of this column followed by the
 * values of each column in [other] in argument order.
 */
public fun <T> DataColumn<T>.concat(vararg other: DataColumn<T>): DataColumn<T> = concatImpl(name, listOf(this) + other)

/**
 * Returns a [DataFrame] containing the rows from every [DataFrame] stored in this column.
 *
 * [DataFrame]s are concatenated in the order in which they appear in the column.
 * Row order within each [DataFrame] is preserved.
 *
 * @include [ConcatDocs.DataFrameSchemaUnification]
 *
 * An empty column of [DataFrame] type produces an empty [DataFrame].
 * A stored [DataFrame] with zero rows adds no rows, while its columns still participate in schema unification.
 *
 * For more information: {@include [DocumentationUrls.Concat]}
 *
 * See also:
 * - [Iterable.concat] — concatenates an iterable of [DataFrame]s.
 * - [DataFrame.concat] — appends [DataFrame]s to a receiver.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ConcatSamples.concatFrameColumn]
 *
 * @param [T] The schema marker type of the [DataFrame]s stored in this [DataColumn].
 * @return A [DataFrame] containing all rows from the [DataFrame]s stored in the [DataColumn].
 */
public fun <T> DataColumn<DataFrame<T>>.concat(): DataFrame<T> = values.concat()

/**
 * Returns a [List] containing the elements of every [Collection] stored in this column.
 *
 * Collections are processed in the order in which they appear in the column. Order within the collections is preserved.
 * Empty collections add no elements.
 *
 * For more information: {@include [DocumentationUrls.Concat]}
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ConcatSamples.concatCollectionColumn]
 *
 * @param [T] The type of elements in the [Collection]s stored in this [DataColumn].
 * @return A [List] containing the concatenated elements of all collections stored in this [DataColumn].
 */
public fun <T> DataColumn<Collection<T>>.concat(): List<T> = values.flatten()

// endregion

// region DataRow

/**
 * Returns a [DataFrame] containing this [DataRow] followed by [rows].
 *
 * Rows are processed in argument order.
 *
 * @include [ConcatDocs.DataRowSchemaUnification]
 *
 * For more information: {@include [DocumentationUrls.Concat]}
 *
 * See also:
 * - [DataFrame.concat] — appends an iterable of rows to a [DataFrame].
 * - [Iterable.concat] — creates a [DataFrame] from an iterable of nullable rows.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ConcatSamples.concatDataRows_properties]
 *
 * @param [T] The schema marker type of this [DataRow] and the rows in [rows].
 * @param [rows] The rows to append to this [DataRow].
 * @return A [DataFrame] containing this [DataRow] followed by [rows] in argument order.
 */
public fun <T> DataRow<T>.concat(vararg rows: DataRow<T>): DataFrame<T> = (listOf(this) + rows).concat()

// endregion

// region DataFrame

/**
 * Returns a [DataFrame] containing the rows of this [DataFrame] followed by the rows of [frames].
 *
 * The [DataFrame]s in [frames] are processed in argument order, and row order within every [DataFrame] is preserved.
 * If no [frames] are supplied, the receiver is returned as the same [DataFrame] instance.
 *
 * @include [ConcatDocs.DataFrameSchemaUnification]
 *
 * A [DataFrame] supplied in [frames] with zero rows adds no rows, while its columns still participate in schema
 * unification.
 *
 * For more information: {@include [DocumentationUrls.Concat]}
 *
 * See also [Iterable.concat], which concatenates an iterable of [DataFrame]s.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ConcatSamples.concatDataFrames]
 *
 * @param [T] The schema marker type of this [DataFrame] and the [DataFrame]s in [frames].
 * @param [frames] The [DataFrame]s whose rows are appended to this [DataFrame].
 * @return A [DataFrame] containing the rows of this [DataFrame] followed by the rows of each [DataFrame] in [frames]
 * in argument order.
 */
public fun <T> DataFrame<T>.concat(vararg frames: DataFrame<T>): DataFrame<T> = concatImpl(listOf(this) + frames)

/**
 * Returns a [DataFrame] containing the rows of this [DataFrame] followed by the rows of [frame].
 *
 * Row order within both [DataFrame]s is preserved.
 *
 * @include [ConcatDocs.DataFrameSchemaUnification]
 *
 * If [frame] has zero rows, it adds no rows, while its columns still participate in schema unification.
 *
 * For more information: {@include [DocumentationUrls.Concat]}
 *
 * See also:
 * - [DataFrame.concat] — other [DataFrame] and row overloads.
 * - [Iterable.concat] — concatenates an iterable of [DataFrame]s.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ConcatSamples.concatDataFramesWithDifferentSchemas]
 *
 * @param [T] The schema marker type of this [DataFrame].
 * @param [T1] The schema marker type of [frame].
 * @param [frame] The [DataFrame] whose rows are appended to this [DataFrame].
 * @return A [DataFrame] with a schema that represents both input schemas. It contains the rows of this [DataFrame]
 * followed by the rows of [frame].
 */
@Refine
@Interpretable("DataFrameConcat")
public infix fun <T, T1> DataFrame<T>.concat(frame: DataFrame<T1>): DataFrame<Any> =
    concatImpl(listOf(this) + frame).cast()

/**
 * Returns a [DataFrame] containing the rows of this [DataFrame] followed by [rows].
 *
 * The rows from [rows] retain their iteration order.
 *
 * @include [ConcatDocs.DataFrameSchemaUnification]
 *
 * An empty [rows] iterable appends no rows.
 *
 * For more information: {@include [DocumentationUrls.Concat]}
 *
 * See also:
 * - [DataFrame.append] — appends rows supplied as values.
 * - [DataRow.concat] — starts with a [DataRow].
 * - [Iterable.concat] — creates a [DataFrame] from an iterable of nullable rows.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ConcatSamples.concatDataFrameAndRows]
 *
 * @param [T] The schema marker type of this [DataFrame] and the rows in [rows].
 * @param [rows] The rows to append to this [DataFrame].
 * @return A [DataFrame] containing the rows of this [DataFrame] followed by [rows] in iteration order.
 */
@JvmName("concatT")
public fun <T> DataFrame<T>.concat(rows: Iterable<DataRow<T>>): DataFrame<T> = (rows() + rows).concat()

/**
 * Returns a [DataFrame] containing the rows of this [DataFrame] followed by the rows from [frames].
 *
 * The [DataFrame]s in [frames] are concatenated in iteration order, and row order within every [DataFrame] is
 * preserved.
 *
 * @include [ConcatDocs.DataFrameSchemaUnification]
 *
 * A [DataFrame] supplied in [frames] with zero rows adds no rows, while its columns still participate in schema
 * unification.
 *
 * If [frames] is empty, the receiver is returned as the same [DataFrame] instance.
 *
 * For more information: {@include [DocumentationUrls.Concat]}
 *
 * See also:
 * - [Iterable.concat] — concatenates an iterable of [DataFrame]s.
 * - [DataFrame.concat] — other [DataFrame] and row overloads.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ConcatSamples.concatDataFrameAndIterable]
 *
 * @param [T] The schema marker type of this [DataFrame] and the [DataFrame]s in [frames].
 * @param [frames] The [DataFrame]s whose rows are appended to this [DataFrame].
 * @return A [DataFrame] containing the rows of this [DataFrame] followed by the rows from [frames] in iteration order.
 */
public fun <T> DataFrame<T>.concat(frames: Iterable<DataFrame<T>>): DataFrame<T> = (listOf(this) + frames).concat()

// endregion

// region GroupBy

/**
 * Returns a [DataFrame] containing all [groups] in this [GroupBy].
 *
 * Groups are concatenated in the order in which they appear in [groups], and row order within every group is
 * preserved. The grouping keys stored in [keys] are not added to the result.
 * If a group already contains a column with the same name as a key, that column is preserved unchanged.
 *
 * Check out [`groupBy` Grammar][GroupByDocs.Grammar] for more information.
 *
 * For more information: {@include [DocumentationUrls.GroupBy]} {@include [DocumentationUrls.Concat]}
 *
 * See also [GroupBy.concatWithKeys], which adds grouping key columns that are missing from the groups.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ConcatSamples.concatGroupBy_properties]
 *
 * @param [T] The schema marker type of the grouping [keys][GroupBy.keys].
 * @param [G] The schema marker type of the groups.
 * @return A [DataFrame] containing the rows from all groups, without any grouping key columns added.
 */
public fun <T, G> GroupBy<T, G>.concat(): DataFrame<G> = groups.concat()

/**
 * Returns a [DataFrame] containing all [groups] in this [GroupBy]
 * and every grouping key column that is missing from those groups.
 *
 * Groups are concatenated in the order in which they appear in [groups], and row order within every group is
 * preserved. The grouping keys stored in [keys] are added to each group unless it already contains a column with
 * the same name. Each added key value is repeated for every row in its group. An existing group column is preserved
 * unchanged.
 *
 * This function is especially useful when grouping by expressions or renamed columns,
 * and you want the resulting [DataFrame] to include those keys as part of the output.
 *
 * Check out [`groupBy` Grammar][GroupByDocs.Grammar] for more information.
 *
 * For more information: {@include [DocumentationUrls.GroupBy]} {@include [DocumentationUrls.Concat]}
 *
 * See also [GroupBy.concat], which ignores separate grouping keys.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ConcatSamples.concatGroupByWithKeys_properties]
 *
 * @param [T] The schema marker type of the grouping [keys][GroupBy.keys].
 * @param [G] The schema marker type of the groups.
 * @return A [DataFrame] containing the rows from all groups and all missing grouping key columns.
 */
@Refine
@Interpretable("ConcatWithKeys")
public fun <T, G> GroupBy<T, G>.concatWithKeys(): DataFrame<G> =
    mapToFrames {
        val rowsCount = group.rowsCount()
        val keyColumns = keys.columns().filter { it.name !in group.columnNames() }.map { keyColumn ->
            DataColumn.createByType(keyColumn.name, List(rowsCount) { key[keyColumn] }, keyColumn.type)
        }
        group.addAll(keyColumns)
    }.concat()

// endregion

// region ReducedGroupBy

/**
 * Applies the stored reducer to every group and returns a [DataFrame] containing one resulting row per group, in
 * the order in which the groups appear in the underlying [GroupBy].
 *
 * If the reduced rows have different schemas, the result contains the union of their schemas. Columns are ordered by
 * their first appearance in the reduced rows. A value column missing from a reduced row is filled with `null`.
 *
 * @include [ConcatDocs.MissingColumnValues]
 *
 * If the reducer returns `null` for a group, the result still contains one row for that group. This row is treated as
 * missing every result column, so its values are filled according to the same rules.
 *
 * For more information:
 *
 * {@include [DocumentationUrls.GroupBy]}
 *
 * {@include [DocumentationUrls.Concat]}
 *
 * See also:
 * - [GroupBy.concat] — concatenates the group rows without applying a reducer.
 * - [GroupBy.concatWithKeys] — also adds grouping keys that are missing from the groups.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ConcatSamples.concatReducedGroupBy_properties]
 *
 * @param [T] The schema marker type of the grouping keys in the underlying [GroupBy].
 * @param [G] The schema marker type of the groups and the resulting [DataFrame].
 * @return A [DataFrame] containing one reduced row for every group.
 */
public fun <T, G> ReducedGroupBy<T, G>.concat(): DataFrame<G> =
    groupBy.groups.values()
        .map { reducer(it, it) }
        .concat()

// endregion

// region Iterable

/**
 * Returns a [DataFrame] containing all rows from the [DataFrame]s in this iterable.
 *
 * The [DataFrame]s are concatenated in iteration order, and row order within every [DataFrame] is preserved.
 *
 * @include [ConcatDocs.DataFrameSchemaUnification]
 *
 * A [DataFrame] with zero rows adds no rows, while its columns still participate in schema unification.
 *
 * An empty iterable produces an empty [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Concat]}
 *
 * See also:
 * - [DataFrame.concat] — appends [DataFrame]s to a receiver.
 * - [DataColumn.concat] — concatenates [DataFrame]s stored in a column.
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ConcatSamples.concatIterableDataFrames]
 *
 * @param [T] The schema marker type of the [DataFrame]s in this iterable.
 * @return A [DataFrame] containing the rows of each input [DataFrame] in iteration order, or an empty [DataFrame] when
 * this iterable is empty.
 */
public fun <T> Iterable<DataFrame<T>>.concat(): DataFrame<T> = concatImpl(asList())

/**
 * Returns a [DataColumn] containing all values from the columns in this iterable.
 *
 * The columns are concatenated in iteration order, and value order within every column is preserved.
 *
 * @include [ConcatDocs.DataColumnUnification]
 *
 * An empty iterable produces an empty column.
 *
 * For more information: {@include [DocumentationUrls.Concat]}
 *
 * See also [DataColumn.concat], which appends columns to a receiver [DataColumn].
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ConcatSamples.concatIterableDataColumns]
 *
 * @param [T] The type of values in the [DataColumn]s in this iterable.
 * @return A [DataColumn] containing the concatenated values, or an empty [DataColumn] when this iterable is empty.
 */
public fun <T> Iterable<DataColumn<T>>.concat(): DataColumn<T> {
    val list = asList()
    if (list.isEmpty()) return DataColumn.empty().cast()
    return concatImpl(list[0].name(), list)
}

/**
 * Returns a [DataFrame] containing the rows in this iterable in iteration order.
 *
 * @include [ConcatDocs.DataRowSchemaUnification]
 *
 * A `null` element of this iterable contributes one result row. Its values are filled according to the missing-column
 * rules above. An empty iterable produces an empty [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Concat]}
 *
 * See also:
 * - [DataRow.concat] — starts with one [DataRow].
 * - [DataFrame.concat] — appends an iterable of rows to a [DataFrame].
 *
 * ### Examples
 *
 * @sample [org.jetbrains.kotlinx.dataframe.samples.api.ConcatSamples.concatIterableRows]
 *
 * @param [T] The schema marker type of the non-null [DataRow]s in this iterable.
 * @return A [DataFrame] containing every row in iteration order. Each `null` element of this iterable contributes one
 * result row whose values follow the missing-column rules above.
 */
@JvmName("concatRows")
public fun <T> Iterable<DataRow<T>?>.concat(): DataFrame<T> =
    concatImpl(map { it?.toDataFrame() ?: DataFrame.empty(1).cast() })

// endregion
