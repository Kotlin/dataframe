package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropDocs
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.documentation.SelectingRows
import org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataColumn

/**
 * Returns a [<code>DataColumn</code>][DataColumn] containing the first [<code>n</code>][n] values of this [<code>DataColumn</code>][DataColumn].
 *
 * If [<code>n</code>][n] is greater than or equal to the size of this [<code>DataColumn</code>][DataColumn], this [<code>DataColumn</code>][DataColumn] is returned as is.
 *
 * For more information: [See `take` on the documentation website.](https://kotlin.github.io/dataframe/slicerows.html#take)
 *
 * See also:
 * - [<code>takeLast</code>][DataColumn.takeLast] — takes the last [<code>n</code>][n] values instead.
 * - [<code>drop</code>][DataColumn.drop]`(n: Int)` — drops the first [<code>n</code>][n] values.
 * - [<code>dropLast</code>][DataColumn.dropLast] — drops the last [<code>n</code>][n] values.
 * - [<code>drop</code>][DataColumn.drop]`{ predicate: Predicate<T> }` — drops every value that matches the predicate.
 *
 * @param [n] The number of values to take. Must not be negative.
 * @return A [<code>DataColumn</code>][DataColumn] containing the first [<code>n</code>][n] values of this [<code>DataColumn</code>][DataColumn],
 * or this [<code>DataColumn</code>][DataColumn] if [<code>n</code>][n] is greater than or equal to its size.
 * @throws [IllegalArgumentException] if [<code>n</code>][n] is negative.
 */
public fun <T> DataColumn<T>.take(n: Int): DataColumn<T> =
    when {
        n == 0 -> get(emptyList())
        n >= size -> this
        else -> get(0 until n)
    }

/**
 * Returns a [<code>DataColumn</code>][DataColumn] containing the last [<code>n</code>][n] values of this [<code>DataColumn</code>][DataColumn].
 *
 * If [<code>n</code>][n] is zero or negative, an empty [<code>DataColumn</code>][DataColumn] is returned.
 *
 * For more information: [See `takeLast` on the documentation website.](https://kotlin.github.io/dataframe/slicerows.html#takelast)
 *
 * See also:
 * - [<code>take</code>][DataColumn.take] — takes the first [<code>n</code>][n] values instead.
 * - [<code>dropLast</code>][DataColumn.dropLast] — drops the last [<code>n</code>][n] values.
 * - [<code>drop</code>][DataColumn.drop]`(n: Int)` — drops the first [<code>n</code>][n] values.
 * - [<code>drop</code>][DataColumn.drop]`{ predicate: Predicate<T> }` — drops every value that matches the predicate.
 *
 * @param [n] The number of values to take. Must not exceed the size of this [<code>DataColumn</code>][DataColumn].
 * @return A [<code>DataColumn</code>][DataColumn] containing the last [<code>n</code>][n] values of this [<code>DataColumn</code>][DataColumn],
 * or an empty [<code>DataColumn</code>][DataColumn] if [<code>n</code>][n] is zero or negative.
 * @throws [IndexOutOfBoundsException] if [<code>n</code>][n] is greater than the size of this [<code>DataColumn</code>][DataColumn].
 */
public fun <T> DataColumn<T>.takeLast(n: Int = 1): DataColumn<T> = drop(size - n)

// endregion

// region DataFrame

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing the first [<code>n</code>][n] rows.
 *
 * If [<code>n</code>][n] is greater than or equal to the number of rows, the whole [<code>DataFrame</code>][DataFrame] is returned.
 *
 * For more information: [See `take` on the documentation website.](https://kotlin.github.io/dataframe/slicerows.html#take)
 *
 * See also:
 * - [<code>takeLast</code>][DataFrame.takeLast] — takes the last [<code>n</code>][n] rows instead.
 * - [<code>takeWhile</code>][DataFrame.takeWhile] — takes the first rows while the predicate holds.
 * - [<code>drop</code>][DataFrame.drop]`(n: Int)` — drops the first [<code>n</code>][n] rows.
 * - [<code>filter</code>][DataFrame.filter] — keeps every row that matches the predicate.
 *
 * @param [n] The number of rows to take. Must not be negative.
 * @return A [<code>DataFrame</code>][DataFrame] containing the first [<code>n</code>][n] rows,
 * or the whole [<code>DataFrame</code>][DataFrame] if [<code>n</code>][n] is greater than or equal to the number of rows.
 * @throws IllegalArgumentException if [<code>n</code>][n] is negative.
 */
public fun <T> DataFrame<T>.take(n: Int): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return getRows(0 until n.coerceAtMost(nrow))
}

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing the last [<code>n</code>][n] rows.
 *
 * If [<code>n</code>][n] is greater than or equal to the number of rows, the whole [<code>DataFrame</code>][DataFrame] is returned.
 *
 * For more information: [See `takeLast` on the documentation website.](https://kotlin.github.io/dataframe/slicerows.html#takelast)
 *
 * See also:
 * - [<code>take</code>][DataFrame.take] — takes the first [<code>n</code>][n] rows instead.
 * - [<code>takeWhile</code>][DataFrame.takeWhile] — takes the first rows while the predicate holds.
 * - [<code>dropLast</code>][DataFrame.dropLast] — drops the last [<code>n</code>][n] rows.
 * - [<code>filter</code>][DataFrame.filter] — keeps every row that matches the predicate.
 *
 * @param [n] The number of rows to take. Must not be negative.
 * @return A [<code>DataFrame</code>][DataFrame] containing the last [<code>n</code>][n] rows,
 * or the whole [<code>DataFrame</code>][DataFrame] if [<code>n</code>][n] is greater than or equal to the number of rows.
 * @throws IllegalArgumentException if [<code>n</code>][n] is negative.
 */
public fun <T> DataFrame<T>.takeLast(n: Int = 1): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return drop((nrow - n).coerceAtLeast(0))
}

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing the first rows that satisfy the given [<code>predicate</code>][predicate].
 *
 * Rows are taken for as long as the [<code>predicate</code>][predicate] holds; the operation stops at the first row that
 * does not satisfy it, and no later row is taken even if it satisfies the [<code>predicate</code>][predicate].
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
 * For more information: [See `takeWhile` on the documentation website.](https://kotlin.github.io/dataframe/slicerows.html#takewhile)
 *
 * See also:
 * - [<code>take</code>][DataFrame.take] — takes a fixed number of first rows.
 * - [<code>takeLast</code>][DataFrame.takeLast] — takes a fixed number of last rows.
 * - [<code>dropWhile</code>][DataFrame.dropWhile] — drops the first rows while the predicate holds.
 * - [<code>filter</code>][DataFrame.filter] — keeps every row that matches the predicate.
 *
 * @param [predicate] The [<code>RowFilter</code>][RowFilter] that the leading rows to take must satisfy.
 * @return A [<code>DataFrame</code>][DataFrame] containing the first rows that satisfy the [<code>predicate</code>][predicate].
 */
public inline fun <T> DataFrame<T>.takeWhile(predicate: RowFilter<T>): DataFrame<T> =
    firstOrNull { !predicate(it, it) }?.let { take(it.index()) } ?: this

// endregion

// region ColumnsSelectionDsl

/**
 * ## Take [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface TakeColumnsSelectionDsl {

    /**
     * ## Take (Last) (Cols) (While) Grammar
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
     *  `columnGroup: `[<code>`SingleColumn`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[<code>`DataRow`</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[<code>`ColumnFilter`</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `number: `[<code>`Int`</code>][Int]
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called directly in the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>**`take`**</code>][ColumnsSelectionDsl.take]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.takeLast]`)`**`(`**[<code>`number`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     *  `| `[<code>**`take`**</code>][ColumnsSelectionDsl.takeWhile]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.takeLastWhile]`)`[<code>**`While`**</code>][ColumnsSelectionDsl.takeWhile]**`  {  `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`take`**</code>][ColumnsSelectionDsl.take]`(`[<code>**`Last`**</code>][ColumnSet.takeLast]`)`**`(`**[<code>`number`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`take`**</code>][ColumnsSelectionDsl.takeWhile]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.takeLastWhile]`)`[<code>**`While`**</code>][ColumnsSelectionDsl.takeWhile]**`  {  `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>Column Group (reference)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnGroup`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`take`**</code>][ColumnsSelectionDsl.takeCols]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.takeLastCols]`)`[<code>**`Cols`**</code>][ColumnsSelectionDsl.takeCols]**`(`**[<code>`number`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`take`**</code>][ColumnsSelectionDsl.takeColsWhile]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.takeLastColsWhile]`)`[<code>**`ColsWhile`**</code>][ColumnsSelectionDsl.takeColsWhile]**`  {  `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
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

        /** [<code>**`take`**</code>][ColumnsSelectionDsl.take]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.takeLast]`)` */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`take`**</code>][ColumnsSelectionDsl.take]`(`[<code>**`Last`**</code>][ColumnSet.takeLast]`)` */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`take`**</code>][ColumnsSelectionDsl.takeCols]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.takeLastCols]`)`[<code>**`Cols`**</code>][ColumnsSelectionDsl.takeCols] */
        public typealias ColumnGroupName = Nothing

        /** [<code>**`take`**</code>][ColumnsSelectionDsl.takeWhile]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.takeLastWhile]`)`[<code>**`While`**</code>][ColumnsSelectionDsl.takeWhile] */
        public typealias PlainDslWhileName = Nothing

        /** __`.`__[<code>**`take`**</code>][ColumnsSelectionDsl.takeWhile]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.takeLastWhile]`)`[<code>**`While`**</code>][ColumnsSelectionDsl.takeWhile] */
        public typealias ColumnSetWhileName = Nothing

        /** __`.`__[<code>**`take`**</code>][ColumnsSelectionDsl.takeColsWhile]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.takeLastColsWhile]`)`[<code>**`ColsWhile`**</code>][ColumnsSelectionDsl.takeColsWhile] */
        public typealias ColumnGroupWhileName = Nothing
    }

    // region take

    /**
     * ## Take (Cols)
     * This collects the first [n] columns from `this` into a [<code>ColumnSet</code>][ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>take</code>][ColumnSet.take]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>take</code>][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>takeCols</code>][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>takeCols</code>][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * See also:
     * - [<code>takeLast</code>][ColumnsSelectionDsl.takeLast] — takes the last `n` columns instead.
     * - [<code>takeWhile</code>][ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>drop</code>][ColumnsSelectionDsl.drop] — drops the first `n` columns.
     *
     * @param [n] The number of columns to take.
     * @return A [<code>ColumnSet</code>][ColumnSet] containing the first [n] columns.
     *
     */
    private typealias CommonTakeFirstDocs = Nothing

    /**
     * ## Take (Cols)
     * This collects the first [n] columns from `this` into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>take</code>][ColumnSet.take]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>take</code>][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>takeCols</code>][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>takeCols</code>][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>().`[<code>take</code>][ColumnSet.take]`(2) }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { .. }.`[<code>take</code>][ColumnSet.take]`(2) }`
     *
     * See also:
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes the last `n` columns instead.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops the first `n` columns.
     *
     * @param [n] The number of columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     *
     */
    @Interpretable("Take0")
    public fun <C> ColumnSet<C>.take(n: Int): ColumnSet<C> = transform { it.take(n) }

    /**
     * ## Take (Cols)
     * This collects the first [n] columns from `this` into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>take</code>][ColumnSet.take]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>take</code>][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>takeCols</code>][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>takeCols</code>][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>take</code>][ColumnsSelectionDsl.take]`(5) }`
     *
     * See also:
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes the last `n` columns instead.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops the first `n` columns.
     *
     * @param [n] The number of columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     *
     */
    @Interpretable("Take1")
    public fun ColumnsSelectionDsl<*>.take(n: Int): ColumnSet<*> = this.asSingleColumn().takeCols(n)

    /**
     * ## Take (Cols)
     * This collects the first [n] columns from `this` into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>take</code>][ColumnSet.take]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>take</code>][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>takeCols</code>][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>takeCols</code>][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>takeCols</code>][SingleColumn.takeCols]`(1) }`
     *
     * See also:
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes the last `n` columns instead.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops the first `n` columns.
     *
     * @param [n] The number of columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     *
     */
    @Interpretable("Take2")
    public fun SingleColumn<DataRow<*>>.takeCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().take(n) }

    /**
     * ## Take (Cols)
     * This collects the first [n] columns from `this` into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>take</code>][ColumnSet.take]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>take</code>][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>takeCols</code>][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>takeCols</code>][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>takeCols</code>][String.takeCols]`(1) }`
     *
     * See also:
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes the last `n` columns instead.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops the first `n` columns.
     *
     * @param [n] The number of columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     *
     */
    public fun String.takeCols(n: Int): ColumnSet<*> = columnGroup(this).takeCols(n)

    /**
     * ## Take (Cols)
     * This collects the first [n] columns from `this` into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>take</code>][ColumnSet.take]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>take</code>][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>takeCols</code>][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>takeCols</code>][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>takeCols</code>][SingleColumn.takeCols]`(1) }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::myColumnGroup.`[<code>takeCols</code>][KProperty.takeCols]`(1) }`
     *
     * See also:
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes the last `n` columns instead.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops the first `n` columns.
     *
     * @param [n] The number of columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     *
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.takeCols(n: Int): ColumnSet<*> = columnGroup(this).takeCols(n)

    /**
     * ## Take (Cols)
     * This collects the first [n] columns from `this` into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>take</code>][ColumnSet.take]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>take</code>][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>takeCols</code>][SingleColumn.takeCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>takeCols</code>][String.takeCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>takeCols</code>][ColumnPath.takeCols]`(1) }`
     *
     * See also:
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes the last `n` columns instead.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops the first `n` columns.
     *
     * @param [n] The number of columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     *
     */
    public fun ColumnPath.takeCols(n: Int): ColumnSet<*> = columnGroup(this).takeCols(n)

    // endregion

    // region takeLast

    /**
     * ## Take Last (Cols)
     * This collects the last [n] columns from `this` into a [<code>ColumnSet</code>][ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>takeLast</code>][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>takeLast</code>][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>takeLastCols</code>][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>takeLastCols</code>][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * See also:
     * - [<code>take</code>][ColumnsSelectionDsl.take] — takes the first `n` columns instead.
     * - [<code>takeWhile</code>][ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>dropLast</code>][ColumnsSelectionDsl.dropLast] — drops the last `n` columns.
     *
     * @param [n] The number of columns to take.
     * @return A [<code>ColumnSet</code>][ColumnSet] containing the last [n] columns.
     *
     */
    private typealias CommonTakeLastDocs = Nothing

    /**
     * ## Take Last (Cols)
     * This collects the last [n] columns from `this` into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>takeLast</code>][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>takeLast</code>][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>takeLastCols</code>][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>takeLastCols</code>][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>().`[<code>takeLast</code>][ColumnSet.takeLast]`(2) }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { .. }.`[<code>takeLast</code>][ColumnSet.takeLast]`(2) }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes the first `n` columns instead.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops the last `n` columns.
     *
     * @param [n] The number of columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     *
     */
    @Interpretable("TakeLast0")
    public fun <C> ColumnSet<C>.takeLast(n: Int = 1): ColumnSet<C> = transform { it.takeLast(n) }

    /**
     * ## Take Last (Cols)
     * This collects the last [n] columns from `this` into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>takeLast</code>][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>takeLast</code>][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>takeLastCols</code>][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>takeLastCols</code>][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>takeLast</code>][ColumnsSelectionDsl.takeLast]`(5) }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes the first `n` columns instead.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops the last `n` columns.
     *
     * @param [n] The number of columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     *
     */
    @Interpretable("TakeLast1")
    public fun ColumnsSelectionDsl<*>.takeLast(n: Int = 1): ColumnSet<*> = asSingleColumn().takeLastCols(n)

    /**
     * ## Take Last (Cols)
     * This collects the last [n] columns from `this` into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>takeLast</code>][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>takeLast</code>][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>takeLastCols</code>][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>takeLastCols</code>][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>takeLast</code>][SingleColumn.takeLastCols]`(1) }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes the first `n` columns instead.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops the last `n` columns.
     *
     * @param [n] The number of columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     *
     */
    @Interpretable("TakeLast2")
    public fun SingleColumn<DataRow<*>>.takeLastCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().takeLast(n) }

    /**
     * ## Take Last (Cols)
     * This collects the last [n] columns from `this` into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>takeLast</code>][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>takeLast</code>][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>takeLastCols</code>][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>takeLastCols</code>][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>takeLastCols</code>][String.takeLastCols]`(1) }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes the first `n` columns instead.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops the last `n` columns.
     *
     * @param [n] The number of columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     *
     */
    public fun String.takeLastCols(n: Int): ColumnSet<*> = columnGroup(this).takeLastCols(n)

    /**
     * ## Take Last (Cols)
     * This collects the last [n] columns from `this` into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>takeLast</code>][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>takeLast</code>][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>takeLastCols</code>][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>takeLastCols</code>][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>takeLastCols</code>][SingleColumn.takeLastCols]`(1) }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::myColumnGroup.`[<code>takeLastCols</code>][KProperty.takeLastCols]`(1) }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes the first `n` columns instead.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops the last `n` columns.
     *
     * @param [n] The number of columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     *
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.takeLastCols(n: Int): ColumnSet<*> = columnGroup(this).takeLastCols(n)

    /**
     * ## Take Last (Cols)
     * This collects the last [n] columns from `this` into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>takeLast</code>][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>takeLast</code>][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>takeLastCols</code>][SingleColumn.takeLastCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>takeLastCols</code>][String.takeLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>takeLastCols</code>][ColumnPath.takeLastCols]`(1) }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes the first `n` columns instead.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops the last `n` columns.
     *
     * @param [n] The number of columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     *
     */
    public fun ColumnPath.takeLastCols(n: Int): ColumnSet<*> = columnGroup(this).takeLastCols(n)

    // endregion

    // region takeWhile

    /**
     * ## Take (Cols) While
     * This operation takes the first columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the taken columns into a
     * [<code>ColumnSet</code>][ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`takeWhile`</code>][ColumnSet.takeWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`takeWhile`</code>][SingleColumn.takeColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`takeColsWhile`</code>][String.takeColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * See also:
     * - [<code>take</code>][ColumnsSelectionDsl.take] — takes a fixed number of first columns.
     * - [<code>takeLast</code>][ColumnsSelectionDsl.takeLast] — takes a fixed number of last columns.
     * - [<code>takeLastWhile</code>][ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>dropWhile</code>][ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [<code>ColumnSet</code>][ColumnSet] containing the first columns
     * adhering to the [predicate].
     *
     */
    private typealias CommonTakeFirstWhileDocs = Nothing

    /**
     * ## Take (Cols) While
     * This operation takes the first columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the taken columns into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`takeWhile`</code>][ColumnSet.takeWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`takeWhile`</code>][SingleColumn.takeColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`takeColsWhile`</code>][String.takeColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>().`[<code>takeWhile</code>][ColumnSet.takeWhile]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { .. }.`[<code>takeWhile</code>][ColumnSet.takeWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes a fixed number of first columns.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes a fixed number of last columns.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns
     * adhering to the [predicate].
     *
     */
    public fun <C> ColumnSet<C>.takeWhile(predicate: (ColumnWithPath<C>) -> Boolean): ColumnSet<C> =
        transform { it.takeWhile(predicate) }

    /**
     * ## Take (Cols) While
     * This operation takes the first columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the taken columns into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`takeWhile`</code>][ColumnSet.takeWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`takeWhile`</code>][SingleColumn.takeColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`takeColsWhile`</code>][String.takeColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>takeWhile</code>][ColumnsSelectionDsl.takeWhile]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes a fixed number of first columns.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes a fixed number of last columns.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns
     * adhering to the [predicate].
     *
     */
    public fun ColumnsSelectionDsl<*>.takeWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        asSingleColumn().takeColsWhile(predicate)

    /**
     * ## Take (Cols) While
     * This operation takes the first columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the taken columns into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`takeWhile`</code>][ColumnSet.takeWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`takeWhile`</code>][SingleColumn.takeColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`takeColsWhile`</code>][String.takeColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>takeWhile</code>][SingleColumn.takeColsWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes a fixed number of first columns.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes a fixed number of last columns.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns
     * adhering to the [predicate].
     *
     */
    public fun SingleColumn<DataRow<*>>.takeColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().takeWhile(predicate) }

    /**
     * ## Take (Cols) While
     * This operation takes the first columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the taken columns into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`takeWhile`</code>][ColumnSet.takeWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`takeWhile`</code>][SingleColumn.takeColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`takeColsWhile`</code>][String.takeColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>takeColsWhile</code>][String.takeColsWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes a fixed number of first columns.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes a fixed number of last columns.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns
     * adhering to the [predicate].
     *
     */
    public fun String.takeColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).takeColsWhile(predicate)

    /**
     * ## Take (Cols) While
     * This operation takes the first columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the taken columns into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`takeWhile`</code>][ColumnSet.takeWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`takeWhile`</code>][SingleColumn.takeColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`takeColsWhile`</code>][String.takeColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>takeColsWhile</code>][SingleColumn.takeColsWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::myColumnGroup.`[<code>takeColsWhile</code>][KProperty.takeColsWhile]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes a fixed number of first columns.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes a fixed number of last columns.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns
     * adhering to the [predicate].
     *
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.takeColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).takeColsWhile(predicate)

    /**
     * ## Take (Cols) While
     * This operation takes the first columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the taken columns into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`takeWhile`</code>][ColumnSet.takeWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`takeWhile`</code>][SingleColumn.takeColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`takeColsWhile`</code>][String.takeColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>takeColsWhile</code>][ColumnPath.takeColsWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes a fixed number of first columns.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes a fixed number of last columns.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns
     * adhering to the [predicate].
     *
     */
    public fun ColumnPath.takeColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).takeColsWhile(predicate)

    // endregion

    // region takeLastWhile

    /**
     * ## Take Last (Cols) While
     * This operation takes the last columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the taken columns into a
     * [<code>ColumnSet</code>][ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`takeLastWhile`</code>][ColumnSet.takeLastWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`takeLastWhile`</code>][SingleColumn.takeLastColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`takeLastColsWhile`</code>][String.takeLastColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * See also:
     * - [<code>take</code>][ColumnsSelectionDsl.take] — takes a fixed number of first columns.
     * - [<code>takeLast</code>][ColumnsSelectionDsl.takeLast] — takes a fixed number of last columns.
     * - [<code>takeWhile</code>][ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [<code>ColumnSet</code>][ColumnSet] containing the last columns
     * adhering to the [predicate].
     *
     */
    private typealias CommonTakeLastWhileDocs = Nothing

    /**
     * ## Take Last (Cols) While
     * This operation takes the last columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the taken columns into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`takeLastWhile`</code>][ColumnSet.takeLastWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`takeLastWhile`</code>][SingleColumn.takeLastColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`takeLastColsWhile`</code>][String.takeLastColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>().`[<code>takeLastWhile</code>][ColumnSet.takeLastWhile]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { .. }.`[<code>takeLastWhile</code>][ColumnSet.takeLastWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes a fixed number of first columns.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes a fixed number of last columns.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns
     * adhering to the [predicate].
     *
     */
    public fun <C> ColumnSet<C>.takeLastWhile(predicate: (ColumnWithPath<C>) -> Boolean): ColumnSet<C> =
        transform { it.takeLastWhile(predicate) }

    /**
     * ## Take Last (Cols) While
     * This operation takes the last columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the taken columns into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`takeLastWhile`</code>][ColumnSet.takeLastWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`takeLastWhile`</code>][SingleColumn.takeLastColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`takeLastColsWhile`</code>][String.takeLastColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>takeLastWhile</code>][ColumnsSelectionDsl.takeLastWhile]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes a fixed number of first columns.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes a fixed number of last columns.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns
     * adhering to the [predicate].
     *
     */
    public fun ColumnsSelectionDsl<*>.takeLastWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        asSingleColumn().takeLastColsWhile(predicate)

    /**
     * ## Take Last (Cols) While
     * This operation takes the last columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the taken columns into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`takeLastWhile`</code>][ColumnSet.takeLastWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`takeLastWhile`</code>][SingleColumn.takeLastColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`takeLastColsWhile`</code>][String.takeLastColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>takeLastColsWhile</code>][SingleColumn.takeLastColsWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes a fixed number of first columns.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes a fixed number of last columns.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns
     * adhering to the [predicate].
     *
     */
    public fun SingleColumn<DataRow<*>>.takeLastColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().takeLastWhile(predicate) }

    /**
     * ## Take Last (Cols) While
     * This operation takes the last columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the taken columns into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`takeLastWhile`</code>][ColumnSet.takeLastWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`takeLastWhile`</code>][SingleColumn.takeLastColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`takeLastColsWhile`</code>][String.takeLastColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>takeLastColsWhile</code>][String.takeLastColsWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes a fixed number of first columns.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes a fixed number of last columns.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns
     * adhering to the [predicate].
     *
     */
    public fun String.takeLastColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).takeLastColsWhile(predicate)

    /**
     * ## Take Last (Cols) While
     * This operation takes the last columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the taken columns into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`takeLastWhile`</code>][ColumnSet.takeLastWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`takeLastWhile`</code>][SingleColumn.takeLastColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`takeLastColsWhile`</code>][String.takeLastColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>takeLastColsWhile</code>][SingleColumn.takeLastColsWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::myColumnGroup.`[<code>takeLastColsWhile</code>][KProperty.takeLastColsWhile]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes a fixed number of first columns.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes a fixed number of last columns.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns
     * adhering to the [predicate].
     *
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.takeLastColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).takeLastColsWhile(predicate)

    /**
     * ## Take Last (Cols) While
     * This operation takes the last columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the taken columns into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See take(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#take-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`takeLastWhile`</code>][ColumnSet.takeLastWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`takeLastWhile`</code>][SingleColumn.takeLastColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`takeLastColsWhile`</code>][String.takeLastColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>takeLastColsWhile</code>][ColumnPath.takeLastColsWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * See also:
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — takes a fixed number of first columns.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — takes a fixed number of last columns.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns
     * adhering to the [predicate].
     *
     */
    public fun ColumnPath.takeLastColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).takeLastColsWhile(predicate)

    // endregion
}

// endregion
