package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.annotations.StringApiInterpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.columnName
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty

// region DataFrame

/**
 * Groups the specified [columns] within the [<code>DataFrame</code>][DataFrame] into
 * [<code>column group</code>][ColumnGroup].
 *
 * This function does not immediately group the columns but instead select columns to group and
 * returns a [<code>GroupClause</code>][GroupClause],
 * which serves as an intermediate step.
 * The [<code>GroupClause</code>][GroupClause] allows specifying the final
 * destination of the selected columns using methods such
 * as [<code>into</code>][GroupClause.into] and,
 * that return a new [<code>DataFrame</code>][DataFrame] with grouped columns.
 * Check out [<code>Grammar</code>][Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][GroupSelectingOptions].
 *
 * For more information: [See `group` on the documentation website.](https://kotlin.github.io/dataframe/group.html)
 *
 * Reverse operation: [<code>ungroup</code>][ungroup].
 *
 * It is a special case of [<code>move</code>][move] operation.
 *
 * Don't confuse this with [<code>groupBy</code>][groupBy],
 * which groups the dataframe by the values in the selected columns!
 */
internal interface GroupDocs {

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
     * <code>`df`</code>`.`[<code>group</code>][org.jetbrains.kotlinx.dataframe.api.group]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>group</code>][org.jetbrains.kotlinx.dataframe.api.group]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>group</code>][org.jetbrains.kotlinx.dataframe.api.group]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>group</code>][org.jetbrains.kotlinx.dataframe.api.group]`("length", "age")`
     *
     *
     *
     */
    typealias GroupSelectingOptions = Nothing

    /**
     * ## Group Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * **[<code>`group`</code>][group]****`  {  `**`columnsSelector: `[<code>`ColumnsSelector`</code>][ColumnsSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code>**`into`**</code>][GroupClause.into]**`(`**`groupName: `[<code>`String`</code>][String]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code>**`into`**</code>][GroupClause.into]` { column: `[<code>`ColumnsSelectionDsl<T>`</code>][ColumnsSelectionDsl]`.(`[<code>`ColumnWithPath<C>`</code>][ColumnWithPath]`) -> `[<code>`String`</code>][String]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[<code>**`into`**</code>][GroupClause.into]` { column: `[<code>`ColumnsSelectionDsl<T>`</code>][ColumnsSelectionDsl]`.(`[<code>`ColumnWithPath<C>`</code>][ColumnWithPath]`) -> `[<code>`AnyColumnReference`</code>][AnyColumnReference]` }`
     *
     */
    typealias Grammar = Nothing
}

/**
 * Groups the specified [columns] within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] into
 * [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 *
 * This function does not immediately group the columns but instead select columns to group and
 * returns a [<code>GroupClause</code>][org.jetbrains.kotlinx.dataframe.api.GroupClause],
 * which serves as an intermediate step.
 * The [<code>GroupClause</code>][org.jetbrains.kotlinx.dataframe.api.GroupClause] allows specifying the final
 * destination of the selected columns using methods such
 * as [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.GroupClause.into] and,
 * that return a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with grouped columns.
 * Check out [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.GroupDocs.Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.GroupDocs.GroupSelectingOptions].
 *
 * For more information: [See `group` on the documentation website.](https://kotlin.github.io/dataframe/group.html)
 *
 * Reverse operation: [<code>ungroup</code>][org.jetbrains.kotlinx.dataframe.api.ungroup].
 *
 * It is a special case of [<code>move</code>][org.jetbrains.kotlinx.dataframe.api.move] operation.
 *
 * Don't confuse this with [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy],
 * which groups the dataframe by the values in the selected columns!
 * ### This Group Overload
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
 * df.group { columnA and columnB }.into("valueCols")
 * df.group { colsOf<String>() }.into { it.name.split(".").first() }
 * ```
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to group.
 */
@Interpretable("Group0")
public fun <T, C> DataFrame<T>.group(columns: ColumnsSelector<T, C>): GroupClause<T, C> = GroupClause(this, columns)

/**
 * Groups the specified [columns] within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] into
 * [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 *
 * This function does not immediately group the columns but instead select columns to group and
 * returns a [<code>GroupClause</code>][org.jetbrains.kotlinx.dataframe.api.GroupClause],
 * which serves as an intermediate step.
 * The [<code>GroupClause</code>][org.jetbrains.kotlinx.dataframe.api.GroupClause] allows specifying the final
 * destination of the selected columns using methods such
 * as [<code>into</code>][org.jetbrains.kotlinx.dataframe.api.GroupClause.into] and,
 * that return a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] with grouped columns.
 * Check out [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.GroupDocs.Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.GroupDocs.GroupSelectingOptions].
 *
 * For more information: [See `group` on the documentation website.](https://kotlin.github.io/dataframe/group.html)
 *
 * Reverse operation: [<code>ungroup</code>][org.jetbrains.kotlinx.dataframe.api.ungroup].
 *
 * It is a special case of [<code>move</code>][org.jetbrains.kotlinx.dataframe.api.move] operation.
 *
 * Don't confuse this with [<code>groupBy</code>][org.jetbrains.kotlinx.dataframe.api.groupBy],
 * which groups the dataframe by the values in the selected columns!
 * ### This Group Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * ### Example:
 * ```kotlin
 * df.group("second").into("valueCols")
 * df.group("prop.A", "prop.B", "cnt.A", "cnt.B").into { it.name.split(".").first() }
 * ```
 * @param [columns] The [<code>Column Names</code>][String] used to select the columns of this [<code>DataFrame</code>][DataFrame] to group.
 */
@StringApiInterpretable(interpreter = "Group0", stringArgument = "columns", targetArgument = "columns")
public fun <T> DataFrame<T>.group(vararg columns: String): GroupClause<T, Any?> = group { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.group(vararg columns: AnyColumnReference): GroupClause<T, Any?> =
    group { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.group(vararg columns: KProperty<*>): GroupClause<T, Any?> = group { columns.toColumnSet() }

// endregion

// region GroupClause

/**
 * An intermediate class used in the [<code>group</code>][group] operation.
 *
 * This class itself does nothing—it is just a transitional step before specifying
 * how to group the selected columns.
 * It must be followed by one of the positioning methods
 * to produce a new [<code>DataFrame</code>][DataFrame] with the updated column structure.
 *
 * Use the following methods to finalize the move:
 * - [<code>into(groupName)</code>][GroupClause.into] – groups selected columns into a one column group.
 * - [<code>into { groupNameExpression }</code>][GroupClause.into] – groups each column into a group
 * by specifying path or name.
 *
 * See [<code>Grammar</code>][GroupDocs.Grammar] for more details.
 */
public class GroupClause<T, C>(internal val df: DataFrame<T>, internal val columns: ColumnsSelector<T, C>) {
    override fun toString(): String = "GroupClause(df=$df, columns=$columns)"
}

// region into

/**
 * Groups columns, previously selected with [<code>group</code>][group], into new or existing column groups
 * within the [<code>DataFrame</code>][DataFrame], using an [<code>ColumnsSelectionDsl</code>][ColumnsSelectionDsl] expression to specify the target group name for each column.
 * The expression is applied to each selected column and determines the name of the column group
 * it will be placed into.
 *
 * If a column group with the specified name does not exist, it will be created.
 *
 * See [<code>Selecting Columns</code>][SelectingColumns].
 *
 * For more information: [See `group` on the documentation website.](https://kotlin.github.io/dataframe/group.html)
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * ### Example:
 * ```kotlin
 * // For each selected column, place it under its under a group with its type as name (individual for each column):
 * df.group { all() }.into { it.type().toString() }
 * ```
 *
 * @param column A [<code>ColumnsSelector</code>][ColumnsSelector] expression that takes a column and returns the name of the [<code>ColumnGroup</code>][ColumnGroup]
 * where that column should be grouped.
 * All selected columns will be moved under the groups defined by this expression.
 */
@Refine
@JvmName("intoString")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
@Interpretable("IntoStringLambda")
public fun <T, C> GroupClause<T, C>.into(column: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> String): DataFrame<T> =
    df.move(columns).under { column(it).toColumnAccessor() }

/**
 * Groups columns, previously selected with [<code>group</code>][group], into a new or existing column group
 * within the [<code>DataFrame</code>][DataFrame] by specifying its path via [<code>ColumnsSelectionDsl</code>][ColumnsSelectionDsl] expression.
 *
 *
 *
 * If the specified path is partially or fully missing — that is, if any segment of the path
 * does not correspond to an existing column or column group — all missing parts will be created automatically.
 *
 * See [<code>Selecting Columns</code>][SelectingColumns].
 *
 * For more information: [See `group` on the documentation website.](https://kotlin.github.io/dataframe/group.html)
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
 * // Group selected columns into an existing column group (common for all selected columns):
 * df.group("age", "weight").into { info }
 *
 * // Group selected columns into a nested column group using a path (common for all selected columns) that may contain both existing and new segments:
 * df.group { employee.age and employee.weight }.into { pathOf("info", "personal") }
 *
 * // For each selected column, place it under its ancestor group from two levels up in the column path hierarchy (individual for each column):
 * df.group { colsAtAnyDepth().colsOf<String>() }.into { it.path.dropLast(2) }
 * ```
 *
 * @param column A [<code>ColumnsSelector</code>][ColumnsSelector] expression that takes a column and returns the full path to the [<code>ColumnGroup</code>][ColumnGroup]
 * where that column should be grouped.
 * All selected columns will be moved under the groups defined by this expression.
 */
@JvmName("intoColumn")
public fun <T, C> GroupClause<T, C>.into(
    column: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> AnyColumnReference,
): DataFrame<T> = df.move(columns).under(column)

/**
 * Groups columns, previously selected with [<code>group</code>][group], into a new or existing column group
 * within the [<code>DataFrame</code>][DataFrame], by specifying its name.
 *
 * If a column group with the specified name does not exist, it will be created.
 *
 * See [<code>Selecting Columns</code>][SelectingColumns].
 *
 * For more information: [See `group` on the documentation website.](https://kotlin.github.io/dataframe/group.html)
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * ### Examples:
 * ```kotlin
 * df.group("age", "weight").into("info")
 * df.group { age and weight }.into("info")
 * ```
 *
 * @param [column] A [<code>ColumnsSelector</code>][ColumnsSelector] that defines the path to a [<code>ColumnGroup</code>][ColumnGroup]
 * in the [<code>DataFrame</code>][DataFrame], where the selected columns will be moved.
 */
@Refine
@Interpretable("Into0")
public fun <T, C> GroupClause<T, C>.into(column: String): DataFrame<T> = into(columnGroup().named(column))

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> GroupClause<T, C>.into(column: AnyColumnGroupAccessor): DataFrame<T> = df.move(columns).under(column)

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> GroupClause<T, C>.into(column: KProperty<*>): DataFrame<T> = into(column.columnName)

// endregion

// endregion
