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
 * Returns a [DataColumn] containing only the values that do not match the given [predicate].
 *
 * For more information: {@include [DocumentationUrls.Drop]}
 *
 * See also:
 * - [filter][DataColumn.filter] — keeps only the values that match the predicate.
 * - [drop][DataColumn.drop]`(n: Int)` — drops a fixed number of first values.
 * - [dropLast][DataColumn.dropLast] — drops a fixed number of last values.
 *
 * @param [predicate] The condition used to exclude values from this [DataColumn].
 * @return A [DataColumn] containing the values that do not match the [predicate].
 */
public inline fun <T> DataColumn<T>.drop(predicate: Predicate<T>): DataColumn<T> = filter { !predicate(it) }

/**
 * Returns a [DataColumn] containing all values of this [DataColumn] except the first [n] values.
 *
 * If [n] is greater than or equal to the size of this [DataColumn], an empty [DataColumn] is returned.
 *
 * See also:
 * - [dropLast][DataColumn.dropLast] — drops the last [n] values instead.
 * - [take][DataColumn.take] — keeps only the first [n] values.
 * - [takeLast][DataColumn.takeLast] — keeps only the last [n] values.
 * - [drop][DataColumn.drop]`{ predicate: Predicate<T> }` — drops every value that matches the predicate.
 *
 * @param [n] The number of values to drop. Must not be negative.
 * @return A [DataColumn] containing all values of this [DataColumn] except the first [n],
 * or an empty [DataColumn] if [n] is greater than or equal to its size.
 * @throws [IndexOutOfBoundsException] if [n] is negative.
 */
public fun <T> DataColumn<T>.drop(n: Int): DataColumn<T> =
    when {
        n == 0 -> this
        n >= size -> get(emptyList())
        else -> get(n until size)
    }

/**
 * Returns a [DataColumn] containing all values of this [DataColumn] except the last [n] values.
 *
 * If [n] is zero or negative, this [DataColumn] is returned as is.
 *
 * See also:
 * - [drop][DataColumn.drop]`(n: Int)` — drops the first [n] values instead.
 * - [takeLast][DataColumn.takeLast] — keeps only the last [n] values.
 * - [take][DataColumn.take] — keeps only the first [n] values.
 * - [drop][DataColumn.drop]`{ predicate: Predicate<T> }` — drops every value that matches the predicate.
 *
 * @param [n] The number of values to drop. Must not exceed the size of this [DataColumn].
 * @return A [DataColumn] containing all values of this [DataColumn] except the last [n],
 * or this [DataColumn] if [n] is zero or negative.
 * @throws [IllegalArgumentException] if [n] is greater than the size of this [DataColumn].
 */
public fun <T> DataColumn<T>.dropLast(n: Int = 1): DataColumn<T> = take(size - n)

// endregion

// region DataFrame

/**
 * Returns a [DataFrame] containing all rows except the first [n] rows.
 *
 * If [n] is greater than or equal to the number of rows, an empty [DataFrame] is returned.
 *
 * For more information: {@include [DocumentationUrls.DropFirst]}
 *
 * See also:
 * - [dropLast][DataFrame.dropLast] — drops the last [n] rows instead.
 * - [dropWhile][DataFrame.dropWhile] — drops the first rows while the predicate holds.
 * - [take][DataFrame.take] — keeps only the first [n] rows.
 * - [drop][DataFrame.drop]`{ predicate: RowFilter<T> }` — drops every row that matches the predicate.
 *
 * @param [n] The number of rows to drop. Must not be negative.
 * @return A [DataFrame] containing all rows except the first [n],
 * or an empty [DataFrame] if [n] is greater than or equal to the number of rows.
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> DataFrame<T>.drop(n: Int): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return getRows(n.coerceAtMost(nrow) until nrow)
}

/**
 * Returns a [DataFrame] containing all rows except the last [n] rows.
 *
 * If [n] is greater than or equal to the number of rows, an empty [DataFrame] is returned.
 *
 * For more information: {@include [DocumentationUrls.DropLast]}
 *
 * See also:
 * - [drop][DataFrame.drop]`(n: Int)` — drops the first [n] rows instead.
 * - [dropWhile][DataFrame.dropWhile] — drops the first rows while the predicate holds.
 * - [takeLast][DataFrame.takeLast] — keeps only the last [n] rows.
 * - [drop][DataFrame.drop]`{ predicate: RowFilter<T> }` — drops every row that matches the predicate.
 *
 * @param [n] The number of rows to drop. Must not be negative.
 * @return A [DataFrame] containing all rows except the last [n],
 * or an empty [DataFrame] if [n] is greater than or equal to the number of rows.
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> DataFrame<T>.dropLast(n: Int = 1): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return take((nrow - n).coerceAtLeast(0))
}

/**
 * Returns a [DataFrame] containing all rows except the rows that satisfy the given [predicate].
 *
 * @include [SelectingRows.RowFilterSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * {@include [DocumentationUrls.Drop]}
 *
 * See also:
 * - [filter][DataFrame.filter] — keeps only the rows that match the predicate.
 * - [dropWhile][DataFrame.dropWhile] — drops only the first rows that match the predicate.
 * - [drop][DataFrame.drop]`(n: Int)` — drops a fixed number of first rows.
 *
 * @param [predicate] The [RowFilter] used to exclude rows from this [DataFrame].
 * @return A [DataFrame] containing all rows that do not satisfy the [predicate].
 */
public inline fun <T> DataFrame<T>.drop(predicate: RowFilter<T>): DataFrame<T> = filter { !predicate(it, it) }

/**
 * Returns a [DataFrame] containing all rows except the first rows that satisfy the given [predicate].
 *
 * Rows are dropped for as long as the [predicate] holds; the operation stops at the first row that
 * does not satisfy it, and no later row is dropped even if it satisfies the [predicate].
 *
 * @include [SelectingRows.RowFilterSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * For more information: {@include [DocumentationUrls.DropWhile]}
 *
 * See also:
 * - [drop][DataFrame.drop]`(n: Int)` — drops a fixed number of first rows.
 * - [dropLast][DataFrame.dropLast] — drops a fixed number of last rows.
 * - [takeWhile][DataFrame.takeWhile] — keeps the first rows while the predicate holds.
 * - [drop][DataFrame.drop]`{ predicate: RowFilter<T> }` — drops every row that matches the predicate.
 *
 * @param [predicate] The [RowFilter] that the leading rows to drop must satisfy.
 * @return A [DataFrame] containing all rows except the first ones that satisfy the [predicate].
 */
public inline fun <T> DataFrame<T>.dropWhile(predicate: RowFilter<T>): DataFrame<T> =
    firstOrNull { !predicate(it, it) }?.let { drop(it.index()) } ?: this

// endregion

// region ColumnsSelectionDsl

/**
 * ## Drop {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface DropColumnsSelectionDsl {

    /**
     * @include [TakeAndDropColumnsSelectionDslGrammar]
     * @set [TakeAndDropColumnsSelectionDslGrammar.TITLE] Drop
     * @set [TakeAndDropColumnsSelectionDslGrammar.OPERATION] drop
     */
    public interface Grammar {

        /** [**`drop`**][ColumnsSelectionDsl.drop]`(`[**`Last`**][ColumnsSelectionDsl.dropLast]`)` */
        public typealias PlainDslName = Nothing

        /** __`.`__[**`drop`**][ColumnsSelectionDsl.drop]`(`[**`Last`**][ColumnSet.dropLast]`)` */
        public typealias ColumnSetName = Nothing

        /** __`.`__[**`drop`**][ColumnsSelectionDsl.dropCols]`(`[**`Last`**][ColumnsSelectionDsl.dropLastCols]`)`[**`Cols`**][ColumnsSelectionDsl.dropCols] */
        public typealias ColumnGroupName = Nothing

        /** [**`drop`**][ColumnsSelectionDsl.dropWhile]`(`[**`Last`**][ColumnsSelectionDsl.dropLastWhile]`)`[**`While`**][ColumnsSelectionDsl.dropWhile] */
        public typealias PlainDslWhileName = Nothing

        /** __`.`__[**`drop`**][ColumnsSelectionDsl.dropWhile]`(`[**`Last`**][ColumnsSelectionDsl.dropLastWhile]`)`[**`While`**][ColumnsSelectionDsl.dropWhile] */
        public typealias ColumnSetWhileName = Nothing

        /** __`.`__[**`drop`**][ColumnsSelectionDsl.dropColsWhile]`(`[**`Last`**][ColumnsSelectionDsl.dropLastColsWhile]`)`[**`ColsWhile`**][ColumnsSelectionDsl.dropColsWhile] */
        public typealias ColumnGroupWhileName = Nothing
    }

    // region drop

    /**
     * @include [CommonTakeAndDropDocs]
     * {@set [CommonTakeAndDropDocs.URL] {@include [DocumentationUrls.DropCols]}}
     * @set [CommonTakeAndDropDocs.TITLE] Drop
     * @set [CommonTakeAndDropDocs.OPERATION] drop
     * @set [CommonTakeAndDropDocs.NOUN] drop
     * @set [CommonTakeAndDropDocs.FIRST_OR_LAST] first
     * @set [CommonTakeAndDropDocs.SEE_ALSO]
     * - [dropLast][ColumnsSelectionDsl.dropLast] — drops the last `n` columns instead.
     * - [dropWhile][ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [dropLastWhile][ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [take][ColumnsSelectionDsl.take] — keeps only the first `n` columns.
     * @set [CommonTakeAndDropDocs.RETURN] A [ColumnSet] containing all columns except the first [n\].
     */
    private typealias CommonDropFirstDocs = Nothing

    /**
     * @include [CommonDropFirstDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[drop][ColumnSet.drop]`(2) }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[drop][ColumnSet.drop]`(2) }`
     */
    @Interpretable("Drop0")
    public fun <C> ColumnSet<C>.drop(n: Int): ColumnSet<C> = transform { it.drop(n) }

    /**
     * @include [CommonDropFirstDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[drop][ColumnsSelectionDsl.drop]`(5) }`
     */
    @Interpretable("Drop1")
    public fun ColumnsSelectionDsl<*>.drop(n: Int): ColumnSet<*> = asSingleColumn().dropCols(n)

    /**
     * @include [CommonDropFirstDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropCols][SingleColumn.dropCols]`(1) }`
     */
    @Interpretable("Drop2")
    public fun SingleColumn<DataRow<*>>.dropCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().drop(n) }

    /**
     * @include [CommonDropFirstDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropCols][String.dropCols]`(1) }`
     */
    public fun String.dropCols(n: Int): ColumnSet<*> = columnGroup(this).dropCols(n)

    /**
     * @include [CommonDropFirstDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropCols][KProperty.dropCols]`(1) }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.dropCols(n: Int): ColumnSet<*> = columnGroup(this).dropCols(n)

    /**
     * @include [CommonDropFirstDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropCols][ColumnPath.dropCols]`(1) }`
     */
    public fun ColumnPath.dropCols(n: Int): ColumnSet<*> = columnGroup(this).dropCols(n)

    // endregion

    // region dropLast

    /**
     * @include [CommonTakeAndDropDocs]
     * {@set [CommonTakeAndDropDocs.URL] {@include [DocumentationUrls.DropCols]}}
     * @set [CommonTakeAndDropDocs.TITLE] Drop Last
     * @set [CommonTakeAndDropDocs.OPERATION] dropLast
     * @set [CommonTakeAndDropDocs.NOUN] drop
     * @set [CommonTakeAndDropDocs.FIRST_OR_LAST] last
     * @set [CommonTakeAndDropDocs.SEE_ALSO]
     * - [drop][ColumnsSelectionDsl.drop] — drops the first `n` columns instead.
     * - [dropWhile][ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [dropLastWhile][ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [takeLast][ColumnsSelectionDsl.takeLast] — keeps only the last `n` columns.
     * @set [CommonTakeAndDropDocs.RETURN] A [ColumnSet] containing all columns except the last [n\].
     */
    private typealias CommonDropLastDocs = Nothing

    /**
     * @include [CommonDropLastDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[dropLast][ColumnSet.dropLast]`(2) }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[dropLast][ColumnSet.dropLast]`() }`
     */
    @Interpretable("DropLast0")
    public fun <C> ColumnSet<C>.dropLast(n: Int = 1): ColumnSet<C> = transform { it.dropLast(n) }

    /**
     * @include [CommonDropLastDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[dropLast][ColumnsSelectionDsl.dropLast]`(5) }`
     */
    @Interpretable("DropLast1")
    public fun ColumnsSelectionDsl<*>.dropLast(n: Int = 1): ColumnSet<*> = this.asSingleColumn().dropLastCols(n)

    /**
     * @include [CommonDropLastDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropLastCols][SingleColumn.dropLastCols]`() }`
     */
    @Interpretable("DropLast2")
    public fun SingleColumn<DataRow<*>>.dropLastCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().dropLast(n) }

    /**
     * @include [CommonDropLastDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropLastCols][String.dropLastCols]`(1) }`
     */
    public fun String.dropLastCols(n: Int): ColumnSet<*> = columnGroup(this).dropLastCols(n)

    /**
     * @include [CommonDropLastDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropLastCols][KProperty.dropLastCols]`(1) }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.dropLastCols(n: Int): ColumnSet<*> = columnGroup(this).dropLastCols(n)

    /**
     * @include [CommonDropLastDocs]
     * @set [CommonTakeAndDropDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropLastCols][ColumnPath.dropLastCols]`(1) }`
     */
    public fun ColumnPath.dropLastCols(n: Int): ColumnSet<*> = columnGroup(this).dropLastCols(n)

    // endregion

    // region dropWhile

    /**
     * @include [CommonTakeAndDropWhileDocs]
     * {@set [CommonTakeAndDropWhileDocs.URL] {@include [DocumentationUrls.DropCols]}}
     * @set [CommonTakeAndDropWhileDocs.TITLE] Drop
     * @set [CommonTakeAndDropWhileDocs.OPERATION] drop
     * @set [CommonTakeAndDropWhileDocs.NOUN] drop
     * @set [CommonTakeAndDropWhileDocs.FIRST_OR_LAST] first
     * @set [CommonTakeAndDropWhileDocs.SEE_ALSO]
     * - [drop][ColumnsSelectionDsl.drop] — drops a fixed number of first columns.
     * - [dropLast][ColumnsSelectionDsl.dropLast] — drops a fixed number of last columns.
     * - [dropLastWhile][ColumnsSelectionDsl.dropLastWhile] — drops the last columns while a predicate holds.
     * - [takeWhile][ColumnsSelectionDsl.takeWhile] — keeps the first columns while a predicate holds.
     * @set [CommonTakeAndDropWhileDocs.RETURN] A [ColumnSet] containing all columns except the first
     * ones adhering to the [predicate\].
     */
    private typealias CommonDropWhileDocs = Nothing

    /**
     * @include [CommonDropWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[dropWhile][ColumnSet.dropWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[dropWhile][ColumnSet.dropWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun <C> ColumnSet<C>.dropWhile(predicate: (ColumnWithPath<C>) -> Boolean): ColumnSet<C> =
        transform { it.dropWhile(predicate) }

    /**
     * @include [CommonDropWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[dropWhile][ColumnsSelectionDsl.dropWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun ColumnsSelectionDsl<*>.dropWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        this.asSingleColumn().dropColsWhile(predicate)

    /**
     * @include [CommonDropWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropColsWhile][SingleColumn.dropColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun SingleColumn<DataRow<*>>.dropColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().dropWhile(predicate) }

    /**
     * @include [CommonDropWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropColsWhile][String.dropColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun String.dropColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).dropColsWhile(predicate)

    /**
     * @include [CommonDropWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropColsWhile][KProperty.dropColsWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.dropColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).dropColsWhile(predicate)

    /**
     * @include [CommonDropWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropColsWhile][ColumnPath.dropColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnPath.dropColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).dropColsWhile(predicate)

    // endregion

    // region dropLastWhile

    /**
     * @include [CommonTakeAndDropWhileDocs]
     * {@set [CommonTakeAndDropWhileDocs.URL] {@include [DocumentationUrls.DropCols]}}
     * @set [CommonTakeAndDropWhileDocs.TITLE] Drop Last
     * @set [CommonTakeAndDropWhileDocs.OPERATION] dropLast
     * @set [CommonTakeAndDropWhileDocs.NOUN] drop
     * @set [CommonTakeAndDropWhileDocs.FIRST_OR_LAST] last
     * @set [CommonTakeAndDropWhileDocs.SEE_ALSO]
     * - [drop][ColumnsSelectionDsl.drop] — drops a fixed number of first columns.
     * - [dropLast][ColumnsSelectionDsl.dropLast] — drops a fixed number of last columns.
     * - [dropWhile][ColumnsSelectionDsl.dropWhile] — drops the first columns while a predicate holds.
     * - [takeLastWhile][ColumnsSelectionDsl.takeLastWhile] — keeps the last columns while a predicate holds.
     * @set [CommonTakeAndDropWhileDocs.RETURN] A [ColumnSet] containing all columns except the last
     * ones adhering to the [predicate\].
     */
    private typealias CommonDropLastWhileDocs = Nothing

    /**
     * @include [CommonDropLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[dropLastWhile][ColumnSet.dropLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[dropLastWhile][ColumnSet.dropLastWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun <C> ColumnSet<C>.dropLastWhile(predicate: (ColumnWithPath<C>) -> Boolean): ColumnSet<C> =
        transform { it.dropLastWhile(predicate) }

    /**
     * @include [CommonDropLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]`  {  `[dropLastWhile][ColumnsSelectionDsl.dropLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun ColumnsSelectionDsl<*>.dropLastWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        this.asSingleColumn().dropLastColsWhile(predicate)

    /**
     * @include [CommonDropLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropLastColsWhile][SingleColumn.dropLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun SingleColumn<DataRow<*>>.dropLastColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().dropLastWhile(predicate) }

    /**
     * @include [CommonDropLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropLastColsWhile][String.dropLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun String.dropLastColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).dropLastColsWhile(predicate)

    /**
     * @include [CommonDropLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[dropLastColsWhile][SingleColumn.dropLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropLastColsWhile][KProperty.dropLastColsWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.dropLastColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).dropLastColsWhile(predicate)

    /**
     * @include [CommonDropLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropLastColsWhile][ColumnPath.dropLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnPath.dropLastColsWhile(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).dropLastColsWhile(predicate)

    // endregion
}

// endregion
