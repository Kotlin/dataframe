package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.annotations.StringApiInterpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnResolutionContext
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.AutoRenamingColumnsInDataFrame
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.DataFrameReceiver
import org.jetbrains.kotlinx.dataframe.impl.api.extractJoinColumns
import org.jetbrains.kotlinx.dataframe.impl.api.joinImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnListImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

/**
 * Joins this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the [<code>other</code>][other] [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] using the selected key columns.
 *
 * Creates a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] by combining [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow]
 * from two input dataframes according to one or more matching key columns.
 *
 * There are two categories of joins:
 * * **Merging joins** — merge matching rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s into a single row.
 * * **Non-merging joins** — select rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] based on whether
 * a match exists in the right one, without merging columns.
 *
 * The exact behavior depends on the specified [<code>join type</code>][type]:
 *
 * **Merging joins:**
 * * [<code>JoinType.Inner</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Inner] (default) — Includes only matching rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s;
 * rows are merged.
 * * [<code>JoinType.Left</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Left] — Includes all rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched right-side values are filled with `null`.
 * * [<code>JoinType.Right</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Right] — Includes all rows from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched left-side values are filled with `null`.
 * * [<code>JoinType.Full</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Full] — Includes all rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s; matching rows are merged,
 * all mismatches are filled with `null`.
 *
 * **Non-merging joins:**
 * * [<code>JoinType.Filter</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Filter] — Includes only rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that have a match in the right one;
 * right-side columns are not merged.
 * * [<code>JoinType.Exclude</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Exclude] — Includes only rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that do *not* have a match in the right one;
 * right-side columns are not merged.
 *
 * If no join columns are specified, all columns with matching names in both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [<code>automatically renamed</code>][org.jetbrains.kotlinx.dataframe.documentation.AutoRenamingColumnsInDataFrame]
 * in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Each join type has a corresponding shortcut function:
 * [<code>innerJoin</code>][org.jetbrains.kotlinx.dataframe.api.innerJoin], [<code>leftJoin</code>][org.jetbrains.kotlinx.dataframe.api.leftJoin], [<code>rightJoin</code>][org.jetbrains.kotlinx.dataframe.api.rightJoin], [<code>fullJoin</code>][org.jetbrains.kotlinx.dataframe.api.fullJoin], [<code>filterJoin</code>][org.jetbrains.kotlinx.dataframe.api.filterJoin], and [<code>excludeJoin</code>][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 * See also [<code>joinWith</code>][org.jetbrains.kotlinx.dataframe.api.joinWith], which performs a join by matching row values condition.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### This `join` overload
 * Select join columns (including those that have different names in different [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s)
 * using [<code>JoinDsl</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl].
 *
 * [<code>JoinDsl</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl] allows you to define the columns used for joining [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right side.
 *
 * Provides the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [<code>right</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl.right] to access columns from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [<code>Columns selection via DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
 *
 * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.join(dfRight) { left -> left.firstName match right.name }
 *
 * // Match columns using String API
 * dfLeft.join(dfRight) { "symbol" match right.getValue<Char>("char") }
 * ```
 * @param other [<code>DataFrame</code>][DataFrame] to join with.
 * @param type [<code>JoinType</code>][JoinType] defining how the resulting rows are constructed.
 * @param selector [<code>JoinColumnsSelector</code>][JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("Join0")
public fun <A, B> DataFrame<A>.join(
    other: DataFrame<B>,
    type: JoinType = JoinType.Inner,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = joinImpl(other, type, addNewColumns = type.addNewColumns, selector)

/**
 * Joins this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the [<code>other</code>][other] [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] using the selected key columns.
 *
 * Creates a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] by combining [<code>rows</code>][org.jetbrains.kotlinx.dataframe.DataRow]
 * from two input dataframes according to one or more matching key columns.
 *
 * There are two categories of joins:
 * * **Merging joins** — merge matching rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s into a single row.
 * * **Non-merging joins** — select rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] based on whether
 * a match exists in the right one, without merging columns.
 *
 * The exact behavior depends on the specified [<code>join type</code>][type]:
 *
 * **Merging joins:**
 * * [<code>JoinType.Inner</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Inner] (default) — Includes only matching rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s;
 * rows are merged.
 * * [<code>JoinType.Left</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Left] — Includes all rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched right-side values are filled with `null`.
 * * [<code>JoinType.Right</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Right] — Includes all rows from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched left-side values are filled with `null`.
 * * [<code>JoinType.Full</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Full] — Includes all rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s; matching rows are merged,
 * all mismatches are filled with `null`.
 *
 * **Non-merging joins:**
 * * [<code>JoinType.Filter</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Filter] — Includes only rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that have a match in the right one;
 * right-side columns are not merged.
 * * [<code>JoinType.Exclude</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Exclude] — Includes only rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that do *not* have a match in the right one;
 * right-side columns are not merged.
 *
 * If no join columns are specified, all columns with matching names in both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [<code>automatically renamed</code>][org.jetbrains.kotlinx.dataframe.documentation.AutoRenamingColumnsInDataFrame]
 * in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Each join type has a corresponding shortcut function:
 * [<code>innerJoin</code>][org.jetbrains.kotlinx.dataframe.api.innerJoin], [<code>leftJoin</code>][org.jetbrains.kotlinx.dataframe.api.leftJoin], [<code>rightJoin</code>][org.jetbrains.kotlinx.dataframe.api.rightJoin], [<code>fullJoin</code>][org.jetbrains.kotlinx.dataframe.api.fullJoin], [<code>filterJoin</code>][org.jetbrains.kotlinx.dataframe.api.filterJoin], and [<code>excludeJoin</code>][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 * See also [<code>joinWith</code>][org.jetbrains.kotlinx.dataframe.api.joinWith], which performs a join by matching row values condition.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### This `join` overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * ### Example
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight, "name", "city")
 * ```
 * @param other [<code>DataFrame</code>][DataFrame] to join with.
 * @param columns [<code>Column Names</code>][String] specifying join columns.
 * @param type [<code>JoinType</code>][JoinType] defining how the resulting rows are constructed.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@StringApiInterpretable("Join0", stringArgument = "columns", targetArgument = "selector")
public fun <A, B> DataFrame<A>.join(
    other: DataFrame<B>,
    vararg columns: String,
    type: JoinType = JoinType.Inner,
): DataFrame<A> = join(other, type) { columns.toColumnSet() }

/**
 * Performs an [<code>inner join</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Inner] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the [<code>other</code>][other] [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes only matching rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s;
 * rows are merged.
 *
 * This is a shortcut for [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join] with [<code>JoinType.Inner</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Inner].
 *
 * If no join columns are specified, all columns with matching names in both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [<code>automatically renamed</code>][org.jetbrains.kotlinx.dataframe.documentation.AutoRenamingColumnsInDataFrame]
 * in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [<code>leftJoin</code>][org.jetbrains.kotlinx.dataframe.api.leftJoin], [<code>rightJoin</code>][org.jetbrains.kotlinx.dataframe.api.rightJoin], [<code>fullJoin</code>][org.jetbrains.kotlinx.dataframe.api.fullJoin], [<code>filterJoin</code>][org.jetbrains.kotlinx.dataframe.api.filterJoin], [<code>excludeJoin</code>][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### This `innerJoin` overload
 * Select join columns (including those that have different names in different [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s)
 * using [<code>JoinDsl</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl].
 *
 * [<code>JoinDsl</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl] allows you to define the columns used for joining [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right side.
 *
 * Provides the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [<code>right</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl.right] to access columns from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [<code>Columns selection via DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
 *
 * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.join(dfRight) { left -> left.firstName match right.name }
 *
 * // Match columns using String API
 * dfLeft.join(dfRight) { "symbol" match right.getValue<Char>("char") }
 * ```
 * @param other [<code>DataFrame</code>][DataFrame] to join with.
 * @param selector [<code>JoinColumnsSelector</code>][JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("InnerJoin")
public fun <A, B> DataFrame<A>.innerJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = join(other, JoinType.Inner, selector = selector)

/**
 * Performs an [<code>inner join</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Inner] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the [<code>other</code>][other] [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes only matching rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s;
 * rows are merged.
 *
 * This is a shortcut for [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join] with [<code>JoinType.Inner</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Inner].
 *
 * If no join columns are specified, all columns with matching names in both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [<code>automatically renamed</code>][org.jetbrains.kotlinx.dataframe.documentation.AutoRenamingColumnsInDataFrame]
 * in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [<code>leftJoin</code>][org.jetbrains.kotlinx.dataframe.api.leftJoin], [<code>rightJoin</code>][org.jetbrains.kotlinx.dataframe.api.rightJoin], [<code>fullJoin</code>][org.jetbrains.kotlinx.dataframe.api.fullJoin], [<code>filterJoin</code>][org.jetbrains.kotlinx.dataframe.api.filterJoin], [<code>excludeJoin</code>][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### This `innerJoin` overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * ### Example
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight, "name", "city")
 * ```
 * @param other [<code>DataFrame</code>][DataFrame] to join with.
 * @param columns [<code>Column Names</code>][String] specifying join columns.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@StringApiInterpretable("InnerJoin", stringArgument = "columns", targetArgument = "selector")
public fun <A, B> DataFrame<A>.innerJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    innerJoin(other) { columns.toColumnSet() }

/**
 * Performs a [<code>left join</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Left] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the [<code>other</code>][other] [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes all rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched right-side values are filled with `null`.
 *
 * This is a shortcut for [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join] with [<code>JoinType.Left</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Left].
 *
 * If no join columns are specified, all columns with matching names in both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [<code>automatically renamed</code>][org.jetbrains.kotlinx.dataframe.documentation.AutoRenamingColumnsInDataFrame]
 * in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [<code>innerJoin</code>][org.jetbrains.kotlinx.dataframe.api.innerJoin], [<code>rightJoin</code>][org.jetbrains.kotlinx.dataframe.api.rightJoin], [<code>fullJoin</code>][org.jetbrains.kotlinx.dataframe.api.fullJoin], [<code>filterJoin</code>][org.jetbrains.kotlinx.dataframe.api.filterJoin], [<code>excludeJoin</code>][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### This `leftJoin` overload
 * Select join columns (including those that have different names in different [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s)
 * using [<code>JoinDsl</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl].
 *
 * [<code>JoinDsl</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl] allows you to define the columns used for joining [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right side.
 *
 * Provides the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [<code>right</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl.right] to access columns from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [<code>Columns selection via DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
 *
 * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.join(dfRight) { left -> left.firstName match right.name }
 *
 * // Match columns using String API
 * dfLeft.join(dfRight) { "symbol" match right.getValue<Char>("char") }
 * ```
 * @param other [<code>DataFrame</code>][DataFrame] to join with.
 * @param selector [<code>JoinColumnsSelector</code>][JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("LeftJoin")
public fun <A, B> DataFrame<A>.leftJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = join(other, JoinType.Left, selector = selector)

/**
 * Performs a [<code>left join</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Left] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the [<code>other</code>][other] [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes all rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched right-side values are filled with `null`.
 *
 * This is a shortcut for [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join] with [<code>JoinType.Left</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Left].
 *
 * If no join columns are specified, all columns with matching names in both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [<code>automatically renamed</code>][org.jetbrains.kotlinx.dataframe.documentation.AutoRenamingColumnsInDataFrame]
 * in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [<code>innerJoin</code>][org.jetbrains.kotlinx.dataframe.api.innerJoin], [<code>rightJoin</code>][org.jetbrains.kotlinx.dataframe.api.rightJoin], [<code>fullJoin</code>][org.jetbrains.kotlinx.dataframe.api.fullJoin], [<code>filterJoin</code>][org.jetbrains.kotlinx.dataframe.api.filterJoin], [<code>excludeJoin</code>][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### This `leftJoin` overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * ### Example
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight, "name", "city")
 * ```
 * @param other [<code>DataFrame</code>][DataFrame] to join with.
 * @param columns [<code>Column Names</code>][String] specifying join columns.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@StringApiInterpretable("LeftJoin", stringArgument = "columns", targetArgument = "selector")
public fun <A, B> DataFrame<A>.leftJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    leftJoin(other) { columns.toColumnSet() }

/**
 * Performs a [<code>right join</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Right] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the [<code>other</code>][other] [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes all rows from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched left-side values are filled with `null`.
 *
 * This is a shortcut for [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join] with [<code>JoinType.Right</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Right].
 *
 * If no join columns are specified, all columns with matching names in both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [<code>automatically renamed</code>][org.jetbrains.kotlinx.dataframe.documentation.AutoRenamingColumnsInDataFrame]
 * in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [<code>innerJoin</code>][org.jetbrains.kotlinx.dataframe.api.innerJoin], [<code>leftJoin</code>][org.jetbrains.kotlinx.dataframe.api.leftJoin], [<code>fullJoin</code>][org.jetbrains.kotlinx.dataframe.api.fullJoin], [<code>filterJoin</code>][org.jetbrains.kotlinx.dataframe.api.filterJoin], [<code>excludeJoin</code>][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### This `rightJoin` overload
 * Select join columns (including those that have different names in different [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s)
 * using [<code>JoinDsl</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl].
 *
 * [<code>JoinDsl</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl] allows you to define the columns used for joining [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right side.
 *
 * Provides the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [<code>right</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl.right] to access columns from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [<code>Columns selection via DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
 *
 * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.join(dfRight) { left -> left.firstName match right.name }
 *
 * // Match columns using String API
 * dfLeft.join(dfRight) { "symbol" match right.getValue<Char>("char") }
 * ```
 * @param other [<code>DataFrame</code>][DataFrame] to join with.
 * @param selector [<code>JoinColumnsSelector</code>][JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("RightJoin")
public fun <A, B> DataFrame<A>.rightJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = join(other, JoinType.Right, selector = selector)

/**
 * Performs a [<code>right join</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Right] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the [<code>other</code>][other] [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes all rows from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched left-side values are filled with `null`.
 *
 * This is a shortcut for [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join] with [<code>JoinType.Right</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Right].
 *
 * If no join columns are specified, all columns with matching names in both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [<code>automatically renamed</code>][org.jetbrains.kotlinx.dataframe.documentation.AutoRenamingColumnsInDataFrame]
 * in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [<code>innerJoin</code>][org.jetbrains.kotlinx.dataframe.api.innerJoin], [<code>leftJoin</code>][org.jetbrains.kotlinx.dataframe.api.leftJoin], [<code>fullJoin</code>][org.jetbrains.kotlinx.dataframe.api.fullJoin], [<code>filterJoin</code>][org.jetbrains.kotlinx.dataframe.api.filterJoin], [<code>excludeJoin</code>][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### This `rightJoin` overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * ### Example
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight, "name", "city")
 * ```
 * @param other [<code>DataFrame</code>][DataFrame] to join with.
 * @param columns [<code>Column Names</code>][String] specifying join columns.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@StringApiInterpretable("RightJoin", stringArgument = "columns", targetArgument = "selector")
public fun <A, B> DataFrame<A>.rightJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    rightJoin(other) { columns.toColumnSet() }

/**
 * Performs a [<code>full join</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Full] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the [<code>other</code>][other] [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes all rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s; matching rows are merged,
 * all mismatches are filled with `null`.
 *
 * This is a shortcut for [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join] with [<code>JoinType.Full</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Full].
 *
 * If no join columns are specified, all columns with matching names in both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [<code>automatically renamed</code>][org.jetbrains.kotlinx.dataframe.documentation.AutoRenamingColumnsInDataFrame]
 * in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [<code>innerJoin</code>][org.jetbrains.kotlinx.dataframe.api.innerJoin], [<code>leftJoin</code>][org.jetbrains.kotlinx.dataframe.api.leftJoin], [<code>rightJoin</code>][org.jetbrains.kotlinx.dataframe.api.rightJoin], [<code>filterJoin</code>][org.jetbrains.kotlinx.dataframe.api.filterJoin], [<code>excludeJoin</code>][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### This `fullJoin` overload
 * Select join columns (including those that have different names in different [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s)
 * using [<code>JoinDsl</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl].
 *
 * [<code>JoinDsl</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl] allows you to define the columns used for joining [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right side.
 *
 * Provides the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [<code>right</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl.right] to access columns from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [<code>Columns selection via DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
 *
 * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.join(dfRight) { left -> left.firstName match right.name }
 *
 * // Match columns using String API
 * dfLeft.join(dfRight) { "symbol" match right.getValue<Char>("char") }
 * ```
 * @param other [<code>DataFrame</code>][DataFrame] to join with.
 * @param selector [<code>JoinColumnsSelector</code>][JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("FullJoin")
public fun <A, B> DataFrame<A>.fullJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = join(other, JoinType.Full, selector = selector)

/**
 * Performs a [<code>full join</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Full] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the [<code>other</code>][other] [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes all rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s; matching rows are merged,
 * all mismatches are filled with `null`.
 *
 * This is a shortcut for [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join] with [<code>JoinType.Full</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Full].
 *
 * If no join columns are specified, all columns with matching names in both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [<code>automatically renamed</code>][org.jetbrains.kotlinx.dataframe.documentation.AutoRenamingColumnsInDataFrame]
 * in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [<code>innerJoin</code>][org.jetbrains.kotlinx.dataframe.api.innerJoin], [<code>leftJoin</code>][org.jetbrains.kotlinx.dataframe.api.leftJoin], [<code>rightJoin</code>][org.jetbrains.kotlinx.dataframe.api.rightJoin], [<code>filterJoin</code>][org.jetbrains.kotlinx.dataframe.api.filterJoin], [<code>excludeJoin</code>][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### This `fullJoin` overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * ### Example
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight, "name", "city")
 * ```
 * @param other [<code>DataFrame</code>][DataFrame] to join with.
 * @param columns [<code>Column Names</code>][String] specifying join columns.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@StringApiInterpretable("FullJoin", stringArgument = "columns", targetArgument = "selector")
public fun <A, B> DataFrame<A>.fullJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    fullJoin(other) { columns.toColumnSet() }

/**
 * Performs a [<code>filter join</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Filter] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the [<code>other</code>][other] [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes only rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that have a match in the right one;
 * right-side columns are not merged.
 *
 * This is a shortcut for [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join] with [<code>JoinType.Filter</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Filter].
 *
 * If no join columns are specified, all columns with matching names in both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [<code>automatically renamed</code>][org.jetbrains.kotlinx.dataframe.documentation.AutoRenamingColumnsInDataFrame]
 * in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [<code>innerJoin</code>][org.jetbrains.kotlinx.dataframe.api.innerJoin], [<code>leftJoin</code>][org.jetbrains.kotlinx.dataframe.api.leftJoin], [<code>rightJoin</code>][org.jetbrains.kotlinx.dataframe.api.rightJoin], [<code>fullJoin</code>][org.jetbrains.kotlinx.dataframe.api.fullJoin], [<code>excludeJoin</code>][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### This `filterJoin` overload
 * Select join columns (including those that have different names in different [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s)
 * using [<code>JoinDsl</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl].
 *
 * [<code>JoinDsl</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl] allows you to define the columns used for joining [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right side.
 *
 * Provides the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [<code>right</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl.right] to access columns from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [<code>Columns selection via DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
 *
 * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.join(dfRight) { left -> left.firstName match right.name }
 *
 * // Match columns using String API
 * dfLeft.join(dfRight) { "symbol" match right.getValue<Char>("char") }
 * ```
 * @param other [<code>DataFrame</code>][DataFrame] to join with.
 * @param selector [<code>JoinColumnsSelector</code>][JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("FilterJoin")
public fun <A, B> DataFrame<A>.filterJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = joinImpl(other, JoinType.Inner, addNewColumns = false, selector = selector)

/**
 * Performs a [<code>filter join</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Filter] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the [<code>other</code>][other] [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes only rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that have a match in the right one;
 * right-side columns are not merged.
 *
 * This is a shortcut for [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join] with [<code>JoinType.Filter</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Filter].
 *
 * If no join columns are specified, all columns with matching names in both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [<code>automatically renamed</code>][org.jetbrains.kotlinx.dataframe.documentation.AutoRenamingColumnsInDataFrame]
 * in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [<code>innerJoin</code>][org.jetbrains.kotlinx.dataframe.api.innerJoin], [<code>leftJoin</code>][org.jetbrains.kotlinx.dataframe.api.leftJoin], [<code>rightJoin</code>][org.jetbrains.kotlinx.dataframe.api.rightJoin], [<code>fullJoin</code>][org.jetbrains.kotlinx.dataframe.api.fullJoin], [<code>excludeJoin</code>][org.jetbrains.kotlinx.dataframe.api.excludeJoin].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information, [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### This `filterJoin` overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * ### Example
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight, "name", "city")
 * ```
 * @param other [<code>DataFrame</code>][DataFrame] to join with.
 * @param columns [<code>Column Names</code>][String] specifying join columns.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@StringApiInterpretable("FilterJoin", stringArgument = "columns", targetArgument = "selector")
public fun <A, B> DataFrame<A>.filterJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    filterJoin(other) { columns.toColumnSet() }

/**
 * Performs an [<code>exclude join</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Exclude] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the [<code>other</code>][other] [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes only rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that do *not* have a match in the right one;
 * right-side columns are not merged.
 *
 * This is a shortcut for [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join] with [<code>JoinType.Exclude</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Exclude].
 *
 * If no join columns are specified, all columns with matching names in both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [<code>automatically renamed</code>][org.jetbrains.kotlinx.dataframe.documentation.AutoRenamingColumnsInDataFrame]
 * in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [<code>innerJoin</code>][org.jetbrains.kotlinx.dataframe.api.innerJoin], [<code>leftJoin</code>][org.jetbrains.kotlinx.dataframe.api.leftJoin], [<code>rightJoin</code>][org.jetbrains.kotlinx.dataframe.api.rightJoin], [<code>filterJoin</code>][org.jetbrains.kotlinx.dataframe.api.filterJoin], [<code>fullJoin</code>][org.jetbrains.kotlinx.dataframe.api.fullJoin].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### This `excludeJoin` overload
 * Select join columns (including those that have different names in different [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s)
 * using [<code>JoinDsl</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl].
 *
 * [<code>JoinDsl</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl] allows you to define the columns used for joining [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right side.
 *
 * Provides the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [<code>right</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl.right] to access columns from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [<code>Columns selection via DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
 *
 * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.join(dfRight) { left -> left.firstName match right.name }
 *
 * // Match columns using String API
 * dfLeft.join(dfRight) { "symbol" match right.getValue<Char>("char") }
 * ```
 * @param other [<code>DataFrame</code>][DataFrame] to join with.
 * @param selector [<code>JoinColumnsSelector</code>][JoinColumnsSelector] specifying join columns;
 * if `null`, same-name columns are used.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@Interpretable("ExcludeJoin")
public fun <A, B> DataFrame<A>.excludeJoin(
    other: DataFrame<B>,
    selector: JoinColumnsSelector<A, B>? = null,
): DataFrame<A> = joinImpl(other, JoinType.Exclude, addNewColumns = false, selector = selector)

/**
 * Performs an [<code>exclude join</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Exclude] of this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with the [<code>other</code>][other] [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]
 * using the selected key columns.
 * Includes only rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that do *not* have a match in the right one;
 * right-side columns are not merged.
 *
 * This is a shortcut for [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join] with [<code>JoinType.Exclude</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Exclude].
 *
 * If no join columns are specified, all columns with matching names in both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are used.
 *
 * If both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s contain columns with the same name that are *not* part of the join keys,
 * such columns are treated as distinct. Such a column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] will be
 * [<code>automatically renamed</code>][org.jetbrains.kotlinx.dataframe.documentation.AutoRenamingColumnsInDataFrame]
 * in the resulting [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * See also general [<code>join</code>][org.jetbrains.kotlinx.dataframe.api.join], as well as other shortcuts with each of join types:
 * [<code>innerJoin</code>][org.jetbrains.kotlinx.dataframe.api.innerJoin], [<code>leftJoin</code>][org.jetbrains.kotlinx.dataframe.api.leftJoin], [<code>rightJoin</code>][org.jetbrains.kotlinx.dataframe.api.rightJoin], [<code>filterJoin</code>][org.jetbrains.kotlinx.dataframe.api.filterJoin], [<code>fullJoin</code>][org.jetbrains.kotlinx.dataframe.api.fullJoin].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### This `excludeJoin` overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * ### Example
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight, "name", "city")
 * ```
 * @param other [<code>DataFrame</code>][DataFrame] to join with.
 * @param columns [<code>Column Names</code>][String] specifying join columns.
 * @return joined [<code>DataFrame</code>][DataFrame].
 */
@Refine
@StringApiInterpretable("ExcludeJoin", stringArgument = "columns", targetArgument = "selector")
public fun <A, B> DataFrame<A>.excludeJoin(other: DataFrame<B>, vararg columns: String): DataFrame<A> =
    excludeJoin(other) { columns.toColumnSet() }

/**
 * Joins all [<code>DataFrame</code>][DataFrame]s in this iterable into a single [<code>DataFrame</code>][DataFrame].
 *
 * Sequentially applies the [<code>join</code>][join] operation to each [<code>DataFrame</code>][DataFrame] in order.
 * Returns `null` if the iterable is empty.
 *
 * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * @param [joinType] [<code>JoinType</code>][JoinType] defining how rows are matched and combined.
 * @param [selector] optional [<code>JoinColumnsSelector</code>][JoinColumnsSelector] specifying key columns.
 * @return resulting [<code>DataFrame</code>][DataFrame], or `null` if the iterable is empty.
 */
public fun <T> Iterable<DataFrame<T>>.joinOrNull(
    joinType: JoinType = JoinType.Inner,
    selector: JoinColumnsSelector<T, T>? = null,
): DataFrame<T>? =
    fold<DataFrame<T>, DataFrame<T>?>(null) { joined, new -> joined?.join(new, joinType, selector = selector) ?: new }

/**
 * A specialized [<code>ColumnsSelectionDsl</code>][ColumnsSelectionDsl] that allows specifying [<code>join</code>][join] matching columns
 * with different names in left and right [<code>DataFrame</code>][DataFrame]s.
 *
 * [<code>JoinDsl</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl] allows you to define the columns used for joining [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s
 * and provides methods to match columns with different names
 * between the left and right side.
 *
 * Provides the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] both as the receiver (`this`) and as the argument (`it`),
 * allowing you to reference its columns directly.
 * Use [<code>right</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl.right] to access columns from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame],
 * and [<code>match</code>][org.jetbrains.kotlinx.dataframe.api.JoinDsl.match] to explicitly pair columns with different names.
 *
 * See also [<code>Columns selection via DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl].
 *
 * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
 *
 * ### Examples
 * ```kotlin
 * // Join by two columns with the same names in both dataframes
 * dfLeft.join(dfRight) { name and city }
 *
 * // Join by one column with different names —
 * // "firstName" in the left dataframe and "name" in the right one
 * dfLeft.join(dfRight) { left -> left.firstName match right.name }
 *
 * // Match columns using String API
 * dfLeft.join(dfRight) { "symbol" match right.getValue<Char>("char") }
 * ```
 */
public interface JoinDsl<out A, out B> : ColumnsSelectionDsl<A> {

    /**
     * Provides access to columns of the right [<code>DataFrame</code>][DataFrame]
     * for further matching with left columns [<code>match</code>][match].
     *
     * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
     */
    public val right: DataFrame<B>

    /** Matches columns from the left and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s for [<code>joining</code>][org.jetbrains.kotlinx.dataframe.api.join].
     *
     * The receiver column must belong to the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame],
     * and the argument ([<code>other</code>][other]) column must belong to the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
     *
     * @receiver column from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     * @param [other] column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     * @return [ColumnMatch][org.jetbrains.kotlinx.dataframe.api.ColumnMatch] representing the column pair used for joining. */
    @Interpretable("Match0")
    public infix fun <C> ColumnReference<C>.match(other: ColumnReference<C>): ColumnMatch<C> = ColumnMatch(this, other)

    /** Matches columns from the left and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s for [<code>joining</code>][org.jetbrains.kotlinx.dataframe.api.join].
     *
     * The receiver column must belong to the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame],
     * and the argument ([<code>other</code>][other]) column must belong to the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
     *
     * @receiver column from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     * @param [other] column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     * @return [ColumnMatch][org.jetbrains.kotlinx.dataframe.api.ColumnMatch] representing the column pair used for joining. */
    public infix fun <C> String.match(other: ColumnReference<C>): ColumnMatch<C> = ColumnMatch(toColumnOf(), other)

    /** Matches columns from the left and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s for [<code>joining</code>][org.jetbrains.kotlinx.dataframe.api.join].
     *
     * The receiver column must belong to the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame],
     * and the argument ([<code>other</code>][other]) column must belong to the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
     *
     * @receiver column from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     * @param [other] column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     * @return [ColumnMatch][org.jetbrains.kotlinx.dataframe.api.ColumnMatch] representing the column pair used for joining. */
    public infix fun <C> ColumnReference<C>.match(other: String): ColumnMatch<C> = ColumnMatch(this, other.toColumnOf())

    /** Matches columns from the left and right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s for [<code>joining</code>][org.jetbrains.kotlinx.dataframe.api.join].
     *
     * The receiver column must belong to the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame],
     * and the argument ([<code>other</code>][other]) column must belong to the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     *
     * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
     *
     * @receiver column from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
     * @param [other] column from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
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
 * A special [<code>ColumnSet</code>][ColumnSet] that specifies a [<code>column match</code>][JoinDsl.match] for the [<code>join</code>][join] operation.
 *
 * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
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
 * Creates a [<code>ColumnMatch</code>][ColumnMatch].
 *
 * Not intended for public API consumption. Please use [<code>match</code>][JoinDsl.match] instead.
 */
public fun <C> ColumnMatch(left: ColumnReference<C>, right: ColumnReference<C>): ColumnMatch<C> =
    ColumnMatchImpl(left, right)

/**
 * A specialized [<code>ColumnsSelector</code>][ColumnsSelector] used for matching columns in a [<code>join</code>][join] operation.
 *
 * Provides [<code>JoinDsl</code>][JoinDsl] both as the receiver and the lambda parameter, and expects
 * a [<code>ColumnsResolver</code>][ColumnsResolver] as the return value.
 *
 * Enables defining matching columns from left and right [<code>DataFrame</code>][DataFrame]s
 * using [<code>right</code>][JoinDsl.right] and [<code>match</code>][JoinDsl.match].
 */
public typealias JoinColumnsSelector<A, B> = JoinDsl<A, B>.(ColumnsContainer<A>) -> ColumnsResolver<*>

/**
 * Represents the type of [<code>join</code>][join] operation.
 *
 * There are two categories of joins:
 * * **Merging joins** — merge matching rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s into a single row.
 * * **Non-merging joins** — select rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] based on whether
 * a match exists in the right one, without merging columns.
 *
 * The exact behavior depends on the specified [<code>join type</code>][type]:
 *
 * **Merging joins:**
 * * [<code>JoinType.Inner</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Inner] (default) — Includes only matching rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s;
 * rows are merged.
 * * [<code>JoinType.Left</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Left] — Includes all rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched right-side values are filled with `null`.
 * * [<code>JoinType.Right</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Right] — Includes all rows from the right [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]; matching rows are merged,
 * unmatched left-side values are filled with `null`.
 * * [<code>JoinType.Full</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Full] — Includes all rows from both [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s; matching rows are merged,
 * all mismatches are filled with `null`.
 *
 * **Non-merging joins:**
 * * [<code>JoinType.Filter</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Filter] — Includes only rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that have a match in the right one;
 * right-side columns are not merged.
 * * [<code>JoinType.Exclude</code>][org.jetbrains.kotlinx.dataframe.api.JoinType.Exclude] — Includes only rows from the left [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] that do *not* have a match in the right one;
 * right-side columns are not merged.
 *
 * For more information: [See `join` on the documentation website.](https://kotlin.github.io/dataframe/join.html)
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
