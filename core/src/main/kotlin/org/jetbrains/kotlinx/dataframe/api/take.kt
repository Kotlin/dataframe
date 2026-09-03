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
 * Returns a [DataColumn] containing the first [n] values of this [DataColumn].
 *
 * If [n] is greater than or equal to the size of this [DataColumn], this [DataColumn] is returned as is.
 *
 * For more information: {@include [DocumentationUrls.TakeFirst]}
 *
 * See also:
 * - [takeLast][DataColumn.takeLast] — takes the last [n] values instead.
 * - [drop][DataColumn.drop]`(n: Int)` — drops the first [n] values.
 * - [dropLast][DataColumn.dropLast] — drops the last [n] values.
 * - [drop][DataColumn.drop]`{ predicate: Predicate<T> }` — drops every value that matches the predicate.
 *
 * @param [n] The number of values to take. Must not be negative.
 * @return A [DataColumn] containing the first [n] values of this [DataColumn],
 * or this [DataColumn] if [n] is greater than or equal to its size.
 * @throws [IllegalArgumentException] if [n] is negative.
 */
public fun <T> DataColumn<T>.take(n: Int): DataColumn<T> =
    when {
        n == 0 -> get(emptyList())
        n >= size -> this
        else -> get(0 until n)
    }

/**
 * Returns a [DataColumn] containing the last [n] values of this [DataColumn].
 *
 * If [n] is zero or negative, an empty [DataColumn] is returned.
 *
 * For more information: {@include [DocumentationUrls.TakeLast]}
 *
 * See also:
 * - [take][DataColumn.take] — takes the first [n] values instead.
 * - [dropLast][DataColumn.dropLast] — drops the last [n] values.
 * - [drop][DataColumn.drop]`(n: Int)` — drops the first [n] values.
 * - [drop][DataColumn.drop]`{ predicate: Predicate<T> }` — drops every value that matches the predicate.
 *
 * @param [n] The number of values to take. Must not exceed the size of this [DataColumn].
 * @return A [DataColumn] containing the last [n] values of this [DataColumn],
 * or an empty [DataColumn] if [n] is zero or negative.
 * @throws [IndexOutOfBoundsException] if [n] is greater than the size of this [DataColumn].
 */
public fun <T> DataColumn<T>.takeLast(n: Int = 1): DataColumn<T> = drop(size - n)

// endregion

// region DataFrame

/**
 * Returns a [DataFrame] containing the first [n] rows.
 *
 * If [n] is greater than or equal to the number of rows, the whole [DataFrame] is returned.
 *
 * For more information: {@include [DocumentationUrls.TakeFirst]}
 *
 * See also:
 * - [takeLast][DataFrame.takeLast] — takes the last [n] rows instead.
 * - [takeWhile][DataFrame.takeWhile] — takes the first rows while the predicate holds.
 * - [drop][DataFrame.drop]`(n: Int)` — drops the first [n] rows.
 * - [filter][DataFrame.filter] — keeps every row that matches the predicate.
 *
 * @param [n] The number of rows to take. Must not be negative.
 * @return A [DataFrame] containing the first [n] rows,
 * or the whole [DataFrame] if [n] is greater than or equal to the number of rows.
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> DataFrame<T>.take(n: Int): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return getRows(0 until n.coerceAtMost(nrow))
}

/**
 * Returns a [DataFrame] containing the last [n] rows.
 *
 * If [n] is greater than or equal to the number of rows, the whole [DataFrame] is returned.
 *
 * For more information: {@include [DocumentationUrls.TakeLast]}
 *
 * See also:
 * - [take][DataFrame.take] — takes the first [n] rows instead.
 * - [takeWhile][DataFrame.takeWhile] — takes the first rows while the predicate holds.
 * - [dropLast][DataFrame.dropLast] — drops the last [n] rows.
 * - [filter][DataFrame.filter] — keeps every row that matches the predicate.
 *
 * @param [n] The number of rows to take. Must not be negative.
 * @return A [DataFrame] containing the last [n] rows,
 * or the whole [DataFrame] if [n] is greater than or equal to the number of rows.
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> DataFrame<T>.takeLast(n: Int = 1): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return drop((nrow - n).coerceAtLeast(0))
}

/**
 * Returns a [DataFrame] containing the first rows that satisfy the given [predicate].
 *
 * Rows are taken for as long as the [predicate] holds; the operation stops at the first row that
 * does not satisfy it, and no later row is taken even if it satisfies the [predicate].
 *
 * @include [SelectingRows.RowFilterSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * For more information: {@include [DocumentationUrls.TakeWhile]}
 *
 * See also:
 * - [take][DataFrame.take] — takes a fixed number of first rows.
 * - [takeLast][DataFrame.takeLast] — takes a fixed number of last rows.
 * - [dropWhile][DataFrame.dropWhile] — drops the first rows while the predicate holds.
 * - [filter][DataFrame.filter] — keeps every row that matches the predicate.
 *
 * @param [predicate] The [RowFilter] that the leading rows to take must satisfy.
 * @return A [DataFrame] containing the first rows that satisfy the [predicate].
 */
public inline fun <T> DataFrame<T>.takeWhile(predicate: RowFilter<T>): DataFrame<T> =
    firstOrNull { !predicate(it, it) }?.let { take(it.index()) } ?: this

// endregion

// region ColumnsSelectionDsl

/**
 * ## Take {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface TakeColumnsSelectionDsl {

    /**
     * @include [TakeAndDropColumnsSelectionDslGrammar]
     * @set [TakeAndDropColumnsSelectionDslGrammar.TITLE] Take
     * @set [TakeAndDropColumnsSelectionDslGrammar.OPERATION] take
     */
    public interface Grammar {

        /** [**`take`**][ColumnsSelectionDsl.take]`(`[**`Last`**][ColumnsSelectionDsl.takeLast]`)` */
        public typealias PlainDslName = Nothing

        /** __`.`__[**`take`**][ColumnsSelectionDsl.take]`(`[**`Last`**][ColumnSet.takeLast]`)` */
        public typealias ColumnSetName = Nothing

        /** __`.`__[**`take`**][ColumnsSelectionDsl.takeCols]`(`[**`Last`**][ColumnsSelectionDsl.takeLastCols]`)`[**`Cols`**][ColumnsSelectionDsl.takeCols] */
        public typealias ColumnGroupName = Nothing

        /** [**`take`**][ColumnsSelectionDsl.takeWhile]`(`[**`Last`**][ColumnsSelectionDsl.takeLastWhile]`)`[**`While`**][ColumnsSelectionDsl.takeWhile] */
        public typealias PlainDslWhileName = Nothing

        /** __`.`__[**`take`**][ColumnsSelectionDsl.takeWhile]`(`[**`Last`**][ColumnsSelectionDsl.takeLastWhile]`)`[**`While`**][ColumnsSelectionDsl.takeWhile] */
        public typealias ColumnSetWhileName = Nothing

        /** __`.`__[**`take`**][ColumnsSelectionDsl.takeColsWhile]`(`[**`Last`**][ColumnsSelectionDsl.takeLastColsWhile]`)`[**`ColsWhile`**][ColumnsSelectionDsl.takeColsWhile] */
        public typealias ColumnGroupWhileName = Nothing
    }

    // region take

    /**
     * @include [CommonTakeAndDropDocs]
     * {@set [CommonTakeAndDropDocs.URL] {@include [DocumentationUrls.TakeCols]}}
     * @set [CommonTakeAndDropDocs.TITLE] Take
     * @set [CommonTakeAndDropDocs.OPERATION] take
     * @set [CommonTakeAndDropDocs.NOUN] take
     * @set [CommonTakeAndDropDocs.SUMMARY] This collects the first [n\] columns from `this` into a [ColumnSet].
     * @set [CommonTakeAndDropDocs.SEE_ALSO]
     * - [takeLast][ColumnsSelectionDsl.takeLast] — takes the last `n` columns instead.
     * - [takeWhile][ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [takeLastWhile][ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [drop][ColumnsSelectionDsl.drop] — drops the first `n` columns.
     * @set [CommonTakeAndDropDocs.RETURN] A [ColumnSet] containing the first [n\] columns.
     */
    private typealias CommonTakeFirstDocs = Nothing

    /**
     * @include [CommonTakeFirstDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[take][ColumnSet.take]`(2) }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[take][ColumnSet.take]`(2) }`
     */
    @Interpretable("Take0")
    public fun <C> ColumnSet<C>.take(n: Int): ColumnSet<C> = transform { it.take(n) }

    /**
     * @include [CommonTakeFirstDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[take][ColumnsSelectionDsl.take]`(5) }`
     */
    @Interpretable("Take1")
    public fun ColumnsSelectionDsl<*>.take(n: Int): ColumnSet<*> = this.asSingleColumn().takeCols(n)

    /**
     * @include [CommonTakeFirstDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(1) }`
     */
    @Interpretable("Take2")
    public fun SingleColumn<DataRow<*>>.takeCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().take(n) }

    /**
     * @include [CommonTakeFirstDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(1) }`
     */
    public fun String.takeCols(n: Int): ColumnSet<*> = columnGroup(this).takeCols(n)

    /**
     * @include [CommonTakeFirstDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeCols][SingleColumn.takeCols]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeCols][KProperty.takeCols]`(1) }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.takeCols(n: Int): ColumnSet<*> = columnGroup(this).takeCols(n)

    /**
     * @include [CommonTakeFirstDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeCols][ColumnPath.takeCols]`(1) }`
     */
    public fun ColumnPath.takeCols(n: Int): ColumnSet<*> = columnGroup(this).takeCols(n)

    // endregion

    // region takeLast

    /**
     * @include [CommonTakeAndDropDocs]
     * {@set [CommonTakeAndDropDocs.URL] {@include [DocumentationUrls.TakeCols]}}
     * @set [CommonTakeAndDropDocs.TITLE] Take Last
     * @set [CommonTakeAndDropDocs.OPERATION] takeLast
     * @set [CommonTakeAndDropDocs.NOUN] take
     * @set [CommonTakeAndDropDocs.SUMMARY] This collects the last [n\] columns from `this` into a [ColumnSet].
     * @set [CommonTakeAndDropDocs.SEE_ALSO]
     * - [take][ColumnsSelectionDsl.take] — takes the first `n` columns instead.
     * - [takeWhile][ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [takeLastWhile][ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [dropLast][ColumnsSelectionDsl.dropLast] — drops the last `n` columns.
     * @set [CommonTakeAndDropDocs.RETURN] A [ColumnSet] containing the last [n\] columns.
     */
    private typealias CommonTakeLastDocs = Nothing

    /**
     * @include [CommonTakeLastDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeLast][ColumnSet.takeLast]`(2) }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeLast][ColumnSet.takeLast]`(2) }`
     */
    @Interpretable("TakeLast0")
    public fun <C> ColumnSet<C>.takeLast(n: Int = 1): ColumnSet<C> = transform { it.takeLast(n) }

    /**
     * @include [CommonTakeLastDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[takeLast][ColumnsSelectionDsl.takeLast]`(5) }`
     */
    @Interpretable("TakeLast1")
    public fun ColumnsSelectionDsl<*>.takeLast(n: Int = 1): ColumnSet<*> = asSingleColumn().takeLastCols(n)

    /**
     * @include [CommonTakeLastDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeLast][SingleColumn.takeLastCols]`(1) }`
     */
    @Interpretable("TakeLast2")
    public fun SingleColumn<DataRow<*>>.takeLastCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().takeLast(n) }

    /**
     * @include [CommonTakeLastDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(1) }`
     */
    public fun String.takeLastCols(n: Int): ColumnSet<*> = columnGroup(this).takeLastCols(n)

    /**
     * @include [CommonTakeLastDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeLastCols][KProperty.takeLastCols]`(1) }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.takeLastCols(n: Int): ColumnSet<*> = columnGroup(this).takeLastCols(n)

    /**
     * @include [CommonTakeLastDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeLastCols][ColumnPath.takeLastCols]`(1) }`
     */
    public fun ColumnPath.takeLastCols(n: Int): ColumnSet<*> = columnGroup(this).takeLastCols(n)

    // endregion

    // region takeWhile

    /**
     * @include [CommonTakeAndDropWhileDocs]
     * {@set [CommonTakeAndDropWhileDocs.URL] {@include [DocumentationUrls.TakeCols]}}
     * @set [CommonTakeAndDropWhileDocs.TITLE] Take
     * @set [CommonTakeAndDropWhileDocs.OPERATION] take
     * @set [CommonTakeAndDropWhileDocs.NOUN] take
     * @set [CommonTakeAndDropWhileDocs.SUMMARY] This operation takes the first columns from `this` while they adhere
     * to the [predicate\], stops as soon as it encounters one that does not, and collects the taken columns into a
     * [ColumnSet].
     * @set [CommonTakeAndDropWhileDocs.SEE_ALSO]
     * - [take][ColumnsSelectionDsl.take] — takes a fixed number of first columns.
     * - [takeLast][ColumnsSelectionDsl.takeLast] — takes a fixed number of last columns.
     * - [takeLastWhile][ColumnsSelectionDsl.takeLastWhile] — takes the last columns while a predicate holds.
     * - [dropWhile][ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * @set [CommonTakeAndDropWhileDocs.RETURN] A [ColumnSet] containing the first columns
     * adhering to the [predicate\].
     */
    private typealias CommonTakeFirstWhileDocs = Nothing

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeWhile][ColumnSet.takeWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeWhile][ColumnSet.takeWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun <C> ColumnSet<C>.takeWhile(predicate: (ColumnWithPath<C>) -> Boolean): ColumnSet<C> =
        transform { it.takeWhile(predicate) }

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[takeWhile][ColumnsSelectionDsl.takeWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun ColumnsSelectionDsl<*>.takeWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        asSingleColumn().takeColsWhile(predicate)

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun SingleColumn<DataRow<*>>.takeColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().takeWhile(predicate) }

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeColsWhile][String.takeColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun String.takeColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).takeColsWhile(predicate)

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeColsWhile][SingleColumn.takeColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeColsWhile][KProperty.takeColsWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.takeColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).takeColsWhile(predicate)

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeColsWhile][ColumnPath.takeColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnPath.takeColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).takeColsWhile(predicate)

    // endregion

    // region takeLastWhile

    /**
     * @include [CommonTakeAndDropWhileDocs]
     * {@set [CommonTakeAndDropWhileDocs.URL] {@include [DocumentationUrls.TakeCols]}}
     * @set [CommonTakeAndDropWhileDocs.TITLE] Take Last
     * @set [CommonTakeAndDropWhileDocs.OPERATION] takeLast
     * @set [CommonTakeAndDropWhileDocs.NOUN] take
     * @set [CommonTakeAndDropWhileDocs.SUMMARY] This operation takes the last columns from `this` while they adhere
     * to the [predicate\], stops as soon as it encounters one that does not, and collects the taken columns into a
     * [ColumnSet].
     * @set [CommonTakeAndDropWhileDocs.SEE_ALSO]
     * - [take][ColumnsSelectionDsl.take] — takes a fixed number of first columns.
     * - [takeLast][ColumnsSelectionDsl.takeLast] — takes a fixed number of last columns.
     * - [takeWhile][ColumnsSelectionDsl.takeWhile] — takes the first columns while a predicate holds.
     * - [dropLastWhile][ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * @set [CommonTakeAndDropWhileDocs.RETURN] A [ColumnSet] containing the last columns
     * adhering to the [predicate\].
     */
    private typealias CommonTakeLastWhileDocs = Nothing

    /**
     * @include [CommonTakeLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeLastWhile][ColumnSet.takeLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeLastWhile][ColumnSet.takeLastWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun <C> ColumnSet<C>.takeLastWhile(predicate: (ColumnWithPath<C>) -> Boolean): ColumnSet<C> =
        transform { it.takeLastWhile(predicate) }

    /**
     * @include [CommonTakeLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[takeLastWhile][ColumnsSelectionDsl.takeLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun ColumnsSelectionDsl<*>.takeLastWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        asSingleColumn().takeLastColsWhile(predicate)

    /**
     * @include [CommonTakeLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeLastColsWhile][SingleColumn.takeLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun SingleColumn<DataRow<*>>.takeLastColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().takeLastWhile(predicate) }

    /**
     * @include [CommonTakeLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeLastColsWhile][String.takeLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun String.takeLastColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).takeLastColsWhile(predicate)

    /**
     * @include [CommonTakeLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeLastColsWhile][SingleColumn.takeLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeLastColsWhile][KProperty.takeLastColsWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.takeLastColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).takeLastColsWhile(predicate)

    /**
     * @include [CommonTakeLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeLastColsWhile][ColumnPath.takeLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnPath.takeLastColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).takeLastColsWhile(predicate)

    // endregion
}

// endregion
