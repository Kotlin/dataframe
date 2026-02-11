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
     * <code>`df`</code>`.`[ungroup][org.jetbrains.kotlinx.dataframe.api.ungroup]` { length `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[ungroup][org.jetbrains.kotlinx.dataframe.api.ungroup]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[ungroup][org.jetbrains.kotlinx.dataframe.api.ungroup]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
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
     * `df.`[ungroup][org.jetbrains.kotlinx.dataframe.api.ungroup]`("length", "age")`
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
     * `df.`[ungroup][org.jetbrains.kotlinx.dataframe.api.ungroup]`(length, age)`
     *
     * ### 4. [KProperties][org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns.KProperties.WithExample]
     * Select columns using [KProperties][KProperty] ([KProperties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.KPropertiesApi]).
     *
     * #### For example:
     * ```kotlin
     * data class Person(val length: Double, val age: Double)
     * ```
     *
     * `df.`[ungroup][org.jetbrains.kotlinx.dataframe.api.ungroup]`(Person::length, Person::age)`
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
 * Select columns using their [column names][String]
 * ([String API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.StringApi]).
 *
 * #### For example:
 *
 * `df.`[ungroup][org.jetbrains.kotlinx.dataframe.api.ungroup]`("length", "age")`
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
