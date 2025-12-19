package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.afterImpl
import org.jetbrains.kotlinx.dataframe.impl.api.beforeImpl
import org.jetbrains.kotlinx.dataframe.impl.api.insertImpl
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.impl.removeAt
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.INSERT_AFTER_COL_PATH
import org.jetbrains.kotlinx.dataframe.util.INSERT_AFTER_COL_PATH_REPLACE
import kotlin.reflect.KProperty

// region DataFrame

// region insert

/**
 * This function does not immediately insert the new column but instead specify a column to insert and
 * returns an [InsertClause],
 * which serves as an intermediate step.
 * The [InsertClause] object provides methods to insert a new column using:
 * - [under][InsertClause.under] - inserts a new column under the specified column group.
 * - [after][InsertClause.after] - inserts a new column after the specified column.
 * - [at][InsertClause.at]- inserts a new column at the specified position.
 *
 * Each method returns a new [DataFrame] with the inserted column.
 *
 * Check out [Grammar].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][InsertSelectingOptions].
 *
 * For more information: {@include [DocumentationUrls.Insert]}
 *
 * See also:
 * - [move][DataFrame.move] - move columns to a new position within the [DataFrame].
 * - [add][DataFrame.add] - add new columns to the [DataFrame]
 * (without specifying a position, to the end of the [DataFrame]).
 */
internal interface InsertDocs {

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetInsertOperationArg]}
     */
    interface InsertSelectingOptions

    /**
     * ## Insert Operation Grammar
     * {@include [LineBreak]}
     * {@include [DslGrammarLink]}
     * {@include [LineBreak]}
     *
     * [**`insert`**][insert]**`(`**`column: `[`DataColumn`][DataColumn]**`)`**` /`
     *
     * [**`insert`**][insert]**`(`**`name: `[`String`][String]**`, `**`infer: `[`Infer`][Infer]**`) { `**`rowExpression: `[`RowExpression`][RowExpression]**`  }`**
     *
     * {@include [Indent]}
     * __`.`__[**`under`**][InsertClause.under]**`  {  `**`column: `[`ColumnSelector`][ColumnSelector]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`under`**][InsertClause.under]**`(`**` columnPath: `[`ColumnPath`][ColumnPath]**`)`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`after`**][InsertClause.after]**`  {  `**`column: `[`ColumnSelector`][ColumnSelector]**` }`**
     *
     * {@include [Indent]}
     * `| `__`.`__[**`at`**][InsertClause.at]**`(`**`position: `[`Int`][Int]**`)`**
     */
    interface Grammar
}

/** {@set [SelectingColumns.OPERATION] [insert][insert]} */
@ExcludeFromSources
private interface SetInsertOperationArg

/**
 * Inserts the given [column] into this [DataFrame].
 *
 * {@include [InsertDocs]}
 *
 * ### Examples:
 * ```kotlin
 * // Insert a new column "age" under the column group with path ("info", "personal").
 * df.insert(age).under(pathOf("info", "personal"))
 *
 * // Insert a new column "count" after the column "url".
 * df.insert(count).after { url }
 * ```
 *
 * @param [column] A single [DataColumn] to insert into the [DataFrame].
 * @return An [InsertClause] for specifying the placement of the new column.
 */
public fun <T, C> DataFrame<T>.insert(column: DataColumn<C>): InsertClause<T> = InsertClause(this, column)

/**
 * Creates a new column using the provided [expression][AddExpression] and inserts it into this [DataFrame].
 *
 * {@include [AddExpressionDocs]}
 *
 * {@include [InsertDocs]}
 *
 * ## Examples
 *
 * ```kotlin
 * // Insert a new column "sum" that contains the sum of values from the "firstValue"
 * // and "secondValue" columns for each row after the "firstValue" column.
 * val dfWithSum = df.insert("sum") { firstValue + secondValue }.after { firstValue }
 *
 * // Insert a new "fibonacci" column with the Fibonacci sequence under a "math" column group:
 * // for the first two rows, the value is 1;
 * // for subsequent rows, it's the sum of the two previous Fibonacci values.
 * val dfWithFibonacci = df.insert("fibonacci") {
 *     if (index() < 2) 1
 *     else prev()!!.newValue<Int>() + prev()!!.prev()!!.newValue<Int>()
 * }.under("math")
 * ```
 *
 * @param [name] The name of the new column to be created and inserted.
 * @param [infer] Controls how values are inferred when building the new column. Defaults to [Infer.Nulls].
 * @param [expression] An [AddExpression] that computes the value for each row of the new column.
 * @return An [InsertClause] for specifying the placement of the newly created column.
 */
@Interpretable("Insert1")
public inline fun <T, reified R> DataFrame<T>.insert(
    name: String,
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): InsertClause<T> = insert(mapToColumn(name, infer, expression))

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public inline fun <T, reified R> DataFrame<T>.insert(
    column: ColumnAccessor<R>,
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): InsertClause<T> = insert(column.name(), infer, expression)

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public inline fun <T, reified R> DataFrame<T>.insert(
    column: KProperty<R>,
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): InsertClause<T> = insert(column.columnName, infer, expression)

// endregion

/**
 * An intermediate class used in the [insert] operation.
 *
 * This class itself does not perform any insertions — it is a transitional step
 * before specifying how to insert the selected columns.
 * It must be followed by one of the inserting methods
 * to produce a new [DataFrame] with an inserted column.
 *
 * Use the following methods to perform the insertion:
 * - [under][InsertClause.under] - inserts a new column under the specified column group.
 * - [after][InsertClause.after] - inserts a new column after the specified column.
 * - [at][InsertClause.at]- inserts a new column at the specified position.
 *
 * See [Grammar][InsertDocs.Grammar] for more details.
 */
public class InsertClause<T>(internal val df: DataFrame<T>, internal val column: AnyCol) {
    override fun toString(): String = "InsertClause(df=$df, column=$column)"
}

// region under

/**
 * Inserts the new column previously specified with [insert] under
 * the selected [column group][column].
 *
 * Works only with existing column groups.
 * To insert into a new column group, use the overloads:
 * `under(path: ColumnPath)` or `under(column: String)`.
 * [Should be fixed](https://github.com/Kotlin/dataframe/issues/1411).
 *
 * For more information: {@include [DocumentationUrls.Insert]}
 *
 * See [Grammar][InsertDocs.Grammar] for more details.
 *
 * See [SelectingColumns.Dsl].
 *
 * ### Examples
 * ```kotlin
 * // Insert a new column "age" under the column group with path ("info", "personal")
 * df.insert(age).under { info.personal }
 *
 * // Insert a new column "sum" under the only top-level column group
 * val dfWithSum = df.insert("sum") { a + b }.under { colGroups().single() }
 * ```
 *
 * @param column The [ColumnSelector] used to choose an existing column group in this [DataFrame]
 * under which the new column will be inserted.
 * @return A new [DataFrame] with the inserted column placed under the selected group.
 */
@[Refine Interpretable("Under0")]
public fun <T> InsertClause<T>.under(column: ColumnSelector<T, *>): DataFrame<T> = under(df.getColumnPath(column))

/**
 * Inserts the new column previously specified with [insert] under
 * the column group defined by the given [columnPath].
 *
 * {@include [org.jetbrains.kotlinx.dataframe.documentation.ColumnPathCreation]}
 *
 * See [Grammar][InsertDocs.Grammar] for more details.
 *
 * For more information: {@include [DocumentationUrls.Insert]}
 *
 * ### Example
 * ```kotlin
 * // Insert a new column "age" under the column group with path ("info", "personal")
 * df.insert(age).under(pathOf("info", "personal"))
 * ```
 *
 * @param [columnPath] The [ColumnPath] specifying the path to a column group in this [DataFrame]
 * under which the new column will be inserted.
 * @return A new [DataFrame] with the inserted column placed under the specified column group.
 */
@[Refine Interpretable("Under1")]
public fun <T> InsertClause<T>.under(columnPath: ColumnPath): DataFrame<T> =
    df.insertImpl(columnPath + column.name, column)

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T> InsertClause<T>.under(column: ColumnAccessor<*>): DataFrame<T> = under(column.path())

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T> InsertClause<T>.under(column: KProperty<*>): DataFrame<T> = under(column.columnName)

/**
 * Inserts the new column previously specified with [insert] under
 * the given column group by its [name][column].
 *
 * If the column group with the provided [name][column] does not exist, it will be created automatically.
 *
 * For more information: {@include [DocumentationUrls.Insert]}
 *
 * See [Grammar][InsertDocs.Grammar] for more details.
 *
 * ### Example
 * ```kotlin
 * // Insert a new column "age" under the "info" column group.
 * df.insert(age).under("info")
 * ```
 *
 * @param [column] The [name][String] of the column group in this [DataFrame].
 * If the group does not exist, it will be created.
 * @return A new [DataFrame] with the inserted column placed under the specified column group.
 */
@[Refine Interpretable("Under4")]
public fun <T> InsertClause<T>.under(column: String): DataFrame<T> = under(pathOf(column))

// endregion

// region after

/**
 * Inserts the new column previously specified with [insert]
 * at the position immediately after the selected [column] (on the same level).
 *
 * For more information: {@include [DocumentationUrls.Insert]}
 *
 * See [Grammar][InsertDocs.Grammar] for more details.
 *
 * See also: [SelectingColumns.Dsl].
 *
 * ### Examples:
 * ```kotlin
 * // Insert a new column "age" after the "name" column
 * df.insert(age).after { name }
 *
 * // Insert a new column "sum" after the nested "min" column (inside the "stats" column group)
 * val dfWithSum = df.insert("sum") { a + b }.after { stats.min }
 * ```
 *
 * @param [column] The [ColumnSelector] used to choose an existing column in this [DataFrame],
 * after which the new column will be inserted.
 * @return A new [DataFrame] with the inserted column placed after the selected column.
 */
@[Refine Interpretable("InsertAfter0")]
public fun <T> InsertClause<T>.after(column: ColumnSelector<T, *>): DataFrame<T> = afterImpl(df.getColumnPath(column))

/**
 * Inserts the new column previously specified with [insert]
 * at the position immediately after the column with the given [name][column].
 *
 * For more information: {@include [DocumentationUrls.Insert]}
 *
 * See [Grammar][InsertDocs.Grammar] for more details.
 *
 * See also: [SelectingColumns.ColumnNames].
 *
 * ### Example
 * ```kotlin
 * // Insert a new column "age" after the "name" column
 * df.insert(age).after("name")
 * ```
 *
 * @param [column] The [String] name of the column in this [DataFrame]
 * after which the new column will be inserted.
 * @return A new [DataFrame] with the inserted column placed after the specified column.
 */
public fun <T> InsertClause<T>.after(column: String): DataFrame<T> = df.add(this.column).move(this.column).after(column)

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T> InsertClause<T>.after(column: ColumnAccessor<*>): DataFrame<T> = afterImpl(column.path())

@[Deprecated(DEPRECATED_ACCESS_API) AccessApiOverload]
public fun <T> InsertClause<T>.after(column: KProperty<*>): DataFrame<T> = after(column.columnName)

@Deprecated(INSERT_AFTER_COL_PATH, ReplaceWith(INSERT_AFTER_COL_PATH_REPLACE), DeprecationLevel.ERROR)
public fun <T> InsertClause<T>.after(columnPath: ColumnPath): DataFrame<T> {
    val dstPath = ColumnPath(columnPath.removeAt(columnPath.size - 1) + column.name())
    return df.insertImpl(dstPath, column).move { dstPath }.after { columnPath }
}

// endregion

// region before

/**
 * Inserts the new column previously specified with [insert]
 * at the position immediately before the selected [column] (on the same level).
 *
 * For more information: {@include [DocumentationUrls.Insert]}
 *
 * See [Grammar][InsertDocs.Grammar] for more details.
 *
 * See also: [SelectingColumns.Dsl].
 *
 * ### Examples:
 * ```kotlin
 * // Insert a new column "age" before the "name" column
 * df.insert(age).before { name }
 *
 * // Insert a new column "sum" before the nested "min" column (inside the "stats" column group)
 * val dfWithSum = df.insert("sum") { a + b }.before { stats.min }
 * ```
 *
 * @param [column] The [ColumnSelector] used to choose an existing column in this [DataFrame],
 * before which the new column will be inserted.
 * @return A new [DataFrame] with the inserted column placed before the selected column.
 */
@[Refine Interpretable("InsertBefore0")]
public fun <T> InsertClause<T>.before(column: ColumnSelector<T, *>): DataFrame<T> = beforeImpl(df.getColumnPath(column))

/**
 * Inserts the new column previously specified with [insert]
 * at the position immediately before the column with the given [name][column].
 *
 * For more information: {@include [DocumentationUrls.Insert]}
 *
 * See [Grammar][InsertDocs.Grammar] for more details.
 *
 * See also: [SelectingColumns.ColumnNames].
 *
 * ### Example
 * ```kotlin
 * // Insert a new column "age" before the "name" column
 * df.insert(age).before("name")
 * ```
 *
 * @param [column] The [String] name of the column in this [DataFrame]
 * before which the new column will be inserted.
 * @return A new [DataFrame] with the inserted column placed before the specified column.
 */
public fun <T> InsertClause<T>.before(column: String): DataFrame<T> =
    df.add(this.column).move(this.column).before(column)

// endregion

// region at

/**
 * Inserts the new column previously specified with [insert]
 * at the given [position] in the [DataFrame].
 *
 * The new column will be placed at the specified index, shifting existing columns to the right.
 *
 * For more information: {@include [DocumentationUrls.Insert]}
 *
 * See [Grammar][InsertDocs.Grammar] for more details.
 *
 * ### Example
 * ```kotlin
 * // Insert a new column "age" at index 3
 * df.insert(age).at(3)
 * ```
 *
 * @param [position] The [Int] index where the new column should be inserted.
 *                 Columns currently at this index and after will be shifted right.
 * @return A new [DataFrame] with the inserted column placed at the specified position.
 */
@[Refine Interpretable("InsertAt")]
public fun <T> InsertClause<T>.at(position: Int): DataFrame<T> = df.add(column).move(column).to(position)

// endregion

// endregion
