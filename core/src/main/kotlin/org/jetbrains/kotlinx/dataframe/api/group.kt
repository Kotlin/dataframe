package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.CandidateForRemoval
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
 * Groups the specified [columns\] within the [DataFrame].
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
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
 *
 * See [Selecting Columns][GroupSelectingOptions].
 *
 * For more information: {@include [DocumentationUrls.Group]}
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
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetGroupOperationArg]}
     */
    interface GroupSelectingOptions

    /**
     * ## Group Operation Grammar
     * {@include [LineBreak]}
     * {@include [DslGrammarLink]}
     * {@include [LineBreak]}
     *
     * **[`group`][group]****`  {  `**`columnsSelector: `[`ColumnsSelector`][ColumnsSelector]**` }`**
     *
     * {@include [Indent]}
     * __`.`__[**`into`**][GroupClause.into]**`(`**`groupName: `[`String`][String]**`)`**
     *
     * {@include [Indent]}
     * __`.`__[**`into`**][GroupClause.into]` { column: `[`ColumnsSelectionDsl<T>`][ColumnsSelectionDsl]`.(`[`ColumnWithPath<C>`][ColumnWithPath]`) -> `[`String`][String]` }`
     *
     * {@include [Indent]}
     * __`.`__[**`into`**][GroupClause.into]` { column: `[`ColumnsSelectionDsl<T>`][ColumnsSelectionDsl]`.(`[`ColumnWithPath<C>`][ColumnWithPath]`) -> `[`AnyColumnReference`][AnyColumnReference]` }`
     *
     */
    interface Grammar
}

/** {@set [SelectingColumns.OPERATION] [group][group]} */
@ExcludeFromSources
private interface SetGroupOperationArg

/**
 * {@include [GroupDocs]}
 * ### This Group Overload
 */
@ExcludeFromSources
private interface CommonGroupDocs

/**
 * @include [CommonGroupDocs]
 * @include [SelectingColumns.Dsl] {@include [SetGroupOperationArg]}
 * ### Examples:
 * ```kotlin
 * df.group { columnA and columnB }.into("valueCols")
 * df.group { colsOf<String>() }.into { it.name.split(".").first() }
 * ```
 * @param [columns\] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame] to group.
 */
@Interpretable("Group0")
public fun <T, C> DataFrame<T>.group(columns: ColumnsSelector<T, C>): GroupClause<T, C> = GroupClause(this, columns)

/**
 * @include [CommonGroupDocs]
 * @include [SelectingColumns.ColumnNames] {@include [SetGroupOperationArg]}
 * ### Example:
 * ```kotlin
 * df.group("second").into("valueCols")
 * df.group("prop.A", "prop.B", "cnt.A", "cnt.B").into { it.name.split(".").first() }
 * ```
 * @param [columns\] The [Column Names][String] used to select the columns of this [DataFrame] to group.
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
 * For more information: {@include [DocumentationUrls.Group]}
 *
 * @include [SelectingColumns.ColumnNames]
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
@JvmName("intoString")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
@CandidateForRemoval
public fun <T, C> GroupClause<T, C>.into(column: ColumnsSelectionDsl<T>.(ColumnWithPath<C>) -> String): DataFrame<T> =
    df.move(columns).under { column(it).toColumnAccessor() }

/**
 * Groups columns, previously selected with [group], into a new or existing column group
 * within the [DataFrame] by specifying its path via [ColumnsSelectionDsl] expression.
 *
 * {@include [org.jetbrains.kotlinx.dataframe.documentation.ColumnPathCreation]}
 *
 * See [Selecting Columns][SelectingColumns].
 *
 * For more information: {@include [DocumentationUrls.Group]}
 *
 * @include [SelectingColumns.Dsl]
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
@CandidateForRemoval
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
 * For more information: {@include [DocumentationUrls.Group]}
 *
 * @include [SelectingColumns.ColumnNames]
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
