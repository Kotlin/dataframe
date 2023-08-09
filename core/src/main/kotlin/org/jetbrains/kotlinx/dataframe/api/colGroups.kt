package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyRow
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl
public interface ColGroupsColumnsSelectionDsl {

    @Deprecated("Use colGroups instead", ReplaceWith("this.colGroups(filter)"))
    public fun ColumnSet<*>.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroupsInternal(filter)

    @Deprecated("Use colGroups instead", ReplaceWith("this.colGroups(filter)"))
    public fun SingleColumn<DataRow<*>>.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        ensureIsColGroup().columnGroupsInternal(filter)

    @Deprecated("Use colGroups instead", ReplaceWith("this.colGroups(filter)"))
    public fun ColumnsSelectionDsl<*>.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        this.asSingleColumn().columnGroupsInternal(filter)

    @Deprecated("Use colGroups instead", ReplaceWith("this.colGroups(filter)"))
    public fun String.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).groups(filter)

    @Deprecated("Use colGroups instead", ReplaceWith("this.colGroups(filter)"))
    public fun KProperty<DataRow<*>>.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).groups(filter)

    @Deprecated("Use colGroups instead", ReplaceWith("this.colGroups(filter)"))
    public fun ColumnPath.groups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).groups(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns that are [ColumnGroups][ColumnGroup] from the current [ColumnSet].
     *
     * If the current [ColumnSet] is a [SingleColumn]
     * (and thus consists of only one column (or [column group][ColumnGroup])),
     * then [colGroups] will create a subset of its children.
     *
     * You can optionally use a [filter\] to only include certain columns.
     * [colGroups] can be called using any of the supported [APIs][AccessApi] (+ [ColumnPath]).
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][SingleColumn.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][SingleColumn.colGroups]`().`[recursively][ColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[colGroups][String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonColGroupsDocs.ExampleArg]}
     *
     * @param [filter\] An optional [predicate][Predicate] to filter the column groups by.
     * @return A ([transformable][TransformableColumnSet]) [ColumnSet] of [ColumnGroups][ColumnGroup].
     */
    private interface CommonColGroupsDocs {

        /** Example argument */
        interface ExampleArg
    }

    /**
     * @include [CommonColGroupsDocs]
     * @setArg [CommonColGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") }.`[colGroups][ColumnSet.colGroups]`() }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][SingleColumn.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun ColumnSet<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroupsInternal(filter)

    /**
     * @include [CommonColGroupsDocs]
     * @setArg [CommonColGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][SingleColumn.colGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colGroups][SingleColumn.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     */
    public fun SingleColumn<DataRow<*>>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        ensureIsColGroup().columnGroupsInternal(filter)

    /** TODO */
    public fun ColumnsSelectionDsl<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        asSingleColumn().columnGroupsInternal(filter)

    /**
     * @include [CommonColGroupsDocs]
     * @setArg [CommonColGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[colGroups][String.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[colGroups][String.colGroups]`() }`
     */
    public fun String.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)

    /**
     * @include [CommonColGroupsDocs]
     * @setArg [CommonColGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[colGroups][SingleColumn.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[asColumnGroup][KProperty.asColumnGroup]`().`[colGroups][SingleColumn.colGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup.`[colGroups][KProperty.colGroups]`().`[recursively][ColumnsSelectionDsl.recursively]`() }`
     */
    public fun KProperty<DataRow<*>>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)

    /**
     * @include [CommonColGroupsDocs]
     * @setArg [CommonColGroupsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[colGroups][ColumnPath.colGroups]`().`[recursively][ColumnsSelectionDsl.recursively]`() }`
     */
    public fun ColumnPath.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)
}
@Suppress("UNCHECKED_CAST")
internal fun ColumnsResolver<*>.columnGroupsInternal(filter: (ColumnGroup<*>) -> Boolean): TransformableColumnSet<AnyRow> =
    colsInternal { it.isColumnGroup() && filter(it.asColumnGroup()) } as TransformableColumnSet<AnyRow>

// endregion
