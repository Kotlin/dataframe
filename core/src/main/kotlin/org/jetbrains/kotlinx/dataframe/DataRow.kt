package org.jetbrains.kotlinx.dataframe

import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.HasSchema
import org.jetbrains.kotlinx.dataframe.annotations.RequiredByIntellijPlugin
import org.jetbrains.kotlinx.dataframe.api.next
import org.jetbrains.kotlinx.dataframe.api.prev
import org.jetbrains.kotlinx.dataframe.columns.ColumnKind
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.documentation.AccessApis
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.impl.toIterable
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_DATA_ROW_COLUMN_REFERENCE_GET
import kotlin.reflect.KProperty

/**
 * Single row of a [DataFrame].
 *
 * A row holds one cell value per column of its [DataFrame].
 *  - a single value for [ValueColumn][org.jetbrains.kotlinx.dataframe.columns.ValueColumn];
 *  - a [DataRow] for [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup];
 *  - a [DataFrame] for [FrameColumn][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
 *
 * Cell values are accessed the same way as columns in [DataFrame] —
 * by [String] column name, [ColumnPath], column index
 * or [Extension Properties][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi].
 * See {@include [org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink]}.
 *
 * Every [DataRow] belongs to a [DataFrame] — the one returned by [df] — and holds no data of its own:
 * its cell values are read from the columns of that [DataFrame] at the row's [index].
 * In this sense, a [DataRow] is similar to a `Map` from columns to values;
 * however, since [DataFrame] is a column-first structure, creating a large number of [DataRow]s is best avoided —
 * unlike a plain `Map`, every row carries its whole [DataFrame] around.
 *
 * For more information: {@include [DocumentationUrls.DataRow]}
 *
 * @param [T] dataframe schema marker. See [DataFrame] type parameter for details.
 */
@HasSchema(schemaArg = 0)
public interface DataRow<out T> {

    /**
     * Returns the index of this row in the [DataFrame] returned by [df]. Row indices start from `0`.
     *
     * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
     *
     * @return The zero-based position of this row within [df].
     */
    public fun index(): Int

    /**
     * Returns the [DataFrame] that this row belongs to.
     *
     * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
     *
     * @return The parent [DataFrame] of this row.
     */
    public fun df(): DataFrame<T>

    // region get cell value

    /**
     * Returns the value of the cell in this row at the given [columnIndex].
     *
     * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
     *
     * @param [columnIndex] The zero-based index of the column in the [DataFrame] this row belongs to.
     * @return The value of the cell in the given column.
     * @throws IllegalArgumentException if [columnIndex] is out of bounds.
     */
    public operator fun get(columnIndex: Int): Any?

    /**
     * Returns the result of the [row expression][expression] computed for this row.
     *
     * [Row expressions][ExpressionsGivenRow] is a
     * lambda that receives each [DataRow] as both `this` and `it`
     * and is expected to return a computed value.
     *
     * It allows you to compute values using the row's values directly,
     * including through [extension properties][AccessApis.ExtensionPropertiesApi]
     * for convenient and type-safe access.
     *
     * For more information: {@include [DocumentationUrls.DataRow.RowExpression]}
     *
     * ### Examples
     *
     * @sample [org.jetbrains.kotlinx.dataframe.samples.api.DataRowApiSamples.getWithExpression]
     *
     * @param [R] The type of the expression result.
     * @param [expression] The [RowExpression] to compute for this row.
     * @return The result of [expression] computed for this row.
     */
    public operator fun <R> get(expression: RowExpression<T, R>): R = expression(this, this)

    @Deprecated(DEPRECATED_DATA_ROW_COLUMN_REFERENCE_GET)
    @AccessApiOverload
    public operator fun <R> get(column: ColumnReference<R>): R

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <R> get(columns: List<ColumnReference<R>>): List<R> = columns.map { get(it) }

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <R> get(property: KProperty<R>): R = get(property.columnName) as R

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun get(first: ColumnReference<*>, vararg other: ColumnReference<*>): DataRow<T> =
        owner.get(first, *other)[index]

    /**
     * Returns a [DataRow] containing only the columns named [first] and [other] from this row.
     *
     * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
     *
     * @param first The name of the first column to include.
     * @param other The names of additional columns to include.
     * @return A subset of this row containing only the selected columns as a new [DataRow].
     * @throws IllegalStateException if there is no column named [first] or any of [other].
     */
    public operator fun get(first: String, vararg other: String): DataRow<T> = owner.get(first, *other)[index]

    /**
     * Returns the value of the cell in the column addressed by the given [path].
     *
     * [path] may address a column nested inside [column groups][getColumnGroup],
     * e.g. the path `("info", "age")` addresses the `age` subcolumn of the `info` column group.
     *
     * If there is no such column, an exception is thrown; use [getOrNull] to retrieve `null` instead.
     *
     * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
     *
     * See [pathOf][org.jetbrains.kotlinx.dataframe.api.pathOf].
     *
     * @param [path] The [ColumnPath] of the column.
     * @return The value of the cell in the column at [path].
     * @throws IllegalArgumentException if there is no column at [path].
     */
    public operator fun get(path: ColumnPath): Any? = owner.get(path)[index]

    /**
     * Returns the value of the cell in the column named [name].
     *
     * If there is no such column, an exception is thrown; use [getOrNull] to retrieve `null` instead.
     *
     * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
     *
     * @param [name] The name of the column.
     * @return The value of the cell in the given column.
     * @throws IllegalArgumentException if there is no column named [name].
     */
    @RequiredByIntellijPlugin
    public operator fun get(name: String): Any?

    /**
     * Returns the value of the cell in the [column group][ColumnKind.Group] named [columnName], as a [DataRow].
     *
     * If there is no such column, or the cell value is not a [DataRow], an exception is thrown.
     * To retrieve `null` instead, you can cast the [getOrNull] result: `getOrNull(columnName) as AnyRow?`.
     *
     * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
     *
     * @param [columnName] The name of the column group.
     * @return The cell value cast to [AnyRow].
     * @throws IllegalArgumentException if there is no column named [columnName].
     * @throws IllegalStateException if the cell value is `null` and the column is not a column group.
     * @throws ClassCastException if the cell value is not a [DataRow].
     */
    public fun getColumnGroup(columnName: String): AnyRow {
        val value = get(columnName)
        if (value == null) {
            val kind = df()[columnName].kind()
            if (kind != ColumnKind.Group) {
                error("Cannot cast null value of a $kind to a ${DataRow::class}")
            }
        }
        return value as AnyRow
    }

    /**
     * Returns the value of the cell in the [frame column][ColumnKind.Frame] named [columnName], as a [DataFrame].
     *
     * If there is no such column, or the cell value is not a [DataFrame], an exception is thrown.
     * To retrieve `null` instead, you can cast the [getOrNull] result: `getOrNull(columnName) as AnyFrame?`.
     *
     * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
     *
     * @param [columnName] The name of the frame column.
     * @return The cell value cast to [AnyFrame].
     * @throws IllegalArgumentException if there is no column named [columnName].
     * @throws IllegalStateException if the cell value is `null` and the column is not a frame column.
     * @throws ClassCastException if the cell value is not a [DataFrame].
     */
    public fun getFrameColumn(columnName: String): AnyFrame {
        val value = get(columnName)
        if (value == null) {
            val kind = df()[columnName].kind()
            if (kind != ColumnKind.Frame) {
                error("Cannot cast null value of a $kind to a ${DataFrame::class}")
            }
        }
        return value as AnyFrame
    }

    /**
     * Returns the value of the cell in the column named [name],
     * or `null` if the [DataFrame] this row belongs to has no column with such name.
     *
     * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
     *
     * @param [name] The name of the column.
     * @return The value of the cell in the given column; `null` if there is no such column.
     */
    public fun getOrNull(name: String): Any?

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <R> getValueOrNull(column: ColumnReference<R>): R?

    // endregion

    /**
     * Returns all cell values of this row, in the order of the columns of the [DataFrame] this row belongs to.
     *
     * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
     *
     * @return The list of the cell values of this row.
     */
    public fun values(): List<Any?>

    /**
     * Creates a [ColumnPath] that starts with this [String] followed by the [path] segments.
     *
     * This is a shorthand for [pathOf][org.jetbrains.kotlinx.dataframe.api.pathOf]:
     * `"info"["age"]` is the same as `pathOf("info", "age")`.
     *
     * Use it in [row expressions][ExpressionsGivenRow]
     * (along with [invoke][ColumnPath.invoke])
     * to provide values in nested columns:
     *
     * `df.filter { "info"["age"]<Int>() >= 18 }`.
     *
     * For more information: {@include [DocumentationUrls.AccessApis.InvokedStringApi]}
     *
     * @param [path] The segments of the path that follow this [String].
     * @return The [ColumnPath] with this [String] as the first segment.
     */
    public operator fun String.get(vararg path: String): ColumnPath = ColumnPath(listOf(this) + path)

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <R> ColumnReference<R>.invoke(): R = get(this)

    /**
     * Returns the value of the cell in the column named by this [String], cast to [R].
     *
     * This is a shortcut for [get][DataRow.get] with this [String] as the column name,
     * with the returned value cast to [R].
     *
     * Use it in [row expressions][ExpressionsGivenRow] to provide values in top-level columns:
     *
     * `df.filter { "age"<Int>() >= 18 }`.
     *
     * For more information: {@include [DocumentationUrls.AccessApis.InvokedStringApi]}
     *
     * @param [R] The type to cast the cell value to.
     * @return The value of the cell in the column named by this [String], cast to [R].
     */
    public operator fun <R> String.invoke(): R = this@DataRow[this@invoke] as R

    /**
     * Returns the value of the cell in the column addressed by this [ColumnPath], cast to [R].
     *
     * This is a shortcut for [get][DataRow.get] with this [ColumnPath] as the column path,
     * with the returned value cast to [R].
     *
     * Use it in [row expressions][ExpressionsGivenRow] to provide values in nested columns:
     * `df.filter { "info"["age"]<Int>() >= 18 }`.
     *
     * For more information: {@include [DocumentationUrls.AccessApis.InvokedStringApi]} {@include [DocumentationUrls.DataRow.RowExpressions]}
     *
     * @param [R] The type to cast the cell value to.
     * @return The value of the cell in the column at this [ColumnPath], cast to [R].
     */
    public operator fun <R> ColumnPath.invoke(): R = this@DataRow.get(this) as R

    /**
     * Returns an [Iterable] that yields this row and all rows after it in the [DataFrame],
     * in the order of increasing [indices][index].
     *
     * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
     *
     * @return An [Iterable] of rows from this row to the last row of the [DataFrame], inclusively.
     */
    public fun forwardIterable(): Iterable<DataRow<T>> = this.toIterable { it.next }

    /**
     * Returns an [Iterable] that yields this row and all rows before it in the [DataFrame],
     * in the order of decreasing [indices][index].
     *
     * For more information: {@include [DocumentationUrls.DataRow.RowFunctions]}
     *
     * @return An [Iterable] of rows from this row to the first row of the [DataFrame], inclusively.
     */
    public fun backwardIterable(): Iterable<DataRow<T>> = this.toIterable { it.prev }

    // region deprecated operators on ColumnReference

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun <R : Comparable<R>> ColumnReference<R>.compareTo(other: R): Int = get(this).compareTo(other)

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Int>.plus(a: Int): Int = get(this) + a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Long>.plus(a: Long): Long = get(this) + a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Double>.plus(a: Double): Double = get(this) + a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<String>.plus(a: String): String = get(this) + a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun Int.plus(col: ColumnReference<Int>): Int = this + get(col)

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun Long.plus(col: ColumnReference<Long>): Long = this + get(col)

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun Double.plus(col: ColumnReference<Double>): Double = this + get(col)

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Int>.minus(a: Int): Int = get(this) - a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Long>.minus(a: Long): Long = get(this) - a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Double>.minus(a: Double): Double = get(this) - a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun Int.minus(col: ColumnReference<Int>): Int = this - get(col)

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun Long.minus(col: ColumnReference<Long>): Long = this - get(col)

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun Double.minus(col: ColumnReference<Double>): Double = this - get(col)

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Int>.times(a: Int): Int = get(this) * a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Long>.times(a: Long): Long = get(this) * a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Double>.times(a: Double): Double = get(this) * a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Double>.times(a: Int): Double = get(this) * a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Long>.times(a: Int): Long = get(this) * a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Double>.times(a: Long): Double = get(this) * a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Int>.div(a: Int): Int = get(this) / a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Long>.div(a: Long): Long = get(this) / a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Double>.div(a: Double): Double = get(this) / a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Double>.div(a: Int): Double = get(this) / a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Long>.div(a: Int): Long = get(this) / a

    @Deprecated(DEPRECATED_ACCESS_API, level = DeprecationLevel.ERROR)
    @AccessApiOverload
    public operator fun ColumnReference<Double>.div(a: Long): Double = get(this) / a

    // endregion

    public companion object {

        /**
         * Returns an empty [AnyRow] — a row of an empty [DataFrame] with no columns.
         */
        public val empty: AnyRow = DataFrame.empty(1)[0]
    }
}

// Property-syntax shortcuts for the corresponding DataRow functions
internal val AnyRow.values: List<Any?> get() = values()
internal val AnyRow.index: Int get() = index()
internal val <T> DataRow<T>.prev: DataRow<T>? get() = this.prev()
internal val <T> DataRow<T>.next: DataRow<T>? get() = this.next()
