package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropDocs
import org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.index
import org.jetbrains.kotlinx.dataframe.nrow
import kotlin.reflect.KProperty

// region DataColumn

public fun <T> DataColumn<T>.take(n: Int): DataColumn<T> = when {
    n == 0 -> get(emptyList())
    n >= size -> this
    else -> get(0 until n)
}

public fun <T> DataColumn<T>.takeLast(n: Int): DataColumn<T> = drop(size - n)

// endregion

// region DataFrame

/**
 * Returns a DataFrame containing first [n] rows.
 *
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> DataFrame<T>.take(n: Int): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return getRows(0 until n.coerceAtMost(nrow))
}

/**
 * Returns a DataFrame containing last [n] rows.
 *
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> DataFrame<T>.takeLast(n: Int): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return drop((nrow - n).coerceAtLeast(0))
}

/**
 * Returns a DataFrame containing first rows that satisfy the given [predicate].
 */
public fun <T> DataFrame<T>.takeWhile(predicate: RowFilter<T>): DataFrame<T> = firstOrNull { !predicate(it, it) }?.let { take(it.index) } ?: this

// endregion

// region ColumnsSelectionDsl
public interface TakeColumnsSelectionDsl {

    // region take

    /**
     * @include [CommonTakeAndDropDocs]
     * @arg [CommonTakeAndDropDocs.TitleArg] Take
     * @arg [CommonTakeAndDropDocs.OperationArg] take
     * @arg [CommonTakeAndDropDocs.NounArg] take
     * @arg [CommonTakeAndDropDocs.FirstOrLastArg] first
     */
    private interface CommonTakeFirstDocs

    /**
     * @include [CommonTakeFirstDocs]
     * @arg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[take][ColumnSet.take]`(2) }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[take][ColumnSet.take]`(2) }`
     */
    public fun <C> ColumnSet<C>.take(n: Int): ColumnSet<C> = transform { it.take(n) }

    /**
     * @include [CommonTakeFirstDocs]
     * @arg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(5) }`
     */
    public fun ColumnsSelectionDsl<*>.take(n: Int): ColumnSet<*> =
        this.asSingleColumn().takeChildren(n)

    /**
     * @include [CommonTakeFirstDocs]
     * @arg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeChildren][SingleColumn.takeChildren]`(1) }`
     */
    public fun SingleColumn<DataRow<*>>.takeChildren(n: Int): ColumnSet<*> =
        ensureIsColGroup().transformSingle { it.children().take(n) }

    /**
     * @include [CommonTakeFirstDocs]
     * @arg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeChildren][String.takeChildren]`(1) }`
     */
    public fun String.takeChildren(n: Int): ColumnSet<*> = columnGroup(this).takeChildren(n)

    /**
     * @include [CommonTakeFirstDocs]
     * @arg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[takeChildren][SingleColumn.takeChildren]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[asColumnGroup][KProperty.asColumnGroup]`().`[takeChildren][SingleColumn.takeChildren]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeChildren][KProperty.takeChildren]`(1) }`
     */
    public fun KProperty<DataRow<*>>.takeChildren(n: Int): ColumnSet<*> = columnGroup(this).takeChildren(n)

    /**
     * @include [CommonTakeFirstDocs]
     * @arg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeChildren][ColumnPath.takeChildren]`(1) }`
     */
    public fun ColumnPath.takeChildren(n: Int): ColumnSet<*> = columnGroup(this).takeChildren(n)

    // endregion

    // region takeLast

    /**
     * @include [CommonTakeAndDropDocs]
     * @arg [CommonTakeAndDropDocs.TitleArg] Take Last
     * @arg [CommonTakeAndDropDocs.OperationArg] takeLast
     * @arg [CommonTakeAndDropDocs.NounArg] take
     * @arg [CommonTakeAndDropDocs.FirstOrLastArg] last
     */
    private interface CommonTakeLastDocs

    /**
     * @include [CommonTakeLastDocs]
     * @arg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeLast][ColumnSet.takeLast]`(2) }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeLast][ColumnSet.takeLast]`(2) }`
     */
    public fun <C> ColumnSet<C>.takeLast(n: Int = 1): ColumnSet<C> = transform { it.takeLast(n) }

    /**
     * @include [CommonTakeLastDocs]
     * @arg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(5) }`
     */
    public fun ColumnsSelectionDsl<*>.takeLast(n: Int = 1): ColumnSet<*> =
        asSingleColumn().takeLastChildren(n)

    /**
     * @include [CommonTakeLastDocs]
     * @arg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeLast][SingleColumn.takeLastChildren]`(1) }`
     */
    public fun SingleColumn<DataRow<*>>.takeLastChildren(n: Int): ColumnSet<*> =
        ensureIsColGroup().transformSingle { it.children().takeLast(n) }

    /**
     * @include [CommonTakeLastDocs]
     * @arg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeLastChildren][String.takeLastChildren]`(1) }`
     */
    public fun String.takeLastChildren(n: Int): ColumnSet<*> = columnGroup(this).takeLastChildren(n)

    /**
     * @include [CommonTakeLastDocs]
     * @arg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[takeLastChildren][SingleColumn.takeLastChildren]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[asColumnGroup][KProperty.asColumnGroup]`().`[takeLastChildren][SingleColumn.takeLastChildren]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeLastChildren][KProperty.takeLastChildren]`(1) }`
     */
    public fun KProperty<DataRow<*>>.takeLastChildren(n: Int): ColumnSet<*> = columnGroup(this).takeLastChildren(n)

    /**
     * @include [CommonTakeLastDocs]
     * @arg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeLastChildren][ColumnPath.takeLastChildren]`(1) }`
     */
    public fun ColumnPath.takeLastChildren(n: Int): ColumnSet<*> = columnGroup(this).takeLastChildren(n)

    // endregion

    // region takeWhile

    /**
     * @include [CommonTakeAndDropWhileDocs]
     * @arg [CommonTakeAndDropWhileDocs.TitleArg] Take
     * @arg [CommonTakeAndDropWhileDocs.OperationArg] take
     * @arg [CommonTakeAndDropWhileDocs.NounArg] take
     * @arg [CommonTakeAndDropWhileDocs.FirstOrLastArg] first
     */
    private interface CommonTakeFirstWhileDocs

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @arg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeWhile][ColumnSet.takeWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeWhile][ColumnSet.takeWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun <C> ColumnSet<C>.takeWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.takeWhile(predicate) }

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @arg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[takeWhile][ColumnsSelectionDsl.takeWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun ColumnsSelectionDsl<*>.takeWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        asSingleColumn().takeChildrenWhile(predicate)

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @arg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun SingleColumn<DataRow<*>>.takeChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        ensureIsColGroup().transformSingle { it.children().takeWhile(predicate) }

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @arg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeChildrenWhile][String.takeChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun String.takeChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeChildrenWhile(predicate)

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @arg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[takeChildrenWhile][SingleColumn.takeChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeChildrenWhile][KProperty.takeChildrenWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun KProperty<DataRow<*>>.takeChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeChildrenWhile(predicate)

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @arg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeChildrenWhile][ColumnPath.takeChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnPath.takeChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeChildrenWhile(predicate)

    // endregion

    // region takeLastWhile

    /**
     * @include [CommonTakeAndDropWhileDocs]
     * @arg [CommonTakeAndDropWhileDocs.TitleArg] Take Last
     * @arg [CommonTakeAndDropWhileDocs.OperationArg] takeLast
     * @arg [CommonTakeAndDropWhileDocs.NounArg] take
     * @arg [CommonTakeAndDropWhileDocs.FirstOrLastArg] last
     */
    private interface CommonTakeLastWhileDocs

    /**
     * @include [CommonTakeLastWhileDocs]
     * @arg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeLastWhile][ColumnSet.takeLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeLastWhile][ColumnSet.takeLastWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun <C> ColumnSet<C>.takeLastWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.takeLastWhile(predicate) }

    /**
     * @include [CommonTakeLastWhileDocs]
     * @arg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[takeLastWhile][ColumnsSelectionDsl.takeLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun ColumnsSelectionDsl<*>.takeLastWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        asSingleColumn().takeLastChildrenWhile(predicate)

    /**
     * @include [CommonTakeLastWhileDocs]
     * @arg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeLastChildrenWhile][SingleColumn.takeLastChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun SingleColumn<DataRow<*>>.takeLastChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        ensureIsColGroup().transformSingle { it.children().takeLastWhile(predicate) }

    /**
     * @include [CommonTakeLastWhileDocs]
     * @arg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeLastChildrenWhile][String.takeLastChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun String.takeLastChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeLastChildrenWhile(predicate)

    /**
     * @include [CommonTakeLastWhileDocs]
     * @arg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[takeLastChildrenWhile][SingleColumn.takeLastChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeLastChildrenWhile][KProperty.takeLastChildrenWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun KProperty<DataRow<*>>.takeLastChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeLastChildrenWhile(predicate)

    /**
     * @include [CommonTakeLastWhileDocs]
     * @arg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeLastChildrenWhile][ColumnPath.takeLastChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnPath.takeLastChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeLastChildrenWhile(predicate)

    // endregion
}
// endregion
