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

// region docs

/**
 *
 *
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
 * ### Xs Modes
 *
 * The key columns are either taken by position or selected explicitly.
 * Both modes exist for [<code>DataFrame</code>][DataFrame] and for [<code>GroupBy</code>][GroupBy]:
 *
 * - [<code>xs</code>][DataFrame.xs]`(keyValues)` — the key columns are the first columns of the [<code>DataFrame</code>][DataFrame].
 * - [<code>xs</code>][DataFrame.xs]`(keyValues) { keyColumns }` — the key columns are the selected ones.
 * - [<code>xs</code>][GroupBy.xs]`(keyValues)` — the key columns are the first columns of [<code>keys</code>][GroupBy.keys].
 * - [<code>xs</code>][GroupBy.xs]`(keyValues) { keyColumns }` — the key columns are the selected ones.
 *
 * See also:
 * - [<code>filter</code>][DataFrame.filter] — selects rows by a condition and leaves all columns in place.
 * - [<code>remove</code>][DataFrame.remove] — only removes columns, without selecting rows.
 *
 * For more information: [See `xs` on the documentation website.](https://kotlin.github.io/dataframe/xs.html)
 */
internal interface XsDocs

// endregion

// region DataFrame

/**
 * Returns a cross-section of this [<code>DataFrame</code>][DataFrame]: only the rows in which the key columns hold
 * the given [<code>keyValues</code>][keyValues], without those key columns.
 *
 *
 *
 * The key columns are the first `n` columns of this [<code>DataFrame</code>][DataFrame],
 * where `n` is the number of given key values.
 * Columns are counted from left to right;
 * a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is not counted itself, but the columns inside it are.
 * If there are fewer columns than key values, an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 *
 * See also:
 * - [<code>xs</code>][DataFrame.xs]`(keyValues) { keyColumns }` — selects the key columns explicitly.
 * - [<code>The Xs Operation</code>][org.jetbrains.kotlinx.dataframe.api.XsDocs] — an overview of all `xs` modes.
 *
 * For more information: [See `xs` on the documentation website.](https://kotlin.github.io/dataframe/xs.html)
 *
 * ### Example
 *
 *
 *
 * The examples below use this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], the same data as the
 * [`xs` page on the documentation website](https://kotlin.github.io/dataframe/xs.html).
 * `firstName` and `lastName` sit inside the `name` [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]:
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
 * @return A new [<code>DataFrame</code>][DataFrame] with the matching rows and without the key columns.
 */
@Refine
@Interpretable("DataFrameXs")
public fun <T> DataFrame<T>.xs(vararg keyValues: Any?): DataFrame<T> =
    xs(*keyValues) {
        colsAtAnyDepth().filter { !it.isColumnGroup() }.take(keyValues.size)
    }

/**
 * Returns a cross-section of this [<code>DataFrame</code>][DataFrame]: only the rows in which the [<code>key columns</code>][keyColumns] hold
 * the given [<code>keyValues</code>][keyValues], without those key columns.
 *
 *
 *
 * The key columns are the columns returned by the [keyColumns] selector,
 * in the order the selector returns them — not in the order they appear in the data.
 * The selector must return exactly one column per key value;
 * otherwise, an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
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
 * See also:
 * - [<code>xs</code>][DataFrame.xs]`(keyValues)` — takes the first columns as the key columns.
 * - [<code>The Xs Operation</code>][org.jetbrains.kotlinx.dataframe.api.XsDocs] — an overview of all `xs` modes.
 *
 * For more information: [See `xs` on the documentation website.](https://kotlin.github.io/dataframe/xs.html)
 *
 * ### Example
 *
 *
 *
 * The examples below use this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], the same data as the
 * [`xs` page on the documentation website](https://kotlin.github.io/dataframe/xs.html).
 * `firstName` and `lastName` sit inside the `name` [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]:
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
 * @param [keyValues] The values that the [<code>key columns</code>][keyColumns] must hold.
 * @param [keyColumns] The [<code>Columns Selector</code>][ColumnsSelector] that defines the key columns.
 * @return A new [<code>DataFrame</code>][DataFrame] with the matching rows and without the key columns.
 */
@Refine
@Interpretable("DataFrameXs")
public fun <T, C> DataFrame<T>.xs(vararg keyValues: C, keyColumns: ColumnsSelector<T, C>): DataFrame<T> =
    xsImpl(keyColumns, false, *keyValues)

// endregion

// region GroupBy

/**
 * Returns a cross-section of this [<code>GroupBy</code>][GroupBy]: only the key–group pairs in which the key columns hold
 * the given [<code>keyValues</code>][keyValues], without those key columns.
 *
 *
 *
 * Both parts of the [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] are filtered: the key–group pairs, and the rows inside the groups.
 * The key columns are removed from [<code>keys</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] and from the groups.
 *
 *
 *
 * The key columns are the first `n` columns of [<code>keys</code>][GroupBy.keys],
 * where `n` is the number of given key values.
 * Columns are counted from left to right;
 * a [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is not counted itself, but the columns inside it are.
 * If there are fewer columns than key values, an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 *
 * See also:
 * - [<code>xs</code>][GroupBy.xs]`(keyValues) { keyColumns }` — selects the key columns explicitly.
 * - [<code>The Xs Operation</code>][org.jetbrains.kotlinx.dataframe.api.XsDocs] — an overview of all `xs` modes.
 *
 * For more information: [See `xs` on the documentation website.](https://kotlin.github.io/dataframe/xs.html)
 *
 * ### Example
 *
 *
 *
 * The examples below use this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], the same data as the
 * [`xs` page on the documentation website](https://kotlin.github.io/dataframe/xs.html).
 * `firstName` and `lastName` sit inside the `name` [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]:
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
 * @return A new [<code>GroupBy</code>][GroupBy] with the matching key–group pairs and without the key columns.
 */
@Refine
@Interpretable("GroupByXs")
public fun <T, G> GroupBy<T, G>.xs(vararg keyValues: Any?): GroupBy<T, G> {
    // the selector is resolved against `keys` plus the frame column that holds the groups;
    // that frame column is not part of `keys`, so it must not be counted as a key column
    val groupsPath = pathOf(groups.name())
    return xs(*keyValues) {
        colsAtAnyDepth().filter { !it.isColumnGroup() && it.path != groupsPath }.take(keyValues.size)
    }
}

/**
 * Returns a cross-section of this [<code>GroupBy</code>][GroupBy]: only the key–group pairs in which
 * the [<code>key columns</code>][keyColumns] hold the given [<code>keyValues</code>][keyValues], without those key columns.
 *
 *
 *
 * Both parts of the [<code>GroupBy</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy] are filtered: the key–group pairs, and the rows inside the groups.
 * The key columns are removed from [<code>keys</code>][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] and from the groups.
 *
 *
 *
 * The key columns are the columns returned by the [keyColumns] selector,
 * in the order the selector returns them — not in the order they appear in the data.
 * The selector must return exactly one column per key value;
 * otherwise, an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 *
 * A key column that exists in only one part is used in that part only.
 * For example, a column that is not a grouping key exists only in the groups,
 * so only the groups are filtered by it.
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
 * See also:
 * - [<code>xs</code>][GroupBy.xs]`(keyValues)` — takes the first key columns as the key columns.
 * - [<code>The Xs Operation</code>][org.jetbrains.kotlinx.dataframe.api.XsDocs] — an overview of all `xs` modes.
 *
 * For more information: [See `xs` on the documentation website.](https://kotlin.github.io/dataframe/xs.html)
 *
 * ### Example
 *
 *
 *
 * The examples below use this [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], the same data as the
 * [`xs` page on the documentation website](https://kotlin.github.io/dataframe/xs.html).
 * `firstName` and `lastName` sit inside the `name` [<code>column group</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]:
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
 * @param [keyValues] The values that the [<code>key columns</code>][keyColumns] must hold.
 * @param [keyColumns] The [<code>Columns Selector</code>][ColumnsSelector] that defines the key columns.
 * @return A new [<code>GroupBy</code>][GroupBy] with the matching key–group pairs and without the key columns.
 */
@Refine
@Interpretable("GroupByXs")
public fun <T, G, C> GroupBy<T, G>.xs(vararg keyValues: C, keyColumns: ColumnsSelector<T, C>): GroupBy<T, G> =
    xsImpl(*keyValues, keyColumns = keyColumns)

// endregion
