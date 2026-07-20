package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataFrameExpression
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Selector
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.api.GroupByDocs.Grammar
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.CSDslLink
import org.jetbrains.kotlinx.dataframe.impl.api.SortFlag
import org.jetbrains.kotlinx.dataframe.impl.api.addFlag
import org.jetbrains.kotlinx.dataframe.impl.api.sortByImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.newColumnWithActualType
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.name
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.type
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.DESC_TO_REVERSED
import kotlin.reflect.KProperty

/**
 * A specialized [ColumnsSelectionDsl] for selecting columns to sort rows by
 * and specifying how each column should be sorted.
 *
 * [SortDsl][org.jetbrains.kotlinx.dataframe.api.SortDsl] allows selecting columns to sort rows by (the order in which columns are selected
 * determines the sort priority).
 * It also allows reversing the sort order for individual columns or column sets,
 * and controlling the position of `null` values.
 *
 * The order in which columns are selected determines the sort priority.
 *
 * By default, all selected columns are sorted in the original order
 * (ascending in [sortBy][org.jetbrains.kotlinx.dataframe.DataFrame.sortBy] and descending in [sortByDesc][org.jetbrains.kotlinx.dataframe.DataFrame.sortByDesc]).
 * Use [reversed] to impose the reverse ordering for a column or column set.
 *
 * By default, `null` values are considered the smallest values when sorting.
 * Use [nullsLast] to treat them as the largest values.
 *
 * ### Examples
 * ```kotlin
 * // Sort rows by "age" column values ascending
 * df.sortBy { age }
 * // Sort rows by "age" column values descending
 * // and then by the ("name"/"firstName") column values ascending
 * df.sortBy { age.reversed() and name.firstName }
 * // Sort rows by "weight" column values ascending with nulls last
 * df.sortBy { weight.nullsLast() }
 * ```
 *
 * Sorting values can also be computed inline using [expr].
 * ```
 * // Sort rows by the product of "volume" and "quantity" descending
 * df.sortBy { expr { volume * quantity }.reversed() }
 * ```
 */
public interface SortDsl<out T> : ColumnsSelectionDsl<T> {

    /**
     * Marks the selected columns to be sorted in reversed order.
     */
    public fun <C> ColumnSet<C>.reversed(): ColumnSet<C> = addFlag(SortFlag.Reversed)

    /**
     * Marks the selected column to be sorted in reversed order.
     */
    public fun <C> SingleColumn<C>.reversed(): SingleColumn<C> = addFlag(SortFlag.Reversed).single()

    /**
     * Marks the selected column to be sorted in reversed order.
     */
    public fun String.reversed(): SingleColumn<Comparable<*>?> = invoke<Comparable<*>>().desc()

    @Deprecated(
        message = "Inside the sorting DSL, reverse() reverses the sorting order. Use reversed() instead.",
        replaceWith = ReplaceWith("reversed()"),
        level = DeprecationLevel.ERROR,
    )
    public fun <T> DataColumn<T>.reverse(): SingleColumn<T> = reversed()

    @Deprecated(DESC_TO_REVERSED, ReplaceWith("reversed()"), DeprecationLevel.WARNING)
    public fun <C> ColumnSet<C>.desc(): ColumnSet<C> = addFlag(SortFlag.Reversed)

    @Deprecated(DESC_TO_REVERSED, ReplaceWith("reversed()"), DeprecationLevel.WARNING)
    public fun <C> SingleColumn<C>.desc(): SingleColumn<C> = addFlag(SortFlag.Reversed).single()

    @Deprecated(DESC_TO_REVERSED, ReplaceWith("reversed()"), DeprecationLevel.WARNING)
    public fun String.desc(): SingleColumn<Comparable<*>?> = invoke<Comparable<*>>().desc()

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.desc(): SingleColumn<C> = toColumnAccessor().desc()

    /**
     * Places `null` values after non-null values when sorting by the selected columns.
     *
     * When [flag] is `false`, the selected columns remain unchanged.
     *
     * @param flag whether `null` values should be placed last.
     */
    public fun <C> ColumnSet<C?>.nullsLast(flag: Boolean = true): ColumnSet<C?> =
        if (flag) addFlag(SortFlag.NullsLast) else this

    /**
     * Places `null` values after non-null values when sorting by this column.
     *
     * When [flag] is `false`, the column remains unchanged.
     *
     * @param flag whether `null` values should be placed last.
     */
    public fun <C> SingleColumn<C?>.nullsLast(flag: Boolean = true): SingleColumn<C?> =
        if (flag) addFlag(SortFlag.NullsLast).single() else this

    /**
     * Places `null` values after non-null values when sorting by this column.
     *
     * When [flag] is `false`, the default `null` ordering is used.
     *
     * @param flag whether `null` values should be placed last.
     */
    public fun String.nullsLast(flag: Boolean = true): SingleColumn<Comparable<*>?> =
        invoke<Comparable<*>>().nullsLast(flag)

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C?>.nullsLast(flag: Boolean = true): SingleColumn<C?> = toColumnAccessor().nullsLast(flag)
}

/**
 * A specialized [ColumnsSelector] used for selecting columns for sorting.
 *
 * Provides [SortDsl] both as the receiver (`this`) and the lambda parameter (`it`), and expects
 * a [ColumnsResolver] as the return value.
 *
 * Enables defining the descending ordering of sort columns using [desc][SortDsl.desc]
 * and specifiyng `null`s place using [nullsLast][SortDsl.nullsLast].
 */
public typealias SortColumnsSelector<T, C> = Selector<SortDsl<T>, ColumnsResolver<C>>

// region DataColumn

/**
 * Sorts the values in this [column][DataColumn] in ascending order.
 *
 * Accepts only [Comparable] values.
 *
 * See also
 *   - [sortDesc][DataColumn.sortDesc] for sorting in descending order;
 *   - [sortWith][DataColumn.sortWith] for sorting by providing a custom comparator.
 *
 * @return A new [ValueColumn] containing the sorted values from the original column.
 */
public fun <T : Comparable<T>> DataColumn<T>.sort(): ValueColumn<T> =
    DataColumn.createValueColumn(name, values().sorted(), type, defaultValue = defaultValue())

/**
 * Sorts the values in this [column][DataColumn] in descengind order.
 *
 * Accepts only [Comparable] values.
 *
 * See also
 *   - [sort][DataColumn.sort] for sorting in ascending order;
 *   - [sortWith][DataColumn.sortWith] for sorting by providing a custom comparator.
 *
 * @return A new [ValueColumn] containing the sorted values from the original column.
 */
public fun <T : Comparable<T>> DataColumn<T>.sortDesc(): ValueColumn<T> =
    DataColumn.createValueColumn(name, values().sortedDescending(), type, defaultValue = defaultValue())

/** Returns the sorted version of the current [ValueColumn][org.jetbrains.kotlinx.dataframe.columns.ValueColumn], [FrameColumn][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] based
 * on the given [Comparator]. The [comparator] can either be given as an instance of [Comparator], or directly
 * as a lambda.
 *
 * #### For example
 *
 * `df`[`[`][org.jetbrains.kotlinx.dataframe.DataFrame.get]`"price"`[`]`][org.jetbrains.kotlinx.dataframe.DataFrame.get]`.`[sortWith][org.jetbrains.kotlinx.dataframe.api.sortWith]` { a, b -> a - b }`
 *
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`name.`[sortWith][org.jetbrains.kotlinx.dataframe.api.sortWith]`(myComparator) `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[allAfter][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`(name)`
 *
 * `}`
 *
 * @receiver The [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] to sort. This can be either a [ValueColumn][org.jetbrains.kotlinx.dataframe.columns.ValueColumn], [FrameColumn][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and will
 *   dictate the return type of the function.
 * @param [comparator] The [Comparator] to use for sorting the [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]. This can either be a [Comparator]<[T]> or
 *   a lambda of type `(`[T][T]`, `[T][T]`) -> `[Int][Int].
 * @return The sorted [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] [this] of the same type as the receiver. */
public fun <T, C : DataColumn<T>> C.sortWith(comparator: Comparator<T>): C =
    DataColumn.createByType(name, values().sortedWith(comparator), type) as C

/** Returns the sorted version of the current [ValueColumn][org.jetbrains.kotlinx.dataframe.columns.ValueColumn], [FrameColumn][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] based
 * on the given [Comparator]. The [comparator] can either be given as an instance of [Comparator], or directly
 * as a lambda.
 *
 * #### For example
 *
 * `df`[`[`][org.jetbrains.kotlinx.dataframe.DataFrame.get]`"price"`[`]`][org.jetbrains.kotlinx.dataframe.DataFrame.get]`.`[sortWith][org.jetbrains.kotlinx.dataframe.api.sortWith]` { a, b -> a - b }`
 *
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;`name.`[sortWith][org.jetbrains.kotlinx.dataframe.api.sortWith]`(myComparator) `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[allAfter][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`(name)`
 *
 * `}`
 *
 * @receiver The [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] to sort. This can be either a [ValueColumn][org.jetbrains.kotlinx.dataframe.columns.ValueColumn], [FrameColumn][org.jetbrains.kotlinx.dataframe.columns.FrameColumn], or [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and will
 *   dictate the return type of the function.
 * @param [comparator] The [Comparator] to use for sorting the [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]. This can either be a [Comparator]<[T]> or
 *   a lambda of type `(`[T][T]`, `[T][T]`) -> `[Int][Int].
 * @return The sorted [DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn] [this] of the same type as the receiver. */
public fun <T, C : DataColumn<T>> C.sortWith(comparator: (T, T) -> Int): C = sortWith(Comparator(comparator))

// endregion

// region DataFrame

/**
 * Sorts this [DataFrame] rows by the specified [columns] in ascending (default) or descending order.
 *
 * Returns a new [DataFrame] containing the same rows, sorted according to the selected columns.
 *
 * Select columns to sort by, adjust sorting order and `null`s position using [SortDsl].
 *
 * [SortDsl][org.jetbrains.kotlinx.dataframe.api.SortDsl] allows selecting columns to sort rows by (the order in which columns are selected
 * determines the sort priority).
 * It also allows reversing the sort order for individual columns or column sets,
 * and controlling the position of `null` values.
 *
 * The order in which columns are selected determines the sort priority.
 *
 * By default, all selected columns are sorted in the original order
 * (ascending in [sortBy][org.jetbrains.kotlinx.dataframe.DataFrame.sortBy] and descending in [sortByDesc][org.jetbrains.kotlinx.dataframe.DataFrame.sortByDesc]).
 * Use [reversed] to impose the reverse ordering for a column or column set.
 *
 * By default, `null` values are considered the smallest values when sorting.
 * Use [nullsLast] to treat them as the largest values.
 *
 * See [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
 *
 * See also
 *   - [sortByDesc][DataFrame.sortByDesc] that sorts rows in descending order by default.
 *   - [sortWith][DataFrame.sortWith] that sorts rows using a custom comparator.
 *
 * ### Examples
 * ```kotlin
 * // Sort rows by "age" column values ascending
 * df.sortBy { age }
 * // Sort rows by "age" column values descending
 * // and then by the ("name"/"firstName") column values ascending
 * df.sortBy { age.reversed() and name.firstName }
 * // Sort rows by "weight" column values ascending with nulls last
 * df.sortBy { weight.nullsLast() }
 * ```
 *
 * Sorting values can also be computed inline using [expr].
 * ```
 * // Sort rows by the product of "volume" and "quantity" descending
 * df.sortBy { expr { volume * quantity }.reversed() }
 * ```
 *
 * @param columns The [Sort Columns Selector][SortColumnsSelector] that defines which columns are used
 *                for sorting, in which order and direction.
 * @return A new [DataFrame] with the original rows sorted based on the specified columns and directions.
 */
public fun <T, C> DataFrame<T>.sortBy(columns: SortColumnsSelector<T, C>): DataFrame<T> =
    sortByImpl(UnresolvedColumnsPolicy.Fail, columns)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.sortBy(vararg cols: ColumnReference<*>): DataFrame<T> = sortBy { cols.toColumnSet() }

/**
 * Sorts this [DataFrame] rows by the specified [columns] in ascending order.
 *
 * Returns a new [DataFrame] containing the same rows, sorted according to the selected columns.
 *
 *
 *
 * Select single or multiple columns using their names as [String]s.
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * The order in which columns are selected determines the sort priority.
 *
 * See also
 *   - [`sortBy { }`][DataFrame.sortBy] overload that uses [SortDsl]
 * for selecting columns, allowing specifying sort directions and `null`s position.
 *   - [sortByDesc][DataFrame.sortByDesc] that sorts rows in descending order.
 *   - [sortWith][DataFrame.sortWith] that sorts rows using a custom comparator.
 *
 * ### Examples
 * ```kotlin
 * df.sortBy("age")
 * // Sort rows by "age" column values ascending
 * // and then by the ("name") column values ascending
 * df.sortBy("age", "name")
 * ```
 *
 * @param cols The [Column Names][String] that defines which columns are used for sorting.
 * @return A new [DataFrame] with the original rows sorted based on the specified columns.
 */
public fun <T> DataFrame<T>.sortBy(vararg cols: String): DataFrame<T> = sortBy { cols.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.sortBy(vararg cols: KProperty<Comparable<*>?>): DataFrame<T> = sortBy { cols.toColumnSet() }

/**
 * Sorts the rows of this [DataFrame] using the specified row [comparator].
 *
 * Returns a new [DataFrame] containing the same rows sorted according to the provided [Comparator].
 *
 * See also [sortBy][DataFrame.sortBy] and [sortByDesc][DataFrame.sortByDesc], which sort
 * rows by selected columns.
 *
 * ### Example
 * ```kotlin
 * // Provide a comparator for `DataRow<Person>` that sorts
 * // by `age` in ascending order first
 * // and then by `salary` in descending order
 * df.sortWith(
 *     compareBy<DataRow<Person>> { it.age }
 *         .thenByDescending { it.salary }
 * )
 * ```
 *
 * @param comparator The [Comparator] used to determine the order of rows.
 * @return A new [DataFrame] containing the same rows sorted according to the provided comparator.
 */
public fun <T> DataFrame<T>.sortWith(comparator: Comparator<DataRow<T>>): DataFrame<T> {
    val permutation = rows().sortedWith(comparator).map { it.index }
    return this[permutation]
}

/**
 * Sorts the rows of this [DataFrame] using the specified row [comparator].
 *
 * The [comparator] is a comparison lambda that takes two [DataRow]s and returns
 * a negative, zero, or positive value depending on their relative order.
 *
 * Returns a new [DataFrame] containing the same rows sorted according to the provided comparator.
 *
 * See also [sortBy][DataFrame.sortBy] and [sortByDesc][DataFrame.sortByDesc], which sort
 * rows by selected columns.
 *
 * ### Example
 * ```kotlin
 * // Sort by `age` in ascending order,
 * // then by `salary` in ascending order
 * df.sortWith { row1, row2 ->
 *     when {
 *         row1.age != row2.age -> row1.age.compareTo(row2.age)
 *         else -> row1.salary.compareTo(row2.salary)
 *     }
 * }
 * ```
 *
 * @param comparator A function that compares two rows and returns a negative, zero,
 * or positive value depending on their relative order.
 * @return A new [DataFrame] containing the same rows sorted according to the provided comparator.
 */
public fun <T> DataFrame<T>.sortWith(comparator: (DataRow<T>, DataRow<T>) -> Int): DataFrame<T> =
    sortWith(Comparator(comparator))

/**
 * Sorts this [DataFrame] rows by the specified [columns] in ascending or descending (default) order.
 *
 * Returns a new [DataFrame] containing the same rows, sorted according to the selected columns.
 *
 * Select columns to sort by, adjust sorting order and `null`s position using [SortDsl].
 *
 * [SortDsl][org.jetbrains.kotlinx.dataframe.api.SortDsl] allows selecting columns to sort rows by (the order in which columns are selected
 * determines the sort priority).
 * It also allows reversing the sort order for individual columns or column sets,
 * and controlling the position of `null` values.
 *
 * The order in which columns are selected determines the sort priority.
 *
 * By default, all selected columns are sorted in the original order
 * (ascending in [sortBy][org.jetbrains.kotlinx.dataframe.DataFrame.sortBy] and descending in [sortByDesc][org.jetbrains.kotlinx.dataframe.DataFrame.sortByDesc]).
 * Use [reversed] to impose the reverse ordering for a column or column set.
 *
 * By default, `null` values are considered the smallest values when sorting.
 * Use [nullsLast] to treat them as the largest values.
 *
 * See [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
 *
 * See also
 *   - [sortBy][DataFrame.sortBy] that sorts rows in ascending order by default.
 *   - [sortWith][DataFrame.sortWith] that sorts rows using a custom comparator.
 *
 * ### Examples
 * ```kotlin
 * // Sort rows by "age" column values descending
 * df.sortByDesc { age }
 * // Sort rows by "age" column values descending
 * // and then by the ("name"/"firstName") column values ascending
 * df.sortByDesc { age and name.firstName.reversed() }
 * // Sort rows by "weight" column values descending with nulls first
 * df.sortByDesc { weight.nullsLast() }
 * ```
 *
 * Sorting values can also be computed inline using [expr].
 * ```
 * // Sort rows by the product of "volume" and "quantity" descending
 * df.sortByDesc { expr { volume * quantity } }
 * ```
 *
 * @param columns The [Sort Columns Selector][SortColumnsSelector] that defines which columns are used
 *                for sorting, in which order and direction.
 * @return A new [DataFrame] with the original rows sorted based on the specified columns and directions.
 */
public fun <T, C> DataFrame<T>.sortByDesc(columns: SortColumnsSelector<T, C>): DataFrame<T> {
    val set = columns.toColumnSet()
    return sortByImpl { set.desc() }
}

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.sortByDesc(vararg columns: KProperty<Comparable<C>?>): DataFrame<T> =
    sortByDesc { columns.toColumnSet() }

/**
 * Sorts this [DataFrame] rows by the specified [columns] in descending order.
 *
 * Returns a new [DataFrame] containing the same rows, sorted according to the selected columns.
 *
 *
 *
 * Select single or multiple columns using their names as [String]s.
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * The order in which columns are selected determines the sort priority.
 *
 * See also
 *   - [`sortByDesc { }`][DataFrame.sortByDesc] overload that uses [SortDsl]
 * for selecting columns, allowing specifying sort directions and `null`s position.
 *   - [sortBy][DataFrame.sortBy] that sorts rows in ascending order.
 *   - [sortWith][DataFrame.sortWith] that sorts rows using a custom comparator.
 *
 * ### Examples
 * ```kotlin
 * df.sortByDesc("age")
 * // Sort rows by "age" column values descending
 * // and then by the ("name") column values descending
 * df.sortByDesc("age", "name")
 * ```
 *
 * @param columns The [Column Names][String] that defines which columns are used for sorting.
 * @return A new [DataFrame] with the original rows sorted based on the specified columns.
 */
public fun <T> DataFrame<T>.sortByDesc(vararg columns: String): DataFrame<T> = sortByDesc { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.sortByDesc(vararg columns: ColumnReference<*>): DataFrame<T> =
    sortByDesc { columns.toColumnSet() }

// endregion

// region GroupBy

/**
 * Sorts this [GroupBy] group rows by the specified [columns][cols] in asending order.
 *
 * Returns a new [GroupBy] containing the same keys and groups,
 * with the groups sorted according to the selected columns.
 *
 *
 *
 * Select single or multiple columns using their names as [String]s.
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * The order in which columns are selected determines the sort priority.
 *
 * Don't confuse this with [sortByGroup] and [sortByKey] that sort key-group pairs order.
 *
 * See also
 *   - [`sortBy { }`][GroupBy.sortBy] overload that uses [SortDsl]
 * for selecting columns, allowing specifying sort directions and `null`s position.
 *   - [sortByDesc][GroupBy.sortByDesc] that sorts rows in descending order.
 *
 * Check out [`GroupBy grammar`][Grammar].
 *
 * For more information: [See "`GroupBy` Transformation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#transformation)
 *
 * ### Examples
 * ```kotlin
 * gb.sortBy("age")
 * // Sort group rows by "age" column values descending
 * // and then by the ("name") column values descending
 * gb.sortByDesc("age", "name")
 * ```
 *
 * @param cols The [Column Names][String] that defines which columns are used for sorting.
 * @return A new [GroupBy] with same keys and groups with the original rows sorted based on the specified columns.
 */
public fun <T, G> GroupBy<T, G>.sortBy(vararg cols: String): GroupBy<T, G> = sortBy { cols.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> GroupBy<T, G>.sortBy(vararg cols: ColumnReference<*>): GroupBy<T, G> = sortBy { cols.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> GroupBy<T, G>.sortBy(vararg cols: KProperty<Comparable<*>?>): GroupBy<T, G> =
    sortBy { cols.toColumnSet() }

/**
 * Sorts this [GroupBy] group rows by the specified [columns][selector] in ascending (default) or descending order.
 *
 * Returns a new [GroupBy] containing the same keys and groups,
 * with the groups sorted according to the selected columns.
 *
 * Select columns to sort by, adjust sorting order and `null`s position using [SortDsl].
 *
 * [SortDsl][org.jetbrains.kotlinx.dataframe.api.SortDsl] allows selecting columns to sort rows by (the order in which columns are selected
 * determines the sort priority).
 * It also allows reversing the sort order for individual columns or column sets,
 * and controlling the position of `null` values.
 *
 * The order in which columns are selected determines the sort priority.
 *
 * By default, all selected columns are sorted in the original order
 * (ascending in [sortBy][org.jetbrains.kotlinx.dataframe.DataFrame.sortBy] and descending in [sortByDesc][org.jetbrains.kotlinx.dataframe.DataFrame.sortByDesc]).
 * Use [reversed] to impose the reverse ordering for a column or column set.
 *
 * By default, `null` values are considered the smallest values when sorting.
 * Use [nullsLast] to treat them as the largest values.
 *
 * See [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
 *
 * See also [sortByDesc][GroupBy.sortByDesc] that sorts rows in descending order by default.
 *
 * Check out [`GroupBy grammar`][Grammar].
 *
 * For more information: [See "`GroupBy` Transformation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#transformation)
 *
 * ### Examples
 * ```kotlin
 * // Sort group rows by "age" column values ascending
 * gb.sortBy { age }
 * // Sort group rows by "age" column values descending
 * // and then by the ("name"/"firstName") column values ascending
 * gb.sortBy { age.reversed() and name.firstName }
 * // Sort group rows by "weight" column values ascending with nulls last
 * gb.sortBy { weight.nullsLast() }
 * ```
 *
 * Sorting values can also be computed inline using [expr].
 * ```
 * // Sort rows by the product of "volume" and "quantity" descending
 * gb.sortBy { expr { volume * quantity }.reversed() }
 * ```
 *
 * @param selector The [Sort Columns Selector][SortColumnsSelector] that defines which columns are used
 *                for sorting, in which order and direction.
 * @return A new [GroupBy] with same keys and groups with the original rows sorted based on the specified columns.
 */
public fun <T, G, C> GroupBy<T, G>.sortBy(selector: SortColumnsSelector<G, C>): GroupBy<T, G> = sortByImpl(selector)

/**
 * Sorts this [DataFrame] rows by the specified [columns][cols] in descending order.
 *
 * Returns a new [GroupBy] containing the same keys and groups,
 * with the groups sorted according to the selected columns.
 *
 *
 *
 * Select single or multiple columns using their names as [String]s.
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * The order in which columns are selected determines the sort priority.
 *
 * See also
 *   - [`sortByDesc { }`][GroupBy.sortByDesc] overload that uses [SortDsl]
 * for selecting columns, allowing specifying sort directions and `null`s position.
 *   - [sortBy][GroupBy.sortBy] that sorts rows in ascending order.
 *
 * Check out [`GroupBy grammar`][Grammar].
 *
 * For more information: [See "`GroupBy` Transformation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#transformation)
 *
 * ### Examples
 * ```kotlin
 * gb.sortByDesc("age")
 * // Sort group rows by "age" column values descending
 * // and then by the ("name") column values descending
 * gb.sortByDesc("age", "name")
 * ```
 *
 * @param cols The [Column Names][String] that defines which columns are used for sorting.
 * @return A new [GroupBy] with same keys and groups with the original rows sorted based on the specified columns.
 */
public fun <T, G> GroupBy<T, G>.sortByDesc(vararg cols: String): GroupBy<T, G> = sortByDesc { cols.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> GroupBy<T, G>.sortByDesc(vararg cols: ColumnReference<*>): GroupBy<T, G> =
    sortByDesc { cols.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, G> GroupBy<T, G>.sortByDesc(vararg cols: KProperty<Comparable<*>?>): GroupBy<T, G> =
    sortByDesc { cols.toColumnSet() }

/**
 * Sorts this [GroupBy] group rows by the specified [columns][selector] in ascending or descending (default) order.
 *
 * Returns a new [GroupBy] containing the same keys and groups,
 * with the groups sorted according to the selected columns.
 *
 * Select columns to sort by, adjust sorting order and `null`s position using [SortDsl].
 *
 * [SortDsl][org.jetbrains.kotlinx.dataframe.api.SortDsl] allows selecting columns to sort rows by (the order in which columns are selected
 * determines the sort priority).
 * It also allows reversing the sort order for individual columns or column sets,
 * and controlling the position of `null` values.
 *
 * The order in which columns are selected determines the sort priority.
 *
 * By default, all selected columns are sorted in the original order
 * (ascending in [sortBy][org.jetbrains.kotlinx.dataframe.DataFrame.sortBy] and descending in [sortByDesc][org.jetbrains.kotlinx.dataframe.DataFrame.sortByDesc]).
 * Use [reversed] to impose the reverse ordering for a column or column set.
 *
 * By default, `null` values are considered the smallest values when sorting.
 * Use [nullsLast] to treat them as the largest values.
 *
 * See [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
 *
 * See also [sortBy][GroupBy.sortBy] that sorts rows in asending order by default.
 *
 * Check out [`GroupBy grammar`][Grammar].
 *
 * For more information: [See "`GroupBy` Transformation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#transformation)
 *
 * ### Examples
 * ```kotlin
 * // Sort group rows by "age" column values descending
 * gb.sortByDesc { age }
 * // Sort group rows by "age" column values ase
 * // and then by the ("name"/"firstName") column values ascending
 * gb.sortByDesc { age and name.firstName.reversed() }
 * // Sort group rows by "weight" column values descending with nulls first
 * gb.sortByDesc { weight.nullsLast() }
 * ```
 *
 * Sorting values can also be computed inline using [expr].
 * ```
 * // Sort rows by the product of "volume" and "quantity" descending
 * gb.sortByDesc { expr { volume * quantity } }
 * ```
 *
 * @param selector The [Sort Columns Selector][SortColumnsSelector] that defines which columns are used
 *                for sorting, in which order and direction.
 * @return A new [GroupBy] with same keys and groups with the original rows sorted based on the specified columns.
 */
public fun <T, G, C> GroupBy<T, G>.sortByDesc(selector: SortColumnsSelector<G, C>): GroupBy<T, G> {
    val set = selector.toColumnSet()
    return sortByImpl { set.desc() }
}

private fun <T, G, C> GroupBy<T, G>.createColumnFromGroupExpression(
    receiver: ColumnsSelectionDsl<T>,
    expression: DataFrameExpression<G, C>,
): DataColumn<C?> =
    receiver.newColumnWithActualType("") { row ->
        val group = row[groups]
        expression(group, group)
    }

/**
 * Sorts the key-group pairs of this [GroupBy] by a value computed for each group
 * using the specified [expression] ascending.
 *
 * [DataFrameExpression] is a lambda that receives each group as a [DataFrame], both as the receiver (`this`)
 * and as the lambda argument (`it`), and returns the value used to order the groups.
 *
 * Returns a new [GroupBy] containing the same keys and groups in the resulting order.
 * The contents of the groups remain unchanged.
 *
 * Don't confuse this with [sortBy][GroupBy.sortBy] that sorts the group rows.
 *
 * See also
 *   - [sortByGroupDesc] that sorts the key-group pairs of this [GroupBy] based on computed values desending.
 *   - [sortByCount] and [sortByCountAsc] that sort the key-group pairs of this [GroupBy] by the group sizes.
 *   - [sortByKey] and [sortByKeyDesc] that sort the key-group pairs of this [GroupBy] by the key values.
 *
 * Check out [`GroupBy grammar`][Grammar].
 *
 * For more information: [See "`GroupBy` Transformation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#transformation)
 *
 * ### Example
 * ```kotlin
 * // Sorts groups by the product of their size and the maximum value of the "quantity" column ascending
 * gb.sortByGroup { group -> group.rowsCount() * group.quantity.max() }
 * ```
 *
 * @param [nullsLast] Whether `null` values should be placed after non-null values.
 * @param [expression] The expression used to compute the sorting value for each group.
 * @return A new [GroupBy] with its key-group pairs sorted by the values produced by [expression].
 */
public fun <T, G, C> GroupBy<T, G>.sortByGroup(
    nullsLast: Boolean = false,
    expression: DataFrameExpression<G, C>,
): GroupBy<T, G> =
    toDataFrame().sortBy {
        createColumnFromGroupExpression(this, expression).nullsLast(nullsLast)
    }.asGroupBy(groups)

/**
 * Sorts the key-group pairs of this [GroupBy] by a value computed for each group
 * using the specified [expression] descending.
 *
 * [DataFrameExpression] is a lambda that receives each group as a [DataFrame], both as the receiver (`this`)
 * and as the lambda argument (`it`), and returns the value used to order the groups.
 *
 * Returns a new [GroupBy] containing the same keys and groups in the resulting order.
 * The contents of the groups remain unchanged.
 *
 * Don't confuse this with [sortByDesc][GroupBy.sortByDesc] that sorts the group rows.
 *
 * See also
 *   - [sortByGroup] that sorts the key-group pairs of this [GroupBy] based on computed values asending.
 *   - [sortByCount] and [sortByCountAsc] that sort the key-group pairs of this [GroupBy] by the group sizes.
 *   - [sortByKey] and [sortByKeyDesc] that sort the key-group pairs of this [GroupBy] by the key values.
 *
 * Check out [`GroupBy grammar`][Grammar].
 *
 * For more information: [See "`GroupBy` Transformation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#transformation)
 *
 * ### Example
 * ```kotlin
 * // Sorts groups by the product of their size and the maximum value of the "quantity" column descending
 * gb.sortByGroupDesc { group -> group.rowsCount() * group.quantity.max() }
 * ```
 *
 * @param [nullsLast] Whether `null` values should be placed after non-null values.
 * @param [expression] The expression used to compute the sorting value for each group.
 * @return A new [GroupBy] with its key-group pairs sorted by the values produced by [expression].
 */
public fun <T, G, C> GroupBy<T, G>.sortByGroupDesc(
    nullsLast: Boolean = false,
    expression: DataFrameExpression<G, C>,
): GroupBy<T, G> =
    toDataFrame().sortBy {
        createColumnFromGroupExpression(this, expression).desc().nullsLast(nullsLast)
    }.asGroupBy(groups)

/**
 * Sorts the key-group pairs of this [GroupBy] by the numer of rows in groups asending.
 *
 * Returns a new [GroupBy] containing the same keys and groups in the resulting order.
 * The contents of the groups remain unchanged.
 *
 * Don't confuse this with [sortBy][GroupBy.sortBy] that sorts the group rows.
 *
 * See also
 *   - [sortByCount] that sort the key-group pairs of this [GroupBy] by the group sizes descending.
 *   - [sortByGroup] and [sortByGroupDesc] that sorts the key-group pairs of this [GroupBy] d on computed values.
 *   - [sortByKey] and [sortByKeyDesc] that sort the key-group pairs of this [GroupBy] by the key values.
 *
 * Check out [`GroupBy grammar`][Grammar].
 *
 * For more information: [See "`GroupBy` Transformation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#transformation)
 *
 * @return A new [GroupBy] with its key-group pairs sorted by the group sizes.
 */
public fun <T, G> GroupBy<T, G>.sortByCountAsc(): GroupBy<T, G> = sortByGroup { nrow }

/**
 * Sorts the key-group pairs of this [GroupBy] by the numer of rows in groups descending.
 *
 * Returns a new [GroupBy] containing the same keys and groups in the resulting order.
 * The contents of the groups remain unchanged.
 *
 * Don't confuse this with [sortBy][GroupBy.sortBy] that sorts the group rows.
 *
 * See also
 *   - [sortByCountAsc] that sort the key-group pairs of this [GroupBy] by the group sizes asdending.
 *   - [sortByGroup] and [sortByGroupDesc] that sorts the key-group pairs of this [GroupBy] based on computed values.
 *   - [sortByKey] and [sortByKeyDesc] that sort the key-group pairs of this [GroupBy] by the key values.
 *
 * Check out [`GroupBy grammar`][Grammar].
 *
 * For more information: [See "`GroupBy` Transformation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#transformation)
 *
 * @return A new [GroupBy] with its key-group pairs sorted by the group sizes.
 */
public fun <T, G> GroupBy<T, G>.sortByCount(): GroupBy<T, G> = sortByGroupDesc { nrow }

/**
 * Sorts the key-group pairs of this [GroupBy] by the keys descending.
 *
 * Returns a new [GroupBy] containing the same keys and groups in the resulting order.
 * The contents of the groups remain unchanged.
 *
 * Don't confuse this with [sortBy][GroupBy.sortBy] that sorts the group rows.
 *
 * See also
 *   - [sortByKey] that sorts the key-group pairs of this [GroupBy] by the key values asending.
 *   - [sortByCount] and [sortByCountAsc] that sort the key-group pairs of this [GroupBy] by the group sizes.
 *   - [sortByGroup] and [sortByGroupDesc] that sorts the key-group pairs of this [GroupBy] based on computed values.
 *
 * Check out [`GroupBy grammar`][Grammar].
 *
 * For more information: [See "`GroupBy` Transformation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#transformation)
 *
 * @param [nullsLast] Whether `null` values should be placed after non-null values.
 * @return A new [GroupBy] with its key-group pairs sorted by the keys.
 */
public fun <T, G> GroupBy<T, G>.sortByKeyDesc(nullsLast: Boolean = false): GroupBy<T, G> =
    toDataFrame()
        .sortBy { keys.columns().toColumnSet().desc().nullsLast(nullsLast) }
        .asGroupBy(groups)

/**
 * Sorts the key-group pairs of this [GroupBy] by the keys asending.
 *
 * Returns a new [GroupBy] containing the same keys and groups in the resulting order.
 * The contents of the groups remain unchanged.
 *
 * Don't confuse this with [sortBy][GroupBy.sortBy] that sorts the group rows.
 *
 * See also
 *   - [sortByKeyDesc] that sorts the key-group pairs of this [GroupBy] by the key values descending.
 *   - [sortByCount] and [sortByCountAsc] that sort the key-group pairs of this [GroupBy] by the group sizes.
 *   - [sortByGroup] and [sortByGroupDesc] that sorts the key-group pairs of this [GroupBy] based on computed values.
 *
 * Check out [`GroupBy grammar`][Grammar].
 *
 * For more information: [See "`GroupBy` Transformation" on the documentation website.](https://kotlin.github.io/dataframe/groupby.html#transformation)
 *
 * @param [nullsLast] Whether `null` values should be placed after non-null values.
 * @return A new [GroupBy] with its key-group pairs sorted by the keys.
 */
public fun <T, G> GroupBy<T, G>.sortByKey(nullsLast: Boolean = false): GroupBy<T, G> =
    toDataFrame()
        .sortBy { keys.columns().toColumnSet().nullsLast(nullsLast) }
        .asGroupBy(groups)

// endregion
