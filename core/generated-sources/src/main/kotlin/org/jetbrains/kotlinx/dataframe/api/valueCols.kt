package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.Predicate
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl
public interface ValueColsColumnsSelectionDsl {

    /**
     * ## Value Columns
     * Creates a subset of columns that are [ValueColumns][ValueColumn] from the current [ColumnSet].
     *
     * If the current [ColumnSet] is a [SingleColumn]
     * (and thus consists of only one column (or [column group][ColumnGroup])),
     * then [valueCols] will create a subset of its children.
     *
     * You can optionally use a [filter\] to only include certain columns.
     * [valueCols] can be called using any of the supported [APIs][AccessApi] (+ [ColumnPath]).
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[valueCols][SingleColumn.valueCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][DataFrame.select]` { `[valueCols][SingleColumn.valueCols]`().`[recursively][ColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[valueCols][String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonValueColsDocs.ExampleArg]}
     *
     * @param [filter\] An optional [predicate][Predicate] to filter the value columns by.
     * @return A ([transformable][TransformableColumnSet]) [ColumnSet] of [ValueColumns][ValueColumn].
     */
    private interface CommonValueColsDocs {

        /** Example argument */
        interface ExampleArg
    }

    /**
     * ## Value Columns
     * Creates a subset of columns that are [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [valueCols][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [valueCols][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.valueCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.valueCols]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[valueCols][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") }.`[valueCols][ColumnSet.valueCols]`() }`
     *
     * `// NOTE: This can be shortened to just:`
     *
     * `df.`[select][DataFrame.select]` { `[valueCols][SingleColumn.valueCols]` { it.`[name][ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     */
    public fun ColumnSet<*>.valueCols(filter: Predicate<ValueColumn<*>> = { true }): TransformableColumnSet<*> =
        valueColumnsInternal(filter)

    /**
     * ## Value Columns
     * Creates a subset of columns that are [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [valueCols][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [valueCols][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.valueCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.valueCols]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[valueCols][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[valueCols][SingleColumn.valueCols]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[valueCols][SingleColumn.valueCols]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     */
    public fun SingleColumn<DataRow<*>>.valueCols(filter: Predicate<ValueColumn<*>> = { true }): TransformableColumnSet<*> =
        ensureIsColGroup().valueColumnsInternal(filter)

    /** TODO */
    public fun ColumnsSelectionDsl<*>.valueCols(filter: Predicate<ValueColumn<*>> = { true }): TransformableColumnSet<*> =
        asSingleColumn().valueColumnsInternal(filter)

    /**
     * ## Value Columns
     * Creates a subset of columns that are [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [valueCols][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [valueCols][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.valueCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.valueCols]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[valueCols][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[valueCols][String.valueCols]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[valueCols][String.valueCols]`() }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     */
    public fun String.valueCols(filter: Predicate<ValueColumn<*>> = { true }): TransformableColumnSet<*> =
        columnGroup(this).valueCols(filter)

    /**
     * ## Value Columns
     * Creates a subset of columns that are [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [valueCols][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [valueCols][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.valueCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.valueCols]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[valueCols][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::myColumnGroup).`[valueCols][SingleColumn.valueCols]` { it.`[any][ColumnWithPath.any]` { it == "Alice" } } }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[asColumnGroup][KProperty.asColumnGroup]`().`[valueCols][SingleColumn.valueCols]`() }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[valueCols][KProperty.valueCols]`().`[recursively][ColumnsSelectionDsl.recursively]`() }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     */
    public fun KProperty<DataRow<*>>.valueCols(filter: Predicate<ValueColumn<*>> = { true }): TransformableColumnSet<*> =
        columnGroup(this).valueCols(filter)

    /**
     * ## Value Columns
     * Creates a subset of columns that are [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn] from the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet].
     *
     * If the current [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]
     * (and thus consists of only one column (or [column group][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup])),
     * then [valueCols][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] will create a subset of its children.
     *
     * You can optionally use a [filter] to only include certain columns.
     * [valueCols][org.jetbrains.kotlinx.dataframe.api.ValueColsColumnsSelectionDsl.valueCols] can be called using any of the supported [APIs][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] (+ [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]).
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.valueCols]` { it.`[name][org.jetbrains.kotlinx.dataframe.columns.ColumnReference.name]`.`[startsWith][String.startsWith]`("my") } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[valueCols][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.valueCols]`().`[recursively][org.jetbrains.kotlinx.dataframe.api.RecursivelyColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[valueCols][kotlin.String.valueCols]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[valueCols][ColumnPath.valueCols]`().`[recursively][ColumnsSelectionDsl.recursively]`() }`
     *
     * @param [filter] An optional [predicate][org.jetbrains.kotlinx.dataframe.Predicate] to filter the value columns by.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of [ValueColumns][org.jetbrains.kotlinx.dataframe.columns.ValueColumn].
     */
    public fun ColumnPath.valueCols(filter: Predicate<ValueColumn<*>> = { true }): TransformableColumnSet<*> =
        columnGroup(this).valueCols(filter)
}

internal fun ColumnsResolver<*>.valueColumnsInternal(filter: (ValueColumn<*>) -> Boolean): TransformableColumnSet<*> =
    colsInternal { it.isValueColumn() && filter(it.asValueColumn()) }

// endregion
