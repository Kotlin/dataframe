package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.api.concatImpl
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.type

// region DataColumn

/**
 * Returns a [<code>DataColumn</code>][DataColumn] containing the values of this column followed by the values of [<code>other</code>][other].
 *
 * The columns in [<code>other</code>][other] are processed in argument order. The order within each column is preserved.
 *
 * The result keeps the name of the first column.
 * If all input columns have the same runtime [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type], the result keeps that type.
 * If their types differ and at least one value is present, the result type is inferred from the concatenated
 * values. If all input columns are empty, their types determine the result type.
 * The result becomes nullable if the resulting values contain `null`.
 *
 * Empty columns add no values.
 *
 * For more information: [See `concat` on the documentation website.](https://kotlin.github.io/dataframe/concat.html)
 *
 * See also [<code>Iterable.concat</code>][Iterable.concat], which concatenates an iterable of columns.
 *
 * ### Examples
 *
 * ```kotlin
 * peopleDf1.age.concat(peopleDf2.age)
 * ```
 *
 * @param [T] The type of values in this [<code>DataColumn</code>][DataColumn] and the columns in [<code>other</code>][other].
 * @param [other] The columns whose values are appended to this column.
 * @return A [<code>DataColumn</code>][DataColumn] with the same name as this column. It contains the values of this column followed by the
 * values of each column in [<code>other</code>][other] in argument order.
 */
public fun <T> DataColumn<T>.concat(vararg other: DataColumn<T>): DataColumn<T> = concatImpl(name, listOf(this) + other)

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing the rows from every [<code>DataFrame</code>][DataFrame] stored in this column.
 *
 * [<code>DataFrame</code>][DataFrame]s are concatenated in the order in which they appear in the column.
 * Row order within each [<code>DataFrame</code>][DataFrame] is preserved.
 *
 * ### Schema unification
 *
 * The result contains the union of the input schemas. Columns are ordered by their first appearance in the
 * inputs. Columns with the same name are concatenated as described in [<code>DataColumn.concat</code>][org.jetbrains.kotlinx.dataframe.DataColumn.concat]:
 * their type is kept if it is the same in all inputs and otherwise inferred from the values.
 * When a column is missing from an input that contributes rows, its values for those rows are filled with `null`,
 * and its result type becomes nullable.
 *
 * An empty column of [<code>DataFrame</code>][DataFrame] type produces an empty [<code>DataFrame</code>][DataFrame].
 * A stored [<code>DataFrame</code>][DataFrame] with zero rows adds no rows, while its columns still participate in schema unification.
 *
 * For more information: [See `concat` on the documentation website.](https://kotlin.github.io/dataframe/concat.html)
 *
 * See also:
 * - [<code>Iterable.concat</code>][Iterable.concat] — concatenates an iterable of [<code>DataFrame</code>][DataFrame]s.
 * - [<code>DataFrame.concat</code>][DataFrame.concat] — appends [<code>DataFrame</code>][DataFrame]s to a receiver.
 *
 * ### Examples
 *
 * ```kotlin
 * val teams = dataFrameOf(
 *     "team" to columnOf("Engineering", "Design"),
 *     "members" to columnOf(
 *         dataFrameOf("name", "age")("Alice", 20, "Bob", 15),
 *         dataFrameOf("name", "age")("Charlie", 25),
 *     ),
 * )
 *
 * teams.members.concat()
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][DataFrame]s stored in this [<code>DataColumn</code>][DataColumn].
 * @return A [<code>DataFrame</code>][DataFrame] containing all rows from the [<code>DataFrame</code>][DataFrame]s stored in the [<code>DataColumn</code>][DataColumn].
 */
public fun <T> DataColumn<DataFrame<T>>.concat(): DataFrame<T> = values.concat()

/**
 * Returns a [<code>List</code>][List] containing the elements of every [<code>Collection</code>][Collection] stored in this column.
 *
 * Collections are processed in the order in which they appear in the column. Order within the collections is preserved.
 * Empty collections add no elements.
 *
 * For more information: [See `concat` on the documentation website.](https://kotlin.github.io/dataframe/concat.html)
 *
 * ### Examples
 *
 * ```kotlin
 * val nameGroups = columnOf<Collection<String>>(listOf("Alice", "Bob"), emptySet(), setOf("Charlie"))
 *
 * nameGroups.concat()
 * ```
 *
 * @param [T] The type of elements in the [<code>Collection</code>][Collection]s stored in this [<code>DataColumn</code>][DataColumn].
 * @return A [<code>List</code>][List] containing the concatenated elements of all collections stored in this [<code>DataColumn</code>][DataColumn].
 */
public fun <T> DataColumn<Collection<T>>.concat(): List<T> = values.flatten()

// endregion

// region DataRow

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing this [<code>DataRow</code>][DataRow] followed by [<code>rows</code>][rows].
 *
 * Rows are processed in argument order.
 *
 * ### Schema unification
 *
 * The result contains the union of the input schemas. Columns are ordered by their first appearance in the
 * inputs. Columns with the same name are concatenated as described in [<code>DataColumn.concat</code>][org.jetbrains.kotlinx.dataframe.DataColumn.concat]:
 * their type is kept if it is the same in all inputs and otherwise inferred from the values.
 * When a column is missing from an input that contributes rows, its values for those rows are filled with `null`,
 * and its result type becomes nullable.
 *
 * For more information: [See `concat` on the documentation website.](https://kotlin.github.io/dataframe/concat.html)
 *
 * See also:
 * - [<code>DataFrame.concat</code>][DataFrame.concat] — appends an iterable of rows to a [<code>DataFrame</code>][DataFrame].
 * - [<code>Iterable.concat</code>][Iterable.concat] — creates a [<code>DataFrame</code>][DataFrame] from an iterable of nullable rows.
 *
 * ### Examples
 *
 * ```kotlin
 * val youngest = df.minBy { age }
 * val oldest = df.maxBy { age }
 *
 * youngest.concat(oldest)
 * ```
 *
 * @param [T] The schema marker type of this [<code>DataRow</code>][DataRow] and the rows in [<code>rows</code>][rows].
 * @param [rows] The rows to append to this [<code>DataRow</code>][DataRow].
 * @return A [<code>DataFrame</code>][DataFrame] containing this [<code>DataRow</code>][DataRow] followed by [<code>rows</code>][rows] in argument order.
 */
public fun <T> DataRow<T>.concat(vararg rows: DataRow<T>): DataFrame<T> = (listOf(this) + rows).concat()

// endregion

// region DataFrame

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing the rows of this [<code>DataFrame</code>][DataFrame] followed by the rows of [<code>frames</code>][frames].
 *
 * The [<code>DataFrame</code>][DataFrame]s in [<code>frames</code>][frames] are processed in argument order, and row order within every [<code>DataFrame</code>][DataFrame] is preserved.
 * If no [<code>frames</code>][frames] are supplied, this [<code>DataFrame</code>][DataFrame] is returned unchanged.
 *
 * ### Schema unification
 *
 * The result contains the union of the input schemas. Columns are ordered by their first appearance in the
 * inputs. Columns with the same name are concatenated as described in [<code>DataColumn.concat</code>][org.jetbrains.kotlinx.dataframe.DataColumn.concat]:
 * their type is kept if it is the same in all inputs and otherwise inferred from the values.
 * When a column is missing from an input that contributes rows, its values for those rows are filled with `null`,
 * and its result type becomes nullable.
 *
 * A [<code>DataFrame</code>][DataFrame] supplied in [<code>frames</code>][frames] with zero rows adds no rows, while its columns still participate in schema
 * unification.
 *
 * For more information: [See `concat` on the documentation website.](https://kotlin.github.io/dataframe/concat.html)
 *
 * See also [<code>Iterable.concat</code>][Iterable.concat], which concatenates an iterable of [<code>DataFrame</code>][DataFrame]s.
 *
 * ### Examples
 *
 * ```kotlin
 * val firstBatch = dataFrameOf("name", "age", "city")("Bob", 15, "Paris")
 * val secondBatch = dataFrameOf("name", "age", "city")("Alice", 20, "London")
 * val thirdBatch = dataFrameOf("name", "age", "city")("Charlie", 25, "London")
 *
 * firstBatch.concat(secondBatch, thirdBatch)
 * ```
 *
 * @param [T] The schema marker type of this [<code>DataFrame</code>][DataFrame] and the [<code>DataFrame</code>][DataFrame]s in [<code>frames</code>][frames].
 * @param [frames] The [<code>DataFrame</code>][DataFrame]s whose rows are appended to this [<code>DataFrame</code>][DataFrame].
 * @return A [<code>DataFrame</code>][DataFrame] containing the rows of this [<code>DataFrame</code>][DataFrame] followed by the rows of each [<code>DataFrame</code>][DataFrame] in [<code>frames</code>][frames]
 * in argument order.
 */
public fun <T> DataFrame<T>.concat(vararg frames: DataFrame<T>): DataFrame<T> = concatImpl(listOf(this) + frames)

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing the rows of this [<code>DataFrame</code>][DataFrame] followed by the rows of [<code>frame</code>][frame].
 *
 * Row order within both [<code>DataFrame</code>][DataFrame]s is preserved.
 *
 * ### Schema unification
 *
 * The result contains the union of the input schemas. Columns are ordered by their first appearance in the
 * inputs. Columns with the same name are concatenated as described in [<code>DataColumn.concat</code>][org.jetbrains.kotlinx.dataframe.DataColumn.concat]:
 * their type is kept if it is the same in all inputs and otherwise inferred from the values.
 * When a column is missing from an input that contributes rows, its values for those rows are filled with `null`,
 * and its result type becomes nullable.
 *
 * If [<code>frame</code>][frame] has zero rows, it adds no rows, while its columns still participate in schema unification.
 *
 * For more information: [See `concat` on the documentation website.](https://kotlin.github.io/dataframe/concat.html)
 *
 * See also:
 * - [<code>DataFrame.concat</code>][DataFrame.concat] — other [<code>DataFrame</code>][DataFrame] and row overloads.
 * - [<code>Iterable.concat</code>][Iterable.concat] — concatenates an iterable of [<code>DataFrame</code>][DataFrame]s.
 *
 * ### Examples
 *
 * ```kotlin
 * (peopleDf1 concat peopleDf2)
 * ```
 *
 * @param [T] The schema marker type of this [<code>DataFrame</code>][DataFrame].
 * @param [T1] The schema marker type of [<code>frame</code>][frame].
 * @param [frame] The [<code>DataFrame</code>][DataFrame] whose rows are appended to this [<code>DataFrame</code>][DataFrame].
 * @return A [<code>DataFrame</code>][DataFrame] with a schema that represents both input schemas. It contains the rows of this [<code>DataFrame</code>][DataFrame]
 * followed by the rows of [<code>frame</code>][frame].
 */
@Refine
@Interpretable("DataFrameConcat")
public infix fun <T, T1> DataFrame<T>.concat(frame: DataFrame<T1>): DataFrame<Any> =
    concatImpl(listOf(this) + frame).cast()

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing the rows of this [<code>DataFrame</code>][DataFrame] followed by [<code>rows</code>][rows].
 *
 * The rows from [<code>rows</code>][rows] retain their iteration order.
 *
 * ### Schema unification
 *
 * The result contains the union of the input schemas. Columns are ordered by their first appearance in the
 * inputs. Columns with the same name are concatenated as described in [<code>DataColumn.concat</code>][org.jetbrains.kotlinx.dataframe.DataColumn.concat]:
 * their type is kept if it is the same in all inputs and otherwise inferred from the values.
 * When a column is missing from an input that contributes rows, its values for those rows are filled with `null`,
 * and its result type becomes nullable.
 *
 * An empty [<code>rows</code>][rows] iterable appends no rows.
 *
 * For more information: [See `concat` on the documentation website.](https://kotlin.github.io/dataframe/concat.html)
 *
 * See also:
 * - [<code>DataFrame.append</code>][DataFrame.append] — appends one row supplied as values.
 * - [<code>DataRow.concat</code>][DataRow.concat] — starts with a [<code>DataRow</code>][DataRow].
 * - [<code>Iterable.concat</code>][Iterable.concat] — creates a [<code>DataFrame</code>][DataFrame] from an iterable of nullable rows.
 *
 * ### Examples
 *
 * ```kotlin
 * val registeredPeople = dataFrameOf("name", "age", "city")("Alice", 20, "London")
 * val newPeople = dataFrameOf("name", "age", "city")("Bob", 15, "Paris", "Charlie", 25, "London")
 *
 * registeredPeople.concat(newPeople.rows())
 * ```
 *
 * @param [T] The schema marker type of this [<code>DataFrame</code>][DataFrame] and the rows in [<code>rows</code>][rows].
 * @param [rows] The rows to append to this [<code>DataFrame</code>][DataFrame].
 * @return A [<code>DataFrame</code>][DataFrame] containing the rows of this [<code>DataFrame</code>][DataFrame] followed by [<code>rows</code>][rows] in iteration order.
 */
@JvmName("concatT")
public fun <T> DataFrame<T>.concat(rows: Iterable<DataRow<T>>): DataFrame<T> = (rows() + rows).concat()

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing the rows of this [<code>DataFrame</code>][DataFrame] followed by the rows from [<code>frames</code>][frames].
 *
 * The [<code>DataFrame</code>][DataFrame]s in [<code>frames</code>][frames] are concatenated in iteration order, and row order within every [<code>DataFrame</code>][DataFrame] is
 * preserved.
 *
 * ### Schema unification
 *
 * The result contains the union of the input schemas. Columns are ordered by their first appearance in the
 * inputs. Columns with the same name are concatenated as described in [<code>DataColumn.concat</code>][org.jetbrains.kotlinx.dataframe.DataColumn.concat]:
 * their type is kept if it is the same in all inputs and otherwise inferred from the values.
 * When a column is missing from an input that contributes rows, its values for those rows are filled with `null`,
 * and its result type becomes nullable.
 *
 * A [<code>DataFrame</code>][DataFrame] supplied in [<code>frames</code>][frames] with zero rows adds no rows, while its columns still participate in schema
 * unification.
 *
 * If [<code>frames</code>][frames] is empty, this same [<code>DataFrame</code>][DataFrame] instance is returned.
 *
 * For more information: [See `concat` on the documentation website.](https://kotlin.github.io/dataframe/concat.html)
 *
 * See also:
 * - [<code>Iterable.concat</code>][Iterable.concat] — concatenates an iterable of [<code>DataFrame</code>][DataFrame]s.
 * - [<code>DataFrame.concat</code>][DataFrame.concat] — other [<code>DataFrame</code>][DataFrame] and row overloads.
 *
 * ### Examples
 *
 * ```kotlin
 * val registeredPeople = dataFrameOf("name", "age", "city")("Bob", 15, "Paris")
 * val newPeople = listOf(
 *     dataFrameOf("name", "age", "city")("Charlie", 25, "London"),
 *     dataFrameOf("name", "age", "city")("Alice", 20, "London"),
 * )
 *
 * registeredPeople.concat(newPeople)
 * ```
 *
 * @param [T] The schema marker type of this [<code>DataFrame</code>][DataFrame] and the [<code>DataFrame</code>][DataFrame]s in [<code>frames</code>][frames].
 * @param [frames] The [<code>DataFrame</code>][DataFrame]s whose rows are appended to this [<code>DataFrame</code>][DataFrame].
 * @return A [<code>DataFrame</code>][DataFrame] containing the rows of this [<code>DataFrame</code>][DataFrame] followed by the rows from [<code>frames</code>][frames] in iteration order.
 */
public fun <T> DataFrame<T>.concat(frames: Iterable<DataFrame<T>>): DataFrame<T> = (listOf(this) + frames).concat()

// endregion

// region GroupBy

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing all [<code>groups</code>][groups] in this [<code>GroupBy</code>][GroupBy].
 *
 * Groups are concatenated in the order in which they appear in [<code>groups</code>][groups], and row order within every group is
 * preserved. The grouping keys stored in [<code>keys</code>][keys] are not added to the result.
 * If a group already contains a column with the same name as a key, that column is preserved unchanged.
 *
 * Check out [<code>`groupBy` Grammar</code>][GroupByDocs.Grammar] for more information.
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html) [See `concat` on the documentation website.](https://kotlin.github.io/dataframe/concat.html)
 *
 * See also [<code>GroupBy.concatWithKeys</code>][GroupBy.concatWithKeys], which adds grouping key columns that are missing from the groups.
 *
 * ### Examples
 *
 * ```kotlin
 * val grouped = df.groupBy {
 *     expr { if (age >= 20) "adult" else "teen" } named "ageGroup"
 * }
 *
 * grouped.concat()
 * ```
 *
 * @param [T] The schema marker type of the grouping [<code>keys</code>][GroupBy.keys].
 * @param [G] The schema marker type of the groups.
 * @return A [<code>DataFrame</code>][DataFrame] containing the rows from all groups, without any grouping key columns added.
 */
public fun <T, G> GroupBy<T, G>.concat(): DataFrame<G> = groups.concat()

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing all [<code>groups</code>][groups] in this [<code>GroupBy</code>][GroupBy]
 * and every grouping key column that is missing from those groups.
 *
 * Groups are concatenated in the order in which they appear in [<code>groups</code>][groups], and row order within every group is
 * preserved. The grouping keys stored in [<code>keys</code>][keys] are added to each group unless it already contains a column with
 * the same name. Each added key value is repeated for every row in its group. An existing group column is preserved
 * unchanged.
 *
 * This function is especially useful when grouping by expressions or renamed columns,
 * and you want the resulting [<code>DataFrame</code>][DataFrame] to include those keys as part of the output.
 *
 * Check out [<code>`groupBy` Grammar</code>][GroupByDocs.Grammar] for more information.
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html) [See `concat` on the documentation website.](https://kotlin.github.io/dataframe/concat.html)
 *
 * See also [<code>GroupBy.concat</code>][GroupBy.concat], which ignores separate grouping keys.
 *
 * ### Examples
 *
 * ```kotlin
 * val grouped = df.groupBy {
 *     expr { if (age >= 20) "adult" else "teen" } named "ageGroup"
 * }
 *
 * grouped.concatWithKeys()
 * ```
 *
 * @param [T] The schema marker type of the grouping [<code>keys</code>][GroupBy.keys].
 * @param [G] The schema marker type of the groups.
 * @return A [<code>DataFrame</code>][DataFrame] containing the rows from all groups and all missing grouping key columns.
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
 * Applies the stored reducer to every group and returns a [<code>DataFrame</code>][DataFrame] containing one resulting row per group, in
 * the order in which the groups appear in the underlying [<code>GroupBy</code>][GroupBy].
 *
 * [<code>ReducedGroupBy</code>][ReducedGroupBy] is returned by reducing operations such as [<code>first</code>][GroupBy.first], [<code>last</code>][GroupBy.last],
 * [<code>minBy</code>][GroupBy.minBy], [<code>maxBy</code>][GroupBy.maxBy], [<code>medianBy</code>][GroupBy.medianBy], and
 * [<code>percentileBy</code>][GroupBy.percentileBy].
 *
 * If the reducer returns `null` for a group, the result still contains one row for that group. Its values are `null`
 * in every column contributed by the other reduced rows. When reduced rows have different schemas, the result
 * contains their union, and columns missing from a row are filled with `null`.
 *
 * For more information: [See `groupBy` on the documentation website.](https://kotlin.github.io/dataframe/groupby.html) [See `concat` on the documentation website.](https://kotlin.github.io/dataframe/concat.html)
 *
 * See also:
 * - [<code>GroupBy.concat</code>][GroupBy.concat] — concatenates the group rows without applying a reducer.
 * - [<code>GroupBy.concatWithKeys</code>][GroupBy.concatWithKeys] — also adds grouping keys that are missing from the groups.
 *
 * ### Examples
 *
 * ```kotlin
 * df.groupBy { city }.first().concat()
 * ```
 *
 * @param [T] The schema marker type of the grouping keys in the underlying [<code>GroupBy</code>][GroupBy].
 * @param [G] The schema marker type of the groups and the resulting [<code>DataFrame</code>][DataFrame].
 * @return A [<code>DataFrame</code>][DataFrame] containing one reduced row for every group.
 */
public fun <T, G> ReducedGroupBy<T, G>.concat(): DataFrame<G> =
    groupBy.groups.values()
        .map { reducer(it, it) }
        .concat()

// endregion

// region Iterable

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing all rows from the [<code>DataFrame</code>][DataFrame]s in this iterable.
 *
 * The [<code>DataFrame</code>][DataFrame]s are concatenated in iteration order, and row order within every [<code>DataFrame</code>][DataFrame] is preserved.
 *
 * ### Schema unification
 *
 * The result contains the union of the input schemas. Columns are ordered by their first appearance in the
 * inputs. Columns with the same name are concatenated as described in [<code>DataColumn.concat</code>][org.jetbrains.kotlinx.dataframe.DataColumn.concat]:
 * their type is kept if it is the same in all inputs and otherwise inferred from the values.
 * When a column is missing from an input that contributes rows, its values for those rows are filled with `null`,
 * and its result type becomes nullable.
 *
 * A [<code>DataFrame</code>][DataFrame] with zero rows adds no rows, while its columns still participate in schema unification.
 *
 * An empty iterable produces an empty [<code>DataFrame</code>][DataFrame].
 *
 * For more information: [See `concat` on the documentation website.](https://kotlin.github.io/dataframe/concat.html)
 *
 * See also:
 * - [<code>DataFrame.concat</code>][DataFrame.concat] — appends [<code>DataFrame</code>][DataFrame]s to a receiver.
 * - [<code>DataColumn.concat</code>][DataColumn.concat] — concatenates [<code>DataFrame</code>][DataFrame]s stored in a column.
 *
 * ### Examples
 *
 * ```kotlin
 * val batches = listOf(
 *     dataFrameOf("name", "age", "city")("Charlie", 25, "London"),
 *     dataFrameOf("name", "age", "city")("Alice", 20, "London", "Bob", 15, "Paris"),
 * )
 *
 * batches.concat()
 * ```
 *
 * @param [T] The schema marker type of the [<code>DataFrame</code>][DataFrame]s in this iterable.
 * @return A [<code>DataFrame</code>][DataFrame] containing the rows of each input [<code>DataFrame</code>][DataFrame] in iteration order, or an empty [<code>DataFrame</code>][DataFrame] when
 * this iterable is empty.
 */
public fun <T> Iterable<DataFrame<T>>.concat(): DataFrame<T> = concatImpl(asList())

/**
 * Returns a [<code>DataColumn</code>][DataColumn] containing all values from the columns in this iterable.
 *
 * The columns are concatenated in iteration order, and value order within every column is preserved.
 *
 * The result keeps the name of the first column.
 * If all input columns have the same runtime [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type], the result keeps that type.
 * If their types differ and at least one value is present, the result type is inferred from the concatenated
 * values. If all input columns are empty, their types determine the result type.
 * The result becomes nullable if the resulting values contain `null`.
 *
 * An empty iterable produces an empty column.
 *
 * For more information: [See `concat` on the documentation website.](https://kotlin.github.io/dataframe/concat.html)
 *
 * See also [<code>DataColumn.concat</code>][DataColumn.concat], which appends columns to a receiver [<code>DataColumn</code>][DataColumn].
 *
 * ### Examples
 *
 * ```kotlin
 * val ageColumns = listOf(
 *     columnOf(20, 25).named("age"),
 *     columnOf(15).named("additionalAges"),
 * )
 *
 * ageColumns.concat()
 * ```
 *
 * @param [T] The type of values in the [<code>DataColumn</code>][DataColumn]s in this iterable.
 * @return A [<code>DataColumn</code>][DataColumn] containing the concatenated values, or an empty [<code>DataColumn</code>][DataColumn] when this iterable is empty.
 */
public fun <T> Iterable<DataColumn<T>>.concat(): DataColumn<T> {
    val list = asList()
    if (list.isEmpty()) return DataColumn.empty().cast()
    return concatImpl(list[0].name(), list)
}

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing the rows in this iterable in iteration order.
 *
 * The result contains the union of the schemas of all non-null rows. Columns are ordered by their first appearance.
 * If a non-null row does not contain a result column, its value in that row is `null`. Values from columns with the
 * same name in different rows form a single result column. If those columns have the same runtime type, the result
 * keeps that type; otherwise, its type is inferred from their combined values.
 *
 * A `null` element of this iterable contributes one result row. That row contains `null` in every column contributed
 * by the non-null rows. An empty iterable produces an empty [<code>DataFrame</code>][DataFrame].
 *
 * For more information: [See `concat` on the documentation website.](https://kotlin.github.io/dataframe/concat.html)
 *
 * See also:
 * - [<code>DataRow.concat</code>][DataRow.concat] — starts with one [<code>DataRow</code>][DataRow].
 * - [<code>DataFrame.concat</code>][DataFrame.concat] — appends an iterable of rows to a [<code>DataFrame</code>][DataFrame].
 *
 * ### Examples
 *
 * ```kotlin
 * val rows = listOf(df[0], null, df[1])
 *
 * rows.concat()
 * ```
 *
 * @param [T] The schema marker type of the non-null [<code>DataRow</code>][DataRow]s in this iterable.
 * @return A [<code>DataFrame</code>][DataFrame] containing every row in iteration order. Each `null` element of this iterable contributes one
 * result row whose values are `null` in every result column.
 */
@JvmName("concatRows")
public fun <T> Iterable<DataRow<T>?>.concat(): DataFrame<T> =
    concatImpl(map { it?.toDataFrame() ?: DataFrame.empty(1).cast() })

// endregion
