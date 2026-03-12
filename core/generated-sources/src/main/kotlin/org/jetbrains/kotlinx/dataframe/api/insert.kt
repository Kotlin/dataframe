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
import org.jetbrains.kotlinx.dataframe.columns.UnresolvedColumnsPolicy
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
import org.jetbrains.kotlinx.dataframe.impl.getColumnPaths
import org.jetbrains.kotlinx.dataframe.impl.removeAt
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.INSERT_AFTER_COL_PATH
import org.jetbrains.kotlinx.dataframe.util.INSERT_AFTER_COL_PATH_REPLACE
import org.jetbrains.kotlinx.dataframe.util.INSERT_UNDER
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
 *
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][InsertSelectingOptions].
 *
 * For more information: [See `insert` on the documentation website.](https://kotlin.github.io/dataframe/insert.html)
 *
 * See also:
 * - [move][DataFrame.move] - move columns to a new position within the [DataFrame].
 * - [add][DataFrame.add] - add new columns to the [DataFrame]
 * (without specifying a position, to the end of the [DataFrame]).
 */
internal interface InsertDocs {

    /**
     *
     *
     *
     * ## Selecting Columns
     *
     * Selecting columns for various [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] operations
     * can be done in the following ways:
     * ### 1. [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
     *
     *
     *
     *
     * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
     * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
     * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     *
     * Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[insert][org.jetbrains.kotlinx.dataframe.api.insert]` { length `[and][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[insert][org.jetbrains.kotlinx.dataframe.api.insert]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[insert][org.jetbrains.kotlinx.dataframe.api.insert]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
     *
     *
     *
     * > There's also a 'single column' variant used sometimes: [Column Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
     * ### 2. [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
     *
     *
     *
     *
     * Select single or multiple columns using their names as [String]s.
     * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[insert][org.jetbrains.kotlinx.dataframe.api.insert]`("length", "age")`
     *
     *
     *
     */
    typealias InsertSelectingOptions = Nothing

    /**
     * ## Insert Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [**`insert`**][insert]**`(`**`column: `[`DataColumn`][DataColumn]**`)`**` /`
     *
     * [**`insert`**][insert]**`(`**`name: `[`String`][String]**`, `**`infer: `[`Infer`][Infer]**`) { `**`rowExpression: `[`RowExpression`][RowExpression]**`  }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[**`under`**][InsertClause.under]**`  {  `**`column: `[`ColumnSelector`][ColumnSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`after`**][InsertClause.after]**`  {  `**`column: `[`ColumnSelector`][ColumnSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[**`at`**][InsertClause.at]**`(`**`position: `[`Int`][Int]**`)`**
     */
    typealias Grammar = Nothing
}

/**
 * Inserts the given [column] into this [DataFrame].
 *
 * This function does not immediately insert the new column but instead specify a column to insert and
 * returns an [InsertClause][org.jetbrains.kotlinx.dataframe.api.InsertClause],
 * which serves as an intermediate step.
 * The [InsertClause][org.jetbrains.kotlinx.dataframe.api.InsertClause] object provides methods to insert a new column using:
 * - [under][org.jetbrains.kotlinx.dataframe.api.InsertClause.under] - inserts a new column under the specified column group.
 * - [after][org.jetbrains.kotlinx.dataframe.api.InsertClause.after] - inserts a new column after the specified column.
 * - [at][org.jetbrains.kotlinx.dataframe.api.InsertClause.at]- inserts a new column at the specified position.
 *
 * Each method returns a new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the inserted column.
 *
 * Check out [Grammar][org.jetbrains.kotlinx.dataframe.api.InsertDocs.Grammar].
 *
 *
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.InsertDocs.InsertSelectingOptions].
 *
 * For more information: [See `insert` on the documentation website.](https://kotlin.github.io/dataframe/insert.html)
 *
 * See also:
 * - [move][org.jetbrains.kotlinx.dataframe.DataFrame.move] - move columns to a new position within the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [add][org.jetbrains.kotlinx.dataframe.DataFrame.add] - add new columns to the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * (without specifying a position, to the end of the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]).
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
 * With an [AddExpression][org.jetbrains.kotlinx.dataframe.api.AddExpression], you define the value that each row in the new column should have.
 * This can be based on values from the same row in the original [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * You can also use functions like [prev][org.jetbrains.kotlinx.dataframe.api.prev] and [next][org.jetbrains.kotlinx.dataframe.api.next] to access other rows, and combine them with
 * [newValue][org.jetbrains.kotlinx.dataframe.api.AddDataRow.newValue] to reference values already computed in the new column.
 * For example, use `prev().newValue()` to access the new column value from the previous row.
 *
 * This function does not immediately insert the new column but instead specify a column to insert and
 * returns an [InsertClause][org.jetbrains.kotlinx.dataframe.api.InsertClause],
 * which serves as an intermediate step.
 * The [InsertClause][org.jetbrains.kotlinx.dataframe.api.InsertClause] object provides methods to insert a new column using:
 * - [under][org.jetbrains.kotlinx.dataframe.api.InsertClause.under] - inserts a new column under the specified column group.
 * - [after][org.jetbrains.kotlinx.dataframe.api.InsertClause.after] - inserts a new column after the specified column.
 * - [at][org.jetbrains.kotlinx.dataframe.api.InsertClause.at]- inserts a new column at the specified position.
 *
 * Each method returns a new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with the inserted column.
 *
 * Check out [Grammar][org.jetbrains.kotlinx.dataframe.api.InsertDocs.Grammar].
 *
 *
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.InsertDocs.InsertSelectingOptions].
 *
 * For more information: [See `insert` on the documentation website.](https://kotlin.github.io/dataframe/insert.html)
 *
 * See also:
 * - [move][org.jetbrains.kotlinx.dataframe.DataFrame.move] - move columns to a new position within the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame].
 * - [add][org.jetbrains.kotlinx.dataframe.DataFrame.add] - add new columns to the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]
 * (without specifying a position, to the end of the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame]).
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

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public inline fun <T, reified R> DataFrame<T>.insert(
    column: ColumnAccessor<R>,
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): InsertClause<T> = insert(column.name(), infer, expression)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
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
 * This works for existing column groups, as well as for ones that don't exist yet;
 * in that case, they will be created.
 *
 * For more information: [See `insert` on the documentation website.](https://kotlin.github.io/dataframe/insert.html)
 *
 * See [Grammar][InsertDocs.Grammar] for more details.
 *
 * See [SelectingColumns.ColumnsSelectionDsl].
 *
 * ### Examples
 * ```kotlin
 * // Insert a new column "age" under the column group with path ("info", "personal")
 * df.insert(age).under { info.personal }
 *
 * // Insert a new column "age" under the previously-nonexistent column group with path ("info", "extraData")
 * df.insert(age).under { info.colGroup("extraData") }
 *
 * // Insert a new column "sum" under the only top-level column group
 * val dfWithSum = df.insert("sum") { a + b }.under { colGroups().single() }
 * ```
 *
 * @param column The [ColumnSelector] used to choose a new or existing column group in this [DataFrame]
 * under which the new column will be inserted.
 * @return A new [DataFrame] with the inserted column placed under the selected group.
 */
@Refine
@Interpretable("Under0")
public fun <T> InsertClause<T>.under(column: ColumnSelector<T, *>): DataFrame<T> {
    val parentPath = df.getColumnPaths(UnresolvedColumnsPolicy.Create, column).single()
    return df.insertImpl(
        path = parentPath + this.column.name,
        column = this.column,
    )
}

/**
 * __DEPRECATED__:
 *
 * Use `insert {}.under { columnPath }` instead.
 *
 * See [Grammar][InsertDocs.Grammar] for more details.
 */
@Deprecated(
    message = INSERT_UNDER,
    replaceWith = ReplaceWith("this.under { columnPath }"),
    level = DeprecationLevel.ERROR,
)
@Refine
@Interpretable("Under1")
public fun <T> InsertClause<T>.under(columnPath: ColumnPath): DataFrame<T> = under { columnPath }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> InsertClause<T>.under(column: ColumnAccessor<*>): DataFrame<T> = under { column }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> InsertClause<T>.under(column: KProperty<*>): DataFrame<T> = under(column.columnName)

/**
 * Inserts the new column previously specified with [insert] under
 * the given column group by its [name][column].
 *
 * If the column group with the provided [name][column] does not exist, it will be created automatically.
 *
 * For more information: [See `insert` on the documentation website.](https://kotlin.github.io/dataframe/insert.html)
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
@Refine
@Interpretable("Under4")
public fun <T> InsertClause<T>.under(column: String): DataFrame<T> = under { pathOf(column) }

// endregion

// region after

/**
 * Inserts the new column previously specified with [insert]
 * at the position immediately after the selected [column] (on the same level).
 *
 * For more information: [See `insert` on the documentation website.](https://kotlin.github.io/dataframe/insert.html)
 *
 * See [Grammar][InsertDocs.Grammar] for more details.
 *
 * See [Column Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl].
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
@Refine
@Interpretable("InsertAfter0")
public fun <T> InsertClause<T>.after(column: ColumnSelector<T, *>): DataFrame<T> = afterImpl(df.getColumnPath(column))

/**
 * Inserts the new column previously specified with [insert]
 * at the position immediately after the column with the given [name][column].
 *
 * For more information: [See `insert` on the documentation website.](https://kotlin.github.io/dataframe/insert.html)
 *
 * See [Grammar][InsertDocs.Grammar] for more details.
 *
 * See also: [SelectingColumns.ColumnNamesApi].
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

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> InsertClause<T>.after(column: ColumnAccessor<*>): DataFrame<T> = afterImpl(column.path())

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
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
 * For more information: [See `insert` on the documentation website.](https://kotlin.github.io/dataframe/insert.html)
 *
 * See [Grammar][InsertDocs.Grammar] for more details.
 *
 * See also: [SelectingColumns.ColumnsSelectionDsl].
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
@Refine
@Interpretable("InsertBefore0")
public fun <T> InsertClause<T>.before(column: ColumnSelector<T, *>): DataFrame<T> = beforeImpl(df.getColumnPath(column))

/**
 * Inserts the new column previously specified with [insert]
 * at the position immediately before the column with the given [name][column].
 *
 * For more information: [See `insert` on the documentation website.](https://kotlin.github.io/dataframe/insert.html)
 *
 * See [Grammar][InsertDocs.Grammar] for more details.
 *
 * See also: [SelectingColumns.ColumnNamesApi].
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
 * For more information: [See `insert` on the documentation website.](https://kotlin.github.io/dataframe/insert.html)
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
@Refine
@Interpretable("InsertAt")
public fun <T> InsertClause<T>.at(position: Int): DataFrame<T> = df.add(column).move(column).to(position)

// endregion

// endregion
