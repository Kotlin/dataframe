package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.DataFrameReceiver
import org.jetbrains.kotlinx.dataframe.impl.api.extractJoinColumns
import org.jetbrains.kotlinx.dataframe.impl.api.joinImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnListImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

/**
 * Joins this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the [other][other] [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] using the selected key columns.
 *
 * Creates a new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] by combining [rows][org.jetbrains.kotlinx.dataframe.DataRow]
 * from two input dataframes according to one or more matching key columns.
 *
 * There are two categories of joins:
 * * **Merging joins** — merge matching rows from both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s into a single row.
 * * **Non-merging joins** — select rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] based on whether
 * a match exists in the right one, without merging columns.
 *
 * The exact behavior depends on the specified [join type][type]:
 *
 * **Merging joins:**
 * * [JoinType.Inner][org.jetbrains.kotlinx.dataframe.api.JoinType.Inner] (default) — Includes only matching rows from both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s;
 * rows are merged.
 * * [JoinType.Left][org.jetbrains.kotlinx.dataframe.api.JoinType.Left] — Includes all rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched right-side values are filled with `null`.
 * * [JoinType.Right][org.jetbrains.kotlinx.dataframe.api.JoinType.Right] — Includes all rows from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched left-side values are filled with `null`.
 * * [JoinType.Full][org.jetbrains.kotlinx.dataframe.api.JoinType.Full] — Includes all rows from both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s; matching rows are merged,
 * all mismatches are filled with `null`.
 *
 * **Non-merging joins:**
 * * [JoinType.Filter][org.jetbrains.kotlinx.dataframe.api.JoinType.Filter] — Includes only rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] that have a match in the right one;
 * right-side columns are not merged.
 * * [JoinType.Exclude][org.jetbrains.kotlinx.dataframe.api.JoinType.Exclude] — Includes only rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] that do *not* have a match in the right one;
 * right-side columns are not merged.
 *
 * If no join columns are specified, all columns with matching names in both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [automatically renamed][org.jetbrains.kotlinx.dataframe.documentation.AutoRenaming]
 * in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Each join type has a corresponding shortcut function:
 * [innerJoin][org.jetbrains.kotlinx.dataframe.api.innerJoin], [leftJoin][org.jetbrains.kotlinx.dataframe.api.leftJoin], [rightJoin][org.jetbrains.kotlinx.dataframe.api.rightJoin], [fullJoin][org.jetbrains.kotlinx.dataframe.api.fullJoin], [filterJoin][org.jetbrains.kotlinx.dataframe.api.filterJoin], and [excludeJoin][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 * See also [joinWith][org.jetbrains.kotlinx.dataframe.api.joinWith], which performs a join by matching row values condition.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html).
 *
 * ### This `join` overload
 * Select join columns (including those that have different names in different [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s)
 * using [JoinDsl][org.jetbrains.kotlinx.dataframe.api.JoinDsl].
 *
 * [JoinDsl][org.jetbrains.kotlinx.dataframe.api.JoinDsl] defines the columns used for joining [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right sides.
 *
 * Provides the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [right][org.jetbrains.kotlinx.dataframe.api.JoinDsl.right] to access columns from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [match][org.jetbrains.kotlinx.dataframe.api.JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [Columns selection via DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl].
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.join(dfRight) { left -> left.firstName match right.name }
 * ```
 * @param other [DataFrame] to join with.
 * @param type [JoinType] defining how the resulting rows are constructed.
 * @param selector [JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("Join0")
public fun <A, B> DataFrame<A>.join(
    other: DataFrame<B>,
    type: JoinType = JoinType.Inner,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = joinImpl(other, type, addNewColumns = type.addNewColumns, selector)

/**
 * Joins this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the [other][other] [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] using the selected key columns.
 *
 * Creates a new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] by combining [rows][org.jetbrains.kotlinx.dataframe.DataRow]
 * from two input dataframes according to one or more matching key columns.
 *
 * There are two categories of joins:
 * * **Merging joins** — merge matching rows from both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s into a single row.
 * * **Non-merging joins** — select rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] based on whether
 * a match exists in the right one, without merging columns.
 *
 * The exact behavior depends on the specified [join type][type]:
 *
 * **Merging joins:**
 * * [JoinType.Inner][org.jetbrains.kotlinx.dataframe.api.JoinType.Inner] (default) — Includes only matching rows from both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s;
 * rows are merged.
 * * [JoinType.Left][org.jetbrains.kotlinx.dataframe.api.JoinType.Left] — Includes all rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched right-side values are filled with `null`.
 * * [JoinType.Right][org.jetbrains.kotlinx.dataframe.api.JoinType.Right] — Includes all rows from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched left-side values are filled with `null`.
 * * [JoinType.Full][org.jetbrains.kotlinx.dataframe.api.JoinType.Full] — Includes all rows from both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s; matching rows are merged,
 * all mismatches are filled with `null`.
 *
 * **Non-merging joins:**
 * * [JoinType.Filter][org.jetbrains.kotlinx.dataframe.api.JoinType.Filter] — Includes only rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] that have a match in the right one;
 * right-side columns are not merged.
 * * [JoinType.Exclude][org.jetbrains.kotlinx.dataframe.api.JoinType.Exclude] — Includes only rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] that do *not* have a match in the right one;
 * right-side columns are not merged.
 *
 * If no join columns are specified, all columns with matching names in both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [automatically renamed][org.jetbrains.kotlinx.dataframe.documentation.AutoRenaming]
 * in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Each join type has a corresponding shortcut function:
 * [innerJoin][org.jetbrains.kotlinx.dataframe.api.innerJoin], [leftJoin][org.jetbrains.kotlinx.dataframe.api.leftJoin], [rightJoin][org.jetbrains.kotlinx.dataframe.api.rightJoin], [fullJoin][org.jetbrains.kotlinx.dataframe.api.fullJoin], [filterJoin][org.jetbrains.kotlinx.dataframe.api.filterJoin], and [excludeJoin][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 * See also [joinWith][org.jetbrains.kotlinx.dataframe.api.joinWith], which performs a join by matching row values condition.
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html).
 *
 * ### This `join` overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 * ### Example
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight, "name", "city")
 * ```
 * @param other [DataFrame] to join with.
 * @param columns [Column Names][String] specifying join columns.
 * @param type [JoinType] defining how the resulting rows are constructed.
 * @return joined [DataFrame].
 */
public fun <A, B> DataFrame<A>.join(
    other: DataFrame<B>,
    vararg columns: String,
    type: JoinType = JoinType.Inner,
): DataFrame<A> = join(other, type) { columns.toColumnSet() }

/**
 * Performs an [inner join][org.jetbrains.kotlinx.dataframe.api.JoinType.Inner] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the [other][other] [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes only matching rows from both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s;
 * rows are merged.
 *
 * This is a shortcut for [join][org.jetbrains.kotlinx.dataframe.api.join] with [JoinType.Inner][org.jetbrains.kotlinx.dataframe.api.JoinType.Inner].
 *
 * If no join columns are specified, all columns with matching names in both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [automatically renamed][org.jetbrains.kotlinx.dataframe.documentation.AutoRenaming]
 * in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [join][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [leftJoin][org.jetbrains.kotlinx.dataframe.api.leftJoin], [rightJoin][org.jetbrains.kotlinx.dataframe.api.rightJoin], [fullJoin][org.jetbrains.kotlinx.dataframe.api.fullJoin], [filterJoin][org.jetbrains.kotlinx.dataframe.api.filterJoin], [excludeJoin][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html).
 *
 * ### This `innerJoin` overload
 * Select join columns (including those that have different names in different [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s)
 * using [JoinDsl][org.jetbrains.kotlinx.dataframe.api.JoinDsl].
 *
 * [JoinDsl][org.jetbrains.kotlinx.dataframe.api.JoinDsl] defines the columns used for joining [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right sides.
 *
 * Provides the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [right][org.jetbrains.kotlinx.dataframe.api.JoinDsl.right] to access columns from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [match][org.jetbrains.kotlinx.dataframe.api.JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [Columns selection via DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl].
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.join(dfRight) { left -> left.firstName match right.name }
 * ```
 * @param other [DataFrame] to join with.
 * @param selector [JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("InnerJoin")
public fun <A, B> DataFrame<A>.innerJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = join(other, JoinType.Inner, selector = selector)

/**
 * Performs an [inner join][org.jetbrains.kotlinx.dataframe.api.JoinType.Inner] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the [other][other] [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes only matching rows from both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s;
 * rows are merged.
 *
 * This is a shortcut for [join][org.jetbrains.kotlinx.dataframe.api.join] with [JoinType.Inner][org.jetbrains.kotlinx.dataframe.api.JoinType.Inner].
 *
 * If no join columns are specified, all columns with matching names in both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [automatically renamed][org.jetbrains.kotlinx.dataframe.documentation.AutoRenaming]
 * in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [join][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [leftJoin][org.jetbrains.kotlinx.dataframe.api.leftJoin], [rightJoin][org.jetbrains.kotlinx.dataframe.api.rightJoin], [fullJoin][org.jetbrains.kotlinx.dataframe.api.fullJoin], [filterJoin][org.jetbrains.kotlinx.dataframe.api.filterJoin], [excludeJoin][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html).
 *
 * ### This `innerJoin` overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 * ### Example
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight, "name", "city")
 * ```
 * @param other [DataFrame] to join with.
 * @param columns [Column Names][String] specifying join columns.
 * @return joined [DataFrame].
 */
public fun <A, B> DataFrame<A>.innerJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    innerJoin(other) { columns.toColumnSet() }

/**
 * Performs a [left join][org.jetbrains.kotlinx.dataframe.api.JoinType.Left] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the [other][other] [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes all rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched right-side values are filled with `null`.
 *
 * This is a shortcut for [join][org.jetbrains.kotlinx.dataframe.api.join] with [JoinType.Left][org.jetbrains.kotlinx.dataframe.api.JoinType.Left].
 *
 * If no join columns are specified, all columns with matching names in both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [automatically renamed][org.jetbrains.kotlinx.dataframe.documentation.AutoRenaming]
 * in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [join][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [innerJoin][org.jetbrains.kotlinx.dataframe.api.innerJoin], [rightJoin][org.jetbrains.kotlinx.dataframe.api.rightJoin], [fullJoin][org.jetbrains.kotlinx.dataframe.api.fullJoin], [filterJoin][org.jetbrains.kotlinx.dataframe.api.filterJoin], [excludeJoin][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html).
 *
 * ### This `leftJoin` overload
 * Select join columns (including those that have different names in different [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s)
 * using [JoinDsl][org.jetbrains.kotlinx.dataframe.api.JoinDsl].
 *
 * [JoinDsl][org.jetbrains.kotlinx.dataframe.api.JoinDsl] defines the columns used for joining [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right sides.
 *
 * Provides the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [right][org.jetbrains.kotlinx.dataframe.api.JoinDsl.right] to access columns from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [match][org.jetbrains.kotlinx.dataframe.api.JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [Columns selection via DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl].
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.join(dfRight) { left -> left.firstName match right.name }
 * ```
 * @param other [DataFrame] to join with.
 * @param selector [JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("LeftJoin")
public fun <A, B> DataFrame<A>.leftJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = join(other, JoinType.Left, selector = selector)

/**
 * Performs a [left join][org.jetbrains.kotlinx.dataframe.api.JoinType.Left] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the [other][other] [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes all rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched right-side values are filled with `null`.
 *
 * This is a shortcut for [join][org.jetbrains.kotlinx.dataframe.api.join] with [JoinType.Left][org.jetbrains.kotlinx.dataframe.api.JoinType.Left].
 *
 * If no join columns are specified, all columns with matching names in both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [automatically renamed][org.jetbrains.kotlinx.dataframe.documentation.AutoRenaming]
 * in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [join][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [innerJoin][org.jetbrains.kotlinx.dataframe.api.innerJoin], [rightJoin][org.jetbrains.kotlinx.dataframe.api.rightJoin], [fullJoin][org.jetbrains.kotlinx.dataframe.api.fullJoin], [filterJoin][org.jetbrains.kotlinx.dataframe.api.filterJoin], [excludeJoin][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html).
 *
 * ### This `leftJoin` overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 * ### Example
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight, "name", "city")
 * ```
 * @param other [DataFrame] to join with.
 * @param columns [Column Names][String] specifying join columns.
 * @return joined [DataFrame].
 */
public fun <A, B> DataFrame<A>.leftJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    leftJoin(other) { columns.toColumnSet() }

/**
 * Performs a [right join][org.jetbrains.kotlinx.dataframe.api.JoinType.Right] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the [other][other] [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes all rows from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched left-side values are filled with `null`.
 *
 * This is a shortcut for [join][org.jetbrains.kotlinx.dataframe.api.join] with [JoinType.Right][org.jetbrains.kotlinx.dataframe.api.JoinType.Right].
 *
 * If no join columns are specified, all columns with matching names in both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [automatically renamed][org.jetbrains.kotlinx.dataframe.documentation.AutoRenaming]
 * in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [join][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [innerJoin][org.jetbrains.kotlinx.dataframe.api.innerJoin], [leftJoin][org.jetbrains.kotlinx.dataframe.api.leftJoin], [fullJoin][org.jetbrains.kotlinx.dataframe.api.fullJoin], [filterJoin][org.jetbrains.kotlinx.dataframe.api.filterJoin], [excludeJoin][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html).
 *
 * ### This `rightJoin` overload
 * Select join columns (including those that have different names in different [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s)
 * using [JoinDsl][org.jetbrains.kotlinx.dataframe.api.JoinDsl].
 *
 * [JoinDsl][org.jetbrains.kotlinx.dataframe.api.JoinDsl] defines the columns used for joining [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right sides.
 *
 * Provides the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [right][org.jetbrains.kotlinx.dataframe.api.JoinDsl.right] to access columns from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [match][org.jetbrains.kotlinx.dataframe.api.JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [Columns selection via DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl].
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.join(dfRight) { left -> left.firstName match right.name }
 * ```
 * @param other [DataFrame] to join with.
 * @param selector [JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("RightJoin")
public fun <A, B> DataFrame<A>.rightJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = join(other, JoinType.Right, selector = selector)

/**
 * Performs a [right join][org.jetbrains.kotlinx.dataframe.api.JoinType.Right] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the [other][other] [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes all rows from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched left-side values are filled with `null`.
 *
 * This is a shortcut for [join][org.jetbrains.kotlinx.dataframe.api.join] with [JoinType.Right][org.jetbrains.kotlinx.dataframe.api.JoinType.Right].
 *
 * If no join columns are specified, all columns with matching names in both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [automatically renamed][org.jetbrains.kotlinx.dataframe.documentation.AutoRenaming]
 * in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [join][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [innerJoin][org.jetbrains.kotlinx.dataframe.api.innerJoin], [leftJoin][org.jetbrains.kotlinx.dataframe.api.leftJoin], [fullJoin][org.jetbrains.kotlinx.dataframe.api.fullJoin], [filterJoin][org.jetbrains.kotlinx.dataframe.api.filterJoin], [excludeJoin][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html).
 *
 * ### This `rightJoin` overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 * ### Example
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight, "name", "city")
 * ```
 * @param other [DataFrame] to join with.
 * @param columns [Column Names][String] specifying join columns.
 * @return joined [DataFrame].
 */
public fun <A, B> DataFrame<A>.rightJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    rightJoin(other) { columns.toColumnSet() }

/**
 * Performs a [full join][org.jetbrains.kotlinx.dataframe.api.JoinType.Full] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the [other][other] [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes all rows from both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s; matching rows are merged,
 * all mismatches are filled with `null`.
 *
 * This is a shortcut for [join][org.jetbrains.kotlinx.dataframe.api.join] with [JoinType.Full][org.jetbrains.kotlinx.dataframe.api.JoinType.Full].
 *
 * If no join columns are specified, all columns with matching names in both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [automatically renamed][org.jetbrains.kotlinx.dataframe.documentation.AutoRenaming]
 * in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [join][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [innerJoin][org.jetbrains.kotlinx.dataframe.api.innerJoin], [leftJoin][org.jetbrains.kotlinx.dataframe.api.leftJoin], [rightJoin][org.jetbrains.kotlinx.dataframe.api.rightJoin], [filterJoin][org.jetbrains.kotlinx.dataframe.api.filterJoin], [excludeJoin][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html).
 *
 * ### This `fullJoin` overload
 * Select join columns (including those that have different names in different [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s)
 * using [JoinDsl][org.jetbrains.kotlinx.dataframe.api.JoinDsl].
 *
 * [JoinDsl][org.jetbrains.kotlinx.dataframe.api.JoinDsl] defines the columns used for joining [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right sides.
 *
 * Provides the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [right][org.jetbrains.kotlinx.dataframe.api.JoinDsl.right] to access columns from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [match][org.jetbrains.kotlinx.dataframe.api.JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [Columns selection via DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl].
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.join(dfRight) { left -> left.firstName match right.name }
 * ```
 * @param other [DataFrame] to join with.
 * @param selector [JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("FullJoin")
public fun <A, B> DataFrame<A>.fullJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = join(other, JoinType.Full, selector = selector)

/**
 * Performs a [full join][org.jetbrains.kotlinx.dataframe.api.JoinType.Full] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the [other][other] [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes all rows from both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s; matching rows are merged,
 * all mismatches are filled with `null`.
 *
 * This is a shortcut for [join][org.jetbrains.kotlinx.dataframe.api.join] with [JoinType.Full][org.jetbrains.kotlinx.dataframe.api.JoinType.Full].
 *
 * If no join columns are specified, all columns with matching names in both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [automatically renamed][org.jetbrains.kotlinx.dataframe.documentation.AutoRenaming]
 * in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [join][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [innerJoin][org.jetbrains.kotlinx.dataframe.api.innerJoin], [leftJoin][org.jetbrains.kotlinx.dataframe.api.leftJoin], [rightJoin][org.jetbrains.kotlinx.dataframe.api.rightJoin], [filterJoin][org.jetbrains.kotlinx.dataframe.api.filterJoin], [excludeJoin][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html).
 *
 * ### This `fullJoin` overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 * ### Example
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight, "name", "city")
 * ```
 * @param other [DataFrame] to join with.
 * @param columns [Column Names][String] specifying join columns.
 * @return joined [DataFrame].
 */
public fun <A, B> DataFrame<A>.fullJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    fullJoin(other) { columns.toColumnSet() }

/**
 * Performs a [filter join][org.jetbrains.kotlinx.dataframe.api.JoinType.Filter] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the [other][other] [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes only rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] that have a match in the right one;
 * right-side columns are not merged.
 *
 * This is a shortcut for [join][org.jetbrains.kotlinx.dataframe.api.join] with [JoinType.Filter][org.jetbrains.kotlinx.dataframe.api.JoinType.Filter].
 *
 * If no join columns are specified, all columns with matching names in both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [automatically renamed][org.jetbrains.kotlinx.dataframe.documentation.AutoRenaming]
 * in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [join][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [innerJoin][org.jetbrains.kotlinx.dataframe.api.innerJoin], [leftJoin][org.jetbrains.kotlinx.dataframe.api.leftJoin], [rightJoin][org.jetbrains.kotlinx.dataframe.api.rightJoin], [fullJoin][org.jetbrains.kotlinx.dataframe.api.fullJoin], [excludeJoin][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html).
 *
 * ### This `filterJoin` overload
 * Select join columns (including those that have different names in different [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s)
 * using [JoinDsl][org.jetbrains.kotlinx.dataframe.api.JoinDsl].
 *
 * [JoinDsl][org.jetbrains.kotlinx.dataframe.api.JoinDsl] defines the columns used for joining [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right sides.
 *
 * Provides the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [right][org.jetbrains.kotlinx.dataframe.api.JoinDsl.right] to access columns from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [match][org.jetbrains.kotlinx.dataframe.api.JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [Columns selection via DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl].
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.join(dfRight) { left -> left.firstName match right.name }
 * ```
 * @param other [DataFrame] to join with.
 * @param selector [JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("FilterJoin")
public fun <A, B> DataFrame<A>.filterJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = joinImpl(other, JoinType.Inner, addNewColumns = false, selector = selector)

/**
 * Performs a [filter join][org.jetbrains.kotlinx.dataframe.api.JoinType.Filter] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the [other][other] [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes only rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] that have a match in the right one;
 * right-side columns are not merged.
 *
 * This is a shortcut for [join][org.jetbrains.kotlinx.dataframe.api.join] with [JoinType.Filter][org.jetbrains.kotlinx.dataframe.api.JoinType.Filter].
 *
 * If no join columns are specified, all columns with matching names in both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [automatically renamed][org.jetbrains.kotlinx.dataframe.documentation.AutoRenaming]
 * in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [join][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [innerJoin][org.jetbrains.kotlinx.dataframe.api.innerJoin], [leftJoin][org.jetbrains.kotlinx.dataframe.api.leftJoin], [rightJoin][org.jetbrains.kotlinx.dataframe.api.rightJoin], [fullJoin][org.jetbrains.kotlinx.dataframe.api.fullJoin], [excludeJoin][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html).
 *
 * ### This `filterJoin` overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 * ### Example
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight, "name", "city")
 * ```
 * @param other [DataFrame] to join with.
 * @param columns [Column Names][String] specifying join columns.
 * @return joined [DataFrame].
 */
public fun <A, B> DataFrame<A>.filterJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    filterJoin(other) { columns.toColumnSet() }

/**
 * Performs an [exclude join][org.jetbrains.kotlinx.dataframe.api.JoinType.Exclude] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the [other][other] [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes only rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] that do *not* have a match in the right one;
 * right-side columns are not merged.
 *
 * This is a shortcut for [join][org.jetbrains.kotlinx.dataframe.api.join] with [JoinType.Exclude][org.jetbrains.kotlinx.dataframe.api.JoinType.Exclude].
 *
 * If no join columns are specified, all columns with matching names in both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [automatically renamed][org.jetbrains.kotlinx.dataframe.documentation.AutoRenaming]
 * in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [join][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [innerJoin][org.jetbrains.kotlinx.dataframe.api.innerJoin], [leftJoin][org.jetbrains.kotlinx.dataframe.api.leftJoin], [rightJoin][org.jetbrains.kotlinx.dataframe.api.rightJoin], [filterJoin][org.jetbrains.kotlinx.dataframe.api.filterJoin], [fullJoin][org.jetbrains.kotlinx.dataframe.api.fullJoin].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html).
 *
 * ### This `excludeJoin` overload
 * Select join columns (including those that have different names in different [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s)
 * using [JoinDsl][org.jetbrains.kotlinx.dataframe.api.JoinDsl].
 *
 * [JoinDsl][org.jetbrains.kotlinx.dataframe.api.JoinDsl] defines the columns used for joining [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right sides.
 *
 * Provides the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [right][org.jetbrains.kotlinx.dataframe.api.JoinDsl.right] to access columns from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [match][org.jetbrains.kotlinx.dataframe.api.JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [Columns selection via DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl].
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.join(dfRight) { left -> left.firstName match right.name }
 * ```
 * @param other [DataFrame] to join with.
 * @param selector [JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [DataFrame].
 */
@Refine
@Interpretable("ExcludeJoin")
public fun <A, B> DataFrame<A>.excludeJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = joinImpl(other, JoinType.Exclude, addNewColumns = false, selector = selector)

/**
 * Performs an [exclude join][org.jetbrains.kotlinx.dataframe.api.JoinType.Exclude] of this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the [other][other] [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes only rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] that do *not* have a match in the right one;
 * right-side columns are not merged.
 *
 * This is a shortcut for [join][org.jetbrains.kotlinx.dataframe.api.join] with [JoinType.Exclude][org.jetbrains.kotlinx.dataframe.api.JoinType.Exclude].
 *
 * If no join columns are specified, all columns with matching names in both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [automatically renamed][org.jetbrains.kotlinx.dataframe.documentation.AutoRenaming]
 * in the resulting [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [join][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [innerJoin][org.jetbrains.kotlinx.dataframe.api.innerJoin], [leftJoin][org.jetbrains.kotlinx.dataframe.api.leftJoin], [rightJoin][org.jetbrains.kotlinx.dataframe.api.rightJoin], [filterJoin][org.jetbrains.kotlinx.dataframe.api.filterJoin], [fullJoin][org.jetbrains.kotlinx.dataframe.api.fullJoin].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html).
 *
 * ### This `excludeJoin` overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 * ### Example
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight, "name", "city")
 * ```
 * @param other [DataFrame] to join with.
 * @param columns [Column Names][String] specifying join columns.
 * @return joined [DataFrame].
 */
public fun <A, B> DataFrame<A>.excludeJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    excludeJoin(other) { columns.toColumnSet() }

/**
 * Joins all [DataFrame]s in this iterable into a single [DataFrame].
 *
 * Sequentially applies the [join] operation to each [DataFrame] in order.
 * Returns `null` if the iterable is empty.
 *
 * @param [joinType] [JoinType] defining how rows are matched and combined.
 * @param [selector] optional [JoinColumnsSelector] specifying key columns.
 * @return resulting [DataFrame], or `null` if the iterable is empty.
 */
public fun <T> Iterable<DataFrame<T>>.joinOrNull(
    joinType: JoinType = JoinType.Inner,
    selector: JoinColumnsSelector<T, T>? = null,
): DataFrame<T>? =
    fold<DataFrame<T>, DataFrame<T>?>(null) { joined, new -> joined?.join(new, joinType, selector = selector) ?: new }

/**
 * A specialized [ColumnsSelectionDsl] that allows specifying [join] matching columns
 * with different names in left and right [DataFrame]s.
 *
 * [JoinDsl][org.jetbrains.kotlinx.dataframe.api.JoinDsl] defines the columns used for joining [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right sides.
 *
 * Provides the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [right][org.jetbrains.kotlinx.dataframe.api.JoinDsl.right] to access columns from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [match][org.jetbrains.kotlinx.dataframe.api.JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [Columns selection via DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl].
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.join(dfRight) { left -> left.firstName match right.name }
 * ```
 */
public interface JoinDsl<out A, out B> : ColumnsSelectionDsl<A> {

    /**
     * Provides access to columns of the right [DataFrame]
     * for further matching with left columns [match].
     */
    public val right: DataFrame<B>

    /** Matches columns from the left and right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s for [joining][org.jetbrains.kotlinx.dataframe.api.join].
     *
     * The receiver column must belong to the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
     * and the argument ([other]) column must belong to the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * @receiver column from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * @param [other] column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * @return [ColumnMatch][org.jetbrains.kotlinx.dataframe.api.ColumnMatch] representing the column pair used for joining. */
    @Interpretable("Match0")
    public infix fun <C> ColumnReference<C>.match(other: ColumnReference<C>): ColumnMatch<C> = ColumnMatch(this, other)

    /** Matches columns from the left and right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s for [joining][org.jetbrains.kotlinx.dataframe.api.join].
     *
     * The receiver column must belong to the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
     * and the argument ([other]) column must belong to the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * @receiver column from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * @param [other] column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * @return [ColumnMatch][org.jetbrains.kotlinx.dataframe.api.ColumnMatch] representing the column pair used for joining. */
    public infix fun <C> String.match(other: ColumnReference<C>): ColumnMatch<C> = ColumnMatch(toColumnOf(), other)

    /** Matches columns from the left and right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s for [joining][org.jetbrains.kotlinx.dataframe.api.join].
     *
     * The receiver column must belong to the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
     * and the argument ([other]) column must belong to the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * @receiver column from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * @param [other] column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * @return [ColumnMatch][org.jetbrains.kotlinx.dataframe.api.ColumnMatch] representing the column pair used for joining. */
    public infix fun <C> ColumnReference<C>.match(other: String): ColumnMatch<C> = ColumnMatch(this, other.toColumnOf())

    /** Matches columns from the left and right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s for [joining][org.jetbrains.kotlinx.dataframe.api.join].
     *
     * The receiver column must belong to the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame],
     * and the argument ([other]) column must belong to the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * @receiver column from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * @param [other] column from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
     * @return [ColumnMatch][org.jetbrains.kotlinx.dataframe.api.ColumnMatch] representing the column pair used for joining. */
    public infix fun String.match(other: String): ColumnMatch<Any?> =
        ColumnMatch(toColumnAccessor(), other.toColumnAccessor())

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.match(other: KProperty<C>): ColumnMatch<C> =
        ColumnMatch(toColumnAccessor(), other.toColumnAccessor())

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> ColumnReference<C>.match(other: KProperty<C>): ColumnMatch<C> =
        ColumnMatch(this, other.toColumnAccessor())

    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.match(other: ColumnReference<C>): ColumnMatch<C> =
        ColumnMatch(toColumnAccessor(), other)

    public companion object {
        /**
         * **For internal use only.**
         * Not intended for public API consumption.
         *
         * Used in Compiler Plugin.
         */
        public fun <A, B> defaultJoinColumns(left: DataFrame<A>, right: DataFrame<B>): JoinColumnsSelector<A, B> =
            {
                left.columnNames().intersect(right.columnNames().toSet())
                    .map { it.toColumnAccessor() }
                    .let { ColumnListImpl(it) }
            }

        /**
         * **For internal use only.**
         * Not intended for public API consumption.
         *
         * Used in Compiler Plugin.
         */
        public fun <A, B> getColumns(
            left: DataFrame<A>,
            other: DataFrame<B>,
            selector: JoinColumnsSelector<A, B>,
        ): List<ColumnMatch<Any?>> {
            val receiver = object : DataFrameReceiver<A>(left, UnresolvedColumnsPolicy.Fail), JoinDsl<A, B> {
                override val right: DataFrame<B> = DataFrameReceiver(other, UnresolvedColumnsPolicy.Fail)
            }
            val columns = selector(receiver, left)
            return columns.extractJoinColumns()
        }
    }
}

/**
 * A special [ColumnSet] that specifies a [column match][JoinDsl.match] for the [join] operation.
 */
public interface ColumnMatch<C> : ColumnSet<C> {
    public val left: ColumnReference<C>
    public val right: ColumnReference<C>
}

internal class ColumnMatchImpl<C>(override val left: ColumnReference<C>, override val right: ColumnReference<C>) :
    ColumnMatch<C> {

    override fun resolve(context: ColumnResolutionContext): List<ColumnWithPath<C>> =
        throw UnsupportedOperationException()
}

/**
 * Creates a [ColumnMatch].
 *
 * Not intended for public API consumption. Please use [match][JoinDsl.match] instead.
 */
public fun <C> ColumnMatch(left: ColumnReference<C>, right: ColumnReference<C>): ColumnMatch<C> =
    ColumnMatchImpl(left, right)

/**
 * A specialized [ColumnsSelector] used for matching columns in a [join] operation.
 *
 * Provides [JoinDsl] both as the receiver and the lambda parameter, and expects
 * a [ColumnsResolver] as the return value.
 *
 * Enables defining matching columns from left and right [DataFrame]s
 * using [right][JoinDsl.right] and [match][JoinDsl.match].
 */
public typealias JoinColumnsSelector<A, B> = JoinDsl<A, B>.(ColumnsContainer<A>) -> ColumnsResolver<*>

/**
 * Represents the type of [join] operation.
 *
 * There are two categories of joins:
 * * **Merging joins** — merge matching rows from both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s into a single row.
 * * **Non-merging joins** — select rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] based on whether
 * a match exists in the right one, without merging columns.
 *
 * The exact behavior depends on the specified [join type][type]:
 *
 * **Merging joins:**
 * * [JoinType.Inner][org.jetbrains.kotlinx.dataframe.api.JoinType.Inner] (default) — Includes only matching rows from both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s;
 * rows are merged.
 * * [JoinType.Left][org.jetbrains.kotlinx.dataframe.api.JoinType.Left] — Includes all rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched right-side values are filled with `null`.
 * * [JoinType.Right][org.jetbrains.kotlinx.dataframe.api.JoinType.Right] — Includes all rows from the right [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched left-side values are filled with `null`.
 * * [JoinType.Full][org.jetbrains.kotlinx.dataframe.api.JoinType.Full] — Includes all rows from both [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]s; matching rows are merged,
 * all mismatches are filled with `null`.
 *
 * **Non-merging joins:**
 * * [JoinType.Filter][org.jetbrains.kotlinx.dataframe.api.JoinType.Filter] — Includes only rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] that have a match in the right one;
 * right-side columns are not merged.
 * * [JoinType.Exclude][org.jetbrains.kotlinx.dataframe.api.JoinType.Exclude] — Includes only rows from the left [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] that do *not* have a match in the right one;
 * right-side columns are not merged.
 */
public enum class JoinType {

    /**
     * Includes all rows from the left [DataFrame]; matching rows are merged,
     * unmatched right-side values are filled with `null`.
     */
    Left,

    /**
     * Includes all rows from the right [DataFrame]; matching rows are merged,
     * unmatched left-side values are filled with `null`.
     */
    Right,

    /**
     * Includes only matching rows from both [DataFrame]s;
     * rows are merged.
     */
    Inner,

    /**
     * Includes only rows from the left [DataFrame] that have a match in the right one;
     * right-side columns are not merged.
     */
    Filter,

    /**
     * Includes all rows from both [DataFrame]s; matching rows are merged,
     * all mismatches are filled with `null`.
     */
    Full,

    /**
     * Includes only rows from the left [DataFrame] that do *not* have a match in the right one;
     * right-side columns are not merged.
     */
    Exclude,
}

internal val JoinType.addNewColumns: Boolean
    get() = when (this) {
        JoinType.Filter, JoinType.Exclude -> false
        JoinType.Left, JoinType.Right, JoinType.Inner, JoinType.Full -> true
    }

internal val JoinType.allowLeftNulls: Boolean
    get() = this == JoinType.Right || this == JoinType.Full

internal val JoinType.allowRightNulls: Boolean
    get() = this == JoinType.Left ||
        this == JoinType.Full ||
        this == JoinType.Exclude
