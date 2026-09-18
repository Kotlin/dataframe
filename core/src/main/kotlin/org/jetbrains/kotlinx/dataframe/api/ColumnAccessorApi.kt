package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference

/**
 * Returns this [ColumnAccessor] with its value type marked as nullable.
 *
 * The cast exists only at compile time: no column is looked up and no value is read,
 * the accessor keeps its name and its path, and there is no check that a column with this name
 * exists or that it really contains `null`s.
 *
 * [ColumnAccessor] is covariant in its value type, so a `ColumnAccessor<Double>` is already
 * accepted where a `ColumnAccessor<Double?>` is expected; what changes here is the declared type
 * of the accessor itself. Reading a value with `getValue(row)` then gives `T?` instead of `T`.
 *
 * Without this cast, a `null` from the column arrives typed as the non-nullable `T`, so the compiler
 * cannot require a null check. The value then travels on until something uses it as a `T`, and that
 * use may throw a [NullPointerException] — for a primitive type such as `Double` it always does.
 *
 * See also [castToNullable][ColumnReference.castToNullable], which marks any [ColumnReference]
 * as nullable but returns a [ColumnReference] instead of a [ColumnAccessor],
 * and [castToNotNullable][ColumnReference.castToNotNullable] for the opposite direction.
 *
 * ### Example
 * ```kotlin
 * // `other` has no "score" column, so after `concat` the score of its rows is `null`:
 * val df = scores.concat(other)
 * val row = df.last()
 *
 * // the accessor for the "score" column, and the same accessor typed as `Double?`:
 * val score by column<Double>()
 * val scoreOrNull = score.nullable()
 *
 * // typed as `Double`, the `null` is invisible to the compiler and throws when the value is used:
 * score.getValue(row) > 0.0 // NullPointerException
 *
 * // typed as `Double?`, the same `null` has to be handled:
 * scoreOrNull.getValue(row)?.let { it > 0.0 } == true // false
 * ```
 *
 * @param [T] The value type of the column this accessor points to.
 * @return This accessor, typed as [ColumnAccessor]`<T?>`.
 */
public inline fun <reified T> ColumnAccessor<T>.nullable(): ColumnAccessor<T?> = cast()
