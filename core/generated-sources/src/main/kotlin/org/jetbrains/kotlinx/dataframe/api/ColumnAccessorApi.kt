package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference

/**
 * Returns this [<code>ColumnAccessor</code>][ColumnAccessor] with its value type marked as nullable.
 *
 * The cast exists only at compile time: no column is looked up and no value is read,
 * the accessor keeps its name and its path, and there is no check that a column with this name
 * exists or that it really contains `null`s.
 *
 * [<code>ColumnAccessor</code>][ColumnAccessor] is covariant in its value type, so a `ColumnAccessor<Double>` is already
 * accepted where a `ColumnAccessor<Double?>` is expected; what changes here is the declared type
 * of the accessor itself. Reading a value with `getValue(row)` then gives `T?` instead of `T`.
 * Without this cast, a `null` from the column arrives as a `null` in the non-nullable type `T`
 * and fails later, at the first use of the value.
 *
 * See also [<code>castToNullable</code>][ColumnReference.castToNullable], which marks any [<code>ColumnReference</code>][ColumnReference]
 * as nullable but returns a [<code>ColumnReference</code>][ColumnReference] instead of a [<code>ColumnAccessor</code>][ColumnAccessor],
 * and [<code>castToNotNullable</code>][ColumnReference.castToNotNullable] for the opposite direction.
 *
 * ### Example
 * ```kotlin
 * // `other` has no "score" column, so after `concat` the score of its rows is `null`:
 * val df = scores.concat(other)
 *
 * // the accessor for the "score" column, and the same accessor typed as `Double?`:
 * val score by column<Double>()
 * val scoreOrNull = score.nullable()
 *
 * // the missing scores can now be handled instead of failing later:
 * df.filter { scoreOrNull.getValue(this) != null }
 * ```
 *
 * @param [T] The value type of the column this accessor points to.
 * @return This accessor, typed as [<code>ColumnAccessor</code>][ColumnAccessor]`<T?>`.
 */
public inline fun <reified T> ColumnAccessor<T>.nullable(): ColumnAccessor<T?> = cast()
