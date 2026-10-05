package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyCol
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.BaseColumn
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarLink
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException
import org.jetbrains.kotlinx.dataframe.get
import org.jetbrains.kotlinx.dataframe.impl.api.ColumnToInsert
import org.jetbrains.kotlinx.dataframe.impl.api.insertImpl
import org.jetbrains.kotlinx.dataframe.impl.api.removeImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region replace

/**
 * Replaces the selected [columns] with new columns.
 *
 * This function does not immediately replace the columns but instead selects columns to replace and
 * returns a [<code>ReplaceClause</code>][ReplaceClause],
 * which serves as an intermediate step.
 * The [<code>ReplaceClause</code>][ReplaceClause] object provides methods to replace the selected columns using:
 * - [<code>with(newColumns)</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceClause.with] - replaces the selected columns with the given columns, one by one.
 * - [<code>with { transform }</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceClause.with] - replaces each selected column with the column
 * returned by a lambda.
 *
 * Each method returns a new [<code>DataFrame</code>][DataFrame].
 * Every new column takes the place of the column it replaces, in the same column group,
 * but keeps its own name, so the name of the column can change.
 *
 * Check out [<code>Grammar</code>][Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][ReplaceSelectingOptions].
 *
 * For more information: [See `replace` on the documentation website.](https://kotlin.github.io/dataframe/replace.html)
 *
 * See also:
 * - [<code>convert</code>][DataFrame.convert]`.`[<code>asColumn</code>][Convert.asColumn] - also builds new columns from the selected ones,
 * but keeps their names. Use it in projects with the compiler plugin, which does not track `replace`:
 * after `replace`, the extension properties keep the old column names and types.
 * - [<code>replaceAll</code>][DataFrame.replaceAll] - replaces values in the columns, not the columns themselves.
 * Don't confuse the two operations.
 */
internal interface ReplaceDocs {

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
     * <code>`df`</code>`.`[<code>replace</code>][org.jetbrains.kotlinx.dataframe.api.replace]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
     *
     * <code>`df`</code>`.`[<code>replace</code>][org.jetbrains.kotlinx.dataframe.api.replace]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
     *
     * <code>`df`</code>`.`[<code>replace</code>][org.jetbrains.kotlinx.dataframe.api.replace]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
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
     * <code>`df`</code>`.`[<code>replace</code>][org.jetbrains.kotlinx.dataframe.api.replace]`("length", "age")`
     *
     *
     *
     */
    typealias ReplaceSelectingOptions = Nothing

    /**
     * ## Replace Operation Grammar
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [<code>**`replace`**</code>][replace]**`  { `**`columnsSelector: `[<code>`ColumnsSelector`</code>][ColumnsSelector]`  `**`}`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`with`**</code>][ReplaceClause.with]**`(`**`newColumns: `[<code>`DataColumn`</code>][DataColumn]**`, ..)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`with`**</code>][ReplaceClause.with]**`(`**`newColumns: `[<code>`List`</code>][List]`<`[<code>`DataColumn`</code>][DataColumn]`>`**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     * `| `__`.`__[<code>**`with`**</code>][ReplaceClause.with]**`  { `**`transform: `[<code>`ColumnsContainer`</code>][ColumnsContainer]`.(`[<code>`DataColumn`</code>][DataColumn]`) -> `[<code>`DataColumn`</code>][DataColumn]` `**`}`**
     */
    typealias Grammar = Nothing
}

/**
 * Replaces the selected [columns] with new columns.
 *
 * This function does not immediately replace the columns but instead selects columns to replace and
 * returns a [<code>ReplaceClause</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceClause],
 * which serves as an intermediate step.
 * The [<code>ReplaceClause</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceClause] object provides methods to replace the selected columns using:
 * - [<code>with(newColumns)</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceClause.with] - replaces the selected columns with the given columns, one by one.
 * - [<code>with { transform }</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceClause.with] - replaces each selected column with the column
 * returned by a lambda.
 *
 * Each method returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * Every new column takes the place of the column it replaces, in the same column group,
 * but keeps its own name, so the name of the column can change.
 *
 * Check out [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceDocs.Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceDocs.ReplaceSelectingOptions].
 *
 * For more information: [See `replace` on the documentation website.](https://kotlin.github.io/dataframe/replace.html)
 *
 * See also:
 * - [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert]`.`[<code>asColumn</code>][org.jetbrains.kotlinx.dataframe.api.Convert.asColumn] - also builds new columns from the selected ones,
 * but keeps their names. Use it in projects with the compiler plugin, which does not track `replace`:
 * after `replace`, the extension properties keep the old column names and types.
 * - [<code>replaceAll</code>][org.jetbrains.kotlinx.dataframe.DataFrame.replaceAll] - replaces values in the columns, not the columns themselves.
 * Don't confuse the two operations.
 * ### This Replace Overload
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
 * If you select a column group together with a column inside it, only the group is replaced:
 * the inner column is skipped. It is not passed to the lambda, no new column is used for it,
 * and no error is reported (#418). Select either the group or the columns inside it.
 * ### Examples:
 * ```kotlin
 * // Replace the column group "name" with its "firstName" column
 * df.replace { name }.with { name.firstName }
 *
 * // Lowercase the top-level String columns; the names stay the same
 * df.replace { colsOf<String?>() }.with { col -> col.map { it?.lowercase() } }
 *
 * // Replace "age" with a new column "year"
 * df.replace { age }.with { 2021 - age named "year" }
 * ```
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns of this [<code>DataFrame</code>][DataFrame] to replace.
 * @return A [<code>ReplaceClause</code>][ReplaceClause] for specifying the new columns.
 */
public fun <T, C> DataFrame<T>.replace(columns: ColumnsSelector<T, C>): ReplaceClause<T, C> =
    ReplaceClause(this, columns)

/**
 * Replaces the selected [columns] with new columns.
 *
 * This function does not immediately replace the columns but instead selects columns to replace and
 * returns a [<code>ReplaceClause</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceClause],
 * which serves as an intermediate step.
 * The [<code>ReplaceClause</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceClause] object provides methods to replace the selected columns using:
 * - [<code>with(newColumns)</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceClause.with] - replaces the selected columns with the given columns, one by one.
 * - [<code>with { transform }</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceClause.with] - replaces each selected column with the column
 * returned by a lambda.
 *
 * Each method returns a new [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * Every new column takes the place of the column it replaces, in the same column group,
 * but keeps its own name, so the name of the column can change.
 *
 * Check out [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceDocs.Grammar].
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * See [<code>Selecting Columns</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceDocs.ReplaceSelectingOptions].
 *
 * For more information: [See `replace` on the documentation website.](https://kotlin.github.io/dataframe/replace.html)
 *
 * See also:
 * - [<code>convert</code>][org.jetbrains.kotlinx.dataframe.DataFrame.convert]`.`[<code>asColumn</code>][org.jetbrains.kotlinx.dataframe.api.Convert.asColumn] - also builds new columns from the selected ones,
 * but keeps their names. Use it in projects with the compiler plugin, which does not track `replace`:
 * after `replace`, the extension properties keep the old column names and types.
 * - [<code>replaceAll</code>][org.jetbrains.kotlinx.dataframe.DataFrame.replaceAll] - replaces values in the columns, not the columns themselves.
 * Don't confuse the two operations.
 * ### This Replace Overload
 *
 *
 * Select single or multiple columns using their names as [<code>String</code>][String]s.
 * ([<code>String API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.StringApi]).
 * ### Examples:
 * ```kotlin
 * // Replace the column group "name" with its "firstName" column
 * df.replace("name").with { this[pathOf("name", "firstName")] }
 *
 * // Replace "age" with a new column "year"
 * df.replace("age").with { 2021 - it.cast<Int>() named "year" }
 * ```
 * @param [columns] The [<code>Column Names</code>][String] used to select the columns of this [<code>DataFrame</code>][DataFrame] to replace.
 * @return A [<code>ReplaceClause</code>][ReplaceClause] for specifying the new columns.
 */
public fun <T> DataFrame<T>.replace(vararg columns: String): ReplaceClause<T, Any?> = replace { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.replace(vararg columns: ColumnReference<C>): ReplaceClause<T, C> =
    replace { columns.toColumnSet() }

@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T, C> DataFrame<T>.replace(vararg columns: KProperty<C>): ReplaceClause<T, C> =
    replace { columns.toColumnSet() }

// endregion

// region replaceAll

/**
 * Replaces values in this [<code>DataFrame</code>][DataFrame]: every cell equal to the first value of a pair gets the second value
 * of that pair.
 *
 * Unlike [<code>replace</code>][replace], which replaces whole columns, `replaceAll` changes only the matching values.
 * The names and the positions of the columns stay the same, and so do the values without a match.
 * If the same value is given in several pairs, the last pair wins.
 * A value can be replaced with `null`, and `null` can be replaced with a value.
 *
 * By default, values are replaced in all columns at any depth, so the columns inside column groups are included.
 * The dataframes inside frame columns are not changed.
 * Use [<code>columns</code>][columns] to replace values only in some of the columns.
 *
 * A new value has to fit the type of the column where it is put,
 * otherwise an [<code>IllegalStateException</code>][IllegalStateException] is thrown.
 * `null` fits any column: the column becomes nullable.
 * For example, `df.replaceAll(null to "Unknown")` fails if a column of [<code>Int</code>][Int] values contains `null`;
 * select only the [<code>String</code>][String] columns in this case.
 *
 * For more information: [See `replace` on the documentation website.](https://kotlin.github.io/dataframe/replace.html)
 *
 * It is a special case of [<code>update</code>][DataFrame.update], which computes new values with an expression.
 *
 * See also [<code>fillNulls</code>][DataFrame.fillNulls], which replaces `null` values.
 *
 * ### Examples:
 * ```kotlin
 * // Replace "Alice" with "Alicia" in all columns, including "firstName" inside the column group "name",
 * // and "Moscow" with null
 * df.replaceAll("Alice" to "Alicia", "Moscow" to null)
 *
 * // Replace nulls with "Unknown" only in the "city" column
 * df.replaceAll(null to "Unknown", columns = { city })
 * ```
 *
 * @param [valuePairs] Pairs of an old value and the new value to put in its place.
 * @param [columns] The [<code>Columns Selector</code>][ColumnsSelector] used to select the columns to replace values in.
 * By default, all columns at any depth except column groups themselves.
 * @return A new [<code>DataFrame</code>][DataFrame] with the values replaced.
 */
public fun <T> DataFrame<T>.replaceAll(
    vararg valuePairs: Pair<Any?, Any?>,
    columns: ColumnsSelector<T, *> = { colsAtAnyDepth().filter { !it.isColumnGroup() } },
): DataFrame<T> {
    val map = valuePairs.toMap()
    // `map[it] ?: it` would treat a pair mapped to `null` as "no replacement"
    return update(columns).with { if (it in map) map[it] else it }
}

// endregion

// region ReplaceClause

/**
 * An intermediate class used in the [<code>replace</code>][replace] operation.
 *
 * This class itself does not replace anything — it is a transitional step
 * before specifying the new columns.
 * It must be followed by one of the [<code>with</code>][ReplaceClause.with] methods
 * to produce a new [<code>DataFrame</code>][DataFrame] with the selected columns replaced:
 * - [<code>with(newColumns)</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceClause.with] - replaces the selected columns with the given columns, one by one.
 * - [<code>with { transform }</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceClause.with] - replaces each selected column with the column
 * returned by a lambda.
 *
 * For more information: [See `replace` on the documentation website.](https://kotlin.github.io/dataframe/replace.html)
 *
 * See [<code>Grammar</code>][ReplaceDocs.Grammar] for more details.
 */
public class ReplaceClause<T, C>(internal val df: DataFrame<T>, internal val columns: ColumnsSelector<T, C>) {
    override fun toString(): String = "ReplaceClause(df=$df, columns=$columns)"
}

/**
 * Replaces the columns previously selected with [<code>replace</code>][org.jetbrains.kotlinx.dataframe.api.replace] with the given new columns.
 *
 * The first selected column is replaced with the first new column, the second one with the second, and so on,
 * in the order of the selection, not in the order of the columns in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * If there are fewer new columns than selected columns, an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * Extra new columns are ignored.
 *
 * Every new column takes the place of the column it replaces, in the same column group,
 * but keeps its own name.
 * It must have as many rows as the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], otherwise an [<code>UnequalColumnSizesException</code>][org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException] is thrown.
 * If its name is already used by another column in the same column group that is not replaced,
 * a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown.
 *
 * If you select a column group together with a column inside it, only the group is replaced:
 * the inner column is skipped. It is not passed to the lambda, no new column is used for it,
 * and no error is reported (#418). Select either the group or the columns inside it.
 *
 * For more information: [See `replace` on the documentation website.](https://kotlin.github.io/dataframe/replace.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceDocs.Grammar] for more details.
 *
 * ### Example:
 * ```kotlin
 * // "weight" is selected first, so it is replaced with "weightInGrams",
 * // although "age" comes first in the dataframe
 * df.replace { weight and age }.with(df.weight * 1000 named "weightInGrams", df.age * 12 named "ageInMonths")
 * ```
 *
 * @param [columns] The new columns, one for each selected column.
 * @return A new [<code>DataFrame</code>][DataFrame] with the selected columns replaced.
 */
public fun <T, C> ReplaceClause<T, C>.with(vararg columns: AnyCol): DataFrame<T> = with(columns.toList())

/**
 * Replaces the columns previously selected with [<code>replace</code>][org.jetbrains.kotlinx.dataframe.api.replace] with the given new columns.
 *
 * The first selected column is replaced with the first new column, the second one with the second, and so on,
 * in the order of the selection, not in the order of the columns in the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame].
 * If there are fewer new columns than selected columns, an [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
 * Extra new columns are ignored.
 *
 * Every new column takes the place of the column it replaces, in the same column group,
 * but keeps its own name.
 * It must have as many rows as the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], otherwise an [<code>UnequalColumnSizesException</code>][org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException] is thrown.
 * If its name is already used by another column in the same column group that is not replaced,
 * a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown.
 *
 * If you select a column group together with a column inside it, only the group is replaced:
 * the inner column is skipped. It is not passed to the lambda, no new column is used for it,
 * and no error is reported (#418). Select either the group or the columns inside it.
 *
 * For more information: [See `replace` on the documentation website.](https://kotlin.github.io/dataframe/replace.html)
 *
 * See [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ReplaceDocs.Grammar] for more details.
 *
 * ### Example:
 * ```kotlin
 * // "weight" is selected first, so it is replaced with "weightInGrams",
 * // although "age" comes first in the dataframe
 * val newColumns = listOf(df.weight * 1000 named "weightInGrams", df.age * 12 named "ageInMonths")
 * df.replace { weight and age }.with(newColumns)
 * ```
 *
 * @param [newColumns] The new columns, one for each selected column.
 * @return A new [<code>DataFrame</code>][DataFrame] with the selected columns replaced.
 */
public fun <T, C> ReplaceClause<T, C>.with(newColumns: List<AnyCol>): DataFrame<T> {
    var index = 0
    return with {
        require(index < newColumns.size) {
            "Insufficient number of new columns in 'replace': ${newColumns.size} instead of ${df[columns].size}"
        }
        newColumns[index++]
    }
}

// TODO(#418): when a column group and its child are both selected, only the group is replaced;
//  the child is silently skipped

/**
 * Replaces each column previously selected with [<code>replace</code>][replace] with the column returned by [<code>transform</code>][transform].
 *
 * [<code>transform</code>][transform] is called once for every selected column, in the order of the selection.
 * Its receiver gives access to all the columns of the original [<code>DataFrame</code>][DataFrame], including the ones being replaced,
 * and its argument is the selected column.
 *
 * Every new column takes the place of the column it replaces, in the same column group,
 * but keeps its own name.
 * It must have as many rows as the [<code>DataFrame</code>][org.jetbrains.kotlinx.dataframe.DataFrame], otherwise an [<code>UnequalColumnSizesException</code>][org.jetbrains.kotlinx.dataframe.exceptions.UnequalColumnSizesException] is thrown.
 * If its name is already used by another column in the same column group that is not replaced,
 * a [<code>DuplicateColumnPathInsertException</code>][org.jetbrains.kotlinx.dataframe.api.DuplicateColumnPathInsertException] is thrown.
 * Return a column with the same name to keep the name,
 * or use [<code>rename</code>][DataColumn.rename] or [<code>named</code>][named] to change it.
 *
 * If you select a column group together with a column inside it, only the group is replaced:
 * the inner column is skipped. It is not passed to the lambda, no new column is used for it,
 * and no error is reported (#418). Select either the group or the columns inside it.
 *
 * To keep the names of the columns and change only their values and types, use
 * [<code>convert</code>][DataFrame.convert]`.`[<code>asColumn</code>][Convert.asColumn] instead.
 * Use it in projects with the compiler plugin, which does not track `replace`:
 * after `replace`, the extension properties keep the old column names and types.
 *
 * For more information: [See `replace` on the documentation website.](https://kotlin.github.io/dataframe/replace.html)
 *
 * See [<code>Grammar</code>][ReplaceDocs.Grammar] for more details.
 *
 * ### Examples:
 * ```kotlin
 * // Replace the column group "name" with its "firstName" column
 * df.replace { name }.with { name.firstName }
 *
 * // Lowercase the top-level String columns; the names stay the same
 * df.replace { colsOf<String?>() }.with { col -> col.map { it?.lowercase() } }
 *
 * // Replace "age" with a new column "year"
 * df.replace { age }.with { 2021 - age named "year" }
 * ```
 *
 * @param [transform] The lambda that returns the new column for each selected column.
 * @return A new [<code>DataFrame</code>][DataFrame] with the selected columns replaced.
 */
public fun <T, C> ReplaceClause<T, C>.with(
    transform: ColumnsContainer<T>.(DataColumn<C>) -> BaseColumn<*>,
): DataFrame<T> {
    val removeResult = df.removeImpl(columns = columns)
    val toInsert = removeResult.removedColumns.map {
        @Suppress("UNCHECKED_CAST")
        val newCol = transform(df, it.data.column as DataColumn<C>)
        ColumnToInsert(it.pathFromRoot().dropLast(1) + newCol.name, newCol, it)
    }
    return removeResult.df.insertImpl(toInsert)
}

// endregion
