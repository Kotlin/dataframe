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
     * {@comment What all `unfold` overloads have in common. KDoc-snippet.
     *    Set [TYPE_SOURCE] to where the properties are taken from,
     *    and [ANY_COLUMNS] to which columns of `Any` stay as they are.}
     *
     * The new columns are named after the properties, in the order of the primary constructor
     * (for a class without one, in the order of their names),
     * and the [ColumnGroup] has the name of the column it is made from.
     * An object that is `null` gives `null` in every new column,
     * except that a list property that becomes a [FrameColumn] gets an empty [DataFrame].
     * The properties are those of {@get [TYPE_SOURCE]}.
     *
     * A column that cannot be unfolded stays as it is.
     * These are columns of simple values, such as numbers, strings, or enums,
     * columns of objects without public properties,
     * {@get [ANY_COLUMNS]} (for example, with objects of different classes),
     * [ColumnGroup]s, and [FrameColumn]s.
     */
    @ExcludeFromSources
    interface CommonSnippet {

        // the key for a @set that names the type the properties are taken from
        @ExcludeFromSources
        typealias TYPE_SOURCE = Nothing

        // the key for a @set that names the columns of `Any` that stay as they are
        @ExcludeFromSources
        typealias ANY_COLUMNS = Nothing
    }

    /**
     * {@comment What the two [DataFrame] overloads have in common. KDoc-snippet.}
     *
     * Returns a new [DataFrame] in which the selected columns of objects are turned into [ColumnGroup]s,
     * with a column for every public property of these objects.
     * The other columns and the order of the columns stay the same.
     *
     * @include [CommonSnippet] {@set [CommonSnippet.TYPE_SOURCE] the [type][DataColumn.type] of each selected column}
     * {@set [CommonSnippet.ANY_COLUMNS] columns whose [type][DataColumn.type] is [Any]}
     */
    @ExcludeFromSources
    typealias DataFrameSnippet = Nothing

    /**
     * {@comment Properties that are unfolded at any depth. KDoc-snippet.}
     *
     * Properties of a [DataSchema] type, lists of them, [DataRow]s, and [DataFrame]s are always unfolded
     * into [ColumnGroup]s and [FrameColumn]s.
     */
    @ExcludeFromSources
    typealias AlwaysUnfoldedSnippet = Nothing

    /**
     * {@comment How deep the overloads with [maxDepth\] go. KDoc-snippet.}
     *
     * With the default [maxDepth\] `= 0`, the values of the properties stay as they are:
     * a nested object stays an object, and a list stays a list.
     * With [maxDepth\] `= 1`, nested objects become [ColumnGroup]s, and lists of objects become [FrameColumn]s;
     * every further step goes one level deeper.
     * {@include [AlwaysUnfoldedSnippet]}
     */
    @ExcludeFromSources
    typealias MaxDepthSnippet = Nothing

    /**
     * {@comment See also section of all `unfold` overloads. KDoc-snippet.}
     *
     * See also:
     * - [toDataFrame][Iterable.toDataFrame] — builds a [DataFrame] from an [Iterable] of objects
     *   by reading their properties.
     * - [flatten][DataFrame.flatten] and [ungroup][DataFrame.ungroup] — remove the column groups
     *   that `unfold` creates.
     * - [convert][DataFrame.convert] and [replace][DataFrame.replace] — change the selected columns in any other way.
     *
     * Don't confuse this with [explode][DataFrame.explode], which puts the elements of lists into separate rows.
     */
    @ExcludeFromSources
    typealias SeeAlsoSnippet = Nothing

    /**
     * @param [roots\] The properties, or getter-like functions such as `getX()`, to make columns from.
     * By default, all public properties of the objects.
     * They only apply to a column that can be unfolded:
     * a column of [String]s stays as it is even with `String::length` among [roots\].
     * @param [maxDepth\] How deep to unfold the values of the properties.
     * By default, `0`: they stay as they are.
     * @comment The `roots` and `maxDepth` parameters. KDoc-snippet.
     *    It sits at the end: a leading `{@comment}` would leave two blank lines inside the `@param` list.
     */
    @ExcludeFromSources
    typealias ParamsSnippet = Nothing

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetUnfoldOperationArg]}
     */
    typealias UnfoldSelectingOptions = Nothing
}

/** {@set [SelectingColumns.OPERATION] [unfold][unfold]} */
@ExcludeFromSources
private typealias SetUnfoldOperationArg = Nothing

// endregion

/**
 * Returns a [ColumnGroup] made from the objects of this column, with a column for every public property
 * of these objects, or this column itself when it cannot be unfolded.
 *
 * @include [UnfoldDocs.CommonSnippet] {@set [UnfoldDocs.CommonSnippet.TYPE_SOURCE] the type `T` of this column
 * as the compiler sees it. When `T` cannot be unfolded itself, such as `Any?` of an untyped column,
 * they are those of the [type][DataColumn.type] of the column instead}
 * {@set [UnfoldDocs.CommonSnippet.ANY_COLUMNS] columns where both `T` and the [type][DataColumn.type] are [Any]}
 *
 * @include [UnfoldDocs.MaxDepthSnippet]
 *
 * For more information: {@include [DocumentationUrls.Unfold]}
 *
 * @include [UnfoldDocs.SeeAlsoSnippet]
 *
 * ### Example
 *
 * ```kotlin
 * // `student` is a column of `Student(name: Name, age: Int, scores: List<Score>)` objects;
 * // `name` becomes a column group too, and `scores` a frame column
 * df.student.unfold(maxDepth = 1)
 * ```
 *
 * @include [UnfoldDocs.ParamsSnippet]
 * @return A [ColumnGroup] with the name of this column, or this column itself when it cannot be unfolded.
 */
public inline fun <reified T> DataColumn<T>.unfold(vararg roots: KCallable<*>, maxDepth: Int = 0): AnyCol =
    unfoldImpl(typeOf<T>()) { properties(roots = roots, maxDepth) }

/**
 * @include [UnfoldDocs.DataFrameSnippet]
 *
 * @include [UnfoldDocs.MaxDepthSnippet]
 *
 * With the compiler plugin, a call with [roots] gets an empty compile-time schema for now,
 * so the result has no typed accessors (#2115).
 *
 * For more information: {@include [DocumentationUrls.Unfold]}
 *
 * @include [UnfoldDocs.SeeAlsoSnippet]
 *
 * See [Selecting Columns][UnfoldDocs.UnfoldSelectingOptions].
 *
 * @include [SelectingColumns.ColumnsSelectionDsl] {@include [SetUnfoldOperationArg]}
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
 * @include [UnfoldDocs.ParamsSnippet]
 * @param [columns] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame] to unfold.
 * @return A new [DataFrame] with the selected columns unfolded.
 */
@Refine
@Interpretable("DataFrameUnfold")
public fun <T> DataFrame<T>.unfold(
    vararg roots: KCallable<*>,
    maxDepth: Int = 0,
    columns: ColumnsSelector<T, *>,
): DataFrame<T> = replace(columns).with { it.unfoldImpl(it.type()) { properties(roots = roots, maxDepth) } }

/**
 * @include [UnfoldDocs.DataFrameSnippet]
 *
 * The values of the properties stay as they are: a nested object stays an object, and a list stays a list.
 * {@include [UnfoldDocs.AlwaysUnfoldedSnippet]}
 * To unfold nested objects as well, or to take only some of the properties,
 * use the overload with a [Columns Selector][ColumnsSelector] and its `maxDepth` and `roots` parameters.
 *
 * For more information: {@include [DocumentationUrls.Unfold]}
 *
 * @include [UnfoldDocs.SeeAlsoSnippet]
 *
 * See [Selecting Columns][UnfoldDocs.UnfoldSelectingOptions].
 *
 * @include [SelectingColumns.ColumnNamesApi.ColumnNamesApiWithExample] {@include [SetUnfoldOperationArg]}
 *
 * ### Example
 *
 * ```kotlin
 * // `student` is a column of `Student(name: Name, age: Int, scores: List<Score>)` objects
 * df.unfold("student")
 * ```
 *
 * @param [columns] The names of the columns of this [DataFrame] to unfold.
 * @return A new [DataFrame] with the selected columns unfolded.
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
