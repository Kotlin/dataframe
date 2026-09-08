package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.columns.createComputedColumnReference
import org.jetbrains.kotlinx.dataframe.impl.columns.newColumn
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.UNIFIED_SIMILAR_CS_API
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region docs

/**
 * {@comment
 *    The Map Operation KDoc-topic; it also holds all common `map` KDoc-snippets.
 *    Link to it with `{@include [MapDocsLink]}`.
 * }
 *
 * ## The Map Operation
 *
 * Computes a new value for every value, row, or group of the receiver, and collects the results.
 *
 * The functions of this family share the name but differ in what they go over and what they give back:
 *
 * - [map][DataColumn.map] and [mapIndexed][DataColumn.mapIndexed] — go over the values of a [DataColumn]
 *   and return a [DataColumn] of the same size.
 * - [map][DataFrame.map] — goes over the rows of a [DataFrame] and returns a [List].
 * - [mapToColumn][DataFrame.mapToColumn] — goes over the rows of a [DataFrame] and returns a single
 *   [DataColumn] that is not part of that [DataFrame].
 * - [mapToFrame][DataFrame.mapToFrame] — returns a new [DataFrame] made of the columns described in an [AddDsl].
 * - [map][GroupBy.map] — goes over the key–group pairs of a [GroupBy] and returns a [List].
 * - [mapToRows][GroupBy.mapToRows] — goes over the key–group pairs of a [GroupBy] and returns a [DataFrame].
 * - [mapToFrames][GroupBy.mapToFrames] — goes over the key–group pairs of a [GroupBy] and returns a [FrameColumn].
 *
 * Each result keeps the order of the values, rows, or key–group pairs it was computed from.
 *
 * See also:
 * - [add][DataFrame.add] — computes a new column in the same way, and returns the [DataFrame] with that column in it.
 * - [expr][ColumnsSelectionDsl.expr] — the same as [mapToColumn][DataFrame.mapToColumn],
 *   for use inside the Columns Selection DSL.
 * - [convert][DataFrame.convert] — computes new values for the selected columns and replaces the old ones.
 *
 * For more information: {@include [DocumentationUrls.Map]}
 */
internal interface MapDocs {

    /**
     * {@comment What the four [DataColumn] overloads have in common. KDoc-snippet.
     *    Set [TYPE_SOURCE] to where the type of the new column comes from.}
     *
     * [transform\] is called once for every value of this column, from the first one to the last one,
     * so the new column has as many values as this one, in the same order.
     * The new column has the same name as this one.
     *
     * The [type][DataColumn.type] of the new column comes from {@get [TYPE_SOURCE]},
     * and [infer\] decides whether that type is used as it is or adjusted to the computed values.
     * That type also decides what the new column is:
     * a column of [DataRow]s is a [ColumnGroup], a column of [DataFrame]s is a [FrameColumn],
     * and any other column is a [ValueColumn].
     */
    @ExcludeFromSources
    interface CommonDataColumnSnippet {

        /*
         * The key for a @set that names where the type of the new column comes from.
         */
        @ExcludeFromSources
        typealias TYPE_SOURCE = Nothing
    }

    /**
     * @param [type\] The type to give to the new column.
     * The computed values are put into the column as they are, without any conversion,
     * so [type\] has to fit them.
     * With [Infer.Type] it is only an upper bound, and the actual type is taken from the computed values.
     * Note that [type\] and the type argument `R` are independent: the result is a `DataColumn<R>` for the
     * compiler, while its [type][DataColumn.type] at runtime is [type\].
     * Keep the two in agreement unless that difference is exactly what you are after.
     * @comment What the explicit [KType] argument of the `map`/`mapIndexed` overloads does. KDoc-snippet.
     *    It sits at the end: a leading `{@comment}` would leave two blank lines inside the `@param` list.
     */
    @ExcludeFromSources
    typealias TypeParamSnippet = Nothing

    /**
     * {@comment What the three [GroupBy] overloads have in common. KDoc-snippet.}
     *
     * [body\] is called once for every key–group pair of this [GroupBy],
     * in the order in which the pairs appear in it.
     * Each pair is given to [body\] as a [GroupWithKey], both as the receiver and as the argument,
     * so the key values are available as [key][GroupWithKey.key] and the rows of the group
     * as [group][GroupWithKey.group].
     */
    @ExcludeFromSources
    typealias CommonGroupBySnippet = Nothing
}

/** [The Map Operation][MapDocs] */
@ExcludeFromSources
private typealias MapDocsLink = Nothing

// endregion

// region ColumnReference

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
internal inline fun <C, reified R> ColumnReference<C>.map(
    infer: Infer = Infer.Nulls,
    noinline transform: (C) -> R,
): ColumnReference<R> = createComputedColumnReference(name(), typeOf<R>(), infer) { transform(this@map()) }

// endregion

// region DataColumn

/**
 * Returns a new [DataColumn] with the values that [transform] computes from the values of this column.
 *
 * @include [MapDocs.CommonDataColumnSnippet] {@set [MapDocs.CommonDataColumnSnippet.TYPE_SOURCE] the reified type argument `R`}
 *
 * Use [mapIndexed] when the position of a value matters,
 * and the overload with an explicit `type` argument when the type is only known at runtime.
 *
 * See also {@include [MapDocsLink]} — an overview of the whole `map` family.
 *
 * ### Example
 *
 * ```kotlin
 * // A column of name lengths, computed from a column of names.
 * // It has the same name as the original column, so it is usually renamed on the spot.
 * val lengths = names.map { it.length }.rename("nameLength")
 * ```
 *
 * For more information: {@include [DocumentationUrls.MapOnColumn]}
 *
 * @param [R] The type of the values of the new column.
 * @include [Infer.ParamDoc] By default: [Nulls][Infer.Nulls].
 * @param [transform] A function that computes a value of the new column from a value of this column.
 * @return A new [DataColumn] with the computed values.
 */
@Interpretable("DataColumnMap")
public inline fun <T, reified R> DataColumn<T>.map(infer: Infer = Infer.Nulls, transform: (T) -> R): DataColumn<R> {
    val newValues = Array(size()) { transform(get(it)) }.asList()
    return DataColumn.createByType(name(), newValues, typeOf<R>(), infer)
}

/**
 * Returns a new [DataColumn] of the given [type] with the values
 * that [transform] computes from the values of this column.
 *
 * @include [MapDocs.CommonDataColumnSnippet] {@set [MapDocs.CommonDataColumnSnippet.TYPE_SOURCE] the [type] argument}
 *
 * Use this overload when the type of the new column is only known at runtime;
 * otherwise, use the [map] overload that takes the type from its reified type argument.
 *
 * See also {@include [MapDocsLink]} — an overview of the whole `map` family.
 *
 * ### Example
 *
 * ```kotlin
 * // The type of the new column is taken from another column
 * // instead of being named at compile time
 * val filled = values.map(fallback.type()) { it ?: 0 }
 * ```
 *
 * For more information: {@include [DocumentationUrls.MapOnColumn]}
 *
 * @param [R] The element type of the resulting [DataColumn], as inferred from [transform].
 * @include [MapDocs.TypeParamSnippet]
 * @include [Infer.ParamDoc] By default: [Nulls][Infer.Nulls].
 * @param [transform] A function that computes a value of the new column from a value of this column.
 * @return A new [DataColumn] with the computed values.
 */
@Interpretable("DataColumnMapKType")
public inline fun <T, R> DataColumn<T>.map(
    type: KType,
    infer: Infer = Infer.Nulls,
    transform: (T) -> R,
): DataColumn<R> {
    val values = Array<Any?>(size()) { transform(get(it)) }.asList()
    return DataColumn.createByType(name(), values, type, infer).cast()
}

/**
 * Returns a new [DataColumn] with the values that [transform] computes
 * from the values of this column and their positions.
 *
 * @include [MapDocs.CommonDataColumnSnippet] {@set [MapDocs.CommonDataColumnSnippet.TYPE_SOURCE] the reified type argument `R`}
 *
 * The position of the first value is `0`.
 *
 * See also {@include [MapDocsLink]} — an overview of the whole `map` family.
 *
 * ### Example
 *
 * ```kotlin
 * // A column that numbers the names: "1. Alice", "2. Bob", ...
 * val numbered = names.mapIndexed { i, name -> "\${i + 1}. \$name" }
 * ```
 *
 * For more information: {@include [DocumentationUrls.MapOnColumn]}
 *
 * @param [R] The type of the values of the new column.
 * @include [Infer.ParamDoc] By default: [Nulls][Infer.Nulls].
 * @param [transform] A function that computes a value of the new column
 * from the position of a value of this column and that value.
 * @return A new [DataColumn] with the computed values.
 */
@Interpretable("DataColumnMapIndexed")
public inline fun <T, reified R> DataColumn<T>.mapIndexed(
    infer: Infer = Infer.Nulls,
    transform: (Int, T) -> R,
): DataColumn<R> {
    val newValues = Array(size()) { transform(it, get(it)) }.asList()
    return DataColumn.createByType(name(), newValues, typeOf<R>(), infer)
}

/**
 * Returns a new [DataColumn] of the given [type] with the values that [transform] computes
 * from the values of this column and their positions.
 *
 * @include [MapDocs.CommonDataColumnSnippet] {@set [MapDocs.CommonDataColumnSnippet.TYPE_SOURCE] the [type] argument}
 *
 * The position of the first value is `0`.
 *
 * Use this overload when the type of the new column is only known at runtime;
 * otherwise, use the [mapIndexed] overload that takes the type from its reified type argument.
 *
 * See also {@include [MapDocsLink]} — an overview of the whole `map` family.
 *
 * ### Example
 *
 * ```kotlin
 * // Fill the gaps from another column of the same size,
 * // and take the type of the new column from that column
 * val filled = values.mapIndexed(fallback.type()) { i, value -> value ?: fallback[i] }
 * ```
 *
 * For more information: {@include [DocumentationUrls.MapOnColumn]}
 *
 * @param [R] The element type of the resulting [DataColumn], as inferred from [transform].
 * @include [MapDocs.TypeParamSnippet]
 * @include [Infer.ParamDoc] By default: [Nulls][Infer.Nulls].
 * @param [transform] A function that computes a value of the new column
 * from the position of a value of this column and that value.
 * @return A new [DataColumn] with the computed values.
 */
@Interpretable("DataColumnMapIndexedKType")
public inline fun <T, R> DataColumn<T>.mapIndexed(
    type: KType,
    infer: Infer = Infer.Nulls,
    transform: (Int, T) -> R,
): DataColumn<R> {
    val values = Array<Any?>(size()) { transform(it, get(it)) }.asList()
    return DataColumn.createByType(name(), values, type, infer).cast()
}

// endregion

// region DataFrame

/**
 * Returns a [List] with the values that [transform] computes from the rows of this [DataFrame].
 *
 * [transform] is called once for every row, from the first one to the last one,
 * so the list has one element per row, in row order.
 *
 * [transform] gets the row both as its receiver and as its argument,
 * so inside it `age` and `it.age` mean the same thing.
 * For more information: {@include [DocumentationUrls.DataRow.RowExpressions]}
 *
 * The result is an ordinary [List], not a [DataFrame] and not a [DataColumn].
 * Use [mapToColumn] to get a [DataColumn], and [mapToFrame] to get a [DataFrame].
 *
 * When the receiver is a [ColumnGroup], this function is the one that is called, not [DataColumn.map],
 * because a [ColumnGroup] is also a [DataFrame].
 * The result is then a [List] with one element per row of the group.
 * To get a [DataColumn] of the same size instead — a column of the [DataRow]s of the group —
 * call [asDataColumn][ColumnGroup.asDataColumn] first, and then [map][DataColumn.map].
 *
 * See also {@include [MapDocsLink]} — an overview of the whole `map` family.
 *
 * ### Example
 *
 * ```kotlin
 * // The list of birth years, one per row
 * df.map { 2021 - age }
 * ```
 *
 * For more information: {@include [DocumentationUrls.MapOverRows]}
 *
 * @param [T] The schema marker type of this [DataFrame].
 * @param [R] The type of the elements of the resulting [List].
 * @param [transform] A [RowExpression] that computes an element of the list from a row of this [DataFrame].
 * @return A [List] with one computed element per row of this [DataFrame].
 */
public inline fun <T, R> DataFrame<T>.map(transform: RowExpression<T, R>): List<R> = rows().map { transform(it, it) }

/**
 * Returns a new [DataColumn] with the given [name] and the values
 * that [body] computes from the rows of this [DataFrame].
 *
 * [body] is called once for every row, from the first one to the last one,
 * so the new column has one value per row, in row order.
 * The new column is standalone: this [DataFrame] is not changed and does not contain it.
 * Use [add][DataFrame.add] to get a [DataFrame] with the new column in it.
 *
 * @include [AddExpressionDocs]
 *
 * See also {@include [MapDocsLink]} — an overview of the whole `map` family.
 *
 * ### Example
 *
 * ```kotlin
 * // A standalone column of birth years; `df` itself is left as it was
 * val yearOfBirth = df.mapToColumn("year of birth") { 2021 - age }
 * ```
 *
 * For more information: {@include [DocumentationUrls.MapToColumn]}
 *
 * @param [T] The schema marker type of this [DataFrame].
 * @param [R] The type of the values of the new column.
 * @param [name] The name to give to the new column.
 * @include [Infer.ParamDoc] By default: [Nulls][Infer.Nulls].
 * @param [body] An [AddExpression] that computes a value of the new column from a row of this [DataFrame].
 * @return A new [DataColumn] with the computed values.
 */
public inline fun <T, reified R> DataFrame<T>.mapToColumn(
    name: String,
    infer: Infer = Infer.Nulls,
    noinline body: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(name, typeOf<R>(), infer, body)

/**
 * Returns a new [DataColumn] with the given [name] and the values
 * that [body] computes from the rows of this [ColumnsContainer].
 *
 * Use [expr][ColumnsSelectionDsl.expr] instead — it does the same and works in every Columns Selection DSL.
 */
@Deprecated(
    UNIFIED_SIMILAR_CS_API,
    replaceWith = ReplaceWith("expr(name, infer, body)", "org.jetbrains.kotlinx.dataframe.api.Infer"),
)
public inline fun <T, reified R> ColumnsContainer<T>.mapToColumn(
    name: String,
    infer: Infer = Infer.Nulls,
    noinline body: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(name, typeOf<R>(), infer, body)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified R> ColumnsContainer<T>.mapToColumn(
    column: ColumnReference<R>,
    infer: Infer = Infer.Nulls,
    noinline body: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(column, typeOf<R>(), infer, body)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified R> ColumnsContainer<T>.mapToColumn(
    column: KProperty<R>,
    infer: Infer = Infer.Nulls,
    noinline body: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(column, typeOf<R>(), infer, body)

@PublishedApi
internal fun <T, R> ColumnsContainer<T>.mapToColumn(
    name: String,
    type: KType,
    infer: Infer = Infer.Nulls,
    body: AddExpression<T, R>,
): DataColumn<R> = newColumn(type, name, infer, body)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R> ColumnsContainer<T>.mapToColumn(
    column: ColumnReference<R>,
    type: KType,
    infer: Infer = Infer.Nulls,
    body: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(column.name(), type, infer, body)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, R> ColumnsContainer<T>.mapToColumn(
    column: KProperty<R>,
    type: KType,
    infer: Infer = Infer.Nulls,
    body: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(column.columnName, type, infer, body)

/**
 * Returns a new [DataFrame] made of the columns described in [body].
 *
 * [body] describes the new columns with the [AddDsl] operations —
 * `from`, `into`, `expr`, `group`,
 * and the unary `+` that takes a column of this [DataFrame] as it is.
 * The result holds only those columns, in the order in which they are described, and nothing else.
 * This is what makes it different from [add][DataFrame.add],
 * where the columns of this [DataFrame] are also part of the result.
 *
 * Every column computed from a row expression has one value per row of this [DataFrame], in row order.
 * An empty [body] describes no columns, so it gives a [DataFrame] with no columns and no rows.
 *
 * See also {@include [MapDocsLink]} — an overview of the whole `map` family.
 *
 * ### Example
 *
 * ```kotlin
 * // A frame of four new columns and a copy of "city";
 * // "name" and "age" of `df` are not in the result
 * df.mapToFrame {
 *     "year of birth" from { 2021 - age }
 *     expr { age > 18 } into "is adult"
 *     name.lastName.map { it.length } into "last name length"
 *     "full name" from { name.firstName + " " + name.lastName }
 *     +city
 * }
 * ```
 *
 * For more information: {@include [DocumentationUrls.MapToFrame]}
 *
 * @param [T] The schema marker type of this [DataFrame].
 * @param [body] An [AddDsl] expression that describes the columns of the new [DataFrame].
 * @return A new [DataFrame] with the described columns.
 */
@Refine
@Interpretable("MapToFrame")
public inline fun <T> DataFrame<T>.mapToFrame(body: AddDsl<T>.() -> Unit): DataFrame<*> {
    val dsl = AddDsl(this)
    body(dsl)
    return dataFrameOf(dsl.columns)
}

// endregion

// region GroupBy

/**
 * Returns a [List] with the values that [body] computes from the key–group pairs of this [GroupBy].
 *
 * @include [MapDocs.CommonGroupBySnippet]
 *
 * A pair for which [body] returns `null` gives no element,
 * so the list can be shorter than the number of key–group pairs.
 * Apart from that, the list has one element per pair, in the same order.
 *
 * Use [mapToRows] to get a [DataFrame] of rows, and [mapToFrames] to get a [FrameColumn] of dataframes.
 *
 * See also {@include [MapDocsLink]} — an overview of the whole `map` family.
 *
 * ### Example
 *
 * ```kotlin
 * // The size of every group, as a list, in the order of the groups
 * df.groupBy { city }.map { group.rowsCount() }
 * ```
 *
 * For more information: {@include [DocumentationUrls.MapOnGroupBy]}
 *
 * @param [T] The schema marker type of the grouping [keys][GroupBy.keys].
 * @param [G] The schema marker type of the groups.
 * @param [R] The type of the elements of the resulting [List].
 * @param [body] A [Selector] that computes an element of the list from a key–group pair.
 * @return A [List] with one computed element per key–group pair, without the `null` results.
 */
public inline fun <T, G, R> GroupBy<T, G>.map(body: Selector<GroupWithKey<T, G>, R>): List<R> =
    keys.rows().mapIndexedNotNull { index, row ->
        val group = groups[index]
        val g = GroupWithKey(row, group)
        body(g, g)
    }

/**
 * Returns a [DataFrame] with the rows that [body] computes from the key–group pairs of this [GroupBy].
 *
 * @include [MapDocs.CommonGroupBySnippet]
 *
 * A pair for which [body] returns `null` gives no row,
 * so the result can have fewer rows than the number of key–group pairs.
 * Apart from that, the result has one row per pair, in the same order.
 * Its columns are the columns of the returned rows — so if [body] returns `null` for every pair,
 * the result has no rows and no columns at all.
 *
 * See also {@include [MapDocsLink]} — an overview of the whole `map` family.
 *
 * ### Example
 *
 * ```kotlin
 * // The oldest person of every city, one row per city
 * df.groupBy { city }.mapToRows { group.sortByDesc { age }.firstOrNull() }
 * ```
 *
 * For more information: {@include [DocumentationUrls.MapOnGroupBy]}
 *
 * @param [T] The schema marker type of the grouping [keys][GroupBy.keys].
 * @param [G] The schema marker type of the groups.
 * @param [body] A [Selector] that computes a row of the result from a key–group pair, or `null` for no row.
 * @return A [DataFrame] with one computed row per key–group pair, without the `null` results.
 */
public fun <T, G> GroupBy<T, G>.mapToRows(body: Selector<GroupWithKey<T, G>, DataRow<G>?>): DataFrame<G> =
    map(body).concat()

/**
 * Returns a [FrameColumn] with the dataframes that [body] computes from the key–group pairs of this [GroupBy].
 *
 * @include [MapDocs.CommonGroupBySnippet]
 *
 * The new column has one [DataFrame] per key–group pair, in the same order,
 * and the same name as [groups][GroupBy.groups].
 *
 * Call `concat()` on the result to get all of those dataframes back as one [DataFrame].
 *
 * See also {@include [MapDocsLink]} — an overview of the whole `map` family.
 *
 * ### Example
 *
 * ```kotlin
 * // A frame column in which every group keeps only its two oldest people
 * df.groupBy { city }.mapToFrames { group.sortByDesc { age }.take(2) }
 * ```
 *
 * For more information: {@include [DocumentationUrls.MapOnGroupBy]}
 *
 * @param [T] The schema marker type of the grouping [keys][GroupBy.keys].
 * @param [G] The schema marker type of the groups.
 * @param [body] A [Selector] that computes a [DataFrame] from a key–group pair.
 * @return A [FrameColumn] with one computed [DataFrame] per key–group pair.
 */
public fun <T, G> GroupBy<T, G>.mapToFrames(body: Selector<GroupWithKey<T, G>, DataFrame<G>>): FrameColumn<G> =
    DataColumn.createFrameColumn(groups.name, map(body))

// endregion
