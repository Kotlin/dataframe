package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.afterOrBefore
import org.jetbrains.kotlinx.dataframe.impl.api.moveImpl
import org.jetbrains.kotlinx.dataframe.impl.api.moveTo
import org.jetbrains.kotlinx.dataframe.impl.api.moveToImpl
import org.jetbrains.kotlinx.dataframe.ncol
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.MOVE_TO_LEFT
import org.jetbrains.kotlinx.dataframe.util.MOVE_TO_LEFT_REPLACE
import org.jetbrains.kotlinx.dataframe.util.MOVE_TO_RIGHT
import org.jetbrains.kotlinx.dataframe.util.MOVE_TO_RIGHT_REPLACE
import org.jetbrains.kotlinx.dataframe.util.TO_LEFT
import org.jetbrains.kotlinx.dataframe.util.TO_LEFT_REPLACE
import org.jetbrains.kotlinx.dataframe.util.TO_RIGHT
import org.jetbrains.kotlinx.dataframe.util.TO_RIGHT_REPLACE
import kotlin.reflect.KProperty

// region DataFrame

// region move

/**
 * Moves the specified [columns] within the [<code>DataFrame</code>][DataFrame].
 *
 * This function does not immediately move the columns but instead select columns to move and
 * returns a [<code>MoveClause</code>][MoveClause],
 * which serves as an intermediate step. The [<code>MoveClause</code>][MoveClause] allows specifying the final
 * destination of the selected columns using methods such as [<code>to</code>][MoveClause.to], [<code>toStart</code>][MoveClause.toStart],
 * [<code>toEnd</code>][MoveClause.toEnd], [<code>into</code>][MoveClause.into], [<code>intoIndexed</code>][MoveClause.intoIndexed], [<code>toTop</code>][MoveClause.toTop],
 * [<code>after</code>][MoveClause.after] or [<code>under</code>][MoveClause.under], that return a new [<code>DataFrame</code>][DataFrame] with updated columns structure.
 *
 * Check out [<code>Grammar</code>][Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MoveSelectingOptions].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 */
internal interface Move {

    /**
     *
     *
     *
     * ## Selecting Columns
     *
     * Selecting columns for various [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] operations
     * can be done in the following ways:
     * ### 1. [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
     *
     *
     *
     *
     * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
     * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
     * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     *
     * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
     * for specifying columns type- and name-safe.
     *
     * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>move</code>][org.jetbrains.kotlinx.dataframe.api.move]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>move</code>][org.jetbrains.kotlinx.dataframe.api.move]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>move</code>][org.jetbrains.kotlinx.dataframe.api.move]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     *
     *
     * > There's also a 'single column' variant used sometimes: [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
     * ### 2. [<code>Column names</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
     *
     *
     *
     *
     * Select single or multiple columns using their names as [<code>String</code>][String]s.
     * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>move</code>][org.jetbrains.kotlinx.dataframe.api.move]`("length", "age")`
     *
     *
     *
     */
    typealias MoveSelectingOptions = Nothing

    /**
     * ## Move Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * **[<code>`move`</code>][move]****`  {  `**`columnsSelector: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code>**`into`**</code>][MoveClause.into]**`  {  `**`targetColumnPaths: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**`  }  `**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`intoIndexed`**</code>][MoveClause.intoIndexed]**`  {  `**`targetColumnPaths: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`under`**</code>][MoveClause.under]**`  {  `**`parentColumnGroupPath: `[<code>`ColumnSelector`</code>][ColumnSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`after`**</code>][MoveClause.after]**`  {  `**`column: `[<code>`ColumnSelector`</code>][ColumnSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`to`**</code>][MoveClause.to]**`(`**`position: `[<code>`Int`</code>][Int]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toTop`**</code>][MoveClause.toTop]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toStart`**</code>][MoveClause.toStart]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`toEnd`**</code>][MoveClause.toEnd]**`()`**
     */
    typealias Grammar = Nothing
}

/**
 * Moves the specified [columns] within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function does not immediately move the columns but instead select columns to move and
 * returns a [<code>MoveClause</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause],
 * which serves as an intermediate step. The [<code>MoveClause</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause] allows specifying the final
 * destination of the selected columns using methods such as [<code>to</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.to], [<code>toStart</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.toStart],
 * [<code>toEnd</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.toEnd], [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.into], [<code>intoIndexed</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.intoIndexed], [<code>toTop</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.toTop],
 * [<code>after</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.after] or [<code>under</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.under], that return a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with updated columns structure.
 *
 * Check out [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.Move.Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Move.MoveSelectingOptions].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 * ### This Move Overload
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 * ### Examples:
 * ```kotlin
 * df.move { columnA and columnB }.after { columnC }
 * df.move { cols(0..3) }.under("info")
 * df.move { colsOf<String>() }.to(5)
 * ```
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to move.
 */
@Interpretable("Move0")
public fun <T, C> DataFrame<T>.move(columns: ColumnsSelector<T, C>): MoveClause<T, C> = MoveClause(this, columns)

/**
 * Moves the specified [columns] within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * This function does not immediately move the columns but instead select columns to move and
 * returns a [<code>MoveClause</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause],
 * which serves as an intermediate step. The [<code>MoveClause</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause] allows specifying the final
 * destination of the selected columns using methods such as [<code>to</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.to], [<code>toStart</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.toStart],
 * [<code>toEnd</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.toEnd], [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.into], [<code>intoIndexed</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.intoIndexed], [<code>toTop</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.toTop],
 * [<code>after</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.after] or [<code>under</code>][org.jetbrains.kotlinx.dataframe.api.MoveClause.under], that return a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with updated columns structure.
 *
 * Check out [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.Move.Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.Move.MoveSelectingOptions].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 * ### This Move Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * ### Examples:
 * ```kotlin
 * df.move("columnA", "columnB").after("columnC")
 * df.move("age").under("info")
 * ```
 * @param [columns] The [<code>Column Names</code>][String] used to select the columns of this [<code>DataFrame</code>][DataFrame] to move.
 */
public fun <T> DataFrame<T>.move(vararg columns: String): MoveClause<T, Any?> = move { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.move(vararg columns: ColumnReference<C>): MoveClause<T, C> =
    move { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.move(vararg columns: KProperty<C>): MoveClause<T, C> = move { columns.toColumnSet() }

// endregion

// region moveTo

/**
 * Moves the specified [columns] to a new position specified by
 * [<code>newColumnIndex</code>][newColumnIndex] within the [<code>DataFrame</code>][DataFrame].
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with updated columns structure.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MoveToSelectingOptions].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 */
internal interface MoveTo {
    /**
     *
     *
     *
     * ## Selecting Columns
     *
     * Selecting columns for various [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] operations
     * can be done in the following ways:
     * ### 1. [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
     *
     *
     *
     *
     * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
     * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
     * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     *
     * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
     * for specifying columns type- and name-safe.
     *
     * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>moveTo</code>][org.jetbrains.kotlinx.dataframe.api.moveTo]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>moveTo</code>][org.jetbrains.kotlinx.dataframe.api.moveTo]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>moveTo</code>][org.jetbrains.kotlinx.dataframe.api.moveTo]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     *
     *
     * > There's also a 'single column' variant used sometimes: [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
     * ### 2. [<code>Column names</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
     *
     *
     *
     *
     * Select single or multiple columns using their names as [<code>String</code>][String]s.
     * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>moveTo</code>][org.jetbrains.kotlinx.dataframe.api.moveTo]`("length", "age")`
     *
     *
     *
     */
    typealias MoveToSelectingOptions = Nothing
}

/**
 * Moves the specified [columns] to a new position specified by
 * [<code>newColumnIndex</code>][newColumnIndex] within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with updated columns structure.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MoveTo.MoveToSelectingOptions].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 * ### This MoveTo Overload
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 * ### Examples:
 * ```kotlin
 * df.moveTo(0) { length and age }
 * df.moveTo(2) { cols(1..5) }
 * ```
 * @param [newColumnIndex] The index specifying the position in the [<code>DataFrame</code>][DataFrame] columns
 * where the selected columns will be moved.
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to move.
 */
@Refine
@Interpretable("MoveTo1")
public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, columns: ColumnsSelector<T, *>): DataFrame<T> =
    move(columns).to(newColumnIndex)

/**
 * Moves the specified [columns] to a new position specified by
 * [<code>newColumnIndex</code>][newColumnIndex] within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with updated columns structure.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MoveTo.MoveToSelectingOptions].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 * ### This MoveTo Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * ### Examples:
 * ```kotlin
 * df.moveTo(0) { length and age }
 * df.moveTo(2) { cols(1..5) }
 * ```
 * @param [newColumnIndex] The index specifying the position in the [<code>DataFrame</code>][DataFrame] columns
 * where the selected columns will be moved.
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to move.
 */
public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg columns: String): DataFrame<T> =
    moveTo(newColumnIndex) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg columns: AnyColumnReference): DataFrame<T> =
    moveTo(newColumnIndex) { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.moveTo(newColumnIndex: Int, vararg columns: KProperty<*>): DataFrame<T> =
    moveTo(newColumnIndex) { columns.toColumnSet() }

/**
 * Moves the specified [columns] to a new position specified
 * by [<code>newColumnIndex</code>][newColumnIndex]. If [<code>insideGroup</code>][insideGroup] is true selected columns
 * will be moved remaining within their [<code>ColumnGroup</code>][ColumnGroup],
 * else they will be moved to the top level.
 *
 * Moves the specified [columns] to a new position specified by
 * [<code>newColumnIndex</code>][newColumnIndex] within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with updated columns structure.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MoveTo.MoveToSelectingOptions].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 * ### This MoveTo Overload
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 * ### Examples:
 * ```kotlin
 * df.moveTo(0, true) { length and age }
 * df.moveTo(2, false) { cols(1..5) }
 * ```
 * @param [newColumnIndex] The index specifying the position in the [<code>DataFrame</code>][DataFrame] columns
 * where the selected columns will be moved.
 * @param [insideGroup] If true, selected columns will be moved remaining inside their group,
 * else they will be moved to the top level.
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to move.
 */
@Refine
@Interpretable("MoveTo1")
public fun <T> DataFrame<T>.moveTo(
    newColumnIndex: Int,
    insideGroup: Boolean,
    columns: ColumnsSelector<T, *>,
): DataFrame<T> = move(columns).to(newColumnIndex, insideGroup)

// endregion

// region moveToStart

/**
 * Moves the specified [columns] to the [<code>DataFrame</code>][DataFrame] start (on top-level).
 * Returns a new [<code>DataFrame</code>][DataFrame] with updated columns structure.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MoveToStartSelectingOptions].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 */
internal interface MoveToStart {
    /**
     *
     *
     *
     * ## Selecting Columns
     *
     * Selecting columns for various [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] operations
     * can be done in the following ways:
     * ### 1. [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
     *
     *
     *
     *
     * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
     * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
     * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     *
     * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
     * for specifying columns type- and name-safe.
     *
     * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>moveToStart</code>][org.jetbrains.kotlinx.dataframe.api.moveToStart]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>moveToStart</code>][org.jetbrains.kotlinx.dataframe.api.moveToStart]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>moveToStart</code>][org.jetbrains.kotlinx.dataframe.api.moveToStart]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     *
     *
     * > There's also a 'single column' variant used sometimes: [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
     * ### 2. [<code>Column names</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
     *
     *
     *
     *
     * Select single or multiple columns using their names as [<code>String</code>][String]s.
     * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>moveToStart</code>][org.jetbrains.kotlinx.dataframe.api.moveToStart]`("length", "age")`
     *
     *
     *
     */
    typealias MoveToStartSelectingOptions = Nothing
}

@Deprecated(MOVE_TO_LEFT, ReplaceWith(MOVE_TO_LEFT_REPLACE), DeprecationLevel.ERROR)
public fun <T> DataFrame<T>.moveToLeft(columns: ColumnsSelector<T, *>): DataFrame<T> = move(columns).toStart()

/**
 * Moves the specified [columns] to the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] start (on top-level).
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with updated columns structure.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MoveToStart.MoveToStartSelectingOptions].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 * ### This MoveToStart Overload
 *
 *
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>moveToStart</code>][org.jetbrains.kotlinx.dataframe.api.moveToStart]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>moveToStart</code>][org.jetbrains.kotlinx.dataframe.api.moveToStart]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>moveToStart</code>][org.jetbrains.kotlinx.dataframe.api.moveToStart]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to move.
 */
@Refine
@Interpretable("MoveToStart1")
public fun <T> DataFrame<T>.moveToStart(columns: ColumnsSelector<T, *>): DataFrame<T> = move(columns).toStart()

/**
 * Moves the specified [columns] to the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] start (on top-level).
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with updated columns structure.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MoveToStart.MoveToStartSelectingOptions].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 * ### This MoveToStart Overload
 *
 *
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>moveToStart</code>][org.jetbrains.kotlinx.dataframe.api.moveToStart]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>moveToStart</code>][org.jetbrains.kotlinx.dataframe.api.moveToStart]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>moveToStart</code>][org.jetbrains.kotlinx.dataframe.api.moveToStart]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to move.
 * @param [insideGroup] If true, selected columns will be moved to the start remaining inside their group,
 * else they will be moved to the start of the top level.
 */
@Refine
@Interpretable("MoveToStart1")
public fun <T> DataFrame<T>.moveToStart(insideGroup: Boolean, columns: ColumnsSelector<T, *>): DataFrame<T> =
    move(columns).toStart(insideGroup)

@Deprecated(MOVE_TO_LEFT, ReplaceWith(MOVE_TO_LEFT_REPLACE), DeprecationLevel.ERROR)
public fun <T> DataFrame<T>.moveToLeft(vararg columns: String): DataFrame<T> = moveToStart { columns.toColumnSet() }

/**
 * Moves the specified [columns] to the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] start (on top-level).
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with updated columns structure.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MoveToStart.MoveToStartSelectingOptions].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 * ### This MoveToStart Overload
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>moveToStart</code>][org.jetbrains.kotlinx.dataframe.api.moveToStart]`("length", "age")`
 *
 *
 *
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to move.
 */
public fun <T> DataFrame<T>.moveToStart(vararg columns: String): DataFrame<T> = moveToStart { columns.toColumnSet() }

@Deprecated(MOVE_TO_LEFT, ReplaceWith(MOVE_TO_LEFT_REPLACE), DeprecationLevel.ERROR)
@AccessApiOverload
public fun <T> DataFrame<T>.moveToLeft(vararg columns: AnyColumnReference): DataFrame<T> =
    moveToStart { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.moveToStart(vararg columns: AnyColumnReference): DataFrame<T> =
    moveToStart { columns.toColumnSet() }

@Deprecated(MOVE_TO_LEFT, ReplaceWith(MOVE_TO_LEFT_REPLACE), DeprecationLevel.ERROR)
@AccessApiOverload
public fun <T> DataFrame<T>.moveToLeft(vararg columns: KProperty<*>): DataFrame<T> =
    moveToStart { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.moveToStart(vararg columns: KProperty<*>): DataFrame<T> =
    moveToStart { columns.toColumnSet() }

// endregion

// region moveToEnd

/**
 * Moves the specified [columns] to the [<code>DataFrame</code>][DataFrame] end.
 * Returns a new [<code>DataFrame</code>][DataFrame] with updated columns structure.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][MoveToEndSelectingOptions].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 */
internal interface MoveToEnd {
    /**
     *
     *
     *
     * ## Selecting Columns
     *
     * Selecting columns for various [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] operations
     * can be done in the following ways:
     * ### 1. [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
     *
     *
     *
     *
     * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
     * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
     * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     *
     * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
     * for specifying columns type- and name-safe.
     *
     * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>moveToEnd</code>][org.jetbrains.kotlinx.dataframe.api.moveToEnd]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>moveToEnd</code>][org.jetbrains.kotlinx.dataframe.api.moveToEnd]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>moveToEnd</code>][org.jetbrains.kotlinx.dataframe.api.moveToEnd]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     *
     *
     * > There's also a 'single column' variant used sometimes: [<code>Column Selection DSL</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
     * ### 2. [<code>Column names</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
     *
     *
     *
     *
     * Select single or multiple columns using their names as [<code>String</code>][String]s.
     * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[<code>moveToEnd</code>][org.jetbrains.kotlinx.dataframe.api.moveToEnd]`("length", "age")`
     *
     *
     *
     */
    typealias MoveToEndSelectingOptions = Nothing
}

@Deprecated(MOVE_TO_RIGHT, ReplaceWith(MOVE_TO_RIGHT_REPLACE), DeprecationLevel.ERROR)
public fun <T> DataFrame<T>.moveToRight(columns: ColumnsSelector<T, *>): DataFrame<T> = move(columns).toEnd()

/**
 * Moves the specified [columns] to the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] end.
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with updated columns structure.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MoveToEnd.MoveToEndSelectingOptions].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 * ### This MoveToEnd Overload
 *
 *
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>moveToEnd</code>][org.jetbrains.kotlinx.dataframe.api.moveToEnd]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>moveToEnd</code>][org.jetbrains.kotlinx.dataframe.api.moveToEnd]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>moveToEnd</code>][org.jetbrains.kotlinx.dataframe.api.moveToEnd]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to move.
 */
@Refine
@Interpretable("MoveToEnd1")
public fun <T> DataFrame<T>.moveToEnd(columns: ColumnsSelector<T, *>): DataFrame<T> = move(columns).toEnd()

/**
 * Moves the specified [columns] to the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] end.
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with updated columns structure.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MoveToEnd.MoveToEndSelectingOptions].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 * ### This MoveToEnd Overload
 *
 *
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>moveToEnd</code>][org.jetbrains.kotlinx.dataframe.api.moveToEnd]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>moveToEnd</code>][org.jetbrains.kotlinx.dataframe.api.moveToEnd]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>moveToEnd</code>][org.jetbrains.kotlinx.dataframe.api.moveToEnd]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to move.
 * @param [insideGroup] If true, selected columns will be moved to the end remaining inside their group,
 * else they will be moved to the end of the top level.
 */
@Refine
@Interpretable("MoveToEnd1")
public fun <T> DataFrame<T>.moveToEnd(insideGroup: Boolean, columns: ColumnsSelector<T, *>): DataFrame<T> =
    move(columns).toEnd(insideGroup)

@Deprecated(MOVE_TO_RIGHT, ReplaceWith(MOVE_TO_RIGHT_REPLACE), DeprecationLevel.ERROR)
public fun <T> DataFrame<T>.moveToRight(vararg columns: String): DataFrame<T> = moveToEnd { columns.toColumnSet() }

/**
 * Moves the specified [columns] to the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] end.
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with updated columns structure.
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.MoveToEnd.MoveToEndSelectingOptions].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 * ### This MoveToEnd Overload
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>moveToEnd</code>][org.jetbrains.kotlinx.dataframe.api.moveToEnd]`("length", "age")`
 *
 *
 *
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to move.
 */
public fun <T> DataFrame<T>.moveToEnd(vararg columns: String): DataFrame<T> = moveToEnd { columns.toColumnSet() }

@Deprecated(MOVE_TO_RIGHT, ReplaceWith(MOVE_TO_RIGHT_REPLACE), DeprecationLevel.ERROR)
@AccessApiOverload
public fun <T> DataFrame<T>.moveToRight(vararg columns: AnyColumnReference): DataFrame<T> =
    moveToEnd { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.moveToEnd(vararg columns: AnyColumnReference): DataFrame<T> =
    moveToEnd { columns.toColumnSet() }

@Deprecated(MOVE_TO_RIGHT, ReplaceWith(MOVE_TO_RIGHT_REPLACE), DeprecationLevel.ERROR)
@AccessApiOverload
public fun <T> DataFrame<T>.moveToRight(vararg columns: KProperty<*>): DataFrame<T> =
    moveToEnd { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.moveToEnd(vararg columns: KProperty<*>): DataFrame<T> = moveToEnd { columns.toColumnSet() }

// endregion

// endregion

// region MoveClause

// region into

/**
 * Moves columns, previously selected with [<code>move</code>][move] into a new position specified by a
 * given column path within the [<code>DataFrame</code>][DataFrame].
 *
 *
 *
 * If the specified path is partially or fully missing — that is, if any segment of the path
 * does not correspond to an existing column or column group — all missing parts will be created automatically.
 *
 * See [<code>Selecting Columns</code>][SelectingColumns].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 *
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Examples:
 * ```kotlin
 * df.move { age and weight }.into { pathOf("info", it.name()) }
 * df.move { age and weight }.into { "info"[it.name()] }
 * df.move { name.firstName and name.lastName }.into { pathOf("fullName", it.name().dropLast(4)) }
 * ```
 *
 * @param [column] The [<code>Column With Path Selector</code>][ColumnsSelector] used to specify
 * a path in the [<code>DataFrame</code>][DataFrame] to move columns.
 */
public fun <T, C> MoveClause<T, C>.into(
    column: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> AnyColumnReference,
): DataFrame<T> =
    moveImpl(
        under = false,
        newPathExpression = column,
    )

/**
 * Moves the selected column, previously specified with [<code>move</code>][move],
 * to the top level of the [<code>DataFrame</code>][DataFrame] and assigns it a new name.
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 *
 * ### Example:
 * ```kotlin
 * // Move "info"."salary" column to the top level with a new name "income"
 * df.move { info.salary }.into("income")
 * ```
 *
 * @param column The new [<code>String</code>][String] name of the column after the move.
 * @return A new [<code>DataFrame</code>][DataFrame] with the column moved and renamed.
 */
@Refine
@Interpretable("MoveInto0")
public fun <T, C> MoveClause<T, C>.into(column: String): DataFrame<T> = pathOf(column).let { path -> into { path } }

/**
 * Moves columns, previously selected with [<code>move</code>][move] into a new position specified by a
 * given column path within the [<code>DataFrame</code>][DataFrame].
 * Provides selected column indices.
 *
 *
 *
 * If the specified path is partially or fully missing — that is, if any segment of the path
 * does not correspond to an existing column or column group — all missing parts will be created automatically.
 *
 * See [<code>Selecting Columns</code>][SelectingColumns].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 *
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Examples:
 * ```kotlin
 * df.move { cols { it.name == "user" } }
 *    .intoIndexed { it, index -> "allUsers"["user$index"] }
 * ```
 *
 * @param [column] The [<code>Column With Path Selector And Indices</code>][ColumnsSelector] used to specify
 * a path in the [<code>DataFrame</code>][DataFrame] to move columns.
 */
public fun <T, C> MoveClause<T, C>.intoIndexed(
    newPathExpression: ColumnsSelectionDsl<T>.(ColumnWithPath<C>, Int) -> AnyColumnReference,
): DataFrame<T> {
    var counter = 0
    return into { col ->
        newPathExpression(this, col, counter++)
    }
}

// endregion

// region under

/**
 * Moves columns, previously selected with [<code>move</code>][move] under a new or
 * an existing column group within the [<code>DataFrame</code>][DataFrame].
 * If the column group doesn't exist, it will be created.
 *
 * See [<code>Selecting Columns</code>][SelectingColumns].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * ### Examples:
 * ```kotlin
 * df.move("age", "weight").under("info")
 * df.move { age and weight }.under("info")
 * ```
 *
 * @param [column] A [<code>ColumnsSelector</code>][ColumnsSelector] that defines the path to a [<code>ColumnGroup</code>][ColumnGroup]
 * in the [<code>DataFrame</code>][DataFrame], where the selected columns will be moved.
 */
@Refine
@Interpretable("MoveUnder0")
public fun <T, C> MoveClause<T, C>.under(column: String): DataFrame<T> = under { pathOf(column) }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> MoveClause<T, C>.under(column: AnyColumnGroupAccessor): DataFrame<T> = under { column.path() }

/**
 * Moves columns, previously selected with [<code>move</code>][move] under a new or
 * an existing column group specified by a
 * column path within the [<code>DataFrame</code>][DataFrame].
 *
 *
 *
 * If the specified path is partially or fully missing — that is, if any segment of the path
 * does not correspond to an existing column or column group — all missing parts will be created automatically.
 *
 * See [<code>Selecting Columns</code>][SelectingColumns].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 *
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Examples:
 * ```kotlin
 * // move under an existing column group
 * df.move { age and weight }.under { info }
 * // move under a new column group
 * df.move { age and weight }.under { columnGroup(info) }
 * ```
 *
 * @param [column] The [<code>ColumnsSelector</code>][ColumnsSelector] that defines the path to a [<code>ColumnGroup</code>][ColumnGroup]
 * in the [<code>DataFrame</code>][DataFrame], where the selected columns will be moved.
 */
@Refine
@Interpretable("MoveUnder1")
public fun <T, C> MoveClause<T, C>.under(
    column: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> AnyColumnReference,
): DataFrame<T> =
    moveImpl(
        under = true,
        newPathExpression = column,
    )

// endregion

// region to

/**
 * Moves columns, previously selected with [<code>move</code>][move] to a new position specified
 * by [<code>columnIndex</code>][columnIndex] within the [<code>DataFrame</code>][DataFrame].
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with updated columns structure.
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 *
 * ### Examples:
 * ```kotlin
 * df.move { age and weight }.to(0)
 * df.move("age", "weight").to(2)
 * ```
 *
 * @param [columnIndex] The index specifying the position in the [<code>DataFrame</code>][DataFrame] columns
 *  * where the selected columns will be moved.
 */
@Refine
@Interpretable("MoveTo")
public fun <T, C> MoveClause<T, C>.to(columnIndex: Int): DataFrame<T> = moveTo(columnIndex)

/**
 * Moves columns, previously selected with [<code>move</code>][move] to a new position specified
 * by [<code>columnIndex</code>][columnIndex]. If [<code>insideGroup</code>][insideGroup] is true, selected columns will be moved remaining within their [<code>ColumnGroup</code>][ColumnGroup],
 * else they will be moved to the top level.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with updated columns structure.
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 *
 * ### Examples:
 * ```kotlin
 * df.move { age and weight }.to(0, true)
 * df.move("age", "weight").to(2, false)
 * ```
 *
 * @param [columnIndex] The index specifying the position in the [<code>ColumnGroup</code>][ColumnGroup] columns
 * where the selected columns will be moved.
 *
 * @param [insideGroup] If true, selected columns will be moved remaining inside their group,
 * else they will be moved to the top level.
 */
@Refine
@Interpretable("MoveTo")
public fun <T, C> MoveClause<T, C>.to(columnIndex: Int, insideGroup: Boolean): DataFrame<T> =
    moveToImpl(columnIndex, insideGroup)

/**
 * Moves columns, previously selected with [<code>move</code>][move] to the top-level within the [<code>DataFrame</code>][DataFrame].
 * Moved columns name can be specified via special ColumnSelectionDsl.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with updated columns.
 *
 * See [<code>Selecting Columns</code>][SelectingColumns].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 *
 * ### Examples:
 * ```kotlin
 * df.move { info.age and info.weight }.toTop()
 * df.move { colsAtAnyDepth { it.name() == "number" } }.toTop { it.parentName + it.name() }
 * ```
 *
 * @param [newColumnName] The special [<code>ColumnsSelector</code>][ColumnsSelector] for define name of moved column.
 * Optional, the original name is used by default
 */
@Refine
@Interpretable("ToTop")
public fun <T, C> MoveClause<T, C>.toTop(
    newColumnName: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> String = { it.name() },
): DataFrame<T> = into { newColumnName(it).toColumnAccessor() }

// endregion

// region after

/**
 * Moves columns, previously selected with [<code>move</code>][org.jetbrains.kotlinx.dataframe.api.move] to the position after the
 * specified [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with updated columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 *
 * ### This After Overload
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Examples:
 * ```kotlin
 * df.move { age and weight }.after { surname }
 * df.move { cols(0..2) }.after { col(3) }
 * ```
 *
 * @param [column] A [<code>ColumnSelector</code>][ColumnSelector] specifying the column
 * after which the selected columns will be placed.
 */
@Refine
@Interpretable("MoveAfter0")
public fun <T, C> MoveClause<T, C>.after(column: ColumnSelector<T, *>): DataFrame<T> = afterOrBefore(column, true)

/**
 * Moves columns, previously selected with [<code>move</code>][org.jetbrains.kotlinx.dataframe.api.move] to the position after the
 * specified [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with updated columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 *
 * ### This After Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * ### Examples:
 * ```kotlin
 * df.move("age", "weight").after("surname")
 * ```
 * @param [column] The [<code>Column Name</code>][String] specifying the column
 * after which the selected columns will be placed.
 */
public fun <T, C> MoveClause<T, C>.after(column: String): DataFrame<T> = after { column.toColumnAccessor() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> MoveClause<T, C>.after(column: AnyColumnReference): DataFrame<T> = after { column }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> MoveClause<T, C>.after(column: KProperty<*>): DataFrame<T> = after { column.toColumnAccessor() }

// endregion

// region before

/**
 * Moves columns, previously selected with [<code>move</code>][org.jetbrains.kotlinx.dataframe.api.move] to the position before the
 * specified [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with updated columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 *
 * ### This Before Overload
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Examples:
 * ```kotlin
 * df.move { age and weight }.before { surname }
 * df.move { cols(3..5) }.before { col(2) }
 * ```
 *
 * @param [column] A [<code>ColumnSelector</code>][ColumnSelector] specifying the column
 * before which the selected columns will be placed.
 */
@Refine
@Interpretable("MoveBefore0")
public fun <T, C> MoveClause<T, C>.before(column: ColumnSelector<T, *>): DataFrame<T> = afterOrBefore(column, false)

/**
 * Moves columns, previously selected with [<code>move</code>][org.jetbrains.kotlinx.dataframe.api.move] to the position before the
 * specified [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with updated columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns].
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 *
 * ### This Before Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * ### Examples:
 * ```kotlin
 * df.move("age", "weight").before("surname")
 * ```
 * @param [column] The [<code>Column Name</code>][String] specifying the column
 * before which the selected columns will be placed.
 */
public fun <T, C> MoveClause<T, C>.before(column: String): DataFrame<T> = before { column.toColumnAccessor() }

// endregion

@Deprecated(TO_LEFT, ReplaceWith(TO_LEFT_REPLACE), DeprecationLevel.ERROR)
public fun <T, C> MoveClause<T, C>.toLeft(): DataFrame<T> = to(0)

/**
 * Moves columns, previously selected with [<code>move</code>][move] to the [<code>DataFrame</code>][DataFrame] start (on top-level).
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with updated columns.
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 *
 * ### Examples:
 * ```kotlin
 * df.move { age and weight }.toStart()
 * df.move { colsOf<String>() }.toStart()
 * df.move("age", "weight").toStart()
 * ```
 */
@Refine
@Interpretable("MoveToStart0")
public fun <T, C> MoveClause<T, C>.toStart(): DataFrame<T> = to(0)

/**
 * If insideGroup is true, moves columns previously selected with [<code>move</code>][move] to the start of their [<code>ColumnGroup</code>][ColumnGroup].
 * Else, selected columns will be moved to the start of their [<code>DataFrame</code>][DataFrame] (to the top-level).
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with updated columns.
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 *
 * ### Examples:
 * ```kotlin
 * df.move { age and weight }.toStart(true)
 * df.move { colsOf<String>() }.toStart(true)
 * df.move("age", "weight").toStart(false)
 * ```
 *
 * @param [insideGroup] If true, selected columns will be moved to the start remaining inside their group,
 * else they will be moved to the start on top level.
 */
@Refine
@Interpretable("MoveToStart0")
public fun <T, C> MoveClause<T, C>.toStart(insideGroup: Boolean): DataFrame<T> = to(0, insideGroup)

@Deprecated(TO_RIGHT, ReplaceWith(TO_RIGHT_REPLACE), DeprecationLevel.ERROR)
public fun <T, C> MoveClause<T, C>.toRight(): DataFrame<T> = to(df.ncol)

/**
 * Moves columns, previously selected with [<code>move</code>][move] to the [<code>DataFrame</code>][DataFrame] end.
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with updated columns.
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 *
 * ### Examples:
 * ```kotlin
 * df.move { age and weight }.toEnd()
 * df.move { colsOf<String>() }.toEnd()
 * df.move("age", "weight").toEnd()
 * ```
 */
@Refine
@Interpretable("MoveToEnd0")
public fun <T, C> MoveClause<T, C>.toEnd(): DataFrame<T> = to(df.ncol)

/**
 * If insideGroup is true, moves columns previously selected with [<code>move</code>][move] to the end of their [<code>ColumnGroup</code>][ColumnGroup].
 * Else, selected columns will be moved to the end of their [<code>DataFrame</code>][DataFrame] (to the top-level).
 *
 * Returns a new [<code>DataFrame</code>][DataFrame] with updated columns.
 *
 * For more information: [See `move` on the documentation website.](https://kotlin.github.io/dataframe/move.html)
 *
 * ### Examples:
 * ```kotlin
 * df.move { age and weight }.toEnd(true)
 * df.move { colsOf<String>() }.toEnd(true)
 * df.move("age", "weight").toEnd(false)
 * ```
 *
 * @param [insideGroup] If true, selected columns will be moved to the end remaining inside their group,
 * else they will be moved to the end on top level.
 */
@Refine
@Interpretable("MoveToEnd0")
public fun <T, C> MoveClause<T, C>.toEnd(insideGroup: Boolean): DataFrame<T> = to(df.ncol, insideGroup)

/**
 * An intermediate class used in the [<code>move</code>][move] operation.
 *
 * This class itself does nothing—it is just a transitional step before specifying
 * where to move the selected columns.
 * It must be followed by one of the positioning methods
 * to produce a new [<code>DataFrame</code>][DataFrame] with the updated column structure.
 *
 * Use the following methods to finalize the move:
 * - [<code>to</code>][to] – moves columns to a specific index.
 * - [<code>toStart</code>][toStart] – moves columns to the beginning.
 * - [<code>toEnd</code>][toEnd] – moves columns to the end.
 * - [<code>into</code>][into] / [<code>intoIndexed</code>][intoIndexed] – moves columns to a new position.
 * - [<code>toTop</code>][toTop] – moves columns to the top-level.
 * - [<code>after</code>][after] – places columns after a specific column.
 * - [<code>under</code>][under] – nests columns under a column group.
 *
 * See [<code>Grammar</code>][Move.Grammar] for more details.
 */
public class MoveClause<T, C>(internal val df: DataFrame<T>, internal val columns: ColumnsSelector<T, C>) {
    override fun toString(): String = "MoveClause(df=$df, columns=$columns)"
}

// endregion
