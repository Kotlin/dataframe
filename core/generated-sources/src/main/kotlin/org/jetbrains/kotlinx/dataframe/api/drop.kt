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
public interface DropColumnsSelectionDsl<out T> : ColumnsSelectionDslExtension<T> {

    // region drop

    /**
     * ## Drop (Children)
     * This function drops the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropChildren][SingleColumn.dropChildren] will drop the first [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [drop][ColumnSet.drop] will drop the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[drop][ColumnSet.drop]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[drop][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropChildren][SingleColumn.dropChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropChildren][String.dropChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonTakeAndDropDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropDocs.ExampleArg]}
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    private interface CommonDropFirstDocs

    /**
     * ## Drop (Children)
     * This function drops the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropChildren][SingleColumn.dropChildren] will drop the first [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [drop][ColumnSet.drop] will drop the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[drop][ColumnSet.drop]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[drop][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropChildren][SingleColumn.dropChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropChildren][String.dropChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[drop][ColumnSet.drop]`(2) }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[drop][ColumnSet.drop]`(2) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun <C> ColumnSet<C>.drop(n: Int): ColumnSet<C> = transform { it.drop(n) }

    /**
     * ## Drop (Children)
     * This function drops the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropChildren][SingleColumn.dropChildren] will drop the first [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [drop][ColumnSet.drop] will drop the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[drop][ColumnSet.drop]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[drop][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropChildren][SingleColumn.dropChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropChildren][String.dropChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[drop][ColumnsSelectionDsl.drop]`(5) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun ColumnsSelectionDsl<*>.drop(n: Int): ColumnSet<*> =
        asSingleColumn().dropChildren(n)

    /**
     * ## Drop (Children)
     * This function drops the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropChildren][SingleColumn.dropChildren] will drop the first [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [drop][ColumnSet.drop] will drop the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[drop][ColumnSet.drop]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[drop][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropChildren][SingleColumn.dropChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropChildren][String.dropChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropChildren][SingleColumn.dropChildren]`(1) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun SingleColumn<DataRow<*>>.dropChildren(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.children().drop(n) }

    /**
     * ## Drop (Children)
     * This function drops the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropChildren][SingleColumn.dropChildren] will drop the first [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [drop][ColumnSet.drop] will drop the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[drop][ColumnSet.drop]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[drop][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropChildren][SingleColumn.dropChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropChildren][String.dropChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropChildren][String.dropChildren]`(1) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun String.dropChildren(n: Int): ColumnSet<*> = columnGroup(this).dropChildren(n)

    /**
     * ## Drop (Children)
     * This function drops the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropChildren][SingleColumn.dropChildren] will drop the first [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [drop][ColumnSet.drop] will drop the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[drop][ColumnSet.drop]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[drop][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropChildren][SingleColumn.dropChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropChildren][String.dropChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[dropChildren][SingleColumn.dropChildren]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[dropChildren][SingleColumn.dropChildren]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropChildren][KProperty.dropChildren]`(1) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun KProperty<DataRow<*>>.dropChildren(n: Int): ColumnSet<*> = columnGroup(this).dropChildren(n)

    /**
     * ## Drop (Children)
     * This function drops the first [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropChildren][SingleColumn.dropChildren] will drop the first [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [drop][ColumnSet.drop] will drop the first [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `drop` is called `dropChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[drop][ColumnSet.drop]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[drop][ColumnsSelectionDsl.drop]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropChildren][SingleColumn.dropChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropChildren][String.dropChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropChildren][ColumnPath.dropChildren]`(1) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first [n] columns.
     */
    public fun ColumnPath.dropChildren(n: Int): ColumnSet<*> = columnGroup(this).dropChildren(n)

    // endregion

    // region dropLast

    /**
     * ## Drop Last (Children)
     * This function drops the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropLastChildren][SingleColumn.dropLastChildren] will drop the last [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropLast][ColumnSet.dropLast] will drop the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLast][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[dropLast][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastChildren][SingleColumn.dropLastChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastChildren][String.dropLastChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonTakeAndDropDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropDocs.ExampleArg]}
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    private interface CommonDropLastDocs

    /**
     * ## Drop Last (Children)
     * This function drops the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropLastChildren][SingleColumn.dropLastChildren] will drop the last [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropLast][ColumnSet.dropLast] will drop the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLast][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[dropLast][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastChildren][SingleColumn.dropLastChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastChildren][String.dropLastChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[dropLast][ColumnSet.dropLast]`(2) }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[dropLast][ColumnSet.dropLast]`() }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun <C> ColumnSet<C>.dropLast(n: Int = 1): ColumnSet<C> = transform { it.dropLast(n) }

    /**
     * ## Drop Last (Children)
     * This function drops the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropLastChildren][SingleColumn.dropLastChildren] will drop the last [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropLast][ColumnSet.dropLast] will drop the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLast][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[dropLast][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastChildren][SingleColumn.dropLastChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastChildren][String.dropLastChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[dropLast][ColumnsSelectionDsl.dropLast]`(5) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun ColumnsSelectionDsl<*>.dropLast(n: Int = 1): ColumnSet<*> =
        this.asSingleColumn().dropLastChildren(n)

    /**
     * ## Drop Last (Children)
     * This function drops the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropLastChildren][SingleColumn.dropLastChildren] will drop the last [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropLast][ColumnSet.dropLast] will drop the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLast][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[dropLast][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastChildren][SingleColumn.dropLastChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastChildren][String.dropLastChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropLastChildren][SingleColumn.dropLastChildren]`() }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun SingleColumn<DataRow<*>>.dropLastChildren(n: Int): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.children().dropLast(n) }

    /**
     * ## Drop Last (Children)
     * This function drops the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropLastChildren][SingleColumn.dropLastChildren] will drop the last [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropLast][ColumnSet.dropLast] will drop the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLast][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[dropLast][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastChildren][SingleColumn.dropLastChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastChildren][String.dropLastChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropLastChildren][String.dropLastChildren]`(1) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun String.dropLastChildren(n: Int): ColumnSet<*> = columnGroup(this).dropLastChildren(n)

    /**
     * ## Drop Last (Children)
     * This function drops the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropLastChildren][SingleColumn.dropLastChildren] will drop the last [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropLast][ColumnSet.dropLast] will drop the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLast][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[dropLast][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastChildren][SingleColumn.dropLastChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastChildren][String.dropLastChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[dropLastChildren][SingleColumn.dropLastChildren]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[dropLastChildren][SingleColumn.dropLastChildren]`(1) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropLastChildren][KProperty.dropLastChildren]`(1) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun KProperty<DataRow<*>>.dropLastChildren(n: Int): ColumnSet<*> = columnGroup(this).dropLastChildren(n)

    /**
     * ## Drop Last (Children)
     * This function drops the last [n] columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropLastChildren][SingleColumn.dropLastChildren] will drop the last [n] children of that column group.
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropLast][ColumnSet.dropLast] will drop the last [n] columns of that column set.
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLast` is called `dropLastChildren` when called on
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLast][ColumnSet.dropLast]`(5) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[dropLast][ColumnsSelectionDsl.dropLast]`(1) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastChildren][SingleColumn.dropLastChildren]`(2) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastChildren][String.dropLastChildren]`(3) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropLastChildren][ColumnPath.dropLastChildren]`(1) }`
     *
     * @param [n] The number of columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last [n] columns.
     */
    public fun ColumnPath.dropLastChildren(n: Int): ColumnSet<*> = columnGroup(this).dropLastChildren(n)

    // endregion

    // region dropWhile

    /**
     * ## Drop (Children) While
     * This function drops the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropWhile][SingleColumn.dropWhile] will drop the
     * first children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropWhile][ColumnSet.dropWhile] will
     * drop the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropWhile][ColumnSet.dropWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropWhile][SingleColumn.dropWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropChildrenWhile][String.dropChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonTakeAndDropWhileDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs.ExampleArg]}
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    private interface CommonDropWhileDocs

    /**
     * ## Drop (Children) While
     * This function drops the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropWhile][SingleColumn.dropWhile] will drop the
     * first children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropWhile][ColumnSet.dropWhile] will
     * drop the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropWhile][ColumnSet.dropWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropWhile][SingleColumn.dropWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropChildrenWhile][String.dropChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[dropWhile][ColumnSet.dropWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[dropWhile][ColumnSet.dropWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun <C> ColumnSet<C>.dropWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.dropWhile(predicate) }

    /**
     * ## Drop (Children) While
     * This function drops the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropWhile][SingleColumn.dropWhile] will drop the
     * first children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropWhile][ColumnSet.dropWhile] will
     * drop the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropWhile][ColumnSet.dropWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropWhile][SingleColumn.dropWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropChildrenWhile][String.dropChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[dropWhile][ColumnsSelectionDsl.dropWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun ColumnsSelectionDsl<*>.dropWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.asSingleColumn().dropChildrenWhile(predicate)

    /**
     * ## Drop (Children) While
     * This function drops the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropWhile][SingleColumn.dropWhile] will drop the
     * first children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropWhile][ColumnSet.dropWhile] will
     * drop the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropWhile][ColumnSet.dropWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropWhile][SingleColumn.dropWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropChildrenWhile][String.dropChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropChildrenWhile][SingleColumn.dropChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun SingleColumn<DataRow<*>>.dropChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.children().dropWhile(predicate) }

    /**
     * ## Drop (Children) While
     * This function drops the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropWhile][SingleColumn.dropWhile] will drop the
     * first children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropWhile][ColumnSet.dropWhile] will
     * drop the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropWhile][ColumnSet.dropWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropWhile][SingleColumn.dropWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropChildrenWhile][String.dropChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropChildrenWhile][String.dropChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun String.dropChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropChildrenWhile(predicate)

    /**
     * ## Drop (Children) While
     * This function drops the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropWhile][SingleColumn.dropWhile] will drop the
     * first children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropWhile][ColumnSet.dropWhile] will
     * drop the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropWhile][ColumnSet.dropWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropWhile][SingleColumn.dropWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropChildrenWhile][String.dropChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[dropChildrenWhile][SingleColumn.dropChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropChildrenWhile][KProperty.dropChildrenWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun KProperty<DataRow<*>>.dropChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropChildrenWhile(predicate)

    /**
     * ## Drop (Children) While
     * This function drops the first columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropWhile][SingleColumn.dropWhile] will drop the
     * first children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropWhile][ColumnSet.dropWhile] will
     * drop the first columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropWhile` is called
     * `dropChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropWhile][ColumnSet.dropWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropWhile][SingleColumn.dropWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropChildrenWhile][String.dropChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropChildrenWhile][ColumnPath.dropChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the first columns adhering to the [predicate].
     */
    public fun ColumnPath.dropChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropChildrenWhile(predicate)

    // endregion

    // region dropLastWhile

    /**
     * ## Drop Last (Children) While
     * This function drops the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropLastWhile][SingleColumn.dropLastWhile] will drop the
     * last children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropLastWhile][ColumnSet.dropLastWhile] will
     * drop the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLastWhile][ColumnSet.dropLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastWhile][SingleColumn.dropLastWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastChildrenWhile][String.dropLastChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonTakeAndDropWhileDocs.ExampleArg][org.jetbrains.kotlinx.dataframe.documentation.CommonTakeAndDropWhileDocs.ExampleArg]}
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    private interface CommonDropLastWhileDocs

    /**
     * ## Drop Last (Children) While
     * This function drops the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropLastWhile][SingleColumn.dropLastWhile] will drop the
     * last children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropLastWhile][ColumnSet.dropLastWhile] will
     * drop the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLastWhile][ColumnSet.dropLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastWhile][SingleColumn.dropLastWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastChildrenWhile][String.dropLastChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[dropLastWhile][ColumnSet.dropLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[dropLastWhile][ColumnSet.dropLastWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun <C> ColumnSet<C>.dropLastWhile(predicate: ColumnFilter<C>): ColumnSet<C> =
        transform { it.dropLastWhile(predicate) }

    /**
     * ## Drop Last (Children) While
     * This function drops the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropLastWhile][SingleColumn.dropLastWhile] will drop the
     * last children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropLastWhile][ColumnSet.dropLastWhile] will
     * drop the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLastWhile][ColumnSet.dropLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastWhile][SingleColumn.dropLastWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastChildrenWhile][String.dropLastChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[dropLastWhile][ColumnsSelectionDsl.dropLastWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun ColumnsSelectionDsl<*>.dropLastWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.asSingleColumn().dropLastChildrenWhile(predicate)

    /**
     * ## Drop Last (Children) While
     * This function drops the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropLastWhile][SingleColumn.dropLastWhile] will drop the
     * last children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropLastWhile][ColumnSet.dropLastWhile] will
     * drop the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLastWhile][ColumnSet.dropLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastWhile][SingleColumn.dropLastWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastChildrenWhile][String.dropLastChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[dropLastChildrenWhile][SingleColumn.dropLastChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun SingleColumn<DataRow<*>>.dropLastChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().transformSingle { it.children().dropLastWhile(predicate) }

    /**
     * ## Drop Last (Children) While
     * This function drops the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropLastWhile][SingleColumn.dropLastWhile] will drop the
     * last children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropLastWhile][ColumnSet.dropLastWhile] will
     * drop the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLastWhile][ColumnSet.dropLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastWhile][SingleColumn.dropLastWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastChildrenWhile][String.dropLastChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[dropLastChildrenWhile][String.dropLastChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun String.dropLastChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropLastChildrenWhile(predicate)

    /**
     * ## Drop Last (Children) While
     * This function drops the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropLastWhile][SingleColumn.dropLastWhile] will drop the
     * last children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropLastWhile][ColumnSet.dropLastWhile] will
     * drop the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLastWhile][ColumnSet.dropLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastWhile][SingleColumn.dropLastWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastChildrenWhile][String.dropLastChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[dropLastChildrenWhile][SingleColumn.dropLastChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[dropLastChildrenWhile][KProperty.dropLastChildrenWhile]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun KProperty<DataRow<*>>.dropLastChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropLastChildrenWhile(predicate)

    /**
     * ## Drop Last (Children) While
     * This function drops the last columns of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] or
     * [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] adhering to the given [predicate].
     *
     * If called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * [dropLastWhile][SingleColumn.dropLastWhile] will drop the
     * last children of that column group adhering to the given [predicate].
     *
     * Else, if called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [dropLastWhile][ColumnSet.dropLastWhile] will
     * drop the last columns of that column set adhering to the given [predicate].
     *
     * Any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] can be used as receiver for these functions.
     *
     * NOTE: To avoid ambiguity, `dropLastWhile` is called
     * `dropLastChildrenWhile` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### Examples:
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[dropLastWhile][ColumnSet.dropLastWhile]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[dropLastWhile][SingleColumn.dropLastWhile]` { it.`[any][org.jetbrains.kotlinx.dataframe.DataColumn.any]` { it == "Alice" } } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[dropLastChildrenWhile][String.dropLastChildrenWhile]` { it.`[kind][org.jetbrains.kotlinx.dataframe.DataColumn.kind]`() == `[ColumnKind.Value][org.jetbrains.kotlinx.dataframe.columns.ColumnKind.Value]` } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[dropLastChildrenWhile][ColumnPath.dropLastChildrenWhile]` { it.`[name][ColumnWithPath.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [predicate] The [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter] to control which columns to drop.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the last columns adhering to the [predicate].
     */
    public fun ColumnPath.dropLastChildrenWhile(predicate: ColumnFilter<*>): ColumnSet<*> =
        columnGroup(this).dropLastChildrenWhile(predicate)

    // endregion
}
// endregion
