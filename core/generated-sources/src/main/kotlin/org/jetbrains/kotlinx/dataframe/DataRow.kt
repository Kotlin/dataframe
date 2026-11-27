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
import org.jetbrains.kotlinx.dataframe.documentation.ExpressionsGivenRow
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.impl.toIterable
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_DATA_ROW_COLUMN_REFERENCE_GET
import kotlin.reflect.KProperty

/**
 * Single row of a [<code>DataFrame</code>][DataFrame].
 *
 * A row holds one cell value per column of its [<code>DataFrame</code>][DataFrame].
 *  - a single value for [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn];
 *  - a [<code>DataRow</code>][DataRow] for [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup];
 *  - a [<code>DataFrame</code>][DataFrame] for [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn].
 *
 * Cell values are accessed the same way as columns in [<code>DataFrame</code>][DataFrame] —
 * by [<code>String</code>][String] column name, [<code>ColumnPath</code>][ColumnPath], column index
 * or [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi].
 * See [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis].
 *
 * Every [<code>DataRow</code>][DataRow] belongs to a [<code>DataFrame</code>][DataFrame] — the one returned by [<code>df</code>][df] — and holds no data of its own:
 * its cell values are read from the columns of that [<code>DataFrame</code>][DataFrame] at the row's [<code>index</code>][index].
 * In this sense, a [<code>DataRow</code>][DataRow] is similar to a `Map` from columns to values;
 * however, since [<code>DataFrame</code>][DataFrame] is a column-first structure, creating a large number of [<code>DataRow</code>][DataRow]s is best avoided —
 * unlike a plain `Map`, every row carries its whole [<code>DataFrame</code>][DataFrame] around.
 *
 * For more information: [See DataRow on the documentation website.](https://kotlin.github.io/dataframe/datarow.html)
 *
 * @param [T] dataframe schema marker. See [<code>DataFrame</code>][DataFrame] type parameter for details.
 */
@HasSchema(schemaArg = 0)
public interface DataRow<out T> {

    /**
     * Returns the index of this row in the [<code>DataFrame</code>][DataFrame] returned by [<code>df</code>][df]. Row indices start from `0`.
     *
     * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
     *
     * @return The zero-based position of this row within [<code>df</code>][df].
     */
    public fun index(): Int

    /**
     * Returns the [<code>DataFrame</code>][DataFrame] that this row belongs to.
     *
     * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
     *
     * @return The parent [<code>DataFrame</code>][DataFrame] of this row.
     */
    public fun df(): DataFrame<T>

    // region get cell value

    /**
     * Returns the value of the cell in this row at the given [<code>columnIndex</code>][columnIndex].
     *
     * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
     *
     * @param [columnIndex] The zero-based index of the column in the [<code>DataFrame</code>][DataFrame] this row belongs to.
     * @return The value of the cell in the given column.
     * @throws IllegalArgumentException if [<code>columnIndex</code>][columnIndex] is out of bounds.
     */
    public operator fun get(columnIndex: Int): Any?

    /**
     * Returns the result of the [<code>row expression</code>][expression] computed for this row.
     *
     * A [<code>row expression</code>][ExpressionsGivenRow] is a
     * lambda that receives each [<code>DataRow</code>][DataRow] as both `this` and `it`
     * and is expected to return a computed value.
     *
     * It allows you to compute values using the row's values directly,
     * including through [<code>extension properties</code>][AccessApis.ExtensionPropertiesApi]
     * for convenient and type-safe access.
     *
     * For more information: [See RowExpression on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowexpression)
     *
     * ### Examples
     *
     * ```kotlin
     * val row = df.first()
     *
     * // Row expression computes a value from the row cells
     * val fullName = row.get { name.firstName + " " + name.lastName }
     * val isAdult = row.get { age >= 18 }
     *
     * // The same via the `[]` operator
     * val isAdultOperator = row[{ age >= 18 }]
     *
     * // The same via the String API
     * val isAdultString = row.get { "age"<Int>() >= 18 }
     * ```
     *
     * @param [R] The type of the expression result.
     * @param [expression] The [<code>RowExpression</code>][RowExpression] to compute for this row.
     * @return The result of [<code>expression</code>][expression] computed for this row.
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
     * Returns a [<code>DataRow</code>][DataRow] containing only the columns named [<code>first</code>][first] and [<code>other</code>][other] from this row.
     *
     * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
     *
     * @param first The name of the first column to include.
     * @param other The names of additional columns to include.
     * @return A subset of this row containing only the selected columns as a new [<code>DataRow</code>][DataRow].
     * @throws IllegalStateException if there is no column named [<code>first</code>][first] or any of [<code>other</code>][other].
     */
    public operator fun get(first: String, vararg other: String): DataRow<T> = owner.get(first, *other)[index]

    /**
     * Returns the value of the cell in the column addressed by the given [<code>path</code>][path].
     *
     * [<code>path</code>][path] may address a column nested inside [<code>column groups</code>][getColumnGroup],
     * e.g. the path `("info", "age")` addresses the `age` subcolumn of the `info` column group.
     *
     * If there is no such column, an exception is thrown; use [<code>getOrNull</code>][getOrNull] to retrieve `null` instead.
     *
     * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
     *
     * See [<code>pathOf</code>][org.jetbrains.kotlinx.dataframe.api.pathOf].
     *
     * @param [path] The [<code>ColumnPath</code>][ColumnPath] of the column.
     * @return The value of the cell in the column at [<code>path</code>][path].
     * @throws IllegalArgumentException if there is no column at [<code>path</code>][path].
     */
    public operator fun get(path: ColumnPath): Any? = owner.get(path)[index]

    /**
     * Returns the value of the cell in the column named [<code>name</code>][name].
     *
     * If there is no such column, an exception is thrown; use [<code>getOrNull</code>][getOrNull] to retrieve `null` instead.
     *
     * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
     *
     * @param [name] The name of the column.
     * @return The value of the cell in the given column.
     * @throws IllegalArgumentException if there is no column named [<code>name</code>][name].
     */
    @RequiredByIntellijPlugin
    public operator fun get(name: String): Any?

    /**
     * Returns the value of the cell in the [<code>column group</code>][ColumnKind.Group] named [<code>columnName</code>][columnName], as a [<code>DataRow</code>][DataRow].
     *
     * If there is no such column, or the cell value is not a [<code>DataRow</code>][DataRow], an exception is thrown.
     * To retrieve `null` instead, use [<code>getColumnGroupOrNull</code>][getColumnGroupOrNull].
     *
     * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
     *
     * @param [columnName] The name of the column group.
     * @return The cell value cast to [<code>AnyRow</code>][AnyRow].
     * @throws IllegalArgumentException if there is no column named [<code>columnName</code>][columnName].
     * @throws IllegalStateException if the cell value is `null` and the column is not a column group.
     * @throws ClassCastException if the cell value is not a [<code>DataRow</code>][DataRow].
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
     * Returns the value of the cell in the [<code>column group</code>][ColumnKind.Group] named [<code>columnName</code>][columnName], as a [<code>DataRow</code>][DataRow],
     * or `null` if the [<code>DataFrame</code>][DataFrame] this row belongs to has no column named [<code>columnName</code>][columnName],
     * or the cell value is not a [<code>DataRow</code>][DataRow].
     *
     * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
     *
     * @param [columnName] The name of the column group.
     * @return The cell value cast to [<code>AnyRow</code>][AnyRow]; `null` if there is no such column or the cell value is not a [<code>DataRow</code>][DataRow].
     */
    public fun getColumnGroupOrNull(columnName: String): AnyRow? = getOrNull(columnName) as? AnyRow

    /**
     * Returns the value of the cell in the [<code>frame column</code>][ColumnKind.Frame] named [<code>columnName</code>][columnName], as a [<code>DataFrame</code>][DataFrame].
     *
     * If there is no such column, or the cell value is not a [<code>DataFrame</code>][DataFrame], an exception is thrown.
     * To retrieve `null` instead, use [<code>getFrameColumnOrNull</code>][getFrameColumnOrNull].
     *
     * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
     *
     * @param [columnName] The name of the frame column.
     * @return The cell value cast to [<code>AnyFrame</code>][AnyFrame].
     * @throws IllegalArgumentException if there is no column named [<code>columnName</code>][columnName].
     * @throws IllegalStateException if the cell value is `null` and the column is not a frame column.
     * @throws ClassCastException if the cell value is not a [<code>DataFrame</code>][DataFrame].
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
     * Returns the value of the cell in the [<code>frame column</code>][ColumnKind.Frame] named [<code>columnName</code>][columnName], as a [<code>DataFrame</code>][DataFrame],
     * or `null` if the [<code>DataFrame</code>][DataFrame] this row belongs to has no column named [<code>columnName</code>][columnName],
     * or the cell value is not a [<code>DataFrame</code>][DataFrame].
     *
     * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
     *
     * @param [columnName] The name of the frame column.
     * @return The cell value cast to [<code>AnyFrame</code>][AnyFrame]; `null` if there is no such column or the cell value is not a [<code>DataFrame</code>][DataFrame].
     */
    public fun getFrameColumnOrNull(columnName: String): AnyFrame? = getOrNull(columnName) as? AnyFrame

    /**
     * Returns the value of the cell in the column named [<code>name</code>][name],
     * or `null` if the [<code>DataFrame</code>][DataFrame] this row belongs to has no column with such name.
     *
     * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
     *
     * @param [name] The name of the column.
     * @return The value of the cell in the given column; `null` if there is no such column.
     */
    public fun getOrNull(name: String): Any?

    /**
     * Returns the value of the cell in the column addressed by the given [<code>path</code>][path],
     * or `null` if the [<code>DataFrame</code>][DataFrame] this row belongs to has no column at [<code>path</code>][path].
     *
     * [<code>path</code>][path] may address a column nested inside [<code>column groups</code>][getColumnGroup],
     * e.g. the path `("info", "age")` addresses the `age` subcolumn of the `info` column group.
     *
     * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
     *
     * See [<code>pathOf</code>][org.jetbrains.kotlinx.dataframe.api.pathOf].
     *
     * @param [path] The [<code>ColumnPath</code>][ColumnPath] of the column.
     * @return The value of the cell in the column at [<code>path</code>][path]; `null` if there is no such column.
     */
    public fun getOrNull(path: ColumnPath): Any? = owner.getColumnOrNull(path)?.get(index)

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <R> getValueOrNull(column: ColumnReference<R>): R?

    // endregion

    /**
     * Returns all cell values of this row, in the order of the columns of the [<code>DataFrame</code>][DataFrame] this row belongs to.
     *
     * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
     *
     * @return The list of the cell values of this row.
     */
    public fun values(): List<Any?>

    /**
     * Creates a [<code>ColumnPath</code>][ColumnPath] that starts with this [<code>String</code>][String] followed by the [<code>path</code>][path] segments.
     *
     * This is a shorthand for [<code>pathOf</code>][org.jetbrains.kotlinx.dataframe.api.pathOf]:
     * `"info"["age"]` is the same as `pathOf("info", "age")`.
     *
     * Use it in [<code>row expressions</code>][ExpressionsGivenRow]
     * (along with [<code>invoke</code>][ColumnPath.invoke])
     * to provide values in nested columns:
     *
     * `df.filter { "info"["age"]<Int>() >= 18 }`.
     *
     * For more information: [See Invoked String API on the documentation website.](https://kotlin.github.io/dataframe/stringapi.html#invoked-string-api)
     *
     * @param [path] The segments of the path that follow this [<code>String</code>][String].
     * @return The [<code>ColumnPath</code>][ColumnPath] with this [<code>String</code>][String] as the first segment.
     */
    public operator fun String.get(vararg path: String): ColumnPath = ColumnPath(listOf(this) + path)

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <R> ColumnReference<R>.invoke(): R = get(this)

    /**
     * Returns the value of the cell in the column named by this [<code>String</code>][String], cast to [<code>R</code>][R].
     *
     * This is a shortcut for [<code>get</code>][DataRow.get] with this [<code>String</code>][String] as the column name,
     * with the returned value cast to [<code>R</code>][R].
     *
     * Use it in [<code>row expressions</code>][ExpressionsGivenRow] to provide values in top-level columns:
     *
     * `df.filter { "age"<Int>() >= 18 }`.
     *
     * For more information: [See Invoked String API on the documentation website.](https://kotlin.github.io/dataframe/stringapi.html#invoked-string-api)
     *
     * @param [R] The type to cast the cell value to.
     * @return The value of the cell in the column named by this [<code>String</code>][String], cast to [<code>R</code>][R].
     */
    public operator fun <R> String.invoke(): R = this@DataRow[this@invoke] as R

    /**
     * Returns the value of the cell in the column addressed by this [<code>ColumnPath</code>][ColumnPath], cast to [<code>R</code>][R].
     *
     * This is a shortcut for [<code>get</code>][DataRow.get] with this [<code>ColumnPath</code>][ColumnPath] as the column path,
     * with the returned value cast to [<code>R</code>][R].
     *
     * Use it in [<code>row expressions</code>][ExpressionsGivenRow] to provide values in nested columns:
     * `df.filter { "info"["age"]<Int>() >= 18 }`.
     *
     * For more information: [See Invoked String API on the documentation website.](https://kotlin.github.io/dataframe/stringapi.html#invoked-string-api) [See Row Expressions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-expressions)
     *
     * @param [R] The type to cast the cell value to.
     * @return The value of the cell in the column at this [<code>ColumnPath</code>][ColumnPath], cast to [<code>R</code>][R].
     */
    public operator fun <R> ColumnPath.invoke(): R = this@DataRow.get(this) as R

    /**
     * Returns an [<code>Iterable</code>][Iterable] that yields this row and all rows after it in the [<code>DataFrame</code>][DataFrame],
     * in the order of increasing [<code>indices</code>][index].
     *
     * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
     *
     * @return An [<code>Iterable</code>][Iterable] of rows from this row to the last row of the [<code>DataFrame</code>][DataFrame], inclusively.
     */
    public fun forwardIterable(): Iterable<DataRow<T>> = this.toIterable { it.next }

    /**
     * Returns an [<code>Iterable</code>][Iterable] that yields this row and all rows before it in the [<code>DataFrame</code>][DataFrame],
     * in the order of decreasing [<code>indices</code>][index].
     *
     * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
     *
     * @return An [<code>Iterable</code>][Iterable] of rows from this row to the first row of the [<code>DataFrame</code>][DataFrame], inclusively.
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
         * Returns an empty [<code>AnyRow</code>][AnyRow] — a row of an empty [<code>DataFrame</code>][DataFrame] with no columns.
         */
        public val empty: AnyRow = DataFrame.empty(1)[0]
    }
}

// Property-syntax shortcuts for the corresponding DataRow functions
internal val AnyRow.values: List<Any?> get() = values()
internal val AnyRow.index: Int get() = index()
internal val <T> DataRow<T>.prev: DataRow<T>? get() = this.prev()
internal val <T> DataRow<T>.next: DataRow<T>? get() = this.next()
