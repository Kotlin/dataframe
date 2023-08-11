package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.ColumnSelector
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
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
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
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

internal fun <T, C> ColumnsSelector<T, C>.filter(predicate: (ColumnWithPath<C>) -> Boolean): ColumnsSelector<T, C> =
    { this@filter(it, it).asColumnSet().filter(predicate) }

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
     * {@getArg [CommonFilterDocs.ExampleArg]}
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
     * @include [CommonFilterDocs]
     * @setArg [CommonFilterDocs.ExampleArg]
     *
     * `// although these can be shortened to just the `[colsOf][SingleColumn.colsOf]` call:`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[filter][ColumnSet.filter]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>()`[`[`][ColumnsSelectionDsl.cols]`{ it.`[any\][ColumnWithPath.any\]` { it == "Alice" } }`[`]`][ColumnsSelectionDsl.cols]` }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.filter(predicate: ColumnFilter<C>): TransformableColumnSet<C> =
        colsInternal(predicate as ColumnFilter<*>) as TransformableColumnSet<C>

    /**
     * @include [CommonFilterDocs]
     * @setArg [CommonFilterDocs.ExampleArg]
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
     * {@include [LineBreak]}
     * NOTE: On a [SingleColumn], [filter][SingleColumn.filter] behaves exactly the same as
     * [children][ColumnsSelectionDsl.children].
     *
     * @see [children\]
     */
    public fun SingleColumn<DataRow<*>>.filter(predicate: ColumnFilter<*>): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal(predicate)

    /**
     * @include [CommonFilterDocs]
     * @setArg [CommonFilterDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol".`[filterChildren][String.filterChildren]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol"`[`[`][String.filterChildren]`{ it.`[any\][ColumnWithPath.any\]` { it == "Alice" } }`[`]`][String.filterChildren]` }`
     */
    public fun String.filterChildren(predicate: ColumnFilter<*>): TransformableColumnSet<*> =
        columnGroup(this).filter(predicate)

    /**
     * @include [CommonFilterDocs]
     * @setArg [CommonFilterDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::columnGroup.`[asColumnGroup][KProperty.asColumnGroup]`().`[filter][SingleColumn.filter]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::columnGroup)`[`[`][SingleColumn.filter]`{ it.`[any\][ColumnWithPath.any\]` { it == "Alice" } }`[`]`][ColumnsSelectionDsl.cols]` }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::columnGroup.`[filter][KProperty.filter]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() } }`
     */
    public fun KProperty<DataRow<*>>.filter(predicate: ColumnFilter<*>): TransformableColumnSet<*> =
        columnGroup(this).filter(predicate)

    /**
     * @include [CommonFilterDocs]
     * @setArg [CommonFilterDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[filterChildren][ColumnPath.filterChildren]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"]`[`[`][ColumnsSelectionDsl.cols]`{ it.`[any\][ColumnWithPath.any\]` { it == "Alice" } }`[`]`][ColumnsSelectionDsl.cols]` }`
     */
    public fun ColumnPath.filterChildren(predicate: ColumnFilter<*>): TransformableColumnSet<*> =
        columnGroup(this).filter(predicate)
}
// endregion
