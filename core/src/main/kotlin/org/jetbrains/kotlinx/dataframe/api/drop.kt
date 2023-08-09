package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
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

public fun <T> DataColumn<T>.drop(predicate: Predicate<T>): DataColumn<T> = filter { !predicate(it) }

public fun <T> DataColumn<T>.drop(n: Int): DataColumn<T> = when {
    n == 0 -> this
    n >= size -> get(emptyList())
    else -> get(n until size)
}

public fun <T> DataColumn<T>.dropLast(n: Int = 1): DataColumn<T> = take(size - n)

// endregion

// region DataFrame

/**
 * Returns a DataFrame containing all rows except first [n] rows.
 *
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> DataFrame<T>.drop(n: Int): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return getRows(n.coerceAtMost(nrow) until nrow)
}

/**
 * Returns a DataFrame containing all rows except last [n] rows.
 *
 * @throws IllegalArgumentException if [n] is negative.
 */
public fun <T> DataFrame<T>.dropLast(n: Int = 1): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return take((nrow - n).coerceAtLeast(0))
}

/**
 * Returns a DataFrame containing all rows except rows that satisfy the given [predicate].
 */
public fun <T> DataFrame<T>.drop(predicate: RowFilter<T>): DataFrame<T> = filter { !predicate(it, it) }

/**
 * Returns a DataFrame containing all rows except first rows that satisfy the given [predicate].
 */
public fun <T> DataFrame<T>.dropWhile(predicate: RowFilter<T>): DataFrame<T> =
    firstOrNull { !predicate(it, it) }?.let { drop(it.index) } ?: this

// endregion

// region ColumnsSelectionDsl
public interface DropColumnsSelectionDsl {

    // region drop

    /**
     * @include [CommonTakeAndDropDocs]
     * @setArg [CommonTakeAndDropDocs.TitleArg] Drop
     * @setArg [CommonTakeAndDropDocs.OperationArg] drop
     * @setArg [CommonTakeAndDropDocs.NounArg] drop
     * @setArg [CommonTakeAndDropDocs.FirstOrLastArg] first
     */
    private interface CommonDropFirstDocs

    /**
     * @include [CommonDropFirstDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[drop][ColumnSet.drop]`(2) }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[drop][ColumnSet.drop]`(2) }`
     */
    public fun <C> ColumnSet<C>.drop(n: Int): ColumnSet<C> = transform { it.drop(n) }

    /**
     * @include [CommonDropFirstDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[drop][ColumnsSelectionDsl.drop]`(5) }`
     */
    public fun ColumnsSelectionDsl<*>.drop(n: Int): ColumnSet<*> =
        asSingleColumn().dropChildren(n)

    /**
     * @include [CommonDropFirstDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropChildren][SingleColumn.dropChildren]`(1) }`
     */
    public fun SingleColumn<DataRow<*>>.dropChildren(n: Int): ColumnSet<*> =
        ensureIsColGroup().transformSingle { it.children().drop(n) }

    /**
     * @include [CommonDropFirstDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropChildren][String.dropChildren]`(1) }`
     */
    public fun String.dropChildren(n: Int): ColumnSet<*> = columnGroup(this).dropChildren(n)

    /**
     * @include [CommonDropFirstDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[dropChildren][SingleColumn.dropChildren]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[asColumnGroup][KProperty.asColumnGroup]`().`[dropChildren][SingleColumn.dropChildren]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropChildren][KProperty.dropChildren]`(1) }`
     */
    public fun KProperty<DataRow<*>>.dropChildren(n: Int): ColumnSet<*> = columnGroup(this).dropChildren(n)

    /**
     * @include [CommonDropFirstDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropChildren][ColumnPath.dropChildren]`(1) }`
     */
    public fun ColumnPath.dropChildren(n: Int): ColumnSet<*> = columnGroup(this).dropChildren(n)

    // endregion

    // region dropLast

    /**
     * @include [CommonTakeAndDropDocs]
     * @setArg [CommonTakeAndDropDocs.TitleArg] Drop Last
     * @setArg [CommonTakeAndDropDocs.OperationArg] dropLast
     * @setArg [CommonTakeAndDropDocs.NounArg] drop
     * @setArg [CommonTakeAndDropDocs.FirstOrLastArg] last
     */
    private interface CommonDropLastDocs

    /**
     * @include [CommonDropLastDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[dropLast][ColumnSet.dropLast]`(2) }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[dropLast][ColumnSet.dropLast]`() }`
     */
    public fun <C> ColumnSet<C>.dropLast(n: Int = 1): ColumnSet<C> = transform { it.dropLast(n) }

    /**
     * @include [CommonDropLastDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[dropLast][ColumnsSelectionDsl.dropLast]`(5) }`
     */
    public fun ColumnsSelectionDsl<*>.dropLast(n: Int = 1): ColumnSet<*> =
        this.asSingleColumn().dropLastChildren(n)

    /**
     * @include [CommonDropLastDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropLastChildren][SingleColumn.dropLastChildren]`() }`
     */
    public fun SingleColumn<DataRow<*>>.dropLastChildren(n: Int): ColumnSet<*> =
        ensureIsColGroup().transformSingle { it.children().dropLast(n) }

    /**
     * @include [CommonDropLastDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropLastChildren][String.dropLastChildren]`(1) }`
     */
    public fun String.dropLastChildren(n: Int): ColumnSet<*> = columnGroup(this).dropLastChildren(n)

    /**
     * @include [CommonDropLastDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[dropLastChildren][SingleColumn.dropLastChildren]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[asColumnGroup][KProperty.asColumnGroup]`().`[dropLastChildren][SingleColumn.dropLastChildren]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropLastChildren][KProperty.dropLastChildren]`(1) }`
     */
    public fun KProperty<DataRow<*>>.dropLastChildren(n: Int): ColumnSet<*> = columnGroup(this).dropLastChildren(n)

    /**
     * @include [CommonDropLastDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropLastChildren][ColumnPath.dropLastChildren]`(1) }`
     */
    public fun ColumnPath.dropLastChildren(n: Int): ColumnSet<*> = columnGroup(this).dropLastChildren(n)

    // endregion

    // region dropWhile

    /**
     * @include [CommonTakeAndDropWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.TitleArg] Drop
     * @setArg [CommonTakeAndDropWhileDocs.OperationArg] drop
     * @setArg [CommonTakeAndDropWhileDocs.NounArg] drop
     * @setArg [CommonTakeAndDropWhileDocs.FirstOrLastArg] first
     */
    private interface CommonDropWhileDocs

    /**
     * @include [CommonDropWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[dropWhile][ColumnSet.dropWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[dropWhile][ColumnSet.dropWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun <C> ColumnSet<C>.dropWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.dropWhile(predicate) }

    /**
     * @include [CommonDropWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[dropWhile][ColumnsSelectionDsl.dropWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun ColumnsSelectionDsl<*>.dropWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.asSingleColumn().dropChildrenWhile(predicate)

    /**
     * @include [CommonDropWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropChildrenWhile][SingleColumn.dropChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun SingleColumn<DataRow<*>>.dropChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        ensureIsColGroup().transformSingle { it.children().dropWhile(predicate) }

    /**
     * @include [CommonDropWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropChildrenWhile][String.dropChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun String.dropChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropChildrenWhile(predicate)

    /**
     * @include [CommonDropWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[dropChildrenWhile][SingleColumn.dropChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropChildrenWhile][KProperty.dropChildrenWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun KProperty<DataRow<*>>.dropChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropChildrenWhile(predicate)

    /**
     * @include [CommonDropWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropChildrenWhile][ColumnPath.dropChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnPath.dropChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropChildrenWhile(predicate)

    // endregion

    // region dropLastWhile

    /**
     * @include [CommonTakeAndDropWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.TitleArg] Drop Last
     * @setArg [CommonTakeAndDropWhileDocs.OperationArg] dropLast
     * @setArg [CommonTakeAndDropWhileDocs.NounArg] drop
     * @setArg [CommonTakeAndDropWhileDocs.FirstOrLastArg] last
     */
    private interface CommonDropLastWhileDocs

    /**
     * @include [CommonDropLastWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[dropLastWhile][ColumnSet.dropLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[dropLastWhile][ColumnSet.dropLastWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun <C> ColumnSet<C>.dropLastWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.dropLastWhile(predicate) }

    /**
     * @include [CommonDropLastWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[dropLastWhile][ColumnsSelectionDsl.dropLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun ColumnsSelectionDsl<*>.dropLastWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.asSingleColumn().dropLastChildrenWhile(predicate)

    /**
     * @include [CommonDropLastWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropLastChildrenWhile][SingleColumn.dropLastChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun SingleColumn<DataRow<*>>.dropLastChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        ensureIsColGroup().transformSingle { it.children().dropLastWhile(predicate) }

    /**
     * @include [CommonDropLastWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropLastChildrenWhile][String.dropLastChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun String.dropLastChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropLastChildrenWhile(predicate)

    /**
     * @include [CommonDropLastWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[dropLastChildrenWhile][SingleColumn.dropLastChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropLastChildrenWhile][KProperty.dropLastChildrenWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun KProperty<DataRow<*>>.dropLastChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropLastChildrenWhile(predicate)

    /**
     * @include [CommonDropLastWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropLastChildrenWhile][ColumnPath.dropLastChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnPath.dropLastChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropLastChildrenWhile(predicate)

    // endregion
}
// endregion
