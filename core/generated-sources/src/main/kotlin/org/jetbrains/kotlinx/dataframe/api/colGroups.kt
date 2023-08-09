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
     * ## Column Groups
     * Creates a subset of columns that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colGroups]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colGroups][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") }.`[colGroups][ColumnSet.colGroups]`() }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][SingleColumn.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     */
    public fun ColumnSet<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroupsInternal(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colGroups]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colGroups][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroups][SingleColumn.colGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[colGroups][SingleColumn.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     */
    public fun SingleColumn<DataRow<*>>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        ensureIsColGroup().columnGroupsInternal(filter)

    /** TODO */
    public fun ColumnsSelectionDsl<*>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        asSingleColumn().columnGroupsInternal(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colGroups]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colGroups][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[colGroups][String.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[colGroups][String.colGroups]`() }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     */
    public fun String.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colGroups]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colGroups][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColGroup).`[colGroups][SingleColumn.colGroups]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[asColumnGroup][KProperty.asColumnGroup]`().`[colGroups][SingleColumn.colGroups]`() }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup.`[colGroups][KProperty.colGroups]`().`[recursively][ColumnsSelectionDsl.recursively]`() }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     */
    public fun KProperty<DataRow<*>>.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)

    /**
     * ## Column Groups
     * Creates a subset of columns that are [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [colGroups][org.jetbrains.kotlinx.dataframe.api.ColGroupsColumnsSelectionDsl.colGroups] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colGroups]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroups][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colGroups]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[colGroups][kotlin.String.colGroups]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[colGroups][ColumnPath.colGroups]`().`[recursively][ColumnsSelectionDsl.recursively]`() }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the column groups by.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     */
    public fun ColumnPath.colGroups(filter: Predicate<ColumnGroup<*>> = { true }): TransformableColumnSet<AnyRow> =
        columnGroup(this).colGroups(filter)
}
@Suppress("UNCHECKED_CAST")
internal fun ColumnsResolver<*>.columnGroupsInternal(filter: (ColumnGroup<*>) -> Boolean): TransformableColumnSet<AnyRow> =
    colsInternal { it.isColumnGroup() && filter(it.asColumnGroup()) } as TransformableColumnSet<AnyRow>

// endregion
