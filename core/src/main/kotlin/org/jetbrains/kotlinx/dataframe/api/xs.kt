package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.api.xsImpl

/**
 * ## The Xs Operation
 *
 * Returns a cross-section: keeps only the rows in which the key columns hold the given key values,
 * and removes the key columns from the result.
 *
 * Key values and key columns are paired by position:
 * the first key value is compared with the first key column,
 * the second key value with the second key column, and so on.
 * A row is kept when every pair is equal.
 *
 * The rows that are kept stay in their original order.
 *
 * For more information: {@include [DocumentationUrls.Xs]}
 */
internal interface XsDocs {

    /**
     * The key columns are the first `n` columns of {@get [SOURCE]},
     * where `n` is the number of given key values.
     * Columns are counted from left to right;
     * a [column group][ColumnGroup] is not counted itself, but the columns inside it are.
     * If there are fewer columns than key values, an [IllegalArgumentException] is thrown.
     */
    @ExcludeFromSources
    typealias DefaultKeyColumnsSnippet = Nothing

    /**
     * The key columns are the columns returned by the `keyColumns` selector,
     * in the order the selector returns them — not in the order they appear in the data.
     * The selector must return exactly one column per key value;
     * otherwise, an [IllegalArgumentException] is thrown.
     */
    @ExcludeFromSources
    typealias SelectedKeyColumnsSnippet = Nothing

    /**
     * The examples below use this [DataFrame], the same data as the `xs` page on the
     * documentation website. `firstName` and `lastName` sit inside the `name`
     * [column group][ColumnGroup]:
     *
     * | name/firstName | name/lastName | age | city   | weight | isHappy |
     * | :------------- | :------------ | :-- | :----- | :----- | :------ |
     * | Alice          | Cooper        | 15  | London | 54     | true    |
     * | Bob            | Dylan         | 45  | Dubai  | 87     | true    |
     * | Charlie        | Daniels       | 20  | Moscow | null   | false   |
     * | Charlie        | Chaplin       | 40  | Milan  | null   | true    |
     * | Bob            | Marley        | 30  | Tokyo  | 68     | true    |
     * | Alice          | Wolf          | 20  | null   | 55     | false   |
     * | Charlie        | Byrd          | 30  | Moscow | 90     | true    |
     */
    @ExcludeFromSources
    typealias ExampleDataSnippet = Nothing

    /*
     * The key for a @set that names the columns the default key columns are taken from.
     */
    @ExcludeFromSources
    typealias SOURCE = Nothing
}

// region DataFrame

/**
 * @include [XsDocs]
 *
 * See also [filter][DataFrame.filter], which selects rows by a condition and leaves all columns in place,
 * and [remove][DataFrame.remove], which only removes columns.
 * ### This `xs` Overload
 */
@ExcludeFromSources
private typealias CommonDataFrameXsDocs = Nothing

/**
 * @include [CommonDataFrameXsDocs]
 * @include [XsDocs.DefaultKeyColumnsSnippet] {@set [XsDocs.SOURCE] this [DataFrame]}
 *
 * ### Example
 *
 * @include [XsDocs.ExampleDataSnippet]
 *
 * With one key value the key column is the first column, `name/firstName`.
 * The `name` group is not a key column itself, so it stays with its remaining column:
 *
 * ```
 * df.xs("Charlie")
 * ```
 * | name/lastName | age | city   | weight | isHappy |
 * | :------------ | :-- | :----- | :----- | :------ |
 * | Daniels       | 20  | Moscow | null   | false   |
 * | Chaplin       | 40  | Milan  | null   | true    |
 * | Byrd          | 30  | Moscow | 90     | true    |
 *
 * With two key values the key columns are `name/firstName` and `name/lastName`.
 * The `name` group loses both of its columns and disappears:
 *
 * ```
 * df.xs("Charlie", "Chaplin")
 * ```
 * | age | city  | weight | isHappy |
 * | :-- | :---- | :----- | :------ |
 * | 40  | Milan | null   | true    |
 *
 * @param [keyValues] The values that the key columns must hold.
 * @return A new [DataFrame] with the matching rows and without the key columns.
 */
@Refine
@Interpretable("DataFrameXs")
public fun <T> DataFrame<T>.xs(vararg keyValues: Any?): DataFrame<T> =
    xs(*keyValues) {
        colsAtAnyDepth().filter { !it.isColumnGroup() }.take(keyValues.size)
    }

/**
 * @include [CommonDataFrameXsDocs]
 * @include [XsDocs.SelectedKeyColumnsSnippet]
 *
 * @include [SelectingColumns.ColumnsSelectionDsl]
 *
 * ### Example
 *
 * @include [XsDocs.ExampleDataSnippet]
 *
 * The key columns do not have to be the first ones —
 * here "city" and "isHappy" are selected explicitly:
 *
 * ```
 * df.xs("Moscow", true) { city and isHappy }
 * ```
 * | name/firstName | name/lastName | age | weight |
 * | :------------- | :------------ | :-- | :----- |
 * | Charlie        | Byrd          | 30  | 90     |
 *
 * @param [keyValues] The values that the [key columns][keyColumns] must hold.
 * @param [keyColumns] The [Columns Selector][ColumnsSelector] that defines the key columns.
 * @return A new [DataFrame] with the matching rows and without the key columns.
 */
@Refine
@Interpretable("DataFrameXs")
public fun <T, C> DataFrame<T>.xs(vararg keyValues: C, keyColumns: ColumnsSelector<T, C>): DataFrame<T> =
    xsImpl(keyColumns, false, *keyValues)

// endregion

// region GroupBy

/**
 * @include [XsDocs]
 *
 * Both parts of a [GroupBy] are filtered: the key–group pairs, and the rows inside the groups.
 * The key columns are removed from [keys][GroupBy.keys] and from the groups.
 * ### This `xs` Overload
 */
@ExcludeFromSources
private typealias CommonGroupByXsDocs = Nothing

/**
 * @include [CommonGroupByXsDocs]
 * @include [XsDocs.DefaultKeyColumnsSnippet] {@set [XsDocs.SOURCE] [keys][GroupBy.keys]}
 *
 * ### Example
 *
 * @include [XsDocs.ExampleDataSnippet]
 *
 * ```kotlin
 * val gb = df.groupBy { city and isHappy }
 * ```
 *
 * The key value is matched against the first key column, "city".
 * Only the pairs whose "city" key is "Moscow" are kept,
 * and "city" is removed from both parts:
 *
 * ```
 * gb.xs("Moscow").keys
 * ```
 * | isHappy |
 * | :------ |
 * | false   |
 * | true    |
 *
 * ```
 * gb.xs("Moscow").concat()
 * ```
 * | name/firstName | name/lastName | age | weight | isHappy |
 * | :------------- | :------------ | :-- | :----- | :------ |
 * | Charlie        | Daniels       | 20  | null   | false   |
 * | Charlie        | Byrd          | 30  | 90     | true    |
 *
 * @param [keyValues] The values that the key columns must hold.
 * @return A new [GroupBy] with the matching key–group pairs and without the key columns.
 */
@Refine
@Interpretable("GroupByXs")
public fun <T, G> GroupBy<T, G>.xs(vararg keyValues: Any?): GroupBy<T, G> =
    xs(*keyValues) {
        colsAtAnyDepth().filter { !it.isColumnGroup() }.take(keyValues.size)
    }

/**
 * @include [CommonGroupByXsDocs]
 * @include [XsDocs.SelectedKeyColumnsSnippet]
 *
 * A key column that exists in only one part is used in that part only.
 * For example, a column that is not a grouping key exists only in the groups,
 * so only the groups are filtered by it.
 *
 * @include [SelectingColumns.ColumnsSelectionDsl]
 *
 * ### Example
 *
 * @include [XsDocs.ExampleDataSnippet]
 *
 * ```kotlin
 * val gb = df.groupBy { city }
 * ```
 *
 * "isHappy" is not a grouping key, so it exists only in the groups.
 * Selecting it filters the rows inside the groups and leaves the keys as they are:
 *
 * ```
 * gb.xs(true) { isHappy }.keys
 * ```
 * | city   |
 * | :----- |
 * | London |
 * | Dubai  |
 * | Moscow |
 * | Milan  |
 * | Tokyo  |
 * | null   |
 *
 * ```
 * gb.xs(true) { isHappy }.concat()
 * ```
 * | name/firstName | name/lastName | age | city   | weight |
 * | :------------- | :------------ | :-- | :----- | :----- |
 * | Alice          | Cooper        | 15  | London | 54     |
 * | Bob            | Dylan         | 45  | Dubai  | 87     |
 * | Charlie        | Byrd          | 30  | Moscow | 90     |
 * | Charlie        | Chaplin       | 40  | Milan  | null   |
 * | Bob            | Marley        | 30  | Tokyo  | 68     |
 *
 * @param [keyValues] The values that the [key columns][keyColumns] must hold.
 * @param [keyColumns] The [Columns Selector][ColumnsSelector] that defines the key columns.
 * @return A new [GroupBy] with the matching key–group pairs and without the key columns.
 */
@Refine
@Interpretable("GroupByXs")
public fun <T, G, C> GroupBy<T, G>.xs(vararg keyValues: C, keyColumns: ColumnsSelector<T, C>): GroupBy<T, G> =
    xsImpl(*keyValues, keyColumns = keyColumns)

// endregion
