package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.removeAt
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

/**
 * Ungroups the specified [column groups][columns] within the [DataFrame], i.e.,
 * replaces each [ColumnGroup] with its nested columns.
 *
 * See [Selecting Columns][UngroupSelectingOptions].
 *
 * For more information: [See `group` on the documentation website.](https://kotlin.github.io/dataframe/ungroup.html)
 *
 * Reverse operation: [group].
 */
internal interface UngroupDocs {
    /**
     *
     * ## Selecting Columns
     *
     * Selecting columns for various [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame] operations
     * can be done in the following ways:
     * ### 1. [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnsSelectionDsl.ColumnsSelectionDslWithExample]
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
     * <code>`df`</code>`.`[ungroup][org.jetbrains.kotlinx.dataframe.api.ungroup]` { length `[and][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[ungroup][org.jetbrains.kotlinx.dataframe.api.ungroup]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[ungroup][org.jetbrains.kotlinx.dataframe.api.ungroup]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
     *
     *
     *
     * > There's also a 'single column' variant used sometimes: [Column Selection DSL][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnSelectionDsl.ColumnsSelectionDslWithExample].
     * ### 2. [Column names][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample]
     * Select single or multiple columns using their names as [String]s.
     * ([String API][`StringAPI`]).
     *
     * #### For example:
     *
     * <code>`df`</code>`.`[ungroup][org.jetbrains.kotlinx.dataframe.api.ungroup]`("length", "age")`
     *
     *
     *
     */
    typealias UngroupSelectingOptions = Nothing
}

/**
 * Ungroups the specified [column groups][columns] within the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], i.e.,
 * replaces each [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] with its nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.UngroupDocs.UngroupSelectingOptions].
 *
 * For more information: [See `group` on the documentation website.](https://kotlin.github.io/dataframe/ungroup.html)
 *
 * Reverse operation: [group][org.jetbrains.kotlinx.dataframe.api.group].
 * ### This Ungroup Overload
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
 * ### Examples:
 * ```kotlin
 * df.ungroup { groupA and groupB }
 * df.ungroup { all() }
 * ```
 * @param [columns] The [Columns Selector][ColumnsSelector] used to select the column groups of this [DataFrame] to ungroup.
 */
@Refine
@Interpretable("Ungroup0")
public fun <T, C> DataFrame<T>.ungroup(columns: ColumnsSelector<T, C>): DataFrame<T> =
    move { columns.toColumnSet().colsInGroups() }
        .into { it.path.removeAt(it.path.size - 2).toPath() }

/**
 * Ungroups the specified [column groups][columns] within the [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], i.e.,
 * replaces each [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] with its nested columns.
 *
 * See [Selecting Columns][org.jetbrains.kotlinx.dataframe.api.UngroupDocs.UngroupSelectingOptions].
 *
 * For more information: [See `group` on the documentation website.](https://kotlin.github.io/dataframe/ungroup.html)
 *
 * Reverse operation: [group][org.jetbrains.kotlinx.dataframe.api.group].
 * ### This Ungroup Overload
 * Select single or multiple columns using their names as [String]s.
 * ([String API][`StringAPI`]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[ungroup][org.jetbrains.kotlinx.dataframe.api.ungroup]`("length", "age")`
 *
 *
 *
 * @param [columns] The [Column Names][String] used to select the columns of this [DataFrame] to ungroup.
 */
public fun <T> DataFrame<T>.ungroup(vararg columns: String): DataFrame<T> = ungroup { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.ungroup(vararg columns: AnyColumnReference): DataFrame<T> =
    ungroup { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.ungroup(vararg columns: KProperty<*>): DataFrame<T> = ungroup { columns.toColumnSet() }

// endregion
