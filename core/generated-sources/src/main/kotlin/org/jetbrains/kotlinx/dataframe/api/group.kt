package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
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
 * Groups the specified [columns] within the [DataFrame] into
 * [column group][ColumnGroup].
 *
 * This function does not immediately group the columns but instead select columns to group and
 * returns a [GroupClause],
 * which serves as an intermediate step.
 * The [GroupClause] allows specifying the final
 * destination of the selected columns using methods such
 * as [into][GroupClause.into] and,
 * that return a new [DataFrame] with grouped columns.
 * Check out [Grammar].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][GroupSelectingOptions].
 *
 * For more information: [See `group` on the documentation website.](https://kotlin.github.io/dataframe/group.html)
 *
 * Reverse operation: [ungroup].
 *
 * It is a special case of [move] operation.
 *
 * Don't confuse this with [groupBy],
 * which groups the dataframe by the values in the selected columns!
 */
internal interface GroupDocs {

    /**
     *
     * ## Selecting Columns
     * Selecting columns for various operations (including but not limited to
     * [DataFrame.select][org.jetbrains.kotlinx.dataframe.DataFrame.select], [DataFrame.update][org.jetbrains.kotlinx.dataframe.DataFrame.update], [DataFrame.gather][org.jetbrains.kotlinx.dataframe.DataFrame.gather], and [DataFrame.fillNulls][org.jetbrains.kotlinx.dataframe.DataFrame.fillNulls])
     * can be done in the following ways:
     * ### 1. [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.Dsl.WithExample]
     * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
     * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
     * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
     * This is an entity formed by calling any (combination) of the functions
     * in the DSL that is or can be resolved into one or more columns.
     * This also allows you to use [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs]
     * for type- and name-safe columns selection.
     *
     * #### NOTE:
     * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
     * in this DSL directly with any function, they are NOT valid return types for the
     * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
     * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
     *
     * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[group][org.jetbrains.kotlinx.dataframe.api.group]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[group][org.jetbrains.kotlinx.dataframe.api.group]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[group][org.jetbrains.kotlinx.dataframe.api.group]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
     *
     *
     *
     * #### NOTE: There's also a 'single column' variant used sometimes: [Column Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.DslSingle.WithExample].
     * ### 2. [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNames.WithExample]
     * Select columns using their [column names][String]
     * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
     *
     * #### For example:
     *
     * `df.`[group][org.jetbrains.kotlinx.dataframe.api.group]`("length", "age")`
     *
     * ### 3. [Column references][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnAccessors.WithExample]
     * Select columns using [column accessors][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]
     * ([Column Accessors API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ColumnAccessorsApi]).
     *
     * #### For example:
     *
     * `val length by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
     *
     * `val age by `[column][org.jetbrains.kotlinx.dataframe.api.column]`<`[Double][Double]`>()`
     *
     * `df.`[group][org.jetbrains.kotlinx.dataframe.api.group]`(length, age)`
     *
     * ### 4. [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample]
     * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
     *
     * #### For example:
     * ```kotlin
     * data class Person(val length: Double, val age: Double)
     * ```
     *
     * `df.`[group][org.jetbrains.kotlinx.dataframe.api.group]`(Person::length, Person::age)`
     *
     */
    typealias GroupSelectingOptions = Nothing

    /**
     * ## Group Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * **[`group`][group]****`  {  `**`columnsSelector: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[**`into`**][GroupClause.into]**`(`**`groupName: `[`String`][String]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[**`into`**][GroupClause.into]` { column: `[`ColumnsSelectionDsl<T>`][ColumnsSelectionDsl]`.(`[`ColumnWithPath<C>`][ColumnWithPath]`) -> `[`String`][String]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * __`.`__[**`into`**][GroupClause.into]` { column: `[`ColumnsSelectionDsl<T>`][ColumnsSelectionDsl]`.(`[`ColumnWithPath<C>`][ColumnWithPath]`) -> `[`AnyColumnReference`][AnyColumnReference]` }`
     *
     */
    typealias Grammar = Nothing
}

/**
 * Groups the specified [columns] within the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] into
 * [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 *
 * This function does not immediately group the columns but instead select columns to group and
 * returns a [GroupClause][org.jetbrains.kotlinx.dataframe.api.GroupClause],
 * which serves as an intermediate step.
 * The [GroupClause][org.jetbrains.kotlinx.dataframe.api.GroupClause] allows specifying the final
 * destination of the selected columns using methods such
 * as [into][org.jetbrains.kotlinx.dataframe.api.GroupClause.into] and,
 * that return a new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with grouped columns.
 * Check out [Grammar][org.jetbrains.kotlinx.dataframe.api.GroupDocs.Grammar].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.GroupDocs.GroupSelectingOptions].
 *
 * For more information: [See `group` on the documentation website.](https://kotlin.github.io/dataframe/group.html)
 *
 * Reverse operation: [ungroup][org.jetbrains.kotlinx.dataframe.api.ungroup].
 *
 * It is a special case of [move][org.jetbrains.kotlinx.dataframe.api.move] operation.
 *
 * Don't confuse this with [groupBy][org.jetbrains.kotlinx.dataframe.api.groupBy],
 * which groups the dataframe by the values in the selected columns!
 * ### This Group Overload
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 * This also allows you to use [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs]
 * for type- and name-safe columns selection.
 *
 * #### NOTE:
 * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * in this DSL directly with any function, they are NOT valid return types for the
 * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
 * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
 *
 * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 * ### Examples:
 * ```kotlin
 * df.group { columnA and columnB }.into("valueCols")
 * df.group { colsOf<String>() }.into { it.name.split(".").first() }
 * ```
 * @param [columns] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame] to group.
 */
@Interpretable("Group0")
public fun <T, C> DataFrame<T>.group(columns: ColumnsSelector<T, C>): GroupClause<T, C> = GroupClause(this, columns)

/**
 * Groups the specified [columns] within the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] into
 * [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
 *
 * This function does not immediately group the columns but instead select columns to group and
 * returns a [GroupClause][org.jetbrains.kotlinx.dataframe.api.GroupClause],
 * which serves as an intermediate step.
 * The [GroupClause][org.jetbrains.kotlinx.dataframe.api.GroupClause] allows specifying the final
 * destination of the selected columns using methods such
 * as [into][org.jetbrains.kotlinx.dataframe.api.GroupClause.into] and,
 * that return a new [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] with grouped columns.
 * Check out [Grammar][org.jetbrains.kotlinx.dataframe.api.GroupDocs.Grammar].
 *
 * This can include [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.GroupDocs.GroupSelectingOptions].
 *
 * For more information: [See `group` on the documentation website.](https://kotlin.github.io/dataframe/group.html)
 *
 * Reverse operation: [ungroup][org.jetbrains.kotlinx.dataframe.api.ungroup].
 *
 * It is a special case of [move][org.jetbrains.kotlinx.dataframe.api.move] operation.
 *
 * Don't confuse this with [groupBy][org.jetbrains.kotlinx.dataframe.api.groupBy],
 * which groups the dataframe by the values in the selected columns!
 * ### This Group Overload
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 * ### Example:
 * ```kotlin
 * df.group("second").into("valueCols")
 * df.group("prop.A", "prop.B", "cnt.A", "cnt.B").into { it.name.split(".").first() }
 * ```
 * @param [columns] The [Column Names][String] used to select the columns of this [DataFrame] to group.
 */
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
 * An intermediate class used in the [group] operation.
 *
 * This class itself does nothing—it is just a transitional step before specifying
 * how to group the selected columns.
 * It must be followed by one of the positioning methods
 * to produce a new [DataFrame] with the updated column structure.
 *
 * Use the following methods to finalize the move:
 * - [into(groupName)][GroupClause.into] – groups selected columns into a one column group.
 * - [into { groupNameExpression }][GroupClause.into] – groups each column into a group
 * by specifying path or name.
 *
 * See [Grammar][GroupDocs.Grammar] for more details.
 */
public class GroupClause<T, C>(internal val df: DataFrame<T>, internal val columns: ColumnsSelector<T, C>) {
    override fun toString(): String = "GroupClause(df=$df, columns=$columns)"
}

// region into

/**
 * Groups columns, previously selected with [group], into new or existing column groups
 * within the [DataFrame], using an [ColumnsSelectionDsl] expression to specify the target group name for each column.
 * The expression is applied to each selected column and determines the name of the column group
 * it will be placed into.
 *
 * If a column group with the specified name does not exist, it will be created.
 *
 * See [Selecting Columns][SelectingColumns].
 *
 * For more information: [See `group` on the documentation website.](https://kotlin.github.io/dataframe/group.html)
 *
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * ### Example:
 * ```kotlin
 * // For each selected column, place it under its under a group with its type as name (individual for each column):
 * df.group { all() }.into { it.type().toString() }
 * ```
 *
 * @param column A [ColumnsSelector] expression that takes a column and returns the name of the [ColumnGroup]
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
 * Groups columns, previously selected with [group], into a new or existing column group
 * within the [DataFrame] by specifying its path via [ColumnsSelectionDsl] expression.
 *
 * If the specified path is partially or fully missing — that is, if any segment of the path
 * does not correspond to an existing column or column group — all missing parts will be created automatically.
 *
 * See [Selecting Columns][SelectingColumns].
 *
 * For more information: [See `group` on the documentation website.](https://kotlin.github.io/dataframe/group.html)
 *
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 * (Any (combination of) [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 * This also allows you to use [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.ExtensionPropertiesAPIDocs]
 * for type- and name-safe columns selection.
 *
 * #### NOTE:
 * While you can use the [String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi] and [KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]
 * in this DSL directly with any function, they are NOT valid return types for the
 * [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda. You'd need to turn them into a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] first, for instance
 * with a function like [`col("name")`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col].
 *
 * ### Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
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
 * @param column A [ColumnsSelector] expression that takes a column and returns the full path to the [ColumnGroup]
 * where that column should be grouped.
 * All selected columns will be moved under the groups defined by this expression.
 */
@JvmName("intoColumn")
public fun <T, C> GroupClause<T, C>.into(
    column: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> AnyColumnReference,
): DataFrame<T> = df.move(columns).under(column)

/**
 * Groups columns, previously selected with [group], into a new or existing column group
 * within the [DataFrame], by specifying its name.
 *
 * If a column group with the specified name does not exist, it will be created.
 *
 * See [Selecting Columns][SelectingColumns].
 *
 * For more information: [See `group` on the documentation website.](https://kotlin.github.io/dataframe/group.html)
 *
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * ### Examples:
 * ```kotlin
 * df.group("age", "weight").into("info")
 * df.group { age and weight }.into("info")
 * ```
 *
 * @param [column] A [ColumnsSelector] that defines the path to a [ColumnGroup]
 * in the [DataFrame], where the selected columns will be moved.
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
