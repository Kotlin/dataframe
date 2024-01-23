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
import org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar
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

public fun <T> DataColumn<T>.takeLast(n: Int = 1): DataColumn<T> = drop(size - n)

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
public fun <T> DataFrame<T>.takeLast(n: Int = 1): DataFrame<T> {
    require(n >= 0) { "Requested rows count $n is less than zero." }
    return drop((nrow - n).coerceAtLeast(0))
}

/**
 * Returns a DataFrame containing first rows that satisfy the given [predicate].
 */
public fun <T> DataFrame<T>.takeWhile(predicate: RowFilter<T>): DataFrame<T> =
    firstOrNull { !predicate(it, it) }?.let { take(it.index) } ?: this

// endregion

// region ColumnsSelectionDsl

public interface TakeColumnsSelectionDsl {

    /**
     * @include [TakeAndDropColumnsSelectionDslGrammar]
     * @setArg [TakeAndDropColumnsSelectionDslGrammar.TitleArg] Take
     * @setArg [TakeAndDropColumnsSelectionDslGrammar.OperationArg] take
     */
    public interface Grammar {

        /** [**take**][ColumnsSelectionDsl.take]`(`[**Last**][ColumnsSelectionDsl.takeLast]`)` */
        public interface PlainDslName

        /** .[**take**][ColumnsSelectionDsl.take]`(`[**Last**][ColumnSet.takeLast]`)` */
        public interface ColumnSetName

        /** .[**take**][ColumnsSelectionDsl.takeCols]`(`[**Last**][ColumnsSelectionDsl.takeLastCols]`)`[**Cols**][ColumnsSelectionDsl.takeCols] */
        public interface ColumnGroupName

        /** [**take**][ColumnsSelectionDsl.takeWhile]`(`[**Last**][ColumnsSelectionDsl.takeLastWhile]`)`[**While**][ColumnsSelectionDsl.takeWhile] */
        public interface PlainDslWhileName

        /** .[**take**][ColumnsSelectionDsl.takeWhile]`(`[**Last**][ColumnsSelectionDsl.takeLastWhile]`)`[**While**][ColumnsSelectionDsl.takeWhile] */
        public interface ColumnSetWhileName

        /** .[**take**][ColumnsSelectionDsl.takeColsWhile]`(`[**Last**][ColumnsSelectionDsl.takeLastColsWhile]`)`[**ColsWhile**][ColumnsSelectionDsl.takeColsWhile] */
        public interface ColumnGroupWhileName
    }

    // region take

    /**
     * @include [CommonTakeAndDropDocs]
     * @setArg [CommonTakeAndDropDocs.TitleArg] Take
     * @setArg [CommonTakeAndDropDocs.OperationArg] take
     * @setArg [CommonTakeAndDropDocs.NounArg] take
     * @setArg [CommonTakeAndDropDocs.FirstOrLastArg] first
     */
    private interface CommonTakeFirstDocs

    /**
     * @include [CommonTakeFirstDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[take][ColumnSet.take]`(2) }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[take][ColumnSet.take]`(2) }`
     */
    public fun <C> ColumnSet<C>.take(n: Int): ColumnSet<C> = transform { it.take(n) }

    /**
     * @include [CommonTakeFirstDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(5) }`
     */
    public fun ColumnsSelectionDsl<*>.take(n: Int): ColumnSet<*> =
        this.asSingleColumn().takeCols(n)

    /**
     * @include [CommonTakeFirstDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeCols][SingleColumn.takeCols]`(1) }`
     */
    public fun SingleColumn<DataRow<*>>.takeCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().take(n) }

    /**
     * @include [CommonTakeFirstDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeCols][String.takeCols]`(1) }`
     */
    public fun String.takeCols(n: Int): ColumnSet<*> = columnGroup(this).takeCols(n)

    /**
     * @include [CommonTakeFirstDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeCols][SingleColumn.takeCols]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeCols][KProperty.takeCols]`(1) }`
     */
    public fun KProperty<*>.takeCols(n: Int): ColumnSet<*> = columnGroup(this).takeCols(n)

    /**
     * @include [CommonTakeFirstDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeCols][ColumnPath.takeCols]`(1) }`
     */
    public fun ColumnPath.takeCols(n: Int): ColumnSet<*> = columnGroup(this).takeCols(n)

    // endregion

    // region takeLast

    /**
     * @include [CommonTakeAndDropDocs]
     * @setArg [CommonTakeAndDropDocs.TitleArg] Take Last
     * @setArg [CommonTakeAndDropDocs.OperationArg] takeLast
     * @setArg [CommonTakeAndDropDocs.NounArg] take
     * @setArg [CommonTakeAndDropDocs.FirstOrLastArg] last
     */
    private interface CommonTakeLastDocs

    /**
     * @include [CommonTakeLastDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeLast][ColumnSet.takeLast]`(2) }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeLast][ColumnSet.takeLast]`(2) }`
     */
    public fun <C> ColumnSet<C>.takeLast(n: Int = 1): ColumnSet<C> = transform { it.takeLast(n) }

    /**
     * @include [CommonTakeLastDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(5) }`
     */
    public fun ColumnsSelectionDsl<*>.takeLast(n: Int = 1): ColumnSet<*> =
        asSingleColumn().takeLastCols(n)

    /**
     * @include [CommonTakeLastDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeLast][SingleColumn.takeLastCols]`(1) }`
     */
    public fun SingleColumn<DataRow<*>>.takeLastCols(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().takeLast(n) }

    /**
     * @include [CommonTakeLastDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeLastCols][String.takeLastCols]`(1) }`
     */
    public fun String.takeLastCols(n: Int): ColumnSet<*> = columnGroup(this).takeLastCols(n)

    /**
     * @include [CommonTakeLastDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeLastCols][SingleColumn.takeLastCols]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeLastCols][KProperty.takeLastCols]`(1) }`
     */
    public fun KProperty<*>.takeLastCols(n: Int): ColumnSet<*> = columnGroup(this).takeLastCols(n)

    /**
     * @include [CommonTakeLastDocs]
     * @setArg [CommonTakeAndDropDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeLastCols][ColumnPath.takeLastCols]`(1) }`
     */
    public fun ColumnPath.takeLastCols(n: Int): ColumnSet<*> = columnGroup(this).takeLastCols(n)

    // endregion

    // region takeWhile

    /**
     * @include [CommonTakeAndDropWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.TitleArg] Take
     * @setArg [CommonTakeAndDropWhileDocs.OperationArg] take
     * @setArg [CommonTakeAndDropWhileDocs.NounArg] take
     * @setArg [CommonTakeAndDropWhileDocs.FirstOrLastArg] first
     */
    private interface CommonTakeFirstWhileDocs

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeWhile][ColumnSet.takeWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeWhile][ColumnSet.takeWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun <C> ColumnSet<C>.takeWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.takeWhile(predicate) }

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[takeWhile][ColumnsSelectionDsl.takeWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun ColumnsSelectionDsl<*>.takeWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        asSingleColumn().takeColsWhile(predicate)

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun SingleColumn<DataRow<*>>.takeColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().takeWhile(predicate) }

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeColsWhile][String.takeColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun String.takeColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeColsWhile(predicate)

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeColsWhile][SingleColumn.takeColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeColsWhile][KProperty.takeColsWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun KProperty<*>.takeColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeColsWhile(predicate)

    /**
     * @include [CommonTakeFirstWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeColsWhile][ColumnPath.takeColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnPath.takeColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeColsWhile(predicate)

    // endregion

    // region takeLastWhile

    /**
     * @include [CommonTakeAndDropWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.TitleArg] Take Last
     * @setArg [CommonTakeAndDropWhileDocs.OperationArg] takeLast
     * @setArg [CommonTakeAndDropWhileDocs.NounArg] take
     * @setArg [CommonTakeAndDropWhileDocs.FirstOrLastArg] last
     */
    private interface CommonTakeLastWhileDocs

    /**
     * @include [CommonTakeLastWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeLastWhile][ColumnSet.takeLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeLastWhile][ColumnSet.takeLastWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun <C> ColumnSet<C>.takeLastWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.takeLastWhile(predicate) }

    /**
     * @include [CommonTakeLastWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[takeLastWhile][ColumnsSelectionDsl.takeLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun ColumnsSelectionDsl<*>.takeLastWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        asSingleColumn().takeLastColsWhile(predicate)

    /**
     * @include [CommonTakeLastWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeLastColsWhile][SingleColumn.takeLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun SingleColumn<DataRow<*>>.takeLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.cols().takeLastWhile(predicate) }

    /**
     * @include [CommonTakeLastWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeLastColsWhile][String.takeLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun String.takeLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeLastColsWhile(predicate)

    /**
     * @include [CommonTakeLastWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeLastColsWhile][SingleColumn.takeLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeLastColsWhile][KProperty.takeLastColsWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     */
    public fun KProperty<*>.takeLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeLastColsWhile(predicate)

    /**
     * @include [CommonTakeLastWhileDocs]
     * @setArg [CommonTakeAndDropWhileDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeLastColsWhile][ColumnPath.takeLastColsWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnPath.takeLastColsWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeLastColsWhile(predicate)

    // endregion
}

// endregion
