package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
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
 * Returns a [<code>DataColumn</code>][DataColumn] containing only the values that do not match the given [<code>predicate</code>][predicate].
 *
 * For more information: [See `drop` on the documentation website.](https://kotlin.github.io/dataframe/drop.html)
 *
 * See also:
 * - [<code>filter</code>][DataColumn.filter] — keeps only the values that match the predicate.
 * - [<code>drop</code>][DataColumn.drop]`(n: Int)` — drops a fixed number of first values.
 * - [<code>dropLast</code>][DataColumn.dropLast] — drops a fixed number of last values.
 *
 * @param [predicate] The condition used to exclude values from this [<code>DataColumn</code>][DataColumn].
 * @return A [<code>DataColumn</code>][DataColumn] containing the values that do not match the [<code>predicate</code>][predicate].
 */
public inline fun <T> DataColumn<T>.drop(predicate: Predicate<T>): DataColumn<T> = filter { !predicate(it) }

/**
 * Returns a [<code>DataColumn</code>][DataColumn] containing all values of this [<code>DataColumn</code>][DataColumn] except the first [<code>n</code>][n] values.
 *
 * If [<code>n</code>][n] is greater than or equal to the size of this [<code>DataColumn</code>][DataColumn], an empty [<code>DataColumn</code>][DataColumn] is returned.
 *
 * For more information: [See `drop` on the documentation website.](https://kotlin.github.io/dataframe/slicerows.html#drop)
 *
 * See also:
 * - [<code>dropLast</code>][DataColumn.dropLast] — drops the last [<code>n</code>][n] values instead.
 * - [<code>take</code>][DataColumn.take] — keeps only the first [<code>n</code>][n] values.
 * - [<code>takeLast</code>][DataColumn.takeLast] — keeps only the last [<code>n</code>][n] values.
 * - [<code>drop</code>][DataColumn.drop]`{ predicate: Predicate<T> }` — drops every value that matches the predicate.
 *
 * @param [n] The number of values to drop. Must not be negative.
 * @return A [<code>DataColumn</code>][DataColumn] containing all values of this [<code>DataColumn</code>][DataColumn] except the first [<code>n</code>][n],
 * or an empty [<code>DataColumn</code>][DataColumn] if [<code>n</code>][n] is greater than or equal to its size.
 * @throws [IndexOutOfBoundsException] if [<code>n</code>][n] is negative.
 */
public fun <T> DataColumn<T>.drop(n: Int): DataColumn<T> =
    when {
        n == 0 -> this
        n >= size -> get(emptyList())
        else -> get(n until size)
    }

/**
 * Returns a [<code>DataColumn</code>][DataColumn] containing all values of this [<code>DataColumn</code>][DataColumn] except the last [<code>n</code>][n] values.
 *
 * If [<code>n</code>][n] is zero or negative, this [<code>DataColumn</code>][DataColumn] is returned as is.
 *
 * For more information: [See `dropLast` on the documentation website.](https://kotlin.github.io/dataframe/slicerows.html#droplast)
 *
 * See also:
 * - [<code>drop</code>][DataColumn.drop]`(n: Int)` — drops the first [<code>n</code>][n] values instead.
 * - [<code>takeLast</code>][DataColumn.takeLast] — keeps only the last [<code>n</code>][n] values.
 * - [<code>take</code>][DataColumn.take] — keeps only the first [<code>n</code>][n] values.
 * - [<code>drop</code>][DataColumn.drop]`{ predicate: Predicate<T> }` — drops every value that matches the predicate.
 *
 * @param [n] The number of values to drop. Must not exceed the size of this [<code>DataColumn</code>][DataColumn].
 * @return A [<code>DataColumn</code>][DataColumn] containing all values of this [<code>DataColumn</code>][DataColumn] except the last [<code>n</code>][n],
 * or this [<code>DataColumn</code>][DataColumn] if [<code>n</code>][n] is zero or negative.
 * @throws [IllegalArgumentException] if [<code>n</code>][n] is greater than the size of this [<code>DataColumn</code>][DataColumn].
 */
public fun <T> DataColumn<T>.dropLast(n: Int = 1): DataColumn<T> = take(size - n)

// endregion

// region DataFrame

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing all rows except the first [<code>n</code>][n] rows.
 *
 * If [<code>n</code>][n] is greater than or equal to the number of rows, an empty [<code>DataFrame</code>][DataFrame] is returned.
 *
 * For more information: [See `drop` on the documentation website.](https://kotlin.github.io/dataframe/slicerows.html#drop)
 *
 * See also:
 * - [<code>dropLast</code>][DataFrame.dropLast] — drops the last [<code>n</code>][n] rows instead.
 * - [<code>dropWhile</code>][DataFrame.dropWhile] — drops the first rows while the predicate holds.
 * - [<code>take</code>][DataFrame.take] — keeps only the first [<code>n</code>][n] rows.
 * - [<code>drop</code>][DataFrame.drop]`{ predicate: RowFilter<T> }` — drops every row that matches the predicate.
 *
 * @param [n] The number of rows to drop. Must not be negative.
 * @return A [<code>DataFrame</code>][DataFrame] containing all rows except the first [<code>n</code>][n],
 * or an empty [<code>DataFrame</code>][DataFrame] if [<code>n</code>][n] is greater than or equal to the number of rows.
 * @throws IllegalArgumentException if [<code>n</code>][n] is negative.
 */
public fun <T> DataFrame<T>.drop(n: Int): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return getRows(n.coerceAtMost(nrow) until nrow)
}

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing all rows except the last [<code>n</code>][n] rows.
 *
 * If [<code>n</code>][n] is greater than or equal to the number of rows, an empty [<code>DataFrame</code>][DataFrame] is returned.
 *
 * For more information: [See `dropLast` on the documentation website.](https://kotlin.github.io/dataframe/slicerows.html#droplast)
 *
 * See also:
 * - [<code>drop</code>][DataFrame.drop]`(n: Int)` — drops the first [<code>n</code>][n] rows instead.
 * - [<code>dropWhile</code>][DataFrame.dropWhile] — drops the first rows while the predicate holds.
 * - [<code>takeLast</code>][DataFrame.takeLast] — keeps only the last [<code>n</code>][n] rows.
 * - [<code>drop</code>][DataFrame.drop]`{ predicate: RowFilter<T> }` — drops every row that matches the predicate.
 *
 * @param [n] The number of rows to drop. Must not be negative.
 * @return A [<code>DataFrame</code>][DataFrame] containing all rows except the last [<code>n</code>][n],
 * or an empty [<code>DataFrame</code>][DataFrame] if [<code>n</code>][n] is greater than or equal to the number of rows.
 * @throws IllegalArgumentException if [<code>n</code>][n] is negative.
 */
public fun <T> DataFrame<T>.dropLast(n: Int = 1): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return take((nrow - n).coerceAtLeast(0))
}

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing all rows except the rows that satisfy the given [<code>predicate</code>][predicate].
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
 * [See `drop` on the documentation website.](https://kotlin.github.io/dataframe/drop.html)
 *
 * See also:
 * - [<code>filter</code>][DataFrame.filter] — keeps only the rows that match the predicate.
 * - [<code>dropWhile</code>][DataFrame.dropWhile] — drops only the first rows that match the predicate.
 * - [<code>drop</code>][DataFrame.drop]`(n: Int)` — drops a fixed number of first rows.
 *
 * @param [predicate] The [<code>RowFilter</code>][RowFilter] used to exclude rows from this [<code>DataFrame</code>][DataFrame].
 * @return A [<code>DataFrame</code>][DataFrame] containing all rows that do not satisfy the [<code>predicate</code>][predicate].
 */
public inline fun <T> DataFrame<T>.drop(predicate: RowFilter<T>): DataFrame<T> = filter { !predicate(it, it) }

/**
 * Returns a [<code>DataFrame</code>][DataFrame] containing all rows except the first rows that satisfy the given [<code>predicate</code>][predicate].
 *
 * Rows are dropped for as long as the [<code>predicate</code>][predicate] holds; the operation stops at the first row that
 * does not satisfy it, and no later row is dropped even if it satisfies the [<code>predicate</code>][predicate].
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
 * For more information: [See `dropWhile` on the documentation website.](https://kotlin.github.io/dataframe/slicerows.html#dropwhile)
 *
 * See also:
 * - [<code>drop</code>][DataFrame.drop]`(n: Int)` — drops a fixed number of first rows.
 * - [<code>dropLast</code>][DataFrame.dropLast] — drops a fixed number of last rows.
 * - [<code>takeWhile</code>][DataFrame.takeWhile] — keeps the first rows while the predicate holds.
 * - [<code>drop</code>][DataFrame.drop]`{ predicate: RowFilter<T> }` — drops every row that matches the predicate.
 *
 * @param [predicate] The [<code>RowFilter</code>][RowFilter] that the leading rows to drop must satisfy.
 * @return A [<code>DataFrame</code>][DataFrame] containing all rows except the first ones that satisfy the [<code>predicate</code>][predicate].
 */
public inline fun <T> DataFrame<T>.dropWhile(predicate: RowFilter<T>): DataFrame<T> =
    firstOrNull { !predicate(it, it) }?.let { drop(it.index()) } ?: this

// endregion

// region ColumnsSelectionDsl

/**
 * ## Drop [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface DropColumnsSelectionDsl {

    /**
     * ## Drop (Last) (Cols) (While) Grammar
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
     *  [<code>**`drop`**</code>][ColumnsSelectionDsl.drop]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.dropLast]`)`**`(`**[<code>`number`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     *  `| `[<code>**`drop`**</code>][ColumnsSelectionDsl.dropWhile]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.dropLastWhile]`)`[<code>**`While`**</code>][ColumnsSelectionDsl.dropWhile]**`  {  `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`drop`**</code>][ColumnsSelectionDsl.drop]`(`[<code>**`Last`**</code>][ColumnSet.dropLast]`)`**`(`**[<code>`number`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`drop`**</code>][ColumnsSelectionDsl.dropWhile]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.dropLastWhile]`)`[<code>**`While`**</code>][ColumnsSelectionDsl.dropWhile]**`  {  `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`drop`**</code>][ColumnsSelectionDsl.dropCols]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.dropLastCols]`)`[<code>**`Cols`**</code>][ColumnsSelectionDsl.dropCols]**`(`**[<code>`number`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`drop`**</code>][ColumnsSelectionDsl.dropColsWhile]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.dropLastColsWhile]`)`[<code>**`ColsWhile`**</code>][ColumnsSelectionDsl.dropColsWhile]**`  {  `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
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

        /** [<code>**`drop`**</code>][ColumnsSelectionDsl.drop]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.dropLast]`)` */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`drop`**</code>][ColumnsSelectionDsl.drop]`(`[<code>**`Last`**</code>][ColumnSet.dropLast]`)` */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`drop`**</code>][ColumnsSelectionDsl.dropCols]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.dropLastCols]`)`[<code>**`Cols`**</code>][ColumnsSelectionDsl.dropCols] */
        public typealias ColumnGroupName = Nothing

        /** [<code>**`drop`**</code>][ColumnsSelectionDsl.dropWhile]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.dropLastWhile]`)`[<code>**`While`**</code>][ColumnsSelectionDsl.dropWhile] */
        public typealias PlainDslWhileName = Nothing

        /** __`.`__[<code>**`drop`**</code>][ColumnsSelectionDsl.dropWhile]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.dropLastWhile]`)`[<code>**`While`**</code>][ColumnsSelectionDsl.dropWhile] */
        public typealias ColumnSetWhileName = Nothing

        /** __`.`__[<code>**`drop`**</code>][ColumnsSelectionDsl.dropColsWhile]`(`[<code>**`Last`**</code>][ColumnsSelectionDsl.dropLastColsWhile]`)`[<code>**`ColsWhile`**</code>][ColumnsSelectionDsl.dropColsWhile] */
        public typealias ColumnGroupWhileName = Nothing
    }

    // region drop

    /**
     * ## Drop (Cols)
     * This operation collects all columns from `this` except the first [n] into
     * a [<code>ColumnSet</code>][ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>drop</code>][ColumnSet.drop]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>drop</code>][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>dropCols</code>][SingleColumn.dropCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>dropCols</code>][String.dropCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * See also:
     * - [<code>dropLast</code>][ColumnsSelectionDsl.dropLast] — drops the last `n` columns instead.
     * - [<code>dropWhile</code>][ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>take</code>][ColumnsSelectionDsl.take] — keeps only the first `n` columns.
     *
     * @param [n] The number of columns to drop.
     * @return A [<code>ColumnSet</code>][ColumnSet] containing all columns except the first [n].
     *
     */
    private typealias CommonDropFirstDocs = Nothing

    /**
     * ## Drop (Cols)
     * This operation collects all columns from `this` except the first [n] into
     * a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>drop</code>][ColumnSet.drop]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>drop</code>][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>dropCols</code>][SingleColumn.dropCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>dropCols</code>][String.dropCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>().`[<code>drop</code>][ColumnSet.drop]`(2) }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { .. }.`[<code>drop</code>][ColumnSet.drop]`(2) }`
     *
     * See also:
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops the last `n` columns instead.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — keeps only the first `n` columns.
     *
     * @param [n] The number of columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the first [n].
     *
     */
    @Interpretable("Drop0")
    public fun <C> ColumnSet<C>.drop(n: Int): ColumnSet<C> = transform { it.drop(n) }

    /**
     * ## Drop (Cols)
     * This operation collects all columns from `this` except the first [n] into
     * a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>drop</code>][ColumnSet.drop]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>drop</code>][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>dropCols</code>][SingleColumn.dropCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>dropCols</code>][String.dropCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>drop</code>][ColumnsSelectionDsl.drop]`(5) }`
     *
     * See also:
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops the last `n` columns instead.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — keeps only the first `n` columns.
     *
     * @param [n] The number of columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the first [n].
     *
     */
    @Interpretable("Drop1")
    public fun ColumnsSelectionDsl<*>.drop(n: Int): ColumnSet<*> = asSingleColumn().dropCols(n)

    /**
     * ## Drop (Cols)
     * This operation collects all columns from `this` except the first [n] into
     * a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>drop</code>][ColumnSet.drop]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>drop</code>][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>dropCols</code>][SingleColumn.dropCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>dropCols</code>][String.dropCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>dropCols</code>][SingleColumn.dropCols]`(1) }`
     *
     * See also:
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops the last `n` columns instead.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — keeps only the first `n` columns.
     *
     * @param [n] The number of columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the first [n].
     *
     */
    @Interpretable("Drop2")
    public fun SingleColumn<DataRow<*>>.dropCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().drop(n) }

    /**
     * ## Drop (Cols)
     * This operation collects all columns from `this` except the first [n] into
     * a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>drop</code>][ColumnSet.drop]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>drop</code>][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>dropCols</code>][SingleColumn.dropCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>dropCols</code>][String.dropCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>dropCols</code>][String.dropCols]`(1) }`
     *
     * See also:
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops the last `n` columns instead.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — keeps only the first `n` columns.
     *
     * @param [n] The number of columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the first [n].
     *
     */
    public fun String.dropCols(n: Int): ColumnSet<*> = columnGroup(this).dropCols(n)

    /**
     * ## Drop (Cols)
     * This operation collects all columns from `this` except the first [n] into
     * a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>drop</code>][ColumnSet.drop]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>drop</code>][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>dropCols</code>][SingleColumn.dropCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>dropCols</code>][String.dropCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::myColumnGroup.`[<code>dropCols</code>][KProperty.dropCols]`(1) }`
     *
     * See also:
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops the last `n` columns instead.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — keeps only the first `n` columns.
     *
     * @param [n] The number of columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the first [n].
     *
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.dropCols(n: Int): ColumnSet<*> = columnGroup(this).dropCols(n)

    /**
     * ## Drop (Cols)
     * This operation collects all columns from `this` except the first [n] into
     * a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>drop</code>][ColumnSet.drop]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>drop</code>][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>dropCols</code>][SingleColumn.dropCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>dropCols</code>][String.dropCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>dropCols</code>][ColumnPath.dropCols]`(1) }`
     *
     * See also:
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops the last `n` columns instead.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>take</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take] — keeps only the first `n` columns.
     *
     * @param [n] The number of columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the first [n].
     *
     */
    public fun ColumnPath.dropCols(n: Int): ColumnSet<*> = columnGroup(this).dropCols(n)

    // endregion

    // region dropLast

    /**
     * ## Drop Last (Cols)
     * This operation collects all columns from `this` except the last [n] into a
     * [<code>ColumnSet</code>][ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>dropLast</code>][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>dropLast</code>][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>dropLastCols</code>][SingleColumn.dropLastCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>dropLastCols</code>][String.dropLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * See also:
     * - [<code>drop</code>][ColumnsSelectionDsl.drop] — drops the first `n` columns instead.
     * - [<code>dropWhile</code>][ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>takeLast</code>][ColumnsSelectionDsl.takeLast] — keeps only the last `n` columns.
     *
     * @param [n] The number of columns to drop.
     * @return A [<code>ColumnSet</code>][ColumnSet] containing all columns except the last [n].
     *
     */
    private typealias CommonDropLastDocs = Nothing

    /**
     * ## Drop Last (Cols)
     * This operation collects all columns from `this` except the last [n] into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>dropLast</code>][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>dropLast</code>][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>dropLastCols</code>][SingleColumn.dropLastCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>dropLastCols</code>][String.dropLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>().`[<code>dropLast</code>][ColumnSet.dropLast]`(2) }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { .. }.`[<code>dropLast</code>][ColumnSet.dropLast]`() }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops the first `n` columns instead.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — keeps only the last `n` columns.
     *
     * @param [n] The number of columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the last [n].
     *
     */
    @Interpretable("DropLast0")
    public fun <C> ColumnSet<C>.dropLast(n: Int = 1): ColumnSet<C> = transform { it.dropLast(n) }

    /**
     * ## Drop Last (Cols)
     * This operation collects all columns from `this` except the last [n] into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>dropLast</code>][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>dropLast</code>][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>dropLastCols</code>][SingleColumn.dropLastCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>dropLastCols</code>][String.dropLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>dropLast</code>][ColumnsSelectionDsl.dropLast]`(5) }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops the first `n` columns instead.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — keeps only the last `n` columns.
     *
     * @param [n] The number of columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the last [n].
     *
     */
    @Interpretable("DropLast1")
    public fun ColumnsSelectionDsl<*>.dropLast(n: Int = 1): ColumnSet<*> = this.asSingleColumn().dropLastCols(n)

    /**
     * ## Drop Last (Cols)
     * This operation collects all columns from `this` except the last [n] into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>dropLast</code>][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>dropLast</code>][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>dropLastCols</code>][SingleColumn.dropLastCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>dropLastCols</code>][String.dropLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>dropLastCols</code>][SingleColumn.dropLastCols]`() }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops the first `n` columns instead.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — keeps only the last `n` columns.
     *
     * @param [n] The number of columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the last [n].
     *
     */
    @Interpretable("DropLast2")
    public fun SingleColumn<DataRow<*>>.dropLastCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().dropLast(n) }

    /**
     * ## Drop Last (Cols)
     * This operation collects all columns from `this` except the last [n] into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>dropLast</code>][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>dropLast</code>][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>dropLastCols</code>][SingleColumn.dropLastCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>dropLastCols</code>][String.dropLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>dropLastCols</code>][String.dropLastCols]`(1) }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops the first `n` columns instead.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — keeps only the last `n` columns.
     *
     * @param [n] The number of columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the last [n].
     *
     */
    public fun String.dropLastCols(n: Int): ColumnSet<*> = columnGroup(this).dropLastCols(n)

    /**
     * ## Drop Last (Cols)
     * This operation collects all columns from `this` except the last [n] into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>dropLast</code>][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>dropLast</code>][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>dropLastCols</code>][SingleColumn.dropLastCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>dropLastCols</code>][String.dropLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::myColumnGroup.`[<code>dropLastCols</code>][KProperty.dropLastCols]`(1) }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops the first `n` columns instead.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — keeps only the last `n` columns.
     *
     * @param [n] The number of columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the last [n].
     *
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.dropLastCols(n: Int): ColumnSet<*> = columnGroup(this).dropLastCols(n)

    /**
     * ## Drop Last (Cols)
     * This operation collects all columns from `this` except the last [n] into a
     * [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastCols` when called on
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>dropLast</code>][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>dropLast</code>][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>dropLastCols</code>][SingleColumn.dropLastCols]`(2) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>dropLastCols</code>][String.dropLastCols]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>dropLastCols</code>][ColumnPath.dropLastCols]`(1) }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops the first `n` columns instead.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>takeLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast] — keeps only the last `n` columns.
     *
     * @param [n] The number of columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the last [n].
     *
     */
    public fun ColumnPath.dropLastCols(n: Int): ColumnSet<*> = columnGroup(this).dropLastCols(n)

    // endregion

    // region dropWhile

    /**
     * ## Drop (Cols) While
     * This operation drops the first columns from `this` while they
     * adhere to the [predicate], stops as soon as it encounters one that does not, and collects the remaining
     * columns into a [<code>ColumnSet</code>][ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`dropWhile`</code>][ColumnSet.dropWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`dropWhile`</code>][SingleColumn.dropColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`dropColsWhile`</code>][String.dropColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * See also:
     * - [<code>drop</code>][ColumnsSelectionDsl.drop] — drops a fixed number of first columns.
     * - [<code>dropLast</code>][ColumnsSelectionDsl.dropLast] — drops a fixed number of last columns.
     * - [<code>dropLastWhile</code>][ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>takeWhile</code>][ColumnsSelectionDsl.takeWhile] — keeps the first columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [<code>ColumnSet</code>][ColumnSet] containing all columns except the first
     * ones adhering to the [predicate].
     *
     */
    private typealias CommonDropWhileDocs = Nothing

    /**
     * ## Drop (Cols) While
     * This operation drops the first columns from `this` while they
     * adhere to the [predicate], stops as soon as it encounters one that does not, and collects the remaining
     * columns into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`dropWhile`</code>][ColumnSet.dropWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`dropWhile`</code>][SingleColumn.dropColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`dropColsWhile`</code>][String.dropColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>().`[<code>dropWhile</code>][ColumnSet.dropWhile]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { .. }.`[<code>dropWhile</code>][ColumnSet.dropWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops a fixed number of first columns.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops a fixed number of last columns.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — keeps the first columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the first
     * ones adhering to the [predicate].
     *
     */
    public fun <C> ColumnSet<C>.dropWhile(predicate: (ColumnWithPath<C>) -> Boolean): ColumnSet<C> =
        transform { it.dropWhile(predicate) }

    /**
     * ## Drop (Cols) While
     * This operation drops the first columns from `this` while they
     * adhere to the [predicate], stops as soon as it encounters one that does not, and collects the remaining
     * columns into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`dropWhile`</code>][ColumnSet.dropWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`dropWhile`</code>][SingleColumn.dropColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`dropColsWhile`</code>][String.dropColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>dropWhile</code>][ColumnsSelectionDsl.dropWhile]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops a fixed number of first columns.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops a fixed number of last columns.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — keeps the first columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the first
     * ones adhering to the [predicate].
     *
     */
    public fun ColumnsSelectionDsl<*>.dropWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        this.asSingleColumn().dropColsWhile(predicate)

    /**
     * ## Drop (Cols) While
     * This operation drops the first columns from `this` while they
     * adhere to the [predicate], stops as soon as it encounters one that does not, and collects the remaining
     * columns into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`dropWhile`</code>][ColumnSet.dropWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`dropWhile`</code>][SingleColumn.dropColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`dropColsWhile`</code>][String.dropColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>dropColsWhile</code>][SingleColumn.dropColsWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops a fixed number of first columns.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops a fixed number of last columns.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — keeps the first columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the first
     * ones adhering to the [predicate].
     *
     */
    public fun SingleColumn<DataRow<*>>.dropColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().dropWhile(predicate) }

    /**
     * ## Drop (Cols) While
     * This operation drops the first columns from `this` while they
     * adhere to the [predicate], stops as soon as it encounters one that does not, and collects the remaining
     * columns into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`dropWhile`</code>][ColumnSet.dropWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`dropWhile`</code>][SingleColumn.dropColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`dropColsWhile`</code>][String.dropColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>dropColsWhile</code>][String.dropColsWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops a fixed number of first columns.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops a fixed number of last columns.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — keeps the first columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the first
     * ones adhering to the [predicate].
     *
     */
    public fun String.dropColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).dropColsWhile(predicate)

    /**
     * ## Drop (Cols) While
     * This operation drops the first columns from `this` while they
     * adhere to the [predicate], stops as soon as it encounters one that does not, and collects the remaining
     * columns into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`dropWhile`</code>][ColumnSet.dropWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`dropWhile`</code>][SingleColumn.dropColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`dropColsWhile`</code>][String.dropColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::myColumnGroup.`[<code>dropColsWhile</code>][KProperty.dropColsWhile]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops a fixed number of first columns.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops a fixed number of last columns.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — keeps the first columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the first
     * ones adhering to the [predicate].
     *
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.dropColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).dropColsWhile(predicate)

    /**
     * ## Drop (Cols) While
     * This operation drops the first columns from `this` while they
     * adhere to the [predicate], stops as soon as it encounters one that does not, and collects the remaining
     * columns into a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`dropWhile`</code>][ColumnSet.dropWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`dropWhile`</code>][SingleColumn.dropColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`dropColsWhile`</code>][String.dropColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>dropColsWhile</code>][ColumnPath.dropColsWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops a fixed number of first columns.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops a fixed number of last columns.
     * - [<code>dropLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [<code>takeWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile] — keeps the first columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the first
     * ones adhering to the [predicate].
     *
     */
    public fun ColumnPath.dropColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).dropColsWhile(predicate)

    // endregion

    // region dropLastWhile

    /**
     * ## Drop Last (Cols) While
     * This operation drops the last columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the remaining columns into
     * a [<code>ColumnSet</code>][ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`dropLastWhile`</code>][ColumnSet.dropLastWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`dropLastWhile`</code>][SingleColumn.dropLastColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`dropLastColsWhile`</code>][String.dropLastColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * See also:
     * - [<code>drop</code>][ColumnsSelectionDsl.drop] — drops a fixed number of first columns.
     * - [<code>dropLast</code>][ColumnsSelectionDsl.dropLast] — drops a fixed number of last columns.
     * - [<code>dropWhile</code>][ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][ColumnsSelectionDsl.takeLastWhile] — keeps the last columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [<code>ColumnSet</code>][ColumnSet] containing all columns except the last
     * ones adhering to the [predicate].
     *
     */
    private typealias CommonDropLastWhileDocs = Nothing

    /**
     * ## Drop Last (Cols) While
     * This operation drops the last columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the remaining columns into
     * a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`dropLastWhile`</code>][ColumnSet.dropLastWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`dropLastWhile`</code>][SingleColumn.dropLastColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`dropLastColsWhile`</code>][String.dropLastColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>().`[<code>dropLastWhile</code>][ColumnSet.dropLastWhile]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { .. }.`[<code>dropLastWhile</code>][ColumnSet.dropLastWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops a fixed number of first columns.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops a fixed number of last columns.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — keeps the last columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the last
     * ones adhering to the [predicate].
     *
     */
    public fun <C> ColumnSet<C>.dropLastWhile(predicate: (ColumnWithPath<C>) -> Boolean): ColumnSet<C> =
        transform { it.dropLastWhile(predicate) }

    /**
     * ## Drop Last (Cols) While
     * This operation drops the last columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the remaining columns into
     * a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`dropLastWhile`</code>][ColumnSet.dropLastWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`dropLastWhile`</code>][SingleColumn.dropLastColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`dropLastColsWhile`</code>][String.dropLastColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>dropLastWhile</code>][ColumnsSelectionDsl.dropLastWhile]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops a fixed number of first columns.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops a fixed number of last columns.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — keeps the last columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the last
     * ones adhering to the [predicate].
     *
     */
    public fun ColumnsSelectionDsl<*>.dropLastWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        this.asSingleColumn().dropLastColsWhile(predicate)

    /**
     * ## Drop Last (Cols) While
     * This operation drops the last columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the remaining columns into
     * a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`dropLastWhile`</code>][ColumnSet.dropLastWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`dropLastWhile`</code>][SingleColumn.dropLastColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`dropLastColsWhile`</code>][String.dropLastColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>dropLastColsWhile</code>][SingleColumn.dropLastColsWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops a fixed number of first columns.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops a fixed number of last columns.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — keeps the last columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the last
     * ones adhering to the [predicate].
     *
     */
    public fun SingleColumn<DataRow<*>>.dropLastColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().dropLastWhile(predicate) }

    /**
     * ## Drop Last (Cols) While
     * This operation drops the last columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the remaining columns into
     * a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`dropLastWhile`</code>][ColumnSet.dropLastWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`dropLastWhile`</code>][SingleColumn.dropLastColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`dropLastColsWhile`</code>][String.dropLastColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>dropLastColsWhile</code>][String.dropLastColsWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops a fixed number of first columns.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops a fixed number of last columns.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — keeps the last columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the last
     * ones adhering to the [predicate].
     *
     */
    public fun String.dropLastColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).dropLastColsWhile(predicate)

    /**
     * ## Drop Last (Cols) While
     * This operation drops the last columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the remaining columns into
     * a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`dropLastWhile`</code>][ColumnSet.dropLastWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`dropLastWhile`</code>][SingleColumn.dropLastColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`dropLastColsWhile`</code>][String.dropLastColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>dropLastColsWhile</code>][SingleColumn.dropLastColsWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::myColumnGroup.`[<code>dropLastColsWhile</code>][KProperty.dropLastColsWhile]` { it.`[<code>any</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops a fixed number of first columns.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops a fixed number of last columns.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — keeps the last columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the last
     * ones adhering to the [predicate].
     *
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.dropLastColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).dropLastColsWhile(predicate)

    /**
     * ## Drop Last (Cols) While
     * This operation drops the last columns from `this` while they adhere
     * to the [predicate], stops as soon as it encounters one that does not, and collects the remaining columns into
     * a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * This function operates solely on columns at the top-level.
     *
     * Any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastColsWhile` when called on a [<code>String</code>][String] or [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For more information: [See drop(Last)(Cols)(While) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#drop-last-cols-while)
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[<code>`dropLastWhile`</code>][ColumnSet.dropLastWhile]` { "my" `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`dropLastWhile`</code>][SingleColumn.dropLastColsWhile]` { it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`dropLastColsWhile`</code>][String.dropLastColsWhile]` { it.`[<code>`kind`</code>][ColumnWithPath.kind]`() == `[<code>`ColumnKind.Value`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>dropLastColsWhile</code>][ColumnPath.dropLastColsWhile]` { it.`[<code>name</code>][ColumnWithPath.name]`.`[<code>startsWith</code>][String.startsWith]`("my") } }`
     *
     * See also:
     * - [<code>drop</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop] — drops a fixed number of first columns.
     * - [<code>dropLast</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast] — drops a fixed number of last columns.
     * - [<code>dropWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [<code>takeLastWhile</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile] — keeps the last columns while a predicate holds.
     *
     * @param [predicate] The [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns except the last
     * ones adhering to the [predicate].
     *
     */
    public fun ColumnPath.dropLastColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).dropLastColsWhile(predicate)

    // endregion
}

// endregion
