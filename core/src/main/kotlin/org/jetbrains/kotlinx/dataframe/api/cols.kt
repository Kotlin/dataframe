package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.CommonColsDocs.Vararg.AccessorType
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnsList
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.columns.transformWithContext
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import kotlin.reflect.KProperty

public interface ColsColumnsSelectionDsl {

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet]) from the current [ColumnsResolver].
     *
     * If the current [ColumnsResolver] is a [SingleColumn] and consists of a [column group][ColumnGroup],
     * then `cols` will create a subset of its contained columns.
     *
     * You can use either a [ColumnFilter], or any of the `vararg` overloads for any
     * {@include [AccessApiLink]}.
     *
     * Aside from calling [cols] directly, you can also use the [get][ColumnSet.get] operator in most cases.
     *
     * #### For example:
     * `df.`[remove][DataFrame.remove]` { `[cols][ColumnsSelectionDsl.cols]` { it.`[hasNulls][DataColumn.hasNulls]`() }.`[recursively][ColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { myGroupCol.`[cols][SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][ColumnSet.cols]`1, 3, 5`[`]`][ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonColsDocs.Examples]}
     *
     */
    private interface CommonColsDocs {

        /**
         * @include [CommonColsDocs]
         *
         * #### Filter vs. Cols:
         * If used with a [predicate\], `cols {}` functions exactly like [`filter {}`][ColumnsSelectionDsl.filter].
         * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][ColumnSet] and
         * `cols {}` on the rest.
         *
         * @param [predicate\] A [ColumnFilter function][ColumnFilter] that takes a [ColumnReference] and returns a [Boolean].
         * @return A ([transformable][TransformableColumnSet]) [ColumnSet] containing the columns that match the given [predicate\].
         */
        interface Predicate

        /**
         * @include [CommonColsDocs]
         *
         * @param [firstCol\] A {@getArg [AccessorType]} that points to a column.
         * @param [otherCols\] Optional additional {@getArg [AccessorType]}s that point to columns.
         * @return A ([transformable][TransformableColumnSet]) [ColumnSet] containing the columns that [firstCol\] and [otherCols\] point to.
         */
        interface Vararg {

            interface AccessorType
        }

        /** Example argument */
        interface Examples
    }

    // region predicate

    /**
     * @include [CommonColsDocs.Predicate]
     * @setArg [CommonColsDocs.Examples]
     *
     * `// although these can be shortened to just the `[colsOf<>{ }][ColumnsSelectionDsl.colsOf]` call`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[String][String]`>().`[cols][ColumnSet.cols]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][ColumnSet.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][ColumnSet.cols]` }`
     *
     * `// identity call, same as `[all][ColumnsSelectionDsl.all]`()`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[String][String]`>().`[cols][ColumnSet.cols]`() }`
     *
     * @see [all\]
     * @see [filter\]
     */
    private interface ColumnSetColsPredicateDocs

    /** @include [ColumnSetColsPredicateDocs] */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(
        predicate: ColumnFilter<C> = { true },
    ): TransformableColumnSet<C> = colsInternal(predicate as ColumnFilter<*>) as TransformableColumnSet<C>

    /** @include [ColumnSetColsPredicateDocs] */
    public operator fun <C> ColumnSet<C>.get(
        predicate: ColumnFilter<C> = { true },
    ): TransformableColumnSet<C> = cols(predicate)

    /**
     * @include [CommonColsDocs.Predicate]
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() }.`[recursively][ColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { this`[`[`][ColumnsSelectionDsl.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][ColumnsSelectionDsl.cols]` }`
     *
     *  `// same as `[all][ColumnsSelectionDsl.all]`()`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]`() }`
     *
     * {@include [LineBreak]}
     *
     * @see [all\]
     */
    private interface ColumnsSelectionDslColsPredicateDocs

    /** @include [ColumnsSelectionDslColsPredicateDocs] */
    public fun ColumnsSelectionDsl<*>.cols(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = this.asSingleColumn().colsInternal(predicate)

    /** @include [ColumnsSelectionDslColsPredicateDocs] */
    public operator fun ColumnsSelectionDsl<*>.get(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = cols(predicate)

    /**
     * @include [CommonColsDocs.Predicate]
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup`.[cols][SingleColumn.cols]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() } }`
     *
     * `// same as `[all][ColumnsSelectionDsl.all]`()`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[cols][SingleColumn.cols]`() }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.cols]`{ ... }`[`]`][SingleColumn.cols]` }`
     * {@include [LineBreak]}
     *
     * @see [all\]
     */
    private interface SingleColumnAnyRowColsPredicateDocs

    /** @include [SingleColumnAnyRowColsPredicateDocs] */
    public fun SingleColumn<DataRow<*>>.cols(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = ensureIsColGroup().colsInternal(predicate)

    /**
     * @include [SingleColumnAnyRowColsPredicateDocs]
     */
    public operator fun SingleColumn<DataRow<*>>.get(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = cols(predicate)

    /**
     * @include [CommonColsDocs.Predicate]
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol".`[cols][String.cols]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol"`[`[`][String.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][String.cols]` }`
     *
     * `// same as `[all][all]`()`
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol".`[cols][String.cols]`() }`
     */
    private interface StringColsPredicateDocs

    /** @include [StringColsPredicateDocs] */
    public fun String.cols(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = columnGroup(this).cols(predicate)

    /** @include [StringColsPredicateDocs] */
    public operator fun String.get(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = cols(predicate)

    /**
     * @include [CommonColsDocs.Predicate]
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { Type::columnGroup.`[cols][KProperty.cols]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { Type::columnGroup`[`[`][SingleColumn.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][SingleColumn.cols]` }`
     *
     * `// same as `[all][all]`()`
     *
     * `df.`[select][DataFrame.select]` { Type::columnGroup.`[cols][SingleColumn.cols]`() }`
     *
     * @see [all\]
     */
    private interface KPropertyColsPredicateDocs

    /** @include [KPropertyColsPredicateDocs] */
    public fun KProperty<*>.cols(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = columnGroup(this).cols(predicate)

    /** @include [KPropertyColsPredicateDocs] */
    public operator fun KProperty<*>.get(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = cols(predicate)

    /**
     * @include [CommonColsDocs.Predicate]
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[cols][ColumnPath.cols]` { "e" `[in\][String.contains\]` it.`[name][ColumnPath.name]`() } }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"]`[`[`][ColumnPath.cols]`{ it.`[any][ColumnWithPath.any]` { it == "Alice" } }`[`]`][ColumnPath.cols]` }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[cols][ColumnPath.cols]`() } // identity call, same as `[all][all]
     */
    private interface ColumnPathPredicateDocs

    /** @include [ColumnPathPredicateDocs] */
    public fun ColumnPath.cols(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = columnGroup(this).cols(predicate)

    /** @include [ColumnPathPredicateDocs] */
    public operator fun ColumnPath.get(
        predicate: ColumnFilter<*> = { true },
    ): TransformableColumnSet<*> = cols(predicate)

    // endregion

    // region references

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [ColumnReference]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[cols][ColumnSet.cols]`(columnA, columnB) }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>()`[`[`][ColumnSet.cols]`columnA, columnB`[`]`][ColumnSet.cols]` }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[cols][ColumnSet.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     */
    private interface ColumnSetColsVarargColumnReferenceDocs

    /** @include [ColumnSetColsVarargColumnReferenceDocs] */
    public fun <C> ColumnSet<C>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = transformWithContext {
        dataFrameOf(it)
            .asColumnGroup()
            .cols(firstCol, *otherCols)
            .resolve(this)
    }

    /** @include [ColumnSetColsVarargColumnReferenceDocs] */
    public operator fun <C> ColumnSet<C>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [ColumnReference]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]`(columnA, columnB) }`
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][DataFrame.select]` { this`[`[`][ColumnsSelectionDsl.cols]`columnA, columnB`[`]`][ColumnsSelectionDsl.cols]` }`
     */
    private interface ColumnsSelectionDslColsVarargColumnReferenceDocs

    /** @include [ColumnsSelectionDslColsVarargColumnReferenceDocs] */
    public fun <C> ColumnsSelectionDsl<*>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = this.asSingleColumn().cols(firstCol, *otherCols)

    /** @include [ColumnsSelectionDslColsVarargColumnReferenceDocs] */
    public operator fun <C> ColumnsSelectionDsl<*>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [ColumnReference]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[cols][SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.cols]`columnA, columnB`[`]`][SingleColumn.cols]` }`
     */
    private interface SingleColumnColsVarargColumnReferenceDocs

    /** @include [SingleColumnColsVarargColumnReferenceDocs] */
    public fun <C> SingleColumn<DataRow<*>>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = headPlusArray(firstCol, otherCols).let { refs ->
        ensureIsColGroup().asColumnSet().transform {
            it.flatMap { col -> refs.mapNotNull { col.getChild(it) } }
        }
    }

    /**
     * @include [SingleColumnColsVarargColumnReferenceDocs]
     */
    public operator fun <C> SingleColumn<DataRow<*>>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [ColumnReference]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[cols][String.cols]`(columnA, columnB) }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[cols][String.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup"`[`[`][String.cols]`columnA, columnB`[`]`][String.cols]` }`
     */
    private interface StringColsVarargColumnReferenceDocs

    /** @include [StringColsVarargColumnReferenceDocs] */
    public fun <C> String.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = columnGroup(this).cols(firstCol, *otherCols)

    /** @include [StringColsVarargColumnReferenceDocs] */
    public operator fun <C> String.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [ColumnReference]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[cols][SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup`[`[`][KProperty.cols]`"pathTo"["colA"], "pathTo"["colB"]`[`]`][KProperty.cols]` }`
     */
    private interface KPropertyColsVarargColumnReferenceDocs

    /** @include [KPropertyColsVarargColumnReferenceDocs] */
    public fun <C> KProperty<*>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = columnGroup(this).cols(firstCol, *otherCols)

    /** @include [KPropertyColsVarargColumnReferenceDocs] */
    public operator fun <C> KProperty<*>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [ColumnReference]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["columnGroup"].`[cols][ColumnPath.cols]`(columnA, columnB) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["columnGroup"].`[cols][ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][ColumnPath.cols]`columnA, columnB`[`]`][ColumnPath.cols]` }`
     */
    private interface ColumnPathColsVarargColumnReferenceDocs

    /** @include [ColumnPathColsVarargColumnReferenceDocs] */
    public fun <C> ColumnPath.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = columnGroup(this).cols(firstCol, *otherCols)

    /** @include [ColumnPathColsVarargColumnReferenceDocs] */
    public operator fun <C> ColumnPath.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    // endregion

    // region names

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [String]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[cols][ColumnSet.cols]`("columnA", "columnB") }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>()`[`[`][ColumnSet.cols]`"columnA", "columnB"`[`]`][ColumnSet.cols]` }`
     */
    private interface ColumnSetColsVarargStringDocs

    /** @include [ColumnSetColsVarargStringDocs] */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<C> = headPlusArray(firstCol, otherCols).let { names ->
        colsInternal { it.name in names } as ColumnSet<C>
    }

    /**
     * @include [ColumnSetColsVarargStringDocs]
     */
    public operator fun <C> ColumnSet<C>.get(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [String]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]`("columnA", "columnB") }`
     *
     * `df.`[select][DataFrame.select]` { this`[`[`][SingleColumn.cols]`"columnA", "columnB"`[`]`][SingleColumn.cols]` }`
     */
    private interface ColumnsSelectionDslVarargColumnReferenceDocs

    /** @include [ColumnsSelectionDslVarargColumnReferenceDocs] */
    public fun ColumnsSelectionDsl<*>.cols(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = this.asSingleColumn().cols(firstCol, *otherCols)

    /** @include [ColumnsSelectionDslVarargColumnReferenceDocs] */
    public operator fun ColumnsSelectionDsl<*>.get(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [String]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[cols][SingleColumn.cols]`("columnA", "columnB") }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.cols]`"columnA", "columnB"`[`]`][SingleColumn.cols]` }`
     */
    private interface SingleColumnColsVarargStringDocs

    /** @include [SingleColumnColsVarargStringDocs] */
    public fun SingleColumn<DataRow<*>>.cols(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = headPlusArray(firstCol, otherCols).let { names ->
        ensureIsColGroup().asColumnSet().transform { it.flatMap { col -> names.mapNotNull { col.getChild(it) } } }
    }

    /**
     * @include [SingleColumnColsVarargStringDocs]
     */
    public operator fun SingleColumn<DataRow<*>>.get(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [String]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "columnGroup".`[cols][String.cols]`("columnA", "columnB") }`
     *
     * `df.`[select][DataFrame.select]` { "columnGroup"`[`[`][String.cols]`"columnA", "columnB"`[`]`][String.cols]` }`
     */
    private interface StringColsVarargStringDocs

    /** @include [StringColsVarargStringDocs] */
    public fun String.cols(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = columnGroup(this).cols(firstCol, *otherCols)

    /** @include [StringColsVarargStringDocs] */
    public operator fun String.get(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [String]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[cols][SingleColumn.cols]`("columnA", "columnB") }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup`[`[`][KProperty.cols]`"columnA", "columnB"`[`]`][KProperty.cols]` }`
     */
    private interface KPropertiesColsVarargStringDocs

    /** @include [KPropertiesColsVarargStringDocs] */
    public fun KProperty<*>.cols(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = columnGroup(this).cols(firstCol, *otherCols)

    /** @include [KPropertiesColsVarargStringDocs] */
    public operator fun KProperty<*>.get(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [String]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["columnGroup"].`[cols][ColumnPath.cols]`("columnA", "columnB") }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][ColumnPath.cols]`"columnA", "columnB"`[`]`][ColumnPath.cols]` }`
     */
    private interface ColumnPathColsVarargStringDocs

    /** @include [ColumnPathColsVarargStringDocs] */
    public fun ColumnPath.cols(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = columnGroup(this).cols(firstCol, *otherCols)

    /** @include [ColumnPathColsVarargStringDocs] */
    public operator fun ColumnPath.get(
        firstCol: String,
        vararg otherCols: String,
    ): ColumnSet<*> = cols(firstCol, *otherCols)

    // endregion

    // region properties

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [KProperty]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[cols][ColumnSet.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[String][String]`>()`[`[`][ColumnSet.cols]`Type::colA, Type::colB`[`]`][ColumnSet.cols]` }`
     */
    private interface ColumnSetColsVarargKPropertyDocs

    /** @include [ColumnSetColsVarargKPropertyDocs] */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = headPlusArray(firstCol, otherCols).map { it.name }.let { names ->
        colsInternal { it.name in names } as ColumnSet<C>
    }

    /** @include [ColumnSetColsVarargKPropertyDocs] */
    public operator fun <C> ColumnSet<C>.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [KProperty]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][DataFrame.select]` { this`[`[`][ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][ColumnsSelectionDsl.cols]` }`
     */
    private interface ColumnsSelectionDslColsVarargKPropertyDocs

    /** @include [ColumnsSelectionDslColsVarargKPropertyDocs] */
    public fun <C> ColumnsSelectionDsl<*>.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = this.asSingleColumn().cols(firstCol, *otherCols)

    /** @include [ColumnsSelectionDslColsVarargKPropertyDocs] */
    public operator fun <C> ColumnsSelectionDsl<*>.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [KProperty]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[cols][SingleColumn.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.cols]`Type::colA, Type::colB`[`]`][SingleColumn.cols]` }`
     */
    private interface SingleColumnColsVarargKPropertyDocs

    /** @include [SingleColumnColsVarargKPropertyDocs] */
    public fun <C> SingleColumn<DataRow<*>>.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = headPlusArray(firstCol, otherCols).let { props ->
        ensureIsColGroup().asColumnSet().transform { it.flatMap { col -> props.mapNotNull { col.getChild(it) } } }
    }

    /** @include [SingleColumnColsVarargKPropertyDocs] */
    public operator fun <C> SingleColumn<DataRow<*>>.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [KProperty]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[cols][String.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup"`[`[`][String.cols]`Type::colA, Type::colB`[`]`][String.cols]` }`
     */
    private interface StringColsVarargKPropertyDocs

    /** @include [StringColsVarargKPropertyDocs] */
    public fun <C> String.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = columnGroup(this).cols(firstCol, *otherCols)

    /** @include [StringColsVarargKPropertyDocs] */
    public operator fun <C> String.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [KProperty]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[cols][SingleColumn.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup`[`[`][SingleColumn.cols]`Type::colA, Type::colB`[`]`][SingleColumn.cols]` }`
     */
    private interface KPropertyColsVarargKPropertyDocs

    /** @include [KPropertyColsVarargKPropertyDocs] */
    public fun <C> KProperty<*>.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = columnGroup(this).cols(firstCol, *otherCols)

    /** @include [KPropertyColsVarargKPropertyDocs] */
    public operator fun <C> KProperty<*>.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@setArg [CommonColsDocs.Vararg.AccessorType] [KProperty]}
     * @setArg [CommonColsDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["columnGroup"].`[cols][ColumnPath.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][ColumnPath.cols]`Type::colA, Type::colB`[`]`][ColumnPath.cols]` }`
     */
    private interface ColumnPathColsVarargKPropertyDocs

    /** @include [ColumnPathColsVarargKPropertyDocs] */
    public fun <C> ColumnPath.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = columnGroup(this).cols(firstCol, *otherCols)

    /** @include [ColumnPathColsVarargKPropertyDocs] */
    public operator fun <C> ColumnPath.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    // endregion

    // region indices

    /**
     * ## Cols: Columns by Indices
     *
     * Retrieves multiple columns in the form of a [ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * If called on a [SingleColumn], [ColumnGroup], or [DataFrame], the function will take the columns found at the
     * given indices inside.
     * Else, if called on a normal [ColumnSet], the function will return a new [ColumnSet] with the columns found at
     * the given indices in the set.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>()`[`[`][SingleColumn.get]`5, 1, 2`[`]`][SingleColumn.get]` }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[cols][String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonColsIndicesDocs.ExampleArg]}
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex\] The index of the first column to retrieve.
     * @param [otherIndices\] The other indices of the columns to retrieve.
     * @return A [ColumnSet] containing the columns found at the given indices.
     */
    private interface CommonColsIndicesDocs {

        /** Example argument */
        interface ExampleArg
    }

    /**
     * @include [CommonColsIndicesDocs]
     * @setArg [CommonColsIndicesDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[cols][ColumnSet.cols]`(1, 3) }`
     *
     * `df.`[select][DataFrame.select]` { `[all][ColumnsSelectionDsl.all]`()`[`[`][ColumnSet.cols]`5, 1`[`]`][ColumnSet.cols]` }`
     */
    private interface ColumnSetColsIndicesDocs

    /** @include [ColumnSetColsIndicesDocs] */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<C> = colsInternal(headPlusArray(firstIndex, otherIndices)) as ColumnSet<C>

    /** @include [ColumnSetColsIndicesDocs] */
    public operator fun <C> ColumnSet<C>.get(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<C> = cols(firstIndex, *otherIndices)

    /**
     * @include [CommonColsIndicesDocs]
     * @setArg [CommonColsIndicesDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]`(1, 3) }`
     */
    private interface ColumnsSelectionDslColsIndicesDocs

    /** @include [ColumnsSelectionDslColsIndicesDocs] */
    public fun ColumnsSelectionDsl<*>.cols(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = this.asSingleColumn().colsInternal(headPlusArray(firstIndex, otherIndices))

    /**
     * @include [CommonColsIndicesDocs]
     * @setArg [CommonColsIndicesDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[cols][SingleColumn.cols]`(1, 3) }`
     */
    private interface SingleColumnColsIndicesDocs

    /** @include [SingleColumnColsIndicesDocs] */
    public fun SingleColumn<DataRow<*>>.cols(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = ensureIsColGroup().colsInternal(headPlusArray(firstIndex, otherIndices))

    /**
     * @include [CommonColsIndicesDocs]
     * @setArg [CommonColsIndicesDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[cols][String.cols]`(5, 3, 1) }`
     */
    private interface StringColsIndicesDocs

    /** @include [StringColsIndicesDocs] */
    public fun String.cols(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = columnGroup(this).cols(firstIndex, *otherIndices)

    /**
     * @include [CommonColsIndicesDocs]
     * @setArg [CommonColsIndicesDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[cols][SingleColumn.cols]`(5, 4) }`
     */
    private interface KPropertyColsIndicesDocs

    /** @include [KPropertyColsIndicesDocs] */
    public fun KProperty<*>.cols(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = columnGroup(this).cols(firstIndex, *otherIndices)

    /**
     * @include [CommonColsIndicesDocs]
     * @setArg [CommonColsIndicesDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[cols][ColumnPath.cols]`(0, 1) }`
     */
    private interface ColumnPathColsIndicesDocs

    /** @include [ColumnPathColsIndicesDocs] */
    public fun ColumnPath.cols(
        firstIndex: Int,
        vararg otherIndices: Int,
    ): ColumnSet<*> = columnGroup(this).cols(firstIndex, *otherIndices)

    // endregion

    // region ranges

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns in the form of a [ColumnSet] by a [range\] of indices.
     * If any of the indices in the [range\] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * If called on a [SingleColumn], [ColumnGroup], or [DataFrame], the function will take the columns found at the
     * given indices inside.
     * Else, if called on a normal [ColumnSet], the function will return a new [ColumnSet] with the columns found at
     * the given indices in the set.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[cols][SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>()`[`[`][ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][ColumnSet.cols]` }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[cols][String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonColsRangeDocs.ExampleArg]}
     *
     * @throws [IndexOutOfBoundsException\] if any of the indices in the [range\] are out of bounds.
     * @throws [IllegalArgumentException\] if the [range\] is empty.
     * @param [range\] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet] containing the columns found at the given indices.
     */
    private interface CommonColsRangeDocs {

        /** Example argument */
        interface ExampleArg
    }

    /**
     * @include [CommonColsRangeDocs]
     * @setArg [CommonColsRangeDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[cols][ColumnSet.cols]`(1`[..][Int.rangeTo]`3) }`
     *
     * `df.`[select][DataFrame.select]` { `[all][all]`()`[`[`][ColumnSet.cols]`1`[..][Int.rangeTo]`5`[`]`][ColumnSet.cols]` }`
     */
    private interface ColumnSetColsRangeDocs

    /** @include [ColumnSetColsRangeDocs] */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(range: IntRange): ColumnSet<C> = colsInternal(range) as ColumnSet<C>

    /** @include [ColumnSetColsRangeDocs] */
    public operator fun <C> ColumnSet<C>.get(range: IntRange): ColumnSet<C> = cols(range)

    /**
     * @include [CommonColsRangeDocs]
     * @setArg [CommonColsRangeDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]`(1`[..][Int.rangeTo]`3) }`
     */
    private interface ColumnsSelectionDslColsRangeDocs

    /** @include [ColumnsSelectionDslColsRangeDocs] */
    public fun ColumnsSelectionDsl<*>.cols(range: IntRange): ColumnSet<*> = this.asSingleColumn().colsInternal(range)

    /**
     * @include [CommonColsRangeDocs]
     * @setArg [CommonColsRangeDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[cols][SingleColumn.cols]`(1`[..][Int.rangeTo]`3) }`
     */
    private interface SingleColumnColsRangeDocs

    /** @include [SingleColumnColsRangeDocs] */
    public fun SingleColumn<DataRow<*>>.cols(range: IntRange): ColumnSet<*> = ensureIsColGroup().colsInternal(range)

    /**
     * @include [CommonColsRangeDocs]
     * @setArg [CommonColsRangeDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[cols][String.cols]`(1`[`..`][Int.rangeTo]`3) }`
     */
    private interface StringColsRangeDocs

    /** @include [StringColsRangeDocs] */
    public fun String.cols(range: IntRange): ColumnSet<*> = columnGroup(this).cols(range)

    /**
     * @include [CommonColsRangeDocs]
     * @setArg [CommonColsRangeDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[cols][SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     */
    private interface KPropertyColsRangeDocs

    /** @include [KPropertyColsRangeDocs] */
    public fun KProperty<*>.cols(range: IntRange): ColumnSet<*> = columnGroup(this).cols(range)

    /**
     * @include [CommonColsRangeDocs]
     * @setArg [CommonColsRangeDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[cols][ColumnPath.cols]`(0`[..][Int.rangeTo]`1) }`
     */
    private interface ColumnPathColsRangeDocs

    /** @include [ColumnPathColsRangeDocs] */
    public fun ColumnPath.cols(range: IntRange): ColumnSet<*> = columnGroup(this).cols(range)

    /**
     * ## Columns by Index Range from List of Columns
     * Helper function to create a [ColumnSet] from a list of columns by specifying a range of indices.
     *
     * {@comment TODO find out usages of this function and add examples}
     */
    public operator fun <C> List<DataColumn<C>>.get(range: IntRange): ColumnSet<C> =
        ColumnsList(subList(range.first, range.last + 1))

    // endregion
}

/**
 * If this [ColumnsResolver] is a [SingleColumn], it
 * returns a new [ColumnSet] containing the columns inside of this [SingleColumn] that
 * match the given [predicate].
 *
 * Else, it returns a new [ColumnSet] containing all columns in this [ColumnsResolver] that
 * match the given [predicate].
 */
internal fun ColumnsResolver<*>.colsInternal(predicate: ColumnFilter<*>): TransformableColumnSet<*> =
    allColumnsInternal().transform { it.filter(predicate) }

internal fun ColumnsResolver<*>.colsInternal(indices: IntArray): TransformableColumnSet<*> =
    allColumnsInternal().transform { cols ->
        indices.map {
            try {
                cols[it]
            } catch (e: IndexOutOfBoundsException) {
                throw IndexOutOfBoundsException("Index $it is out of bounds for column set of size ${cols.size}")
            }
        }
    }

internal fun ColumnsResolver<*>.colsInternal(range: IntRange): TransformableColumnSet<*> =
    allColumnsInternal().transform {
        try {
            it.subList(range.first, range.last + 1)
        } catch (e: IndexOutOfBoundsException) {
            throw IndexOutOfBoundsException("Range $range is out of bounds for column set of size ${it.size}")
        }
    }
