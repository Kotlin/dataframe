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
 * Replaces the selected [columns\] with new columns.
 *
 * This function does not immediately replace the columns but instead selects columns to replace and
 * returns a [ReplaceClause],
 * which serves as an intermediate step.
 * The [ReplaceClause] object provides methods to replace the selected columns using:
 * @include [ReplaceMethodsSnippet]
 *
 * Each method returns a new [DataFrame].
 * Every new column takes the place of the column it replaces, in the same column group,
 * but keeps its own name, so the name of the column can change.
 *
 * Check out [Grammar].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * See [Selecting Columns][ReplaceSelectingOptions].
 *
 * For more information: {@include [DocumentationUrls.Replace]}
 *
 * See also:
 * - [convert][DataFrame.convert]`.`[asColumn][Convert.asColumn] - also builds new columns from the selected ones,
 * but keeps their names. Use it in projects with the compiler plugin, which does not track `replace`:
 * after `replace`, the extension properties keep the old column names and types.
 * - [replaceAll][DataFrame.replaceAll] - replaces values in the columns, not the columns themselves.
 * Don't confuse the two operations.
 */
internal interface ReplaceDocs {

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetReplaceOperationArg]}
     */
    typealias ReplaceSelectingOptions = Nothing

    /**
     * ## Replace Operation Grammar
     * @include [LineBreak]
     * @include [DslGrammarLink]
     * @include [LineBreak]
     *
     * [**`replace`**][replace]**`  { `**`columnsSelector: `[`ColumnsSelector`][ColumnsSelector]`  `**`}`**
     *
     * @include [Indent]
     * `| `__`.`__[**`with`**][ReplaceClause.with]**`(`**`newColumns: `[`DataColumn`][DataColumn]**`, ..)`**
     *
     * @include [Indent]
     * `| `__`.`__[**`with`**][ReplaceClause.with]**`(`**`newColumns: `[`List`][List]`<`[`DataColumn`][DataColumn]`>`**`)`**
     *
     * @include [Indent]
     * `| `__`.`__[**`with`**][ReplaceClause.with]**`  { `**`transform: `[`ColumnsContainer`][ColumnsContainer]`.(`[`DataColumn`][DataColumn]`) -> `[`DataColumn`][DataColumn]` `**`}`**
     */
    typealias Grammar = Nothing
}

/**
 * - [with(newColumns)][ReplaceClause.with] - replaces the selected columns with the given columns, one by one.
 * - [with { transform }][ReplaceClause.with] - replaces each selected column with the column
 * returned by a lambda.
 */
@ExcludeFromSources
private typealias ReplaceMethodsSnippet = Nothing

/**
 * If you select a column group together with a column inside it, only the group is replaced:
 * the inner column is skipped. It is not passed to the lambda, no new column is used for it,
 * and no error is reported (#418). Select either the group or the columns inside it.
 */
@ExcludeFromSources
private typealias ReplaceGroupAndChildSnippet = Nothing

/**
 * Every new column takes the place of the column it replaces, in the same column group,
 * but keeps its own name.
 * It must have as many rows as the [DataFrame], otherwise an [UnequalColumnSizesException] is thrown.
 * If its name is already used by another column in the same column group that is not replaced,
 * a [DuplicateColumnPathInsertException] is thrown.
 */
@ExcludeFromSources
private typealias ReplaceNewColumnRulesSnippet = Nothing

/**
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
 */
@ExcludeFromSources
private typealias ReplaceWithTransformExamples = Nothing

/** {@set [SelectingColumns.OPERATION] [replace][replace]} */
@ExcludeFromSources
private typealias SetReplaceOperationArg = Nothing

/**
 * @include [ReplaceDocs]
 * ### This Replace Overload
 */
@ExcludeFromSources
private typealias CommonReplaceDocs = Nothing

/**
 * @include [CommonReplaceDocs]
 * @include [SelectingColumns.ColumnsSelectionDsl] {@include [SetReplaceOperationArg]}
 *
 * @include [ReplaceGroupAndChildSnippet]
 * @include [ReplaceWithTransformExamples]
 * @param [columns\] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame] to replace.
 * @return A [ReplaceClause] for specifying the new columns.
 */
public fun <T, C> DataFrame<T>.replace(columns: ColumnsSelector<T, C>): ReplaceClause<T, C> =
    ReplaceClause(this, columns)

/**
 * @include [CommonReplaceDocs]
 * @include [SelectingColumns.ColumnNamesApi] {@include [SetReplaceOperationArg]}
 * ### Examples:
 * ```kotlin
 * // Replace the column group "name" with its "firstName" column
 * df.replace("name").with { this[pathOf("name", "firstName")] }
 *
 * // Replace "age" with a new column "year"
 * df.replace("age").with { 2021 - it.cast<Int>() named "year" }
 * ```
 * @param [columns\] The [Column Names][String] used to select the columns of this [DataFrame] to replace.
 * @return A [ReplaceClause] for specifying the new columns.
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
 * Replaces values in this [DataFrame]: every cell equal to the first value of a pair gets the second value
 * of that pair.
 *
 * Unlike [replace], which replaces whole columns, `replaceAll` changes only the matching values.
 * The names and the positions of the columns stay the same, and so do the values without a match.
 * If the same value is given in several pairs, the last pair wins.
 * A value can be replaced with `null`, and `null` can be replaced with a value.
 *
 * By default, values are replaced in all columns at any depth, so the columns inside column groups are included.
 * The dataframes inside frame columns are not changed.
 * Use [columns] to replace values only in some of the columns.
 *
 * A new value has to fit the type of the column where it is put,
 * otherwise an [IllegalStateException] is thrown.
 * `null` fits any column: the column becomes nullable.
 * For example, `df.replaceAll(null to "Unknown")` fails if a column of [Int] values contains `null`;
 * select only the [String] columns in this case.
 *
 * For more information: {@include [DocumentationUrls.Replace]}
 *
 * It is a special case of [update][DataFrame.update], which computes new values with an expression.
 *
 * See also [fillNulls][DataFrame.fillNulls], which replaces `null` values.
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
 * @param [columns] The [Columns Selector][ColumnsSelector] used to select the columns to replace values in.
 * By default, all columns at any depth except column groups themselves.
 * @return A new [DataFrame] with the values replaced.
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
 * An intermediate class used in the [replace] operation.
 *
 * This class itself does not replace anything — it is a transitional step
 * before specifying the new columns.
 * It must be followed by one of the [with][ReplaceClause.with] methods
 * to produce a new [DataFrame] with the selected columns replaced:
 * @include [ReplaceMethodsSnippet]
 *
 * For more information: {@include [DocumentationUrls.Replace]}
 *
 * See [Grammar][ReplaceDocs.Grammar] for more details.
 */
public class ReplaceClause<T, C>(internal val df: DataFrame<T>, internal val columns: ColumnsSelector<T, C>) {
    override fun toString(): String = "ReplaceClause(df=$df, columns=$columns)"
}

/**
 * Replaces the columns previously selected with [replace] with the given new columns.
 *
 * The first selected column is replaced with the first new column, the second one with the second, and so on,
 * in the order of the selection, not in the order of the columns in the [DataFrame].
 * If there are fewer new columns than selected columns, an [IllegalArgumentException] is thrown.
 * Extra new columns are ignored.
 *
 * @include [ReplaceNewColumnRulesSnippet]
 *
 * @include [ReplaceGroupAndChildSnippet]
 *
 * For more information: {@include [DocumentationUrls.Replace]}
 *
 * See [Grammar][ReplaceDocs.Grammar] for more details.
 */
@ExcludeFromSources
private typealias ReplaceWithColumnsDocs = Nothing

/**
 * @include [ReplaceWithColumnsDocs]
 *
 * ### Example:
 * ```kotlin
 * // "weight" is selected first, so it is replaced with "weightInGrams",
 * // although "age" comes first in the dataframe
 * df.replace { weight and age }.with(df.weight * 1000 named "weightInGrams", df.age * 12 named "ageInMonths")
 * ```
 *
 * @param [columns] The new columns, one for each selected column.
 * @return A new [DataFrame] with the selected columns replaced.
 */
public fun <T, C> ReplaceClause<T, C>.with(vararg columns: AnyCol): DataFrame<T> = with(columns.toList())

/**
 * @include [ReplaceWithColumnsDocs]
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
 * @return A new [DataFrame] with the selected columns replaced.
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
 * Replaces each column previously selected with [replace] with the column returned by [transform].
 *
 * [transform] is called once for every selected column, in the order of the selection.
 * Its receiver gives access to all the columns of the original [DataFrame], including the ones being replaced,
 * and its argument is the selected column.
 *
 * @include [ReplaceNewColumnRulesSnippet]
 * Return a column with the same name to keep the name,
 * or use [rename][DataColumn.rename] or [named] to change it.
 *
 * @include [ReplaceGroupAndChildSnippet]
 *
 * To keep the names of the columns and change only their values and types, use
 * [convert][DataFrame.convert]`.`[asColumn][Convert.asColumn] instead.
 * Use it in projects with the compiler plugin, which does not track `replace`:
 * after `replace`, the extension properties keep the old column names and types.
 *
 * For more information: {@include [DocumentationUrls.Replace]}
 *
 * See [Grammar][ReplaceDocs.Grammar] for more details.
 *
 * @include [ReplaceWithTransformExamples]
 *
 * @param [transform] The lambda that returns the new column for each selected column.
 * @return A new [DataFrame] with the selected columns replaced.
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
