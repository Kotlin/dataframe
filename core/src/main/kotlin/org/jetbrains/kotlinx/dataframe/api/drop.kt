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
import org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar
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

/**
 * ## Drop {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface DropColumnsSelectionDsl {

    /**
     * @include [TakeAndDropColumnsSelectionDslGrammar]
     * @set [TakeAndDropColumnsSelectionDslGrammar.TitleArg] Drop
     * @set [TakeAndDropColumnsSelectionDslGrammar.OperationArg] drop
     */
    public interface Grammar {

        /** [**`drop`**][ColumnsSelectionDsl.drop]`(`[**`Last`**][ColumnsSelectionDsl.dropLast]`)` */
        public interface PlainDslName

        /** __`.`__[**`drop`**][ColumnsSelectionDsl.drop]`(`[**`Last`**][ColumnSet.dropLast]`)` */
        public interface ColumnSetName

        /** __`.`__[**`drop`**][ColumnsSelectionDsl.dropCols]`(`[**`Last`**][ColumnsSelectionDsl.dropLastCols]`)`[**`Cols`**][ColumnsSelectionDsl.dropCols] */
        public interface ColumnGroupName

        /** [**`drop`**][ColumnsSelectionDsl.dropWhile]`(`[**`Last`**][ColumnsSelectionDsl.dropLastWhile]`)`[**`While`**][ColumnsSelectionDsl.dropWhile] */
        public interface PlainDslWhileName

        /** __`.`__[**`drop`**][ColumnsSelectionDsl.dropWhile]`(`[**`Last`**][ColumnsSelectionDsl.dropLastWhile]`)`[**`While`**][ColumnsSelectionDsl.dropWhile] */
        public interface ColumnSetWhileName

        /** __`.`__[**`drop`**][ColumnsSelectionDsl.dropColsWhile]`(`[**`Last`**][ColumnsSelectionDsl.dropLastColsWhile]`)`[**`ColsWhile`**][ColumnsSelectionDsl.dropColsWhile] */
        public interface ColumnGroupWhileName
    }

    // region drop

    /**
     * @include [CommonTakeAndDropDocs]
     * @set [CommonTakeAndDropDocs.TitleArg] Drop
     * @set [CommonTakeAndDropDocs.OperationArg] drop
     * @set [CommonTakeAndDropDocs.NounArg] drop
     * @set [CommonTakeAndDropDocs.FirstOrLastArg] first
     */
    private interface CommonDropFirstDocs

    /**
     * @include [CommonDropFirstDocs]
     * @set [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[drop][ColumnSet.drop]`(2) }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[drop][ColumnSet.drop]`(2) }`
     */
    public fun <C> ColumnSet<C>.drop(n: Int): ColumnSet<C> = transform { it.drop(n) }

    /**
     * @include [CommonDropFirstDocs]
     * @set [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]`  {  `[drop][ColumnsSelectionDsl.drop]`(5) }`
     */
    public fun ColumnsSelectionDsl<*>.drop(n: Int): ColumnSet<*> =
        asSingleColumn().dropCols(n)

    /**
     * @include [CommonDropFirstDocs]
     * @set [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropCols][SingleColumn.dropCols]`(1) }`
     */
    public fun SingleColumn<DataRow<*>>.dropCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().drop(n) }

    /**
     * @include [CommonDropFirstDocs]
     * @set [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropCols][String.dropCols]`(1) }`
     */
    public fun String.dropCols(n: Int): ColumnSet<*> = columnGroup(this).dropCols(n)

    /**
     * @include [CommonDropFirstDocs]
     * @set [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropCols][KProperty.dropCols]`(1) }`
     */
    public fun KProperty<*>.dropCols(n: Int): ColumnSet<*> = columnGroup(this).dropCols(n)

    /**
     * @include [CommonDropFirstDocs]
     * @set [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropCols][ColumnPath.dropCols]`(1) }`
     */
    public fun ColumnPath.dropCols(n: Int): ColumnSet<*> = columnGroup(this).dropCols(n)

    // endregion

    // region dropLast

    /**
     * @include [CommonTakeAndDropDocs]
     * @set [CommonTakeAndDropDocs.TitleArg] Drop Last
     * @set [CommonTakeAndDropDocs.OperationArg] dropLast
     * @set [CommonTakeAndDropDocs.NounArg] drop
     * @set [CommonTakeAndDropDocs.FirstOrLastArg] last
     */
    private interface CommonDropLastDocs

    /**
     * @include [CommonDropLastDocs]
     * @set [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[dropLast][ColumnSet.dropLast]`(2) }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[dropLast][ColumnSet.dropLast]`() }`
     */
    public fun <C> ColumnSet<C>.dropLast(n: Int = 1): ColumnSet<C> = transform { it.dropLast(n) }

    /**
     * @include [CommonDropLastDocs]
     * @set [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]`  {  `[dropLast][ColumnsSelectionDsl.dropLast]`(5) }`
     */
    public fun ColumnsSelectionDsl<*>.dropLast(n: Int = 1): ColumnSet<*> =
        this.asSingleColumn().dropLastCols(n)

    /**
     * @include [CommonDropLastDocs]
     * @set [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropLastCols][SingleColumn.dropLastCols]`() }`
     */
    public fun SingleColumn<DataRow<*>>.dropLastCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().dropLast(n) }

    /**
     * @include [CommonDropLastDocs]
     * @set [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropLastCols][String.dropLastCols]`(1) }`
     */
    public fun String.dropLastCols(n: Int): ColumnSet<*> = columnGroup(this).dropLastCols(n)

    /**
     * @include [CommonDropLastDocs]
     * @set [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropLastCols][KProperty.dropLastCols]`(1) }`
     */
    public fun KProperty<*>.dropLastCols(n: Int): ColumnSet<*> = columnGroup(this).dropLastCols(n)

    /**
     * @include [CommonDropLastDocs]
     * @set [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropLastCols][ColumnPath.dropLastCols]`(1) }`
     */
    public fun ColumnPath.dropLastCols(n: Int): ColumnSet<*> = columnGroup(this).dropLastCols(n)

    // endregion

    // region dropWhile

    /**
     * @include [CommonTakeAndDropWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.TitleArg] Drop
     * @set [CommonTakeAndDropWhileDocs.OperationArg] drop
     * @set [CommonTakeAndDropWhileDocs.NounArg] drop
     * @set [CommonTakeAndDropWhileDocs.FirstOrLastArg] first
     */
    private interface CommonDropWhileDocs

    /**
     * @include [CommonDropWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[dropWhile][ColumnSet.dropWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[dropWhile][ColumnSet.dropWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun <C> ColumnSet<C>.dropWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.dropWhile(predicate) }

    /**
     * @include [CommonDropWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]`  {  `[dropWhile][ColumnsSelectionDsl.dropWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun ColumnsSelectionDsl<*>.dropWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.asSingleColumn().dropColsWhile(predicate)

    /**
     * @include [CommonDropWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropColsWhile][SingleColumn.dropColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun SingleColumn<DataRow<*>>.dropColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().dropWhile(predicate) }

    /**
     * @include [CommonDropWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropColsWhile][String.dropColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun String.dropColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropColsWhile(predicate)

    /**
     * @include [CommonDropWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropColsWhile][KProperty.dropColsWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun KProperty<*>.dropColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropColsWhile(predicate)

    /**
     * @include [CommonDropWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropColsWhile][ColumnPath.dropColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnPath.dropColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropColsWhile(predicate)

    // endregion

    // region dropLastWhile

    /**
     * @include [CommonTakeAndDropWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.TitleArg] Drop Last
     * @set [CommonTakeAndDropWhileDocs.OperationArg] dropLast
     * @set [CommonTakeAndDropWhileDocs.NounArg] drop
     * @set [CommonTakeAndDropWhileDocs.FirstOrLastArg] last
     */
    private interface CommonDropLastWhileDocs

    /**
     * @include [CommonDropLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[dropLastWhile][ColumnSet.dropLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[dropLastWhile][ColumnSet.dropLastWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun <C> ColumnSet<C>.dropLastWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.dropLastWhile(predicate) }

    /**
     * @include [CommonDropLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]`  {  `[dropLastWhile][ColumnsSelectionDsl.dropLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun ColumnsSelectionDsl<*>.dropLastWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.asSingleColumn().dropLastColsWhile(predicate)

    /**
     * @include [CommonDropLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropLastColsWhile][SingleColumn.dropLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun SingleColumn<DataRow<*>>.dropLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().dropLastWhile(predicate) }

    /**
     * @include [CommonDropLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropLastColsWhile][String.dropLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun String.dropLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropLastColsWhile(predicate)

    /**
     * @include [CommonDropLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[dropLastColsWhile][SingleColumn.dropLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropLastColsWhile][KProperty.dropLastColsWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun KProperty<*>.dropLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropLastColsWhile(predicate)

    /**
     * @include [CommonDropLastWhileDocs]
     * @set [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropLastColsWhile][ColumnPath.dropLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnPath.dropLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropLastColsWhile(predicate)

    // endregion
}

// endregion
