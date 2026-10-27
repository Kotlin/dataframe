package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.RequiredByIntellijPlugin
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.owner
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.GET_ROWS_ITERABLE_REPLACE
import org.jetbrains.kotlinx.dataframe.util.GET_ROWS_RANGE_REPLACE
import org.jetbrains.kotlinx.dataframe.util.GET_ROW_OR_NULL_REPLACE
import org.jetbrains.kotlinx.dataframe.util.GET_ROW_REPLACE
import org.jetbrains.kotlinx.dataframe.util.IS_EMPTY_REPLACE
import org.jetbrains.kotlinx.dataframe.util.IS_NOT_EMPTY_REPLACE
import org.jetbrains.kotlinx.dataframe.util.MESSAGE_SHORTCUT
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty
import kotlin.reflect.KType

@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(IS_EMPTY_REPLACE), DeprecationLevel.WARNING)
public fun DataRow<*>.isEmpty(): Boolean = owner.columns().all { it[index] == null }

@Suppress("DEPRECATION_ERROR")
@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(IS_NOT_EMPTY_REPLACE), DeprecationLevel.WARNING)
public fun DataRow<*>.isNotEmpty(): Boolean = !isEmpty()

// region docs

// endregion

/**
 * Returns the values of this [<code>DataRow</code>][DataRow] that are of type [<code>R</code>][R], without their column names.
 *
 * The type check is done with `is R` on each value, so a nullable `R` (like `Int?`) also keeps the `null` values.
 *
 * There are three functions listing the values of a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]; they differ in what is filtered and what is returned:
 * - [<code>valuesOf</code>][org.jetbrains.kotlinx.dataframe.DataRow.valuesOf]`<R>()` — only the values of type `R`, without their column names;
 * - [<code>namedValues</code>][org.jetbrains.kotlinx.dataframe.DataRow.namedValues]`()` — all values, each paired with its column name in a [<code>NameValuePair</code>][org.jetbrains.kotlinx.dataframe.api.NameValuePair];
 * - [<code>namedValuesOf</code>][org.jetbrains.kotlinx.dataframe.DataRow.namedValuesOf]`<R>()` — only the [<code>NameValuePair</code>][org.jetbrains.kotlinx.dataframe.api.NameValuePair]s whose value is of type `R`.
 *
 * All of them go over the top-level columns of the row, in column order:
 * the value of a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]
 * and is not split into the values of its nested columns.
 * Use [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataRow.values] to get all values without filtering.
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * ### Example
 *
 * ```kotlin
 * val row = df[0] // name: (Alice, Cooper), age: 15, city: London, weight: 54, isHappy: true
 *
 * // Only the values, filtered by type
 * row.valuesOf<Int>() // [15, 54]
 *
 * // All values, paired with their column names
 * row.namedValues() // [(name, (Alice, Cooper)), (age, 15), (city, London), (weight, 54), (isHappy, true)]
 *
 * // Only the name-value pairs whose value is an Int
 * row.namedValuesOf<Int>() // [(age, 15), (weight, 54)]
 * ```
 *
 * @param [R] the type of values to keep.
 * @return A [<code>List</code>][List] of the values of type [<code>R</code>][R], in column order.
 */
public inline fun <reified R> DataRow<*>.valuesOf(): List<R> = values().filterIsInstance<R>()

// region DataSchema

/**
 * A [<code>column name</code>][name] paired with a [<code>value</code>][value]: the [<code>DataSchema</code>][DataSchema] of a single cell of a [<code>DataRow</code>][DataRow] seen as a map entry.
 *
 * It is the element type of [<code>namedValues</code>][DataRow.namedValues] and [<code>namedValuesOf</code>][DataRow.namedValuesOf],
 * and the schema of the [<code>DataFrame</code>][DataFrame] returned by [<code>transpose</code>][DataRow.transpose] and
 * [<code>transposeTo</code>][DataRow.transposeTo], which has a `name` and a `value` column.
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * @param [V] the type of [<code>value</code>][value].
 * @property [name] the name of the column the [<code>value</code>][value] comes from.
 * @property [value] the cell value.
 */
@DataSchema
@RequiredByIntellijPlugin
public data class NameValuePair<V>(override val name: String, override val value: V) : NameValueProperty<V>

// Without these overloads row.transpose().name or row.map { name } won't resolve

/**
 * Returns the `name` column of this [<code>NameValuePair</code>][NameValuePair] [<code>DataFrame</code>][DataFrame]: the column names of the transposed [<code>DataRow</code>][DataRow].
 *
 * This accessor is declared explicitly for the star-projected schema `NameValuePair<*>`,
 * which is what [<code>transpose</code>][org.jetbrains.kotlinx.dataframe.DataRow.transpose] returns.
 * The generated extension properties of [<code>NameValuePair</code>][org.jetbrains.kotlinx.dataframe.api.NameValuePair] require a concrete value type,
 * so without this overload `row.transpose().name`, `row.transpose().value`,
 * or `row.transpose().map { name }` would not resolve.
 *
 * ### Example
 *
 * ```kotlin
 * val transposed = df[0].transpose()
 * transposed.name // DataColumn<String>: [name, age, city, weight, isHappy]
 * transposed.value // DataColumn<*>: [(Alice, Cooper), 15, London, 54, true]
 *
 * transposed[1].name // "age"
 * transposed[1].value // 15
 * ```
 */
public val ColumnsContainer<NameValuePair<*>>.name: DataColumn<String>
    @JvmName("NameValuePairAny_name")
    get() = this["name"] as DataColumn<String>

/**
 * Returns the `name` of this [<code>NameValuePair</code>][NameValuePair] row: the name of the column the [<code>value</code>][DataRow.value] comes from.
 *
 * This accessor is declared explicitly for the star-projected schema `NameValuePair<*>`,
 * which is what [<code>transpose</code>][org.jetbrains.kotlinx.dataframe.DataRow.transpose] returns.
 * The generated extension properties of [<code>NameValuePair</code>][org.jetbrains.kotlinx.dataframe.api.NameValuePair] require a concrete value type,
 * so without this overload `row.transpose().name`, `row.transpose().value`,
 * or `row.transpose().map { name }` would not resolve.
 *
 * ### Example
 *
 * ```kotlin
 * val transposed = df[0].transpose()
 * transposed.name // DataColumn<String>: [name, age, city, weight, isHappy]
 * transposed.value // DataColumn<*>: [(Alice, Cooper), 15, London, 54, true]
 *
 * transposed[1].name // "age"
 * transposed[1].value // 15
 * ```
 */
public val DataRow<NameValuePair<*>>.name: String
    @JvmName("NameValuePairAny_name")
    get() = this["name"] as String

/**
 * Returns the `value` column of this [<code>NameValuePair</code>][NameValuePair] [<code>DataFrame</code>][DataFrame]: the cell values of the transposed [<code>DataRow</code>][DataRow].
 * Its type is the common type of all these values.
 *
 * This accessor is declared explicitly for the star-projected schema `NameValuePair<*>`,
 * which is what [<code>transpose</code>][org.jetbrains.kotlinx.dataframe.DataRow.transpose] returns.
 * The generated extension properties of [<code>NameValuePair</code>][org.jetbrains.kotlinx.dataframe.api.NameValuePair] require a concrete value type,
 * so without this overload `row.transpose().name`, `row.transpose().value`,
 * or `row.transpose().map { name }` would not resolve.
 *
 * ### Example
 *
 * ```kotlin
 * val transposed = df[0].transpose()
 * transposed.name // DataColumn<String>: [name, age, city, weight, isHappy]
 * transposed.value // DataColumn<*>: [(Alice, Cooper), 15, London, 54, true]
 *
 * transposed[1].name // "age"
 * transposed[1].value // 15
 * ```
 */
public val ColumnsContainer<NameValuePair<*>>.value: DataColumn<*>
    @JvmName("NameValuePairAny_value")
    get() = this["value"]

/**
 * Returns the `value` of this [<code>NameValuePair</code>][NameValuePair] row: the cell value of the column named [<code>name</code>][DataRow.name].
 *
 * This accessor is declared explicitly for the star-projected schema `NameValuePair<*>`,
 * which is what [<code>transpose</code>][org.jetbrains.kotlinx.dataframe.DataRow.transpose] returns.
 * The generated extension properties of [<code>NameValuePair</code>][org.jetbrains.kotlinx.dataframe.api.NameValuePair] require a concrete value type,
 * so without this overload `row.transpose().name`, `row.transpose().value`,
 * or `row.transpose().map { name }` would not resolve.
 *
 * ### Example
 *
 * ```kotlin
 * val transposed = df[0].transpose()
 * transposed.name // DataColumn<String>: [name, age, city, weight, isHappy]
 * transposed.value // DataColumn<*>: [(Alice, Cooper), 15, London, 54, true]
 *
 * transposed[1].name // "age"
 * transposed[1].value // 15
 * ```
 */
public val DataRow<NameValuePair<*>>.value: Any?
    @JvmName("NameValuePairAny_value")
    get() = this["value"]

// endregion

/**
 * Returns the values of this [<code>DataRow</code>][DataRow] that are of type [<code>R</code>][R], each paired with its column name in a [<code>NameValuePair</code>][NameValuePair].
 *
 * The type check is done with `is R` on each value, so a nullable `R` (like `Int?`) also keeps the `null` values.
 *
 * There are three functions listing the values of a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]; they differ in what is filtered and what is returned:
 * - [<code>valuesOf</code>][org.jetbrains.kotlinx.dataframe.DataRow.valuesOf]`<R>()` — only the values of type `R`, without their column names;
 * - [<code>namedValues</code>][org.jetbrains.kotlinx.dataframe.DataRow.namedValues]`()` — all values, each paired with its column name in a [<code>NameValuePair</code>][org.jetbrains.kotlinx.dataframe.api.NameValuePair];
 * - [<code>namedValuesOf</code>][org.jetbrains.kotlinx.dataframe.DataRow.namedValuesOf]`<R>()` — only the [<code>NameValuePair</code>][org.jetbrains.kotlinx.dataframe.api.NameValuePair]s whose value is of type `R`.
 *
 * All of them go over the top-level columns of the row, in column order:
 * the value of a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]
 * and is not split into the values of its nested columns.
 * Use [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataRow.values] to get all values without filtering.
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * See also [<code>transposeTo</code>][DataRow.transposeTo], which converts all values to [<code>R</code>][R] instead of skipping
 * the values of other types, and returns a [<code>DataFrame</code>][DataFrame].
 *
 * ### Example
 *
 * ```kotlin
 * val row = df[0] // name: (Alice, Cooper), age: 15, city: London, weight: 54, isHappy: true
 *
 * // Only the values, filtered by type
 * row.valuesOf<Int>() // [15, 54]
 *
 * // All values, paired with their column names
 * row.namedValues() // [(name, (Alice, Cooper)), (age, 15), (city, London), (weight, 54), (isHappy, true)]
 *
 * // Only the name-value pairs whose value is an Int
 * row.namedValuesOf<Int>() // [(age, 15), (weight, 54)]
 * ```
 *
 * @param [R] the type of values to keep.
 * @return A [<code>List</code>][List] of [<code>NameValuePair</code>][NameValuePair]s with the values of type [<code>R</code>][R], in column order.
 */
public inline fun <reified R> DataRow<*>.namedValuesOf(): List<NameValuePair<R>> =
    values().zip(columnNames()).filter { it.first is R }.map { NameValuePair(it.second, it.first as R) }

/**
 * Returns all values of this [<code>DataRow</code>][DataRow], each paired with its column name in a [<code>NameValuePair</code>][NameValuePair].
 *
 * There are three functions listing the values of a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]; they differ in what is filtered and what is returned:
 * - [<code>valuesOf</code>][org.jetbrains.kotlinx.dataframe.DataRow.valuesOf]`<R>()` — only the values of type `R`, without their column names;
 * - [<code>namedValues</code>][org.jetbrains.kotlinx.dataframe.DataRow.namedValues]`()` — all values, each paired with its column name in a [<code>NameValuePair</code>][org.jetbrains.kotlinx.dataframe.api.NameValuePair];
 * - [<code>namedValuesOf</code>][org.jetbrains.kotlinx.dataframe.DataRow.namedValuesOf]`<R>()` — only the [<code>NameValuePair</code>][org.jetbrains.kotlinx.dataframe.api.NameValuePair]s whose value is of type `R`.
 *
 * All of them go over the top-level columns of the row, in column order:
 * the value of a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]
 * and is not split into the values of its nested columns.
 * Use [<code>values</code>][org.jetbrains.kotlinx.dataframe.DataRow.values] to get all values without filtering.
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * See also [<code>transpose</code>][DataRow.transpose], which returns the same name-value pairs as a [<code>DataFrame</code>][DataFrame].
 *
 * ### Example
 *
 * ```kotlin
 * val row = df[0] // name: (Alice, Cooper), age: 15, city: London, weight: 54, isHappy: true
 *
 * // Only the values, filtered by type
 * row.valuesOf<Int>() // [15, 54]
 *
 * // All values, paired with their column names
 * row.namedValues() // [(name, (Alice, Cooper)), (age, 15), (city, London), (weight, 54), (isHappy, true)]
 *
 * // Only the name-value pairs whose value is an Int
 * row.namedValuesOf<Int>() // [(age, 15), (weight, 54)]
 * ```
 *
 * @return A [<code>List</code>][List] of [<code>NameValuePair</code>][NameValuePair]s, one for each column, in column order.
 */
@RequiredByIntellijPlugin
public fun DataRow<*>.namedValues(): List<NameValuePair<Any?>> =
    values().zip(columnNames()) { value, name -> NameValuePair(name, value) }

// region getValue

/**
 * Returns the value of the column with the given [columnName] in this [<code>DataRow</code>][DataRow],
 * cast to [T]. Throws an [<code>IllegalArgumentException</code>][IllegalArgumentException] if there is no such column.
 *
 * The cast to [T] is unchecked: because of type erasure, a wrong [T]
 * (like `getValue<Int>("city")`) does not fail here, but later with a [<code>ClassCastException</code>][ClassCastException]
 * where the value is used as [T].
 * Use a nullable [T] for columns that can contain `null`.
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * See also [<code>getValueOrNull</code>][DataRow.getValueOrNull],
 * which returns `null` instead of throwing if there is no such column.
 *
 * ### Example
 *
 * ```kotlin
 * val row = df[0]
 * row.getValue<Int>("age") // 15
 * row.getValue<Int?>("weight") // 54
 *
 * row.getValueOrNull<Int>("height") // null: there is no "height" column
 * // row.getValue<Int>("height") would throw an IllegalArgumentException instead
 * ```
 *
 *
 *
 * @param [T] the type to cast the value to.
 * @param [columnName] the name of a top-level column of the [<code>DataFrame</code>][DataFrame] this row belongs to.
 * @return The value of the column [<code>columnName</code>][columnName], cast to [<code>T</code>][T].
 * @throws [IllegalArgumentException] if there is no column with the given [<code>columnName</code>][columnName].
 */
public fun <T> DataRow<*>.getValue(columnName: String): T = get(columnName) as T

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataRow<*>.getValue(column: ColumnReference<T>): T = get(column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataRow<*>.getValue(column: KProperty<T>): T = get(column)

/**
 * Returns the value of the column with the given [columnName] in this [<code>DataRow</code>][DataRow],
 * cast to [T], or `null` if there is no such column.
 *
 * The cast to [T] is unchecked: because of type erasure, a wrong [T]
 * (like `getValue<Int>("city")`) does not fail here, but later with a [<code>ClassCastException</code>][ClassCastException]
 * where the value is used as [T].
 * Use a nullable [T] for columns that can contain `null`.
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * See also [<code>getValue</code>][DataRow.getValue], which throws if there is no such column,
 * and [<code>containsKey</code>][DataRow.containsKey], which checks if there is such a column.
 *
 * Note that `null` is also returned if the column exists and its value is `null`.
 *
 * ### Example
 *
 * ```kotlin
 * val row = df[0]
 * row.getValue<Int>("age") // 15
 * row.getValue<Int?>("weight") // 54
 *
 * row.getValueOrNull<Int>("height") // null: there is no "height" column
 * // row.getValue<Int>("height") would throw an IllegalArgumentException instead
 * ```
 *
 *
 *
 * @param [T] the type to cast the value to.
 * @param [columnName] the name of a top-level column of the [<code>DataFrame</code>][DataFrame] this row belongs to.
 * @return The value of the column [<code>columnName</code>][columnName], cast to [<code>T</code>][T], or `null` if there is no such column.
 */
public fun <T> DataRow<*>.getValueOrNull(columnName: String): T? = getOrNull(columnName) as T?

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataRow<*>.getValueOrNull(column: KProperty<T>): T? = getValueOrNull<T>(column.columnName)

// endregion

// region contains

/**
 * Returns `true` if the [<code>DataFrame</code>][DataFrame] this row belongs to has a top-level column with the given [<code>columnName</code>][columnName].
 *
 * Nested columns of a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] are not checked;
 * use [<code>contains</code>][DataRow.contains] with a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
 * (like `pathOf("name", "firstName") in row`) for them.
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * See also [<code>getValue</code>][DataRow.getValue], which throws if there is no such column,
 * and [<code>getValueOrNull</code>][DataRow.getValueOrNull], which returns `null` instead.
 *
 * ### Example
 *
 * ```kotlin
 * val row = df[0]
 * row.containsKey("age") // true
 * row.containsKey("height") // false
 *
 * // `containsKey` only checks top-level columns, a column path can point to a nested one
 * row.containsKey("firstName") // false
 * pathOf("name", "firstName") in row // true
 * ```
 *
 * @param [columnName] the name of the column to look for.
 * @return `true` if there is a top-level column with the given [<code>columnName</code>][columnName], `false` otherwise.
 */
public fun DataRow<*>.containsKey(columnName: String): Boolean = owner.containsColumn(columnName)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun DataRow<*>.containsKey(column: ColumnReference<*>): Boolean = owner.containsColumn(column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun DataRow<*>.containsKey(column: KProperty<*>): Boolean = owner.containsColumn(column)

/**
 * Returns `true` if the [<code>DataFrame</code>][DataFrame] this row belongs to has the given [<code>column</code>][column].
 * Allows writing `column in row`.
 *
 * Unlike [<code>containsKey</code>][DataRow.containsKey] with a column name, the [<code>column</code>][column] is resolved by its path,
 * so a [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
 * (like `pathOf("name", "firstName")`) can point to a nested column of a column group.
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * ### Example
 *
 * ```kotlin
 * val row = df[0]
 * row.containsKey("age") // true
 * row.containsKey("height") // false
 *
 * // `containsKey` only checks top-level columns, a column path can point to a nested one
 * row.containsKey("firstName") // false
 * pathOf("name", "firstName") in row // true
 * ```
 *
 * @param [column] the reference to the column to look for.
 * @return `true` if the [<code>column</code>][column] is found, `false` otherwise.
 */
public operator fun DataRow<*>.contains(column: ColumnReference<*>): Boolean = containsKey(column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public operator fun DataRow<*>.contains(column: KProperty<*>): Boolean = containsKey(column)

// endregion

/**
 * Calculates the difference between the results of a row expression computed on the current and previous [<code>DataRow</code>][DataRow].
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * @return [firstRowValue] for the first row; difference between expression computed for current and previous row for the following rows
 */
internal typealias DiffDocs = Nothing

/**
 * Calculates the difference between the results of a row expression computed on the current and previous [<code>DataRow</code>][DataRow].
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * @return null for the first row; difference between expression computed for current and previous row for the following rows
 */
internal typealias DiffOrNullDocs = Nothing

/**
 * Calculates the difference between the results of a row expression computed on the current and previous [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow].
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * @return [firstRowValue] for the first row; difference between expression computed for current and previous row for the following rows
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <T> DataRow<T>.diff(firstRowResult: Double, expression: RowExpression<T, Double>): Double =
    prev()?.let { p -> expression(this, this) - expression(p, p) }
        ?: firstRowResult

/**
 * Calculates the difference between the results of a row expression computed on the current and previous [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow].
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * @return [firstRowValue] for the first row; difference between expression computed for current and previous row for the following rows
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
// required to resolve `diff(0) { intValue }`
public inline fun <T> DataRow<T>.diff(firstRowResult: Int, expression: RowExpression<T, Int>): Int =
    prev()?.let { p -> expression(this, this) - expression(p, p) }
        ?: firstRowResult

/**
 * Calculates the difference between the results of a row expression computed on the current and previous [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow].
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * @return [firstRowValue] for the first row; difference between expression computed for current and previous row for the following rows
 */
public inline fun <T> DataRow<T>.diff(firstRowResult: Long, expression: RowExpression<T, Long>): Long =
    prev()?.let { p -> expression(this, this) - expression(p, p) }
        ?: firstRowResult

/**
 * Calculates the difference between the results of a row expression computed on the current and previous [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow].
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * @return [firstRowValue] for the first row; difference between expression computed for current and previous row for the following rows
 */
public inline fun <T> DataRow<T>.diff(firstRowResult: Float, expression: RowExpression<T, Float>): Float =
    prev()?.let { p -> expression(this, this) - expression(p, p) }
        ?: firstRowResult

/**
 * Calculates the difference between the results of a row expression computed on the current and previous [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow].
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * @return null for the first row; difference between expression computed for current and previous row for the following rows
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <T> DataRow<T>.diffOrNull(expression: RowExpression<T, Double>): Double? =
    prev()?.let { p -> expression(this, this) - expression(p, p) }

/**
 * Calculates the difference between the results of a row expression computed on the current and previous [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow].
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * @return null for the first row; difference between expression computed for current and previous row for the following rows
 */
public inline fun <T> DataRow<T>.diffOrNull(expression: RowExpression<T, Int>): Int? =
    prev()?.let { p -> expression(this, this) - expression(p, p) }

/**
 * Calculates the difference between the results of a row expression computed on the current and previous [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow].
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * @return null for the first row; difference between expression computed for current and previous row for the following rows
 */
public inline fun <T> DataRow<T>.diffOrNull(expression: RowExpression<T, Long>): Long? =
    prev()?.let { p -> expression(this, this) - expression(p, p) }

/**
 * Calculates the difference between the results of a row expression computed on the current and previous [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow].
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * @return null for the first row; difference between expression computed for current and previous row for the following rows
 */
public inline fun <T> DataRow<T>.diffOrNull(expression: RowExpression<T, Float>): Float? =
    prev()?.let { p -> expression(this, this) - expression(p, p) }

/**
 * Returns the number of columns of this [<code>DataRow</code>][DataRow], which is the number of its values.
 *
 * As seen from a row, this describes the columns of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] the row belongs to (see [<code>df</code>][org.jetbrains.kotlinx.dataframe.DataRow.df]).
 * For a row of a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
 * these are the columns of that group, not of the top-level [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * See also [<code>columnsCount</code>][org.jetbrains.kotlinx.dataframe.DataRow.columnsCount], [<code>columnNames</code>][org.jetbrains.kotlinx.dataframe.DataRow.columnNames],
 * and [<code>columnTypes</code>][org.jetbrains.kotlinx.dataframe.DataRow.columnTypes].
 *
 * ### Example
 *
 * ```kotlin
 * val row = df[0]
 * row.columnsCount() // 5
 * row.columnNames() // [name, age, city, weight, isHappy]
 * row.columnTypes() // [DataRow<*>, Int, String?, Int?, Boolean]
 *
 * // A row of a column group describes the columns of that group
 * row.name.columnNames() // [firstName, lastName]
 * ```
 *
 * @return The number of top-level columns of the [<code>DataFrame</code>][DataFrame] this row belongs to.
 */
@RequiredByIntellijPlugin
public fun DataRow<*>.columnsCount(): Int = df().ncol

/**
 * Returns the names of the columns of this [<code>DataRow</code>][DataRow], in column order.
 *
 * As seen from a row, this describes the columns of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] the row belongs to (see [<code>df</code>][org.jetbrains.kotlinx.dataframe.DataRow.df]).
 * For a row of a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
 * these are the columns of that group, not of the top-level [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * See also [<code>columnsCount</code>][org.jetbrains.kotlinx.dataframe.DataRow.columnsCount], [<code>columnNames</code>][org.jetbrains.kotlinx.dataframe.DataRow.columnNames],
 * and [<code>columnTypes</code>][org.jetbrains.kotlinx.dataframe.DataRow.columnTypes].
 *
 * ### Example
 *
 * ```kotlin
 * val row = df[0]
 * row.columnsCount() // 5
 * row.columnNames() // [name, age, city, weight, isHappy]
 * row.columnTypes() // [DataRow<*>, Int, String?, Int?, Boolean]
 *
 * // A row of a column group describes the columns of that group
 * row.name.columnNames() // [firstName, lastName]
 * ```
 *
 * @return A [<code>List</code>][List] of the names of the top-level columns of the [<code>DataFrame</code>][DataFrame] this row belongs to.
 */
public fun DataRow<*>.columnNames(): List<String> = df().columnNames()

/**
 * Returns the [<code>types</code>][KType] of the columns of this [<code>DataRow</code>][DataRow], in column order.
 *
 * These are the types of the columns, not of the values in this row:
 * for a column of type `Int?`, it is `Int?` even if the value in this row is not `null`.
 *
 * As seen from a row, this describes the columns of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] the row belongs to (see [<code>df</code>][org.jetbrains.kotlinx.dataframe.DataRow.df]).
 * For a row of a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
 * these are the columns of that group, not of the top-level [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * See also [<code>columnsCount</code>][org.jetbrains.kotlinx.dataframe.DataRow.columnsCount], [<code>columnNames</code>][org.jetbrains.kotlinx.dataframe.DataRow.columnNames],
 * and [<code>columnTypes</code>][org.jetbrains.kotlinx.dataframe.DataRow.columnTypes].
 *
 * ### Example
 *
 * ```kotlin
 * val row = df[0]
 * row.columnsCount() // 5
 * row.columnNames() // [name, age, city, weight, isHappy]
 * row.columnTypes() // [DataRow<*>, Int, String?, Int?, Boolean]
 *
 * // A row of a column group describes the columns of that group
 * row.name.columnNames() // [firstName, lastName]
 * ```
 *
 * @return A [<code>List</code>][List] of the [<code>KType</code>][KType]s of the top-level columns of the [<code>DataFrame</code>][DataFrame] this row belongs to.
 */
public fun DataRow<*>.columnTypes(): List<KType> = df().columnTypes()

@Suppress("DEPRECATION_ERROR")
@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(GET_ROW_REPLACE), DeprecationLevel.WARNING)
public fun <T> DataRow<T>.getRow(index: Int): DataRow<T> = getRowOrNull(index)!!

@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(GET_ROWS_ITERABLE_REPLACE), DeprecationLevel.WARNING)
public fun <T> DataRow<T>.getRows(indices: Iterable<Int>): DataFrame<T> = df().getRows(indices)

@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(GET_ROWS_RANGE_REPLACE), DeprecationLevel.WARNING)
public fun <T> DataRow<T>.getRows(indices: IntRange): DataFrame<T> = df().getRows(indices)

@Deprecated(MESSAGE_SHORTCUT, ReplaceWith(GET_ROW_OR_NULL_REPLACE), DeprecationLevel.WARNING)
public fun <T> DataRow<T>.getRowOrNull(index: Int): DataRow<T>? {
    val df = df()
    return if (index >= 0 && index < df.nrow) df[index] else null
}

/**
 * Returns the previous [<code>row</code>][DataRow] in the [<code>DataFrame</code>][DataFrame] relative to the current row.
 * If the current row is the first row in the [<code>DataFrame</code>][DataFrame], it returns `null`.
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * See also [<code>next</code>][DataRow.next] and [<code>relative</code>][DataRow.relative].
 *
 * @return The previous [<code>DataRow</code>][DataRow] if it exists, or `null` if the current row is the first in the [<code>DataFrame</code>][DataFrame].
 */
public fun <T> DataRow<T>.prev(): DataRow<T>? {
    val index = index()
    return if (index > 0) df()[index - 1] else null
}

/**
 * Returns the next [<code>row</code>][DataRow] in the [<code>DataFrame</code>][DataFrame] relative to the current row.
 * If the current row is the last row in the [<code>DataFrame</code>][DataFrame], it returns `null`.
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * See also [<code>prev</code>][DataRow.prev] and [<code>relative</code>][DataRow.relative].
 *
 * @return The next [<code>DataRow</code>][DataRow] if it exists, or `null` if the current row is the last in the [<code>DataFrame</code>][DataFrame].
 */
public fun <T> DataRow<T>.next(): DataRow<T>? {
    val index = index()
    val df = df()
    return if (index < df.nrow - 1) df[index + 1] else null
}

/**
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the rows of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] this row belongs to (see [<code>df</code>][org.jetbrains.kotlinx.dataframe.DataRow.df]),
 * at the given offsets relative to the [<code>index</code>][org.jetbrains.kotlinx.dataframe.DataRow.index] of this row:
 * offset `0` is this row, `-1` is the [<code>previous</code>][org.jetbrains.kotlinx.dataframe.DataRow.prev] row, `1` is the [<code>next</code>][org.jetbrains.kotlinx.dataframe.DataRow.next] row.
 *
 * The rows are returned in the order of [relativeIndices]. Offsets that point outside
 * the [<code>DataFrame</code>][DataFrame] are dropped, so the result can have fewer rows than [relativeIndices] or no rows at all.
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * See also [<code>prev</code>][org.jetbrains.kotlinx.dataframe.DataRow.prev], [<code>next</code>][org.jetbrains.kotlinx.dataframe.DataRow.next], and [<code>movingAverage</code>][org.jetbrains.kotlinx.dataframe.DataRow.movingAverage],
 * which aggregates a window of the previous rows instead of returning it.
 *
 * ### Example
 *
 * ```kotlin
 * val row = df[2]
 * // The previous, current, and next rows: rows 1, 2, and 3
 * row.relative(-1..1)
 *
 * // Offsets outside the DataFrame are dropped: rows 2 and 3
 * row.relative(listOf(-5, 0, 1))
 *
 * // The bounds of a range are coerced to the DataFrame: rows 0, 1, and 2
 * row.relative(-5..0)
 * ```
 *
 *
 * @param [relativeIndices] the offsets of the rows to return, relative to the index of this row.
 * @return A [<code>DataFrame</code>][DataFrame] with the rows at the given offsets that exist.
 */
@Suppress("DEPRECATION_ERROR")
public fun <T> DataRow<T>.relative(relativeIndices: Iterable<Int>): DataFrame<T> =
    getRows(relativeIndices.mapNotNull { (index + it).let { if (it >= 0 && it < df().rowsCount()) it else null } })

/**
 * Returns a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the rows of the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] this row belongs to (see [<code>df</code>][org.jetbrains.kotlinx.dataframe.DataRow.df]),
 * at the given offsets relative to the [<code>index</code>][org.jetbrains.kotlinx.dataframe.DataRow.index] of this row:
 * offset `0` is this row, `-1` is the [<code>previous</code>][org.jetbrains.kotlinx.dataframe.DataRow.prev] row, `1` is the [<code>next</code>][org.jetbrains.kotlinx.dataframe.DataRow.next] row.
 *
 * Unlike the overload with an [<code>Iterable</code>][Iterable] of offsets, the range is not filtered
 * offset by offset: both of its bounds, shifted by the index of this row, are coerced into the valid row indices
 * of the [<code>DataFrame</code>][DataFrame]. So a range that partly sticks out of the [<code>DataFrame</code>][DataFrame], like `-5..0` for the third row,
 * is cut to the rows that exist.
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * See also [<code>prev</code>][org.jetbrains.kotlinx.dataframe.DataRow.prev], [<code>next</code>][org.jetbrains.kotlinx.dataframe.DataRow.next], and [<code>movingAverage</code>][org.jetbrains.kotlinx.dataframe.DataRow.movingAverage],
 * which aggregates a window of the previous rows instead of returning it.
 *
 * ### Example
 *
 * ```kotlin
 * val row = df[2]
 * // The previous, current, and next rows: rows 1, 2, and 3
 * row.relative(-1..1)
 *
 * // Offsets outside the DataFrame are dropped: rows 2 and 3
 * row.relative(listOf(-5, 0, 1))
 *
 * // The bounds of a range are coerced to the DataFrame: rows 0, 1, and 2
 * row.relative(-5..0)
 * ```
 *
 *
 * @param [relativeIndices] the range of offsets of the rows to return, relative to the index of this row.
 * @return A [<code>DataFrame</code>][DataFrame] with the rows in the given range of offsets, cut to the bounds of the [<code>DataFrame</code>][DataFrame].
 */
@Suppress("DEPRECATION_ERROR")
public fun <T> DataRow<T>.relative(relativeIndices: IntRange): DataFrame<T> =
    getRows(
        (relativeIndices.first + index).coerceIn(df().indices)..(relativeIndices.last + index).coerceIn(df().indices),
    )

/**
 * Returns the average of the [<code>expression</code>][expression] computed on this row and the `k - 1` rows before it.
 *
 * Near the start of the [<code>DataFrame</code>][DataFrame], fewer than [<code>k</code>][k] rows are available, so the sum is divided
 * by the actual number of rows: for the first row, the result is the value of [<code>expression</code>][expression] for that row.
 * If [<code>k</code>][k] is `0`, no rows are taken and the result is [<code>Double.NaN</code>][Double.NaN].
 *
 * The [<code>expression</code>][expression] is called for each averaged row with that row as `it`, while `this` stays
 * the current row, so access the averaged values through `it`, like `movingAverage(3) { it.age }`.
 *
 * For more information: [See Row Functions on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#row-functions)
 *
 * See also [<code>relative</code>][DataRow.relative], which returns a window of rows around this row as a [<code>DataFrame</code>][DataFrame],
 * and [<code>prev</code>][DataRow.prev].
 *
 * ### Example
 *
 * ```kotlin
 * // The average age of the current and the two previous rows.
 * // The first two rows have fewer rows to average over: 15.0, 30.0, 26.67, ...
 * // `it` is the row being averaged; `this` stays the current row
 * df.add("ageMovingAverage") { movingAverage(3) { it.age } }
 * ```
 *
 * @param [k] the size of the window: the number of rows to average over, including this row.
 * @throws [IllegalArgumentException] if [<code>k</code>][k] is negative.
 * @param [expression] the [<code>row expression</code>][RowExpression] computing the [<code>Number</code>][Number] to average for each row.
 * @return The average of [<code>expression</code>][expression] over the current row and up to `k - 1` previous rows,
 * as a [<code>Double</code>][Double].
 */
public inline fun <T> DataRow<T>.movingAverage(k: Int, expression: RowExpression<T, Number>): Double {
    var count = 0
    return backwardIterable().take(k).sumOf {
        count++
        expression(it).toDouble()
    } / count
}
