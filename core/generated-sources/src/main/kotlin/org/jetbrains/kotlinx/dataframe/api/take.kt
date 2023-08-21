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
     * ## Take (Children)
     * This function takes the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeChildren][SingleColumn.takeChildren] will take the first [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [take][ColumnSet.take] will take the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeChildren][SingleColumn.takeChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeChildren][String.takeChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonTakeAndDropDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropDocs.ExampleArg]}
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    private interface CommonTakeFirstDocs

    /**
     * ## Take (Children)
     * This function takes the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeChildren][SingleColumn.takeChildren] will take the first [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [take][ColumnSet.take] will take the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeChildren][SingleColumn.takeChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeChildren][String.takeChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[take][ColumnSet.take]`(2) }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[take][ColumnSet.take]`(2) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun <C> ColumnSet<C>.take(n: Int): ColumnSet<C> = transform { it.take(n) }

    /**
     * ## Take (Children)
     * This function takes the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeChildren][SingleColumn.takeChildren] will take the first [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [take][ColumnSet.take] will take the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeChildren][SingleColumn.takeChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeChildren][String.takeChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(5) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun ColumnsSelectionDsl<*>.take(n: Int): ColumnSet<*> =
        this.asSingleColumn().takeChildren(n)

    /**
     * ## Take (Children)
     * This function takes the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeChildren][SingleColumn.takeChildren] will take the first [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [take][ColumnSet.take] will take the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeChildren][SingleColumn.takeChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeChildren][String.takeChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeChildren][SingleColumn.takeChildren]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun SingleColumn<DataRow<*>>.takeChildren(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.children().take(n) }

    /**
     * ## Take (Children)
     * This function takes the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeChildren][SingleColumn.takeChildren] will take the first [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [take][ColumnSet.take] will take the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeChildren][SingleColumn.takeChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeChildren][String.takeChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeChildren][String.takeChildren]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun String.takeChildren(n: Int): ColumnSet<*> = columnGroup(this).takeChildren(n)

    /**
     * ## Take (Children)
     * This function takes the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeChildren][SingleColumn.takeChildren] will take the first [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [take][ColumnSet.take] will take the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeChildren][SingleColumn.takeChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeChildren][String.takeChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[takeChildren][SingleColumn.takeChildren]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeChildren][SingleColumn.takeChildren]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeChildren][KProperty.takeChildren]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun KProperty<DataRow<*>>.takeChildren(n: Int): ColumnSet<*> = columnGroup(this).takeChildren(n)

    /**
     * ## Take (Children)
     * This function takes the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeChildren][SingleColumn.takeChildren] will take the first [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [take][ColumnSet.take] will take the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `take` is called `takeChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[take][ColumnSet.take]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[take][ColumnsSelectionDsl.take]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeChildren][SingleColumn.takeChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeChildren][String.takeChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeChildren][ColumnPath.takeChildren]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun ColumnPath.takeChildren(n: Int): ColumnSet<*> = columnGroup(this).takeChildren(n)

    // endregion

    // region takeLast

    /**
     * ## Take Last (Children)
     * This function takes the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastChildren][SingleColumn.takeLastChildren] will take the last [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLast][ColumnSet.takeLast] will take the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastChildren][SingleColumn.takeLastChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastChildren][String.takeLastChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonTakeAndDropDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropDocs.ExampleArg]}
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    private interface CommonTakeLastDocs

    /**
     * ## Take Last (Children)
     * This function takes the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastChildren][SingleColumn.takeLastChildren] will take the last [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLast][ColumnSet.takeLast] will take the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastChildren][SingleColumn.takeLastChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastChildren][String.takeLastChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeLast][ColumnSet.takeLast]`(2) }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeLast][ColumnSet.takeLast]`(2) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun <C> ColumnSet<C>.takeLast(n: Int = 1): ColumnSet<C> = transform { it.takeLast(n) }

    /**
     * ## Take Last (Children)
     * This function takes the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastChildren][SingleColumn.takeLastChildren] will take the last [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLast][ColumnSet.takeLast] will take the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastChildren][SingleColumn.takeLastChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastChildren][String.takeLastChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(5) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun ColumnsSelectionDsl<*>.takeLast(n: Int = 1): ColumnSet<*> =
        asSingleColumn().takeLastChildren(n)

    /**
     * ## Take Last (Children)
     * This function takes the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastChildren][SingleColumn.takeLastChildren] will take the last [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLast][ColumnSet.takeLast] will take the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastChildren][SingleColumn.takeLastChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastChildren][String.takeLastChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeLast][SingleColumn.takeLastChildren]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun SingleColumn<DataRow<*>>.takeLastChildren(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.children().takeLast(n) }

    /**
     * ## Take Last (Children)
     * This function takes the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastChildren][SingleColumn.takeLastChildren] will take the last [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLast][ColumnSet.takeLast] will take the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastChildren][SingleColumn.takeLastChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastChildren][String.takeLastChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeLastChildren][String.takeLastChildren]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun String.takeLastChildren(n: Int): ColumnSet<*> = columnGroup(this).takeLastChildren(n)

    /**
     * ## Take Last (Children)
     * This function takes the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastChildren][SingleColumn.takeLastChildren] will take the last [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLast][ColumnSet.takeLast] will take the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastChildren][SingleColumn.takeLastChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastChildren][String.takeLastChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[takeLastChildren][SingleColumn.takeLastChildren]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[takeLastChildren][SingleColumn.takeLastChildren]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeLastChildren][KProperty.takeLastChildren]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun KProperty<DataRow<*>>.takeLastChildren(n: Int): ColumnSet<*> = columnGroup(this).takeLastChildren(n)

    /**
     * ## Take Last (Children)
     * This function takes the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastChildren][SingleColumn.takeLastChildren] will take the last [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLast][ColumnSet.takeLast] will take the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLast` is called `takeLastChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLast][ColumnSet.takeLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[takeLast][ColumnsSelectionDsl.takeLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastChildren][SingleColumn.takeLastChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastChildren][String.takeLastChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeLastChildren][ColumnPath.takeLastChildren]`(1) }`
     *
     * @param [n] The number of columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun ColumnPath.takeLastChildren(n: Int): ColumnSet<*> = columnGroup(this).takeLastChildren(n)

    // endregion

    // region takeWhile

    /**
     * ## Take (Children) While
     * This function takes the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeWhile][SingleColumn.takeWhile] will take the
     * first children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeWhile][ColumnSet.takeWhile] will
     * take the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeWhile][ColumnSet.takeWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeChildrenWhile][String.takeChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonTakeAndDropWhileDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs.ExampleArg]}
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    private interface CommonTakeFirstWhileDocs

    /**
     * ## Take (Children) While
     * This function takes the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeWhile][SingleColumn.takeWhile] will take the
     * first children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeWhile][ColumnSet.takeWhile] will
     * take the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeWhile][ColumnSet.takeWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeChildrenWhile][String.takeChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeWhile][ColumnSet.takeWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeWhile][ColumnSet.takeWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun <C> ColumnSet<C>.takeWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.takeWhile(predicate) }

    /**
     * ## Take (Children) While
     * This function takes the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeWhile][SingleColumn.takeWhile] will take the
     * first children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeWhile][ColumnSet.takeWhile] will
     * take the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeWhile][ColumnSet.takeWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeChildrenWhile][String.takeChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[takeWhile][ColumnsSelectionDsl.takeWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun ColumnsSelectionDsl<*>.takeWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        asSingleColumn().takeChildrenWhile(predicate)

    /**
     * ## Take (Children) While
     * This function takes the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeWhile][SingleColumn.takeWhile] will take the
     * first children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeWhile][ColumnSet.takeWhile] will
     * take the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeWhile][ColumnSet.takeWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeChildrenWhile][String.takeChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun SingleColumn<DataRow<*>>.takeChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.children().takeWhile(predicate) }

    /**
     * ## Take (Children) While
     * This function takes the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeWhile][SingleColumn.takeWhile] will take the
     * first children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeWhile][ColumnSet.takeWhile] will
     * take the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeWhile][ColumnSet.takeWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeChildrenWhile][String.takeChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeChildrenWhile][String.takeChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun String.takeChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeChildrenWhile(predicate)

    /**
     * ## Take (Children) While
     * This function takes the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeWhile][SingleColumn.takeWhile] will take the
     * first children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeWhile][ColumnSet.takeWhile] will
     * take the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeWhile][ColumnSet.takeWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeChildrenWhile][String.takeChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[takeChildrenWhile][SingleColumn.takeChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeChildrenWhile][KProperty.takeChildrenWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun KProperty<DataRow<*>>.takeChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeChildrenWhile(predicate)

    /**
     * ## Take (Children) While
     * This function takes the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeWhile][SingleColumn.takeWhile] will take the
     * first children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeWhile][ColumnSet.takeWhile] will
     * take the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeWhile` is called
     * `takeChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeWhile][ColumnSet.takeWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeWhile][SingleColumn.takeWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeChildrenWhile][String.takeChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeChildrenWhile][ColumnPath.takeChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun ColumnPath.takeChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeChildrenWhile(predicate)

    // endregion

    // region takeLastWhile

    /**
     * ## Take Last (Children) While
     * This function takes the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastWhile][SingleColumn.takeLastWhile] will take the
     * last children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLastWhile][ColumnSet.takeLastWhile] will
     * take the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLastWhile][ColumnSet.takeLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastWhile][SingleColumn.takeLastWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastChildrenWhile][String.takeLastChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonTakeAndDropWhileDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs.ExampleArg]}
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    private interface CommonTakeLastWhileDocs

    /**
     * ## Take Last (Children) While
     * This function takes the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastWhile][SingleColumn.takeLastWhile] will take the
     * last children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLastWhile][ColumnSet.takeLastWhile] will
     * take the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLastWhile][ColumnSet.takeLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastWhile][SingleColumn.takeLastWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastChildrenWhile][String.takeLastChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[takeLastWhile][ColumnSet.takeLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[takeLastWhile][ColumnSet.takeLastWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun <C> ColumnSet<C>.takeLastWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.takeLastWhile(predicate) }

    /**
     * ## Take Last (Children) While
     * This function takes the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastWhile][SingleColumn.takeLastWhile] will take the
     * last children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLastWhile][ColumnSet.takeLastWhile] will
     * take the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLastWhile][ColumnSet.takeLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastWhile][SingleColumn.takeLastWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastChildrenWhile][String.takeLastChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[takeLastWhile][ColumnsSelectionDsl.takeLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun ColumnsSelectionDsl<*>.takeLastWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        asSingleColumn().takeLastChildrenWhile(predicate)

    /**
     * ## Take Last (Children) While
     * This function takes the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastWhile][SingleColumn.takeLastWhile] will take the
     * last children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLastWhile][ColumnSet.takeLastWhile] will
     * take the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLastWhile][ColumnSet.takeLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastWhile][SingleColumn.takeLastWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastChildrenWhile][String.takeLastChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[takeLastChildrenWhile][SingleColumn.takeLastChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun SingleColumn<DataRow<*>>.takeLastChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.children().takeLastWhile(predicate) }

    /**
     * ## Take Last (Children) While
     * This function takes the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastWhile][SingleColumn.takeLastWhile] will take the
     * last children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLastWhile][ColumnSet.takeLastWhile] will
     * take the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLastWhile][ColumnSet.takeLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastWhile][SingleColumn.takeLastWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastChildrenWhile][String.takeLastChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[takeLastChildrenWhile][String.takeLastChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun String.takeLastChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeLastChildrenWhile(predicate)

    /**
     * ## Take Last (Children) While
     * This function takes the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastWhile][SingleColumn.takeLastWhile] will take the
     * last children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLastWhile][ColumnSet.takeLastWhile] will
     * take the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLastWhile][ColumnSet.takeLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastWhile][SingleColumn.takeLastWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastChildrenWhile][String.takeLastChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[takeLastChildrenWhile][SingleColumn.takeLastChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[takeLastChildrenWhile][KProperty.takeLastChildrenWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun KProperty<DataRow<*>>.takeLastChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeLastChildrenWhile(predicate)

    /**
     * ## Take Last (Children) While
     * This function takes the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [takeLastWhile][SingleColumn.takeLastWhile] will take the
     * last children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [takeLastWhile][ColumnSet.takeLastWhile] will
     * take the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `takeLastWhile` is called
     * `takeLastChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[takeLastWhile][ColumnSet.takeLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[takeLastWhile][SingleColumn.takeLastWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[takeLastChildrenWhile][String.takeLastChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[takeLastChildrenWhile][ColumnPath.takeLastChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to take.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun ColumnPath.takeLastChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).takeLastChildrenWhile(predicate)

    // endregion
}
// endregion
