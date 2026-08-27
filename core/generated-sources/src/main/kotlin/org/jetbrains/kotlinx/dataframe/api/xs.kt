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
 * For more information: [See `xs` on the documentation website.](https://kotlin.github.io/dataframe/xs.html)
 */
internal interface XsDocs

// region DataFrame

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
 * For more information: [See `xs` on the documentation website.](https://kotlin.github.io/dataframe/xs.html)
 *
 * See also [filter][org.jetbrains.kotlinx.dataframe.DataFrame.filter], which selects rows by a condition and leaves all columns in place,
 * and [remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove], which only removes columns.
 * ### This `xs` Overload
 * The key columns are the first `n` columns of this [DataFrame],
 * where `n` is the number of given key values.
 * Columns are counted from left to right;
 * a [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is not counted itself, but the columns inside it are.
 * If there are fewer columns than key values, an [IllegalArgumentException] is thrown.
 *
 * ### Example
 *
 * The examples below use this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the same data as the `xs` page on the
 * documentation website. `firstName` and `lastName` sit inside the `name`
 * [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]:
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
 * @return A new [DataFrame] with the matching rows and without the key columns.
 */
@Refine
@Interpretable("DataFrameXs")
public fun <T> DataFrame<T>.xs(vararg keyValues: Any?): DataFrame<T> =
    xs(*keyValues) {
        colsAtAnyDepth().filter { !it.isColumnGroup() }.take(keyValues.size)
    }

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
 * For more information: [See `xs` on the documentation website.](https://kotlin.github.io/dataframe/xs.html)
 *
 * See also [filter][org.jetbrains.kotlinx.dataframe.DataFrame.filter], which selects rows by a condition and leaves all columns in place,
 * and [remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove], which only removes columns.
 * ### This `xs` Overload
 * The key columns are the columns returned by the `keyColumns` selector,
 * in the order the selector returns them — not in the order they appear in the data.
 * The selector must return exactly one column per key value;
 * otherwise, an [IllegalArgumentException] is thrown.
 *
 *
 *
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [Extension Properties][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Example
 *
 * The examples below use this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the same data as the `xs` page on the
 * documentation website. `firstName` and `lastName` sit inside the `name`
 * [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]:
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
 * For more information: [See `xs` on the documentation website.](https://kotlin.github.io/dataframe/xs.html)
 *
 * Both parts of a [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy] are filtered: the key–group pairs, and the rows inside the groups.
 * The key columns are removed from [keys][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] and from the groups.
 * ### This `xs` Overload
 * The key columns are the first `n` columns of [keys][GroupBy.keys],
 * where `n` is the number of given key values.
 * Columns are counted from left to right;
 * a [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is not counted itself, but the columns inside it are.
 * If there are fewer columns than key values, an [IllegalArgumentException] is thrown.
 *
 * ### Example
 *
 * The examples below use this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the same data as the `xs` page on the
 * documentation website. `firstName` and `lastName` sit inside the `name`
 * [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]:
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
 * @return A new [GroupBy] with the matching key–group pairs and without the key columns.
 */
@Refine
@Interpretable("GroupByXs")
public fun <T, G> GroupBy<T, G>.xs(vararg keyValues: Any?): GroupBy<T, G> =
    xs(*keyValues) {
        colsAtAnyDepth().filter { !it.isColumnGroup() }.take(keyValues.size)
    }

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
 * For more information: [See `xs` on the documentation website.](https://kotlin.github.io/dataframe/xs.html)
 *
 * Both parts of a [GroupBy][org.jetbrains.kotlinx.dataframe.api.GroupBy] are filtered: the key–group pairs, and the rows inside the groups.
 * The key columns are removed from [keys][org.jetbrains.kotlinx.dataframe.api.GroupBy.keys] and from the groups.
 * ### This `xs` Overload
 * The key columns are the columns returned by the `keyColumns` selector,
 * in the order the selector returns them — not in the order they appear in the data.
 * The selector must return exactly one column per key value;
 * otherwise, an [IllegalArgumentException] is thrown.
 *
 * A key column that exists in only one part is used in that part only.
 * For example, a column that is not a grouping key exists only in the groups,
 * so only the groups are filtered by it.
 *
 *
 *
 * Select or express columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [Columns Selector][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [Extension Properties][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [Columns Selection DSL Grammar][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * ### Example
 *
 * The examples below use this [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], the same data as the `xs` page on the
 * documentation website. `firstName` and `lastName` sit inside the `name`
 * [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]:
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
 * @param [keyValues] The values that the [key columns][keyColumns] must hold.
 * @param [keyColumns] The [Columns Selector][ColumnsSelector] that defines the key columns.
 * @return A new [GroupBy] with the matching key–group pairs and without the key columns.
 */
@Refine
@Interpretable("GroupByXs")
public fun <T, G, C> GroupBy<T, G>.xs(vararg keyValues: C, keyColumns: ColumnsSelector<T, C>): GroupBy<T, G> =
    xsImpl(*keyValues, keyColumns = keyColumns)

// endregion
