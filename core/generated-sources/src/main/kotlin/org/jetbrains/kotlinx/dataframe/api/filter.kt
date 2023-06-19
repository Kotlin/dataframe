package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.toIndices
import org.jetbrains.kotlinx.dataframe.indices
import kotlin.reflect.KProperty

// region DataColumn

public fun <T> DataColumn<T>.filter(predicate: Predicate<T>): DataColumn<T> = indices.filter {
    predicate(get(it))
}.let { get(it) }

// endregion

// region DataFrame

public fun <T> DataFrame<T>.filter(predicate: RowFilter<T>): DataFrame<T> =
    indices.filter {
        val row = get(it)
        predicate(row, row)
    }.let { get(it) }

public fun <T> DataFrame<T>.filterBy(column: ColumnSelector<T, Boolean>): DataFrame<T> =
    getRows(getColumn(column).toList().toIndices())

public fun <T> DataFrame<T>.filterBy(column: String): DataFrame<T> = filterBy { column.toColumnOf() }

public fun <T> DataFrame<T>.filterBy(column: ColumnReference<Boolean>): DataFrame<T> = filterBy { column }

public fun <T> DataFrame<T>.filterBy(column: KProperty<Boolean>): DataFrame<T> = filterBy { column.toColumnAccessor() }

// endregion

// region ColumnsSelectionDsl

// TODO remove confusing overloads
public interface FilterColumnsSelectionDsl {

    /**
     * ## Filter (Children)
     * Creates a subset of columns ([ColumnSet]) from the current [ColumnsResolver] that
     * adhere to the given [predicate\].
     *
     * If the current [ColumnsResolver] is a [SingleColumn] and consists of a [column group][ColumnGroup],
     * then `filter` will create a subset of its children.
     *
     * Aside from calling [filter][ColumnSet.filter] directly, you can also use the [get][ColumnsSelectionDsl.get] operator
     * in most cases. This function belongs to [cols][ColumnsSelectionDsl.cols] but operates the same.
     *
     * NOTE: To avoid ambiguity, `filter` is named `filterChildren` when called on a [String] or [ColumnPath] resembling
     * a [ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[remove][DataFrame.remove]` { `[filter][SingleColumn.filter]` { it.`[hasNulls][DataColumn.hasNulls]`() } }`
     *
     * `// and although this can be shortened to just the `[colsOf][colsOf]` call:`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][colsOf]`<`[String][String]`>().`[filter][ColumnSet.filter]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() } }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonFilterDocs.ExampleArg]}
     *
     * #### Filter vs. Cols:
     * [cols][ColumnsSelectionDsl.cols] with predicate functions exactly like [filter][SingleColumn.filter].
     * This is intentional, however; it is recommended to use `cols` on [SingleColumns][SingleColumn] and
     * `filter` on [ColumnSets][ColumnSet].
     *
     * @param [predicate\] A [ColumnFilter function][ColumnFilter] that takes a [ColumnReference] and returns a [Boolean].
     * @return A ([transformable][TransformableColumnSet]) [ColumnSet] containing the columns that match the given [predicate\].
     * @see [cols\]
     */
    private interface CommonFilterDocs {

        interface ExampleArg
    }

    /**
     * ## Filter (Children)
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] that
     * adhere to the given [predicate].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of a [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then `filter` will create a subset of its children.
     *
     * Aside from calling [filter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.filter] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator
     * in most cases. This function belongs to [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] but operates the same.
     *
     * NOTE: To avoid ambiguity, `filter` is named `filterChildren` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[filter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.filter]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `// and although this can be shortened to just the `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]` call:`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[String][String]`>().`[filter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.filter]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * #### Examples for this overload:
     *
     * `// although these can be shortened to just the `[colsOf][SingleColumn.colsOf]` call:`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[filter][ColumnSet.filter]` { "e" `[in][String.contains]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>()`[`[`][ColumnsSelectionDsl.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][ColumnsSelectionDsl.cols]` }`
     *
     * #### Filter vs. Cols:
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] with predicate functions exactly like [filter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.filter].
     * This is intentional, however; it is recommended to use `cols` on [SingleColumns][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and
     * `filter` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [cols]
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.filter(predicate: ColumnFilter<C>): TransformableColumnSet<C> =
        colsInternal(predicate as ColumnFilter<*>) as TransformableColumnSet<C>

    /**
     * ## Filter (Children)
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] that
     * adhere to the given [predicate].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of a [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then `filter` will create a subset of its children.
     *
     * Aside from calling [filter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.filter] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator
     * in most cases. This function belongs to [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] but operates the same.
     *
     * NOTE: To avoid ambiguity, `filter` is named `filterChildren` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[filter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.filter]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `// and although this can be shortened to just the `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]` call:`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[String][String]`>().`[filter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.filter]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[filter][SingleColumn.filter]` { "e" `[in][String.contains]` it.`[name][ColumnPath.name]`() }.`[recursively][ColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { this`[`[`][SingleColumn.filter]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][SingleColumn.filter]` }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup`.[filter][SingleColumn.filter]` { "e" `[in][String.contains]` it.`[name][ColumnPath.name]`() } }`
     *
     * `// NOTE: there's a `[DataFrame.get]` overload that prevents this:`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup`[`[`][ColumnsSelectionDsl.cols]`{ ... }`[`]`][ColumnsSelectionDsl.cols]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: On a [SingleColumn], [filter][SingleColumn.filter] behaves exactly the same as
     * [children][ColumnsSelectionDsl.children].
     *
     * #### Filter vs. Cols:
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] with predicate functions exactly like [filter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.filter].
     * This is intentional, however; it is recommended to use `cols` on [SingleColumns][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and
     * `filter` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [cols]
     * @see [children\]
     */
    public fun SingleColumn<DataRow<*>>.filter(predicate: ColumnFilter<*>): TransformableColumnSet<*> =
        ensureIsColGroup().colsInternal(predicate)

    /**
     * ## Filter (Children)
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] that
     * adhere to the given [predicate].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of a [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then `filter` will create a subset of its children.
     *
     * Aside from calling [filter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.filter] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator
     * in most cases. This function belongs to [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] but operates the same.
     *
     * NOTE: To avoid ambiguity, `filter` is named `filterChildren` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[filter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.filter]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `// and although this can be shortened to just the `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]` call:`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[String][String]`>().`[filter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.filter]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol".`[filterChildren][String.filterChildren]` { "e" `[in][String.contains]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol"`[`[`][String.filterChildren]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][String.filterChildren]` }`
     *
     * #### Filter vs. Cols:
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] with predicate functions exactly like [filter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.filter].
     * This is intentional, however; it is recommended to use `cols` on [SingleColumns][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and
     * `filter` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [cols]
     */
    public fun String.filterChildren(predicate: ColumnFilter<*>): TransformableColumnSet<*> =
        columnGroup(this).filter(predicate)

    /**
     * ## Filter (Children)
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] that
     * adhere to the given [predicate].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of a [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then `filter` will create a subset of its children.
     *
     * Aside from calling [filter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.filter] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator
     * in most cases. This function belongs to [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] but operates the same.
     *
     * NOTE: To avoid ambiguity, `filter` is named `filterChildren` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[filter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.filter]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `// and although this can be shortened to just the `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]` call:`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[String][String]`>().`[filter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.filter]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::columnGroup.`[asColumnGroup][KProperty.asColumnGroup]`().`[filter][SingleColumn.filter]` { "e" `[in][String.contains]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::columnGroup)`[`[`][SingleColumn.filter]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][ColumnsSelectionDsl.cols]` }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::columnGroup.`[filter][KProperty.filter]` { "e" `[in][String.contains]` it.`[name][ColumnPath.name]`() } }`
     *
     * #### Filter vs. Cols:
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] with predicate functions exactly like [filter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.filter].
     * This is intentional, however; it is recommended to use `cols` on [SingleColumns][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and
     * `filter` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [cols]
     */
    public fun KProperty<DataRow<*>>.filter(predicate: ColumnFilter<*>): TransformableColumnSet<*> =
        columnGroup(this).filter(predicate)

    /**
     * ## Filter (Children)
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] that
     * adhere to the given [predicate].
     *
     * If the current [ColumnsResolver][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and consists of a [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup],
     * then `filter` will create a subset of its children.
     *
     * Aside from calling [filter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.filter] directly, you can also use the [get][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.get] operator
     * in most cases. This function belongs to [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] but operates the same.
     *
     * NOTE: To avoid ambiguity, `filter` is named `filterChildren` when called on a [String] or [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] resembling
     * a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * #### For example:
     *
     * `df.`[remove][org.jetbrains.kotlinx.dataframe.DataFrame.remove]` { `[filter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.filter]` { it.`[hasNulls][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `// and although this can be shortened to just the `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]` call:`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[String][String]`>().`[filter][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.filter]` { "e" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[filterChildren][ColumnPath.filterChildren]` { "e" `[in][String.contains]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"]`[`[`][ColumnsSelectionDsl.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][ColumnsSelectionDsl.cols]` }`
     *
     * #### Filter vs. Cols:
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] with predicate functions exactly like [filter][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.filter].
     * This is intentional, however; it is recommended to use `cols` on [SingleColumns][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] and
     * `filter` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [cols]
     */
    public fun ColumnPath.filterChildren(predicate: ColumnFilter<*>): TransformableColumnSet<*> =
        columnGroup(this).filter(predicate)
}
// endregion
