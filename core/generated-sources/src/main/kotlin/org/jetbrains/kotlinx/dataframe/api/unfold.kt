package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.annotations.StringApiInterpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.unfoldImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KCallable
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

// region docs

// holds the common `unfold` KDoc-snippets; it stays in the sources, as the KDocs link to `UnfoldSelectingOptions`
internal interface UnfoldDocs {

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
     * <code>`df`</code>`.`[<code>unfold</code>][org.jetbrains.kotlinx.dataframe.api.unfold]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>unfold</code>][org.jetbrains.kotlinx.dataframe.api.unfold]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>unfold</code>][org.jetbrains.kotlinx.dataframe.api.unfold]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>unfold</code>][org.jetbrains.kotlinx.dataframe.api.unfold]`("length", "age")`
     *
     *
     *
     */
    typealias UnfoldSelectingOptions = Nothing
}

// endregion

/**
 * Returns a [<code>ColumnGroup</code>][ColumnGroup] made from the objects of this column, with a column for every public property
 * of these objects, or this column itself when it cannot be unfolded.
 *
 *
 *
 * The new columns are named after the properties, in the order of the primary constructor
 * (for a class without one, in the order of their names),
 * and the [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] has the name of the column it is made from.
 * An object that is `null` gives `null` in every new column,
 * except that a list property that becomes a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] gets an empty [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * The properties are those of the type `T` of this column
 * as the compiler sees it. When `T` cannot be unfolded itself, such as `Any?` of an untyped column,
 * they are those of the [<code>type</code>][DataColumn.type] of the column instead.
 *
 * A column that cannot be unfolded stays as it is.
 * These are columns of simple values, such as numbers, strings, or enums,
 * columns of objects without public properties,
 * columns whose [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] is [<code>Any</code>][Any] (for example, with objects of different classes),
 * [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]s, and [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]s.
 *
 *
 *
 * With the default [maxDepth] `= 0`, the values of the properties stay as they are:
 * a nested object stays an object, and a list stays a list.
 * With [maxDepth] `= 1`, nested objects become [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]s, and lists of objects become [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]s;
 * every further step goes one level deeper.
 *
 *
 * Properties of a [<code>DataSchema</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] type, lists of them, [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]s, and [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are always unfolded
 * into [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]s and [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]s.
 *
 * For more information: [See `unfold` on the documentation website.](https://kotlin.github.io/dataframe/unfold.html)
 *
 *
 *
 * See also:
 * - [<code>toDataFrame</code>][kotlin.collections.Iterable.toDataFrame] — builds a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] from an [<code>Iterable</code>][Iterable] of objects
 *   by reading their properties.
 * - [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] and [<code>ungroup</code>][org.jetbrains.kotlinx.dataframe.DataFrame.ungroup] — remove the column groups
 *   that `unfold` creates.
 * - [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] and [<code>replace</code>][org.jetbrains.kotlinx.dataframe.DataFrame.replace] — change the selected columns in any other way.
 *
 * Don't confuse this with [<code>explode</code>][org.jetbrains.kotlinx.dataframe.DataFrame.explode], which puts the elements of lists into separate rows.
 *
 * ### Example
 *
 * ```kotlin
 * // `student` is a column of `Student(name: Name, age: Int, scores: List<Score>)` objects;
 * // `name` becomes a column group too, and `scores` a frame column
 * df.student.unfold(maxDepth = 1)
 * ```
 *
 * @param [roots] The properties, or getter-like functions such as `getX()`, to make columns from.
 * By default, all public properties of the objects.
 * They only apply to a column that can be unfolded:
 * a column of [<code>String</code>][String]s stays as it is even with `String::length` among [roots].
 * @param [maxDepth] How deep to unfold the values of the properties.
 * By default, `0`: they stay as they are.
 * @return A [<code>ColumnGroup</code>][ColumnGroup] with the name of this column, or this column itself when it cannot be unfolded.
 */
public inline fun <reified T> DataColumn<T>.unfold(vararg roots: KCallable<*>, maxDepth: Int = 0): AnyCol =
    unfoldImpl(typeOf<T>()) { properties(roots = roots, maxDepth) }

/**
 *
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] in which the selected columns of objects are turned into [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]s,
 * with a column for every public property of these objects.
 * The other columns and the order of the columns stay the same.
 *
 *
 *
 * The new columns are named after the properties, in the order of the primary constructor
 * (for a class without one, in the order of their names),
 * and the [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] has the name of the column it is made from.
 * An object that is `null` gives `null` in every new column,
 * except that a list property that becomes a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] gets an empty [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * The properties are those of the [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] of each selected column.
 *
 * A column that cannot be unfolded stays as it is.
 * These are columns of simple values, such as numbers, strings, or enums,
 * columns of objects without public properties,
 * columns whose [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] is [<code>Any</code>][Any] (for example, with objects of different classes),
 * [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]s, and [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]s.
 *
 *
 *
 * With the default [maxDepth] `= 0`, the values of the properties stay as they are:
 * a nested object stays an object, and a list stays a list.
 * With [maxDepth] `= 1`, nested objects become [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]s, and lists of objects become [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]s;
 * every further step goes one level deeper.
 *
 *
 * Properties of a [<code>DataSchema</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] type, lists of them, [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]s, and [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are always unfolded
 * into [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]s and [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]s.
 *
 * With the compiler plugin, a call with [<code>roots</code>][roots] gets an empty compile-time schema for now,
 * so the result has no typed accessors (#2115).
 *
 * For more information: [See `unfold` on the documentation website.](https://kotlin.github.io/dataframe/unfold.html)
 *
 *
 *
 * See also:
 * - [<code>toDataFrame</code>][kotlin.collections.Iterable.toDataFrame] — builds a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] from an [<code>Iterable</code>][Iterable] of objects
 *   by reading their properties.
 * - [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] and [<code>ungroup</code>][org.jetbrains.kotlinx.dataframe.DataFrame.ungroup] — remove the column groups
 *   that `unfold` creates.
 * - [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] and [<code>replace</code>][org.jetbrains.kotlinx.dataframe.DataFrame.replace] — change the selected columns in any other way.
 *
 * Don't confuse this with [<code>explode</code>][org.jetbrains.kotlinx.dataframe.DataFrame.explode], which puts the elements of lists into separate rows.
 *
 * See [<code>Selecting Columns</code>][UnfoldDocs.UnfoldSelectingOptions].
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
 * ### Examples
 *
 * ```kotlin
 * // `student` is a column of `Student(name: Name, age: Int, scores: List<Score>)` objects
 * df.unfold { student }
 *
 * // `name` becomes a column group too, and `scores` a frame column
 * df.unfold(maxDepth = 1) { student }
 *
 * // only the `name` and `age` properties
 * df.unfold(Student::name, Student::age) { student }
 * ```
 *
 * @param [roots] The properties, or getter-like functions such as `getX()`, to make columns from.
 * By default, all public properties of the objects.
 * They only apply to a column that can be unfolded:
 * a column of [<code>String</code>][String]s stays as it is even with `String::length` among [roots].
 * @param [maxDepth] How deep to unfold the values of the properties.
 * By default, `0`: they stay as they are.
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to unfold.
 * @return A new [<code>DataFrame</code>][DataFrame] with the selected columns unfolded.
 */
@Refine
@Interpretable("DataFrameUnfold")
public fun <T> DataFrame<T>.unfold(
    vararg roots: KCallable<*>,
    maxDepth: Int = 0,
    columns: ColumnsSelector<T, *>,
): DataFrame<T> = replace(columns).with { it.unfoldImpl(it.type()) { properties(roots = roots, maxDepth) } }

/**
 *
 *
 * Returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] in which the selected columns of objects are turned into [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]s,
 * with a column for every public property of these objects.
 * The other columns and the order of the columns stay the same.
 *
 *
 *
 * The new columns are named after the properties, in the order of the primary constructor
 * (for a class without one, in the order of their names),
 * and the [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] has the name of the column it is made from.
 * An object that is `null` gives `null` in every new column,
 * except that a list property that becomes a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] gets an empty [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * The properties are those of the [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] of each selected column.
 *
 * A column that cannot be unfolded stays as it is.
 * These are columns of simple values, such as numbers, strings, or enums,
 * columns of objects without public properties,
 * columns whose [<code>type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] is [<code>Any</code>][Any] (for example, with objects of different classes),
 * [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]s, and [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]s.
 *
 * The values of the properties stay as they are: a nested object stays an object, and a list stays a list.
 *
 *
 * Properties of a [<code>DataSchema</code>][org.jetbrains.kotlinx.dataframe.annotations.DataSchema] type, lists of them, [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow]s, and [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame]s are always unfolded
 * into [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]s and [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn]s.
 * To unfold nested objects as well, or to take only some of the properties,
 * use the overload with a [<code>Columns Selector</code>][ColumnsSelector] and its `maxDepth` and `roots` parameters.
 *
 * For more information: [See `unfold` on the documentation website.](https://kotlin.github.io/dataframe/unfold.html)
 *
 *
 *
 * See also:
 * - [<code>toDataFrame</code>][kotlin.collections.Iterable.toDataFrame] — builds a [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame] from an [<code>Iterable</code>][Iterable] of objects
 *   by reading their properties.
 * - [<code>flatten</code>][org.jetbrains.kotlinx.dataframe.DataFrame.flatten] and [<code>ungroup</code>][org.jetbrains.kotlinx.dataframe.DataFrame.ungroup] — remove the column groups
 *   that `unfold` creates.
 * - [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert] and [<code>replace</code>][org.jetbrains.kotlinx.dataframe.DataFrame.replace] — change the selected columns in any other way.
 *
 * Don't confuse this with [<code>explode</code>][org.jetbrains.kotlinx.dataframe.DataFrame.explode], which puts the elements of lists into separate rows.
 *
 * See [<code>Selecting Columns</code>][UnfoldDocs.UnfoldSelectingOptions].
 *
 *
 *
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>unfold</code>][org.jetbrains.kotlinx.dataframe.api.unfold]`("length", "age")`
 *
 *
 *
 *
 * ### Example
 *
 * ```kotlin
 * // `student` is a column of `Student(name: Name, age: Int, scores: List<Score>)` objects
 * df.unfold("student")
 * ```
 *
 * @param [columns] The names of the columns of this [<code>DataFrame</code>][DataFrame] to unfold.
 * @return A new [<code>DataFrame</code>][DataFrame] with the selected columns unfolded.
 */
@Refine
@StringApiInterpretable(interpreter = "DataFrameUnfold", stringArgument = "columns", targetArgument = "columns")
public fun <T> DataFrame<T>.unfold(vararg columns: String): DataFrame<T> = unfold { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.unfold(vararg columns: AnyColumnReference): DataFrame<T> = unfold { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.unfold(vararg columns: KProperty<*>): DataFrame<T> = unfold { columns.toColumnSet() }
