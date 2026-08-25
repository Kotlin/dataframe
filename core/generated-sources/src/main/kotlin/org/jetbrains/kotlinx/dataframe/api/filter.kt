package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.documentation.SelectingRows
import org.jetbrains.kotlinx.dataframe.impl.getTrueIndices
import org.jetbrains.kotlinx.dataframe.indices
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.FILTER_BY
import org.jetbrains.kotlinx.dataframe.util.FILTER_BY_REPLACE
import kotlin.reflect.KProperty

// region DataColumn

/**
 * Returns a new [<code>DataColumn</code>][DataColumn] containing only the elements that match the given [<code>predicate</code>][predicate].
 *
 * For more information: [See `filter` on the documentation website.](https://kotlin.github.io/dataframe/filter.html#filter-on-a-datacolumn)
 *
 * @param predicate the condition used to filter the elements in the DataColumn.
 * @return a new DataColumn containing elements that satisfy the predicate.
 */
public inline fun <T> DataColumn<T>.filter(predicate: Predicate<T>): DataColumn<T> =
    indices
        .filter { predicate(get(it)) }
        .let { get(it) }

// endregion

// region DataFrame

/**
 * Filters the rows of this [<code>DataFrame</code>][DataFrame] based on the provided [<code>RowFilter</code>][RowFilter].
 * Returns a new [<code>DataFrame</code>][DataFrame] containing only the rows that satisfy the given [<code>predicate</code>][predicate].
 *
 *
 *
 * The [predicate] is a [<code>RowFilter</code>][org.jetbrains.kotlinx.dataframe.RowFilter] — a lambda that receives each [<code>DataRow</code>][org.jetbrains.kotlinx.dataframe.DataRow] as both `this` and `it`
 * and is expected to return a [<code>Boolean</code>][Boolean] value.
 *
 * It allows you to define conditions using the row's values directly,
 * including through [<code>extension properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for convenient and type-safe access.
 *
 * Fore more information, [See RowFilter on the documentation website.](https://kotlin.github.io/dataframe/datarow.html#rowfilter)
 *
 *
 *
 * This can include [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] and nested columns.
 *
 * For more information, see: [See `filter` on the documentation website.](https://kotlin.github.io/dataframe/filter.html)
 *
 * See also:
 *  - [<code>drop</code>][DataFrame.drop], which drops rows based on values within the row.
 *  - [<code>distinct</code>][DataFrame.distinct], which filters out rows with duplicated values.
 *
 * ### Example
 * ```kotlin
 * // Select rows where the value in the "age" column is greater than 18
 * // and the "name/firstName" column starts with 'A'
 * df.filter { age > 18 && name.firstName.startsWith("A") }
 * ```
 *
 * @param predicate A lambda that takes a row (twice for compatibility) and returns `true`
 * if the row should be included in the result.
 * @return A new [<code>DataFrame</code>][DataFrame] containing only the rows that satisfy the predicate.
 */
public inline fun <T> DataFrame<T>.filter(predicate: RowFilter<T>): DataFrame<T> =
    indices().filter {
        val row = get(it)
        predicate(row, row)
    }.let { get(it) }

@Deprecated(message = FILTER_BY, replaceWith = ReplaceWith(FILTER_BY_REPLACE), level = DeprecationLevel.ERROR)
public fun <T> DataFrame<T>.filterBy(column: ColumnSelector<T, Boolean>): DataFrame<T> =
    getRows(getColumn(column).toList().getTrueIndices())

@Suppress("DEPRECATION_ERROR")
@Deprecated(message = FILTER_BY, replaceWith = ReplaceWith(FILTER_BY_REPLACE), level = DeprecationLevel.ERROR)
public fun <T> DataFrame<T>.filterBy(column: String): DataFrame<T> = filterBy { column.toColumnOf() }

@Suppress("DEPRECATION_ERROR")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.filterBy(column: ColumnReference<Boolean>): DataFrame<T> = filterBy { column }

@Suppress("DEPRECATION_ERROR")
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.filterBy(column: KProperty<Boolean>): DataFrame<T> = filterBy { column.toColumnAccessor() }

// endregion

internal fun <T, C> ColumnsSelector<T, C>.filter(predicate: (ColumnWithPath<C>) -> Boolean): ColumnsSelector<T, C> =
    { this@filter(it).asColumnSet().filter(predicate) }

// region ColumnsSelectionDsl

/**
 * ## Filter [<code>ColumnSet</code>][ColumnSet] [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface FilterColumnsSelectionDsl {

    /**
     * ## Filter [<code>ColumnSet</code>][ColumnSet] Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[<code>`ColumnSet`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[<code>`ColumnFilter`</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter]
     *
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`filter`**</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]**` { `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
    public interface Grammar {

        /** __`.`__[<code>**`filter`**</code>][ColumnsSelectionDsl.filter] */
        public typealias ColumnSetName = Nothing
    }

    /**
     * ## Filter [<code>ColumnSet</code>][ColumnSet]
     *
     * Creates a subset of columns ([<code>ColumnSet</code>][ColumnSet]) from the current [<code>ColumnSet</code>][ColumnSet] that
     * adhere to the given [<code>predicate</code>][predicate].
     *
     * Aside from calling [<code>filter</code>][ColumnSet.filter] directly, you can also use the [<code>get</code>][ColumnsSelectionDsl.get] operator
     * in most cases. This function belongs to [<code>cols</code>][ColumnsSelectionDsl.cols] but operates identically.
     *
     * For more information: [See `filter` in the Columns Selection DSL on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#filter)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`remove`</code>][DataFrame.remove]`  {  `[<code>`all`</code>][ColumnsSelectionDsl.all]`().`[<code>`filter`</code>][ColumnSet.filter]` { it.`[<code>`hasNulls`</code>][DataColumn.hasNulls]`() } }`
     *
     * `// and although this can be shortened to just the `[<code>`colsOf`</code>][colsOf]` call:`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`colsOf`</code>][colsOf]`<`[<code>`String`</code>][String]`>().`[<code>`filter`</code>][ColumnSet.filter]`  { "e"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][ColumnPath.name]`() } }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`colsOf`</code>][SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][ColumnsSelectionDsl.cols]`{ it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } }`[<code>`]`</code>][ColumnsSelectionDsl.cols]` }`
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][ColumnFilter] that takes a [<code>ColumnReference</code>][ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][ColumnSet] containing the columns that match the given [<code>predicate</code>][predicate].
     * @see [ColumnsSelectionDsl.cols]
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.filter(predicate: (ColumnWithPath<C>) -> Boolean): ColumnSet<C> =
        colsInternal(predicate as ColumnFilter<*>).cast()
}

// endregion
