package org.jetbrains.kotlinx.dataframe.api

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
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.removeAt
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

public class UngroupWrongColumnKindException(public val df: DataFrame<*>, public val col: ColumnWithPath<*>) :
    IllegalArgumentException() {
    override val message: String =
        "Column '${col.path.joinToString()}' cannot be ungrouped: expected a ColumnGroup but got ${col.kind()}."
}

// region DataFrame

/**
 * Ungroups the specified [<code>column groups</code>][columns] within the [<code>DataFrame</code>][DataFrame], i.e.,
 * replaces each [<code>ColumnGroup</code>][ColumnGroup] with its nested columns.
 *
 * This can include nested column groups.
 *
 * See [<code>Selecting Columns</code>][UngroupSelectingOptions].
 *
 * For more information: [See `ungroup` on the documentation website.](https://kotlin.github.io/dataframe/ungroup.html)
 *
 * Reverse operation: [<code>group</code>][group].
 */
internal interface UngroupDocs {
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
     * <code>`df`</code>`.`[<code>ungroup</code>][org.jetbrains.kotlinx.dataframe.api.ungroup]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>ungroup</code>][org.jetbrains.kotlinx.dataframe.api.ungroup]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>ungroup</code>][org.jetbrains.kotlinx.dataframe.api.ungroup]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>ungroup</code>][org.jetbrains.kotlinx.dataframe.api.ungroup]`("length", "age")`
     *
     *
     *
     */
    typealias UngroupSelectingOptions = Nothing
}

/**
 * Ungroups the specified [<code>column groups</code>][columns] within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], i.e.,
 * replaces each [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] with its nested columns.
 *
 * This can include nested column groups.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.UngroupDocs.UngroupSelectingOptions].
 *
 * For more information: [See `ungroup` on the documentation website.](https://kotlin.github.io/dataframe/ungroup.html)
 *
 * Reverse operation: [<code>group</code>][org.jetbrains.kotlinx.dataframe.api.group].
 * ### This Ungroup Overload
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
 * // Ungroups "groupA" and "groupB" column groups
 * df.ungroup { groupA and groupB }
 * // Ungroups all column groups at any depth which name contains "group" substring
 * df.ungroup { colsAtAnyDepth().colGroups { it.name().contains("group") } }
 * ```
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the column groups of this [<code>DataFrame</code>][DataFrame] to ungroup.
 * @return A new [<code>DataFrame</code>][DataFrame] with ungrouped columns.
 * @throws IllegalArgumentException if the specified columns are not a [<code>ColumnGroup</code>][ColumnGroup].
 */
@Refine
@Interpretable("Ungroup0")
public fun <T, C> DataFrame<T>.ungroup(columns: ColumnsSelector<T, C>): DataFrame<T> {
    getColumnsWithPaths(columns).forEach { col ->
        if (!col.isColumnGroup()) {
            throw UngroupWrongColumnKindException(this, col)
        }
    }
    return move { columns.toColumnSet().colsInGroups() }
        .into { it.path.removeAt(it.path.size - 2).toPath() }
}

/**
 * Ungroups the specified [<code>column groups</code>][columns] within the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], i.e.,
 * replaces each [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] with its nested columns.
 *
 * This can include nested column groups.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.UngroupDocs.UngroupSelectingOptions].
 *
 * For more information: [See `ungroup` on the documentation website.](https://kotlin.github.io/dataframe/ungroup.html)
 *
 * Reverse operation: [<code>group</code>][org.jetbrains.kotlinx.dataframe.api.group].
 * ### This Ungroup Overload
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>ungroup</code>][org.jetbrains.kotlinx.dataframe.api.ungroup]`("length", "age")`
 *
 *
 *
 * @param [columns] The [<code>Column Names</code>][String] used to select the columns of this [<code>DataFrame</code>][DataFrame] to ungroup.
 * @return A new [<code>DataFrame</code>][DataFrame] with ungrouped columns.
 * @throws IllegalArgumentException if the specified columns are not a [<code>ColumnGroup</code>][ColumnGroup].
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
