package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl
public interface ChildrenColumnsSelectionDsl {

    /**
     * ## Children
     *
     * [Children][ColumnSet.children] is an interesting operations, since it behaves
     * slightly differently depending on what you call it on. It will return the "children"
     * adhering to the given (optional) [predicate\], however what "children" means depends
     * whether it's called on a [ColumnSet] or a [SingleColumn]:
     *
     * ### On a [SingleColumn]:
     * When called on a [SingleColumn] consisting of a [ColumnGroup], [children][SingleColumn.children] will return the (filtered) children of that
     * column group. This makes the function behave similarly to [all][ColumnsSelectionDsl.all] and exactly the same as
     * [cols][ColumnsSelectionDsl.cols] and [filter][ColumnsSelectionDsl.filter].
     *
     * #### For example:
     *
     * To select some columns or "children" of `myColumnGroup`, you can do:
     * - `df.`[select][DataFrame.select]` { myColumnGroup.`[cols][ColumnsSelectionDsl.cols]` { it.`[name][DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     * - `df.`[select][DataFrame.select]` { myColumnGroup.`[filter][ColumnsSelectionDsl.filter]` { it.`[name][DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     * - `df.`[select][DataFrame.select]` { myColumnGroup.`[children][SingleColumn.children]` { it.`[name][DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     *
     * Similarly, to select _all_ columns or "children" of a [DataFrame], you can do:
     * - `df.`[select][DataFrame.select]` { `[all][ColumnsSelectionDsl.all]`() }`
     * - `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]`() }`
     * - `df.`[select][DataFrame.select]` { `[filter][ColumnsSelectionDsl.filter]` { true } }`
     * - `df.`[select][DataFrame.select]` { `[children][SingleColumn.children]`() }`
     *
     * ### On a [ColumnSet]:
     * When called on a [ColumnSet], [children][ColumnSet.children] will return the (filtered) children of all [ColumnGroups][ColumnGroup]
     * in that column set.
     *
     * #### For example:
     *
     * To get only the children of all column groups in a [DataFrame], you can do:
     * - `df.`[select][DataFrame.select]` { `[colGroups][ColumnsSelectionDsl.colGroups]`().`[children][ColumnSet.children]`() }`
     * - `df.`[select][DataFrame.select]` { `[all][ColumnsSelectionDsl.all]`().`[children][ColumnSet.children]`() }`
     *
     * Similarly, you can take the children of all [column groups][ColumnGroup] in a [ColumnSet]:
     * - `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][DataColumn.name]` }.`[children][ColumnSet.children]`() }`
     *
     * #### Examples of this overload:
     *
     * {@includeArg [ChildrenDocs.ExampleArg]}
     *
     * @see [cols\]
     * @see [filter\]
     * @see [all\]
     * @param [predicate\] An optional predicate to filter the children by.
     * @return A [TransformableColumnSet] containing the (filtered) children.
     */
    private interface ChildrenDocs {

        /** Example argument to use */
        interface ExampleArg
    }

    /**
     * ## Children
     *
     * [Children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children] is an interesting operations, since it behaves
     * slightly differently depending on what you call it on. It will return the "children"
     * adhering to the given (optional) [predicate], however what "children" means depends
     * whether it's called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]:
     *
     * ### On a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]:
     * When called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] consisting of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], [children][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.children] will return the (filtered) children of that
     * column group. This makes the function behave similarly to [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all] and exactly the same as
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] and [filter][ColumnsSelectionDsl.filter].
     *
     * #### For example:
     *
     * To select some columns or "children" of `myColumnGroup`, you can do:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[filter][ColumnsSelectionDsl.filter]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[children][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.children]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     *
     * Similarly, to select _all_ columns or "children" of a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], you can do:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[filter][ColumnsSelectionDsl.filter]` { true } }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[children][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.children]`() }`
     *
     * ### On a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     * When called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children] will return the (filtered) children of all [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * in that column set.
     *
     * #### For example:
     *
     * To get only the children of all column groups in a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], you can do:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children]`() }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children]`() }`
     *
     * Similarly, you can take the children of all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children]`() }`
     *
     * #### Examples of this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[children][ColumnSet.children]` { "my" `[in][String.contains]` it.`[name][DataColumn.name]` }.`[recursively][ColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[DataRow][DataRow]`<MyGroupType>>().`[children][ColumnSet.children]`() }`
     *
     * @see [cols]
     * @see [filter]
     * @see [all]
     * @param [predicate] An optional predicate to filter the children by.
     * @return A [TransformableColumnSet][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet] containing the (filtered) children.
     */
    public fun ColumnSet<*>.children(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        transform { it.flatMap { it.children().filter { predicate(it) } } }

    /**
     * ## Children
     *
     * [Children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children] is an interesting operations, since it behaves
     * slightly differently depending on what you call it on. It will return the "children"
     * adhering to the given (optional) [predicate], however what "children" means depends
     * whether it's called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]:
     *
     * ### On a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]:
     * When called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] consisting of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], [children][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.children] will return the (filtered) children of that
     * column group. This makes the function behave similarly to [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all] and exactly the same as
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] and [filter][ColumnsSelectionDsl.filter].
     *
     * #### For example:
     *
     * To select some columns or "children" of `myColumnGroup`, you can do:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[filter][ColumnsSelectionDsl.filter]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[children][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.children]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     *
     * Similarly, to select _all_ columns or "children" of a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], you can do:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[filter][ColumnsSelectionDsl.filter]` { true } }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[children][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.children]`() }`
     *
     * ### On a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     * When called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children] will return the (filtered) children of all [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * in that column set.
     *
     * #### For example:
     *
     * To get only the children of all column groups in a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], you can do:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children]`() }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children]`() }`
     *
     * Similarly, you can take the children of all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children]`() }`
     *
     * #### Examples of this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[children][SingleColumn.children]`().`[recursively][ColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { `[children][SingleColumn.children]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @see [cols]
     * @see [filter]
     * @see [all]
     * @param [predicate] An optional predicate to filter the children by.
     * @return A [TransformableColumnSet][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet] containing the (filtered) children.
     */
    public fun SingleColumn<DataRow<*>>.children(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        ensureIsColGroup().asColumnSet().colsInternal(predicate)

    /**
     * ## Children
     *
     * [Children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children] is an interesting operations, since it behaves
     * slightly differently depending on what you call it on. It will return the "children"
     * adhering to the given (optional) [predicate], however what "children" means depends
     * whether it's called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]:
     *
     * ### On a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]:
     * When called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] consisting of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], [children][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.children] will return the (filtered) children of that
     * column group. This makes the function behave similarly to [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all] and exactly the same as
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] and [filter][ColumnsSelectionDsl.filter].
     *
     * #### For example:
     *
     * To select some columns or "children" of `myColumnGroup`, you can do:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[filter][ColumnsSelectionDsl.filter]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[children][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.children]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     *
     * Similarly, to select _all_ columns or "children" of a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], you can do:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[filter][ColumnsSelectionDsl.filter]` { true } }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[children][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.children]`() }`
     *
     * ### On a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     * When called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children] will return the (filtered) children of all [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * in that column set.
     *
     * #### For example:
     *
     * To get only the children of all column groups in a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], you can do:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children]`() }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children]`() }`
     *
     * Similarly, you can take the children of all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children]`() }`
     *
     * #### Examples of this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[children][String.children]`().`[recursively][ColumnsSelectionDsl.recursively]`() }`
     *
     * @see [cols]
     * @see [filter]
     * @see [all]
     * @param [predicate] An optional predicate to filter the children by.
     * @return A [TransformableColumnSet][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet] containing the (filtered) children.
     */
    public fun String.children(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        columnGroup(this).children(predicate)

    /**
     * ## Children
     *
     * [Children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children] is an interesting operations, since it behaves
     * slightly differently depending on what you call it on. It will return the "children"
     * adhering to the given (optional) [predicate], however what "children" means depends
     * whether it's called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]:
     *
     * ### On a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]:
     * When called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] consisting of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], [children][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.children] will return the (filtered) children of that
     * column group. This makes the function behave similarly to [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all] and exactly the same as
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] and [filter][ColumnsSelectionDsl.filter].
     *
     * #### For example:
     *
     * To select some columns or "children" of `myColumnGroup`, you can do:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[filter][ColumnsSelectionDsl.filter]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[children][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.children]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     *
     * Similarly, to select _all_ columns or "children" of a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], you can do:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[filter][ColumnsSelectionDsl.filter]` { true } }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[children][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.children]`() }`
     *
     * ### On a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     * When called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children] will return the (filtered) children of all [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * in that column set.
     *
     * #### For example:
     *
     * To get only the children of all column groups in a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], you can do:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children]`() }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children]`() }`
     *
     * Similarly, you can take the children of all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children]`() }`
     *
     * #### Examples of this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[children][SingleColumn.children]`().`[recursively][TransformableColumnSet.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[children][SingleColumn.children]`() }`
     *
     * @see [cols]
     * @see [filter]
     * @see [all]
     * @param [predicate] An optional predicate to filter the children by.
     * @return A [TransformableColumnSet][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet] containing the (filtered) children.
     */
    public fun KProperty<DataRow<*>>.children(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        columnGroup(this).children(predicate)

    /**
     * ## Children
     *
     * [Children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children] is an interesting operations, since it behaves
     * slightly differently depending on what you call it on. It will return the "children"
     * adhering to the given (optional) [predicate], however what "children" means depends
     * whether it's called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] or a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]:
     *
     * ### On a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]:
     * When called on a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] consisting of a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], [children][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.children] will return the (filtered) children of that
     * column group. This makes the function behave similarly to [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all] and exactly the same as
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] and [filter][ColumnsSelectionDsl.filter].
     *
     * #### For example:
     *
     * To select some columns or "children" of `myColumnGroup`, you can do:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[filter][ColumnsSelectionDsl.filter]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[children][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.children]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`("e") } }`
     *
     * Similarly, to select _all_ columns or "children" of a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], you can do:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[filter][ColumnsSelectionDsl.filter]` { true } }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[children][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.children]`() }`
     *
     * ### On a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     * When called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], [children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children] will return the (filtered) children of all [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * in that column set.
     *
     * #### For example:
     *
     * To get only the children of all column groups in a [DataFrame][org.jetbrains.kotlinx.dataframe.DataFrame], you can do:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`().`[children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children]`() }`
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children]`() }`
     *
     * Similarly, you can take the children of all [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     * - `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { "my" `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }.`[children][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.children]`() }`
     *
     * #### Examples of this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[children][ColumnPath.children]`().`[recursively][ColumnsSelectionDsl.recursively]`() }`
     *
     * @see [cols]
     * @see [filter]
     * @see [all]
     * @param [predicate] An optional predicate to filter the children by.
     * @return A [TransformableColumnSet][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet] containing the (filtered) children.
     */
    public fun ColumnPath.children(predicate: ColumnFilter<*> = { true }): TransformableColumnSet<*> =
        columnGroup(this).children(predicate)
}
// endregion
