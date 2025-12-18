package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.impl.api.concatImpl
import org.jetbrains.kotlinx.dataframe.impl.asList
import org.jetbrains.kotlinx.dataframe.type

// region DataColumn

public fun <T> DataColumn<T>.concat(vararg other: DataColumn<T>): DataColumn<T> = concatImpl(name, listOf(this) + other)

public fun <T> DataColumn<DataFrame<T>>.concat(): DataFrame<T> = values.concat()

public fun <T> DataColumn<Collection<T>>.concat(): List<T> = values.flatten()

// endregion

// region DataRow

public fun <T> DataRow<T>.concat(vararg rows: DataRow<T>): DataFrame<T> = (listOf(this) + rows).concat()

// endregion

// region DataFrame

public fun <T> DataFrame<T>.concat(vararg frames: DataFrame<T>): DataFrame<T> = concatImpl(listOf(this) + frames)

public infix fun <T> DataFrame<T>.concat(frame: DataFrame<T>): DataFrame<T> = concatImpl(listOf(this) + frame)

@JvmName("concatT")
public fun <T> DataFrame<T>.concat(rows: Iterable<DataRow<T>>): DataFrame<T> = (rows() + rows).concat()

public fun <T> DataFrame<T>.concat(frames: Iterable<DataFrame<T>>): DataFrame<T> = (listOf(this) + frames).concat()

// endregion

// region GroupBy

public fun <T, G> GroupBy<T, G>.concat(): DataFrame<G> = groups.concat()

/**
 * Concatenates all groups in this [GroupBy] into a single [DataFrame],
 * preserving and including all grouping key columns that are not present in the group's columns.
 *
 * Doesn't affect key columns that have the same name as columns inside the groups (even if their content differs).
 *
 * This function is especially useful when grouping by expressions or renamed columns,
 * and you want the resulting [DataFrame] to include those keys as part of the output.
 *
 * ### Example
 *
 * ```kotlin
 * val df = dataFrameOf(
 *     "value" to listOf(1, 2, 3, 3),
 *     "type" to listOf("a", "b", "a", "b")
 * )
 *
 * val gb = df.groupBy { expr { "Category: \${type.uppercase()}" } named "category" }
 * ```
 *
 * A regular `concat()` will return a [DataFrame] similar to the original `df`
 * (with the same columns and rows but in the different orders):
 *
 * ```
 * gb.concat()
 * ```
 * | value | type |
 * | :---- | :--- |
 * | 1     | a    |
 * | 3     | a    |
 * | 2     | b    |
 * | 3     | b    |
 *
 * But `concatWithKeys()` will include the new "category" key column:
 *
 * ```
 * gb.concatWithKeys()
 * ```
 * | value | type | category      |
 * | :---- | :--- | :------------ |
 * | 1     | a    | Category: A   |
 * | 3     | a    | Category: A   |
 * | 2     | b    | Category: B   |
 * | 3     | b    | Category: B   |
 *
 * @return A new [DataFrame] where all groups are combined and additional key columns are included in each row.
 */
@[Refine Interpretable("ConcatWithKeys")]
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

public fun <T, G> ReducedGroupBy<T, G>.concat(): DataFrame<G> =
    groupBy.groups.values()
        .map { reducer(it, it) }
        .concat()

// endregion

// region Iterable

public fun <T> Iterable<DataFrame<T>>.concat(): DataFrame<T> = concatImpl(asList())

public fun <T> Iterable<DataColumn<T>>.concat(): DataColumn<T> {
    val list = asList()
    if (list.isEmpty()) return DataColumn.empty().cast()
    return concatImpl(list[0].name(), list)
}

@JvmName("concatRows")
public fun <T> Iterable<DataRow<T>?>.concat(): DataFrame<T> =
    concatImpl(map { it?.toDataFrame() ?: DataFrame.empty(1).cast() })

// endregion
