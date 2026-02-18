package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.Issues
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.impl.headPlusArray
import org.jetbrains.kotlinx.dataframe.util.COLS_TO_ALL
import org.jetbrains.kotlinx.dataframe.util.COLS_TO_ALL_COLS
import org.jetbrains.kotlinx.dataframe.util.COLS_TO_ALL_COLS_REPLACE
import org.jetbrains.kotlinx.dataframe.util.COLS_TO_ALL_REPLACE
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

/**
 * ## Cols {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 * @param _UNUSED {@include [Issues.ConflictingOverloadsK2Link]}
 */
public interface ColsColumnsSelectionDsl<out _UNUSED> {

    /**
     * ## Cols Grammar
     *
     * @include [DslGrammarTemplate]
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.IndexDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ConditionDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnTypeDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.IndexRangeDef]}
     *  {@include [LineBreak]}
     * }
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [PlainDslName]}`[`**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>`**`]`**`(`**{@include [DslGrammarTemplate.ColumnRef]}**`,`**`  .. |  `{@include [DslGrammarTemplate.IndexRef]}**`,`**`  .. |  `{@include [DslGrammarTemplate.IndexRangeRef]}**`)`**
     *
     *  `| `{@include [PlainDslName]}`  [  `**`  {  `**{@include [DslGrammarTemplate.ConditionRef]}**`  \}  `**`]`
     *
     *  `| `**`this`**`/`**`it `**[**`[`**][cols]**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**[**`]`**][cols]
     *
     *  `| `**`this`**`/`**`it `**[**`[`**][cols]{@include [DslGrammarTemplate.ColumnRef]}**`,`**`  ..  `[**`]`**][cols]
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnSetName]}**`(`**{@include [DslGrammarTemplate.IndexRef]}**`,`**`  .. |  `{@include [DslGrammarTemplate.IndexRangeRef]}**`)`**
     *
     *  {@include [Indent]}`| `{@include [ColumnSetName]}`  [  `**`  {  `**{@include [DslGrammarTemplate.ConditionRef]}**`  \}  `**`]`
     *
     *  {@include [Indent]}`| `[**`[`**][cols]**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**[**`]`**][cols]
     *
     *  {@include [Indent]}`| `[**`[`**][cols]{@include [DslGrammarTemplate.IndexRef]}**`,`**`  .. |  `{@include [DslGrammarTemplate.IndexRangeRef]}[**`]`**][cols]`
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_GROUP_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnGroupName]}`[`**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>`**`]`**`(`**{@include [DslGrammarTemplate.ColumnRef]}**`,`**`  .. |  `{@include [DslGrammarTemplate.IndexRef]}**`,`**`  .. |  `{@include [DslGrammarTemplate.IndexRangeRef]}**`)`**
     *
     *  {@include [Indent]}`| `{@include [ColumnGroupName]}`  [  `**`  {  `**{@include [DslGrammarTemplate.ConditionRef]}**`  \}  `**`]`
     *
     *  {@include [Indent]}`| `[**`[`**][cols]**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**[**`]`**][cols]
     *
     *  {@include [Indent]}`| `[**`[`**][cols]{@include [DslGrammarTemplate.ColumnRef]}**`,`**` ..`[**`]`**][cols]
     * }
     */
    public interface Grammar {

        /** [**`cols`**][ColumnsSelectionDsl.cols] */
        public typealias PlainDslName = Nothing

        /** __`.`__[**`cols`**][ColumnsSelectionDsl.cols] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[**`cols`**][ColumnsSelectionDsl.cols] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet]) from [this\].
     *
     * You can use either a [ColumnFilter], or any of the `vararg` overloads for any
     * {@include [AccessApiLink]}. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols] directly, you can also use the [`get`][ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     * `df.`[`remove`][DataFrame.remove]`  {  `[`cols`][ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][DataFrame.select]` { myGroupCol.`[`cols`][SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][ColumnSet.cols]`1, 3, 5`[`]`][ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * {@get [CommonColsDocs.Examples]}
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
         * @return A [ColumnSet] containing the columns that match the given [predicate\].
         * @see [ColumnsSelectionDsl.filter\]
         * @see [ColumnsSelectionDsl.colsOfKind\]
         * @see [ColumnsSelectionDsl.valueCols\]
         * @see [ColumnsSelectionDsl.frameCols\]
         * @see [ColumnsSelectionDsl.colGroups\]
         */
        typealias Predicate = Nothing

        /**
         * @include [CommonColsDocs]
         *
         * @param [firstCol\] A {@get [AccessorType]} that points to a relative column.
         * @param [otherCols\] Optional additional {@get [AccessorType]}s that point to relative columns.
         * @throws [IllegalArgumentException\] if any of the given [ColumnReference]s point to a column that doesn't
         *   exist.
         * @return A [ColumnSet] containing the columns that [firstCol\] and [otherCols\] point to.
         */
        interface Vararg {

            typealias AccessorType = Nothing
        }

        /** Example argument */
        typealias Examples = Nothing
    }

    /**
     * ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this\] in the form of a [ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][SingleColumn.get]`5, 1, 2`[`]`][SingleColumn.get]` }`
     *
     * `df.`[`select`][DataFrame.select]` { "myColumnGroup".`[`cols`][String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * {@get [CommonColsIndicesDocs.EXAMPLE]}
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex\] The index of the first column to retrieve.
     * @param [otherIndices\] The other indices of the columns to retrieve.
     * @return A [ColumnSet] containing the columns found at the given indices.
     */
    private interface CommonColsIndicesDocs {

        /** Example argument */
        typealias EXAMPLE = Nothing
    }

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this\] in the form of a [ColumnSet] by a [range\] of indices.
     * If any of the indices in the [range\] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][ColumnSet.cols]` }`
     *
     * `df.`[`select`][DataFrame.select]` { "myColGroup".`[`cols`][String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * {@get [CommonColsRangeDocs.EXAMPLE]}
     *
     * @throws [IndexOutOfBoundsException\] if any of the indices in the [range\] are out of bounds.
     * @throws [IllegalArgumentException\] if the [range\] is empty.
     * @param [range\] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet] containing the columns found at the given indices.
     */
    private interface CommonColsRangeDocs {

        /** Example argument */
        typealias EXAMPLE = Nothing
    }

    // region predicate

    /**
     * @include [CommonColsDocs.Predicate]
     * @set [CommonColsDocs.Examples]
     *
     * `// although these can be shortened to just the `[`colsOf<>{ }`][ColumnsSelectionDsl.colsOf]` call`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>().`[`cols`][ColumnSet.cols]`  { "e"  `[`in`\][String.contains\]` it.`[`name`][ColumnPath.name]`() } }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][ColumnSet.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][ColumnSet.cols]` }`
     *
     * `// identity call, same as `[`all`][ColumnsSelectionDsl.all]`()`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>().`[`cols`][ColumnSet.cols]`() }`
     *
     * @see [ColumnsSelectionDsl.all\]
     * @see [ColumnsSelectionDsl.filter\]
     */
    private typealias ColumnSetColsPredicateDocs = Nothing

    /** @include [ColumnSetColsPredicateDocs] */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(predicate: ColumnFilter<C>): ColumnSet<C> =
        colsInternal(predicate as ColumnFilter<*>).cast()

    @Deprecated(COLS_TO_ALL, ReplaceWith(COLS_TO_ALL_REPLACE), DeprecationLevel.ERROR)
    public fun <C> ColumnSet<C>.cols(): ColumnSet<C> = cols { true }

    /** @include [ColumnSetColsPredicateDocs] */
    public operator fun <C> ColumnSet<C>.get(predicate: ColumnFilter<C> = { true }): ColumnSet<C> = cols(predicate)

    /**
     * @include [CommonColsDocs.Predicate]
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`  { "e"  `[`in`\][String.contains\]` it.`[`name`][ColumnPath.name]`() } }`
     *
     * `df.`[`select`][DataFrame.select]` { this`[`[`][ColumnsSelectionDsl.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][ColumnsSelectionDsl.cols]` }`
     *
     * `// same as `[`all`][ColumnsSelectionDsl.all]`()`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`() }`
     *
     * {@include [LineBreak]}
     *
     * @see [ColumnsSelectionDsl.all\]
     */
    private typealias ColumnsSelectionDslColsPredicateDocs = Nothing

    /** @include [ColumnsSelectionDslColsPredicateDocs] */
    public fun ColumnsSelectionDsl<*>.cols(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.asSingleColumn().colsInternal(predicate)

    @Deprecated(COLS_TO_ALL, ReplaceWith(COLS_TO_ALL_REPLACE), DeprecationLevel.ERROR)
    public fun ColumnsSelectionDsl<*>.cols(): ColumnSet<*> = cols { true }

    /** @include [ColumnsSelectionDslColsPredicateDocs] */
    public operator fun ColumnsSelectionDsl<*>.get(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        cols(predicate)

    /**
     * @include [CommonColsDocs.Predicate]
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup`.[`cols`][SingleColumn.cols]`  { "e"  `[`in`\][String.contains\]` it.`[`name`][ColumnPath.name]`() } }`
     *
     * `// same as `[`allCols`][ColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`() }`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.cols]`{ ... }`[`]`][SingleColumn.cols]` }`
     * {@include [LineBreak]}
     *
     * @see [ColumnsSelectionDsl.allCols\]
     */
    private typealias SingleColumnAnyRowColsPredicateDocs = Nothing

    /** @include [SingleColumnAnyRowColsPredicateDocs] */
    public fun SingleColumn<DataRow<*>>.cols(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal(predicate)

    @Deprecated(COLS_TO_ALL_COLS, ReplaceWith(COLS_TO_ALL_COLS_REPLACE), DeprecationLevel.ERROR)
    public fun SingleColumn<DataRow<*>>.cols(): ColumnSet<*> = cols { true }

    /**
     * @include [SingleColumnAnyRowColsPredicateDocs]
     */
    public operator fun SingleColumn<DataRow<*>>.get(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        cols(predicate)

    /**
     * @include [CommonColsDocs.Predicate]
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { "myGroupCol".`[`cols`][String.cols]`  { "e"  `[`in`\][String.contains\]` it.`[`name`][ColumnPath.name]`() } }`
     *
     * `df.`[`select`][DataFrame.select]` { "myGroupCol"`[`[`][String.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][String.cols]` }`
     *
     * `// same as `[`allCols`][ColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[`select`][DataFrame.select]` { "myGroupCol".`[`cols`][String.cols]`() }`
     */
    private typealias StringColsPredicateDocs = Nothing

    /** @include [StringColsPredicateDocs] */
    public fun String.cols(predicate: ColumnFilter<*>): ColumnSet<*> = columnGroup(this).cols(predicate)

    @Deprecated(COLS_TO_ALL_COLS, ReplaceWith(COLS_TO_ALL_COLS_REPLACE), DeprecationLevel.ERROR)
    public fun String.cols(): ColumnSet<*> = cols { true }

    /** @include [StringColsPredicateDocs] */
    public operator fun String.get(predicate: ColumnFilter<*> = { true }): ColumnSet<*> = cols(predicate)

    /**
     * @include [CommonColsDocs.Predicate]
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { Type::columnGroup.`[`cols`][KProperty.cols]`  { "e"  `[`in`\][String.contains\]` it.`[`name`][ColumnPath.name]`() } }`
     *
     * `df.`[`select`][DataFrame.select]` { Type::columnGroup`[`[`][SingleColumn.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][SingleColumn.cols]` }`
     *
     * `// same as `[`allCols`][ColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[`select`][DataFrame.select]` { Type::columnGroup.`[`cols`][SingleColumn.cols]`() }`
     *
     * @see [ColumnsSelectionDsl.allCols\]
     */
    private typealias KPropertyColsPredicateDocs = Nothing

    /** @include [KPropertyColsPredicateDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.cols(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        columnGroup(this).cols(predicate)

    /** @include [KPropertyColsPredicateDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun KProperty<*>.get(predicate: ColumnFilter<*> = { true }): ColumnSet<*> = cols(predicate)

    /**
     * @include [CommonColsDocs.Predicate]
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["myGroupCol"].`[`cols`][ColumnPath.cols]`  { "e"  `[`in`\][String.contains\]` it.`[`name`][ColumnPath.name]`() } }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["myGroupCol"]`[`[`][ColumnPath.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][ColumnPath.cols]` }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["myGroupCol"].`[`cols`][ColumnPath.cols]`() } // identity call, same as `[`allCols`][ColumnsSelectionDsl.allCols]
     */
    private typealias ColumnPathPredicateDocs = Nothing

    /** @include [ColumnPathPredicateDocs] */
    public fun ColumnPath.cols(predicate: ColumnFilter<*>): ColumnSet<*> = columnGroup(this).cols(predicate)

    @Deprecated(COLS_TO_ALL_COLS, ReplaceWith(COLS_TO_ALL_COLS_REPLACE), DeprecationLevel.ERROR)
    public fun ColumnPath.cols(): ColumnSet<*> = cols { true }

    /** @include [ColumnPathPredicateDocs] */
    public operator fun ColumnPath.get(predicate: ColumnFilter<*> = { true }): ColumnSet<*> = cols(predicate)

    // endregion

    // region references

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [ColumnReference]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`(colGroup.columnA, columnB) }`
     *
     * `df.`[`select`][DataFrame.select]` { this`[`[`][ColumnsSelectionDsl.cols]`colGroup.columnA, columnB`[`]`][ColumnsSelectionDsl.cols]` }`
     */
    private typealias ColumnsSelectionDslColsVarargColumnReferenceDocs = Nothing

    /** @include [ColumnsSelectionDslColsVarargColumnReferenceDocs] */
    @Interpretable("Cols0")
    public fun <C> ColumnsSelectionDsl<*>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = asSingleColumn().cols(firstCol, *otherCols)

    /** @include [ColumnsSelectionDslColsVarargColumnReferenceDocs] */
    public operator fun <C> ColumnsSelectionDsl<*>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [ColumnReference]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.cols]`columnA, columnB`[`]`][SingleColumn.cols]` }`
     */
    private typealias SingleColumnColsVarargColumnReferenceDocs = Nothing

    /** @include [SingleColumnColsVarargColumnReferenceDocs] */
    public fun <C> SingleColumn<DataRow<*>>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = colsInternal(listOf(firstCol, *otherCols)).cast()

    /**
     * @include [SingleColumnColsVarargColumnReferenceDocs]
     */
    public operator fun <C> SingleColumn<DataRow<*>>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [ColumnReference]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { "myColumnGroup".`[`cols`][String.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][DataFrame.select]` { "myColumnGroup"`[`[`][String.cols]`columnA, columnB`[`]`][String.cols]` }`
     */
    private typealias StringColsVarargColumnReferenceDocs = Nothing

    /** @include [StringColsVarargColumnReferenceDocs] */
    public fun <C> String.cols(firstCol: ColumnReference<C>, vararg otherCols: ColumnReference<C>): ColumnSet<C> =
        columnGroup(this).cols(firstCol, *otherCols)

    /** @include [StringColsVarargColumnReferenceDocs] */
    public operator fun <C> String.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [ColumnReference]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][DataFrame.select]` { DataSchemaType::myColumnGroup`[`[`][KProperty.cols]`"pathTo"["colA"], "pathTo"["colB"]`[`]`][KProperty.cols]` }`
     */
    private typealias KPropertyColsVarargColumnReferenceDocs = Nothing

    /** @include [KPropertyColsVarargColumnReferenceDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = columnGroup(this).cols(firstCol, *otherCols)

    /** @include [KPropertyColsVarargColumnReferenceDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> KProperty<*>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [ColumnReference]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][ColumnPath.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][ColumnPath.cols]`columnA, columnB`[`]`][ColumnPath.cols]` }`
     */
    private typealias ColumnPathColsVarargColumnReferenceDocs = Nothing

    /** @include [ColumnPathColsVarargColumnReferenceDocs] */
    public fun <C> ColumnPath.cols(firstCol: ColumnReference<C>, vararg otherCols: ColumnReference<C>): ColumnSet<C> =
        columnGroup(this).cols(firstCol, *otherCols)

    /** @include [ColumnPathColsVarargColumnReferenceDocs] */
    public operator fun <C> ColumnPath.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    // endregion

    // region names

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [String]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`<`[`String`][String]`>("columnA", "columnB") }`
     *
     * `df.`[`select`][DataFrame.select]` { this`[`[`][ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][ColumnsSelectionDsl.cols]` }`
     */
    private typealias ColumnsSelectionDslVarargStringDocs = Nothing

    /** @include [ColumnsSelectionDslVarargStringDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnsSelectionDsl<*>.cols(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** @include [ColumnsSelectionDslVarargStringDocs] */
    public fun <T> ColumnsSelectionDsl<*>.cols(firstCol: String, vararg otherCols: String): ColumnSet<T> =
        this.asSingleColumn().cols(firstCol, *otherCols).cast()

    /** @include [ColumnsSelectionDslVarargStringDocs] */
    public operator fun ColumnsSelectionDsl<*>.get(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [String]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.cols]`"columnA", "columnB"`[`]`][SingleColumn.cols]` }`
     */
    private typealias SingleColumnColsVarargStringDocs = Nothing

    /** @include [SingleColumnColsVarargStringDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun SingleColumn<DataRow<*>>.cols(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** @include [SingleColumnColsVarargStringDocs] */
    public fun <T> SingleColumn<DataRow<*>>.cols(firstCol: String, vararg otherCols: String): ColumnSet<T> =
        colsInternal(listOf(firstCol, *otherCols).map { pathOf(it) }).cast()

    /**
     * @include [SingleColumnColsVarargStringDocs]
     */
    public operator fun SingleColumn<DataRow<*>>.get(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [String]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { "columnGroup".`[`cols`][String.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][DataFrame.select]` { "columnGroup"`[`[`][String.cols]`"columnA", "columnB"`[`]`][String.cols]` }`
     */
    private typealias StringColsVarargStringDocs = Nothing

    /** @include [StringColsVarargStringDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun String.cols(firstCol: String, vararg otherCols: String): ColumnSet<*> = cols<Any?>(firstCol, *otherCols)

    /** @include [StringColsVarargStringDocs] */
    public fun <T> String.cols(firstCol: String, vararg otherCols: String): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** @include [StringColsVarargStringDocs] */
    public operator fun String.get(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [String]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][KProperty.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][DataFrame.select]` { DataSchemaType::myColumnGroup`[`[`][KProperty.cols]`"columnA", "columnB"`[`]`][KProperty.cols]` }`
     */
    private typealias KPropertiesColsVarargStringDocs = Nothing

    /** @include [KPropertiesColsVarargStringDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.cols(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** @include [KPropertiesColsVarargStringDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <T> KProperty<*>.cols(firstCol: String, vararg otherCols: String): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** @include [KPropertiesColsVarargStringDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun KProperty<*>.get(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [String]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][ColumnPath.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][ColumnPath.cols]`"columnA", "columnB"`[`]`][ColumnPath.cols]` }`
     */
    private typealias ColumnPathColsVarargStringDocs = Nothing

    /** @include [ColumnPathColsVarargStringDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnPath.cols(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** @include [ColumnPathColsVarargStringDocs] */
    public fun <T> ColumnPath.cols(firstCol: String, vararg otherCols: String): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** @include [ColumnPathColsVarargStringDocs] */
    public operator fun ColumnPath.get(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    // endregion

    // region paths

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [String]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { this`[`[`][ColumnsSelectionDsl.cols]`"pathTo"["colA"], "pathTo"["colB"])`[`]`][ColumnsSelectionDsl.cols]` }`
     */
    private typealias ColumnsSelectionDslVarargColumnPathDocs = Nothing

    /** @include [ColumnsSelectionDslVarargColumnPathDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnsSelectionDsl<*>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** @include [ColumnsSelectionDslVarargColumnPathDocs] */
    public fun <T> ColumnsSelectionDsl<*>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<T> =
        asSingleColumn().cols<T>(firstCol, *otherCols)

    /** @include [ColumnsSelectionDslVarargColumnPathDocs] */
    public operator fun ColumnsSelectionDsl<*>.get(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [ColumnPath]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.cols]`"pathTo"["colA"], "pathTo"["colB"])`[`]`][SingleColumn.cols]` }`
     */
    private typealias SingleColumnColsVarargColumnPathDocs = Nothing

    /** @include [SingleColumnColsVarargColumnPathDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun SingleColumn<DataRow<*>>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** @include [SingleColumnColsVarargColumnPathDocs] */
    public fun <T> SingleColumn<DataRow<*>>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<T> =
        colsInternal(listOf(firstCol, *otherCols)).cast()

    /**
     * @include [SingleColumnColsVarargColumnPathDocs]
     */
    public operator fun SingleColumn<DataRow<*>>.get(
        firstCol: ColumnPath,
        vararg otherCols: ColumnPath,
    ): ColumnSet<*> = cols<Any?>(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [ColumnPath]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { "columnGroup".`[`cols`][String.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { "columnGroup".`[`cols`][String.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { "columnGroup"`[`[`][String.cols]`""pathTo"["colA"], "pathTo"["colB"])`[`]`][ColumnPath.cols]` }`
     */
    private typealias StringColsVarargColumnPathDocs = Nothing

    /** @include [StringColsVarargColumnPathDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun String.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** @include [StringColsVarargColumnPathDocs] */
    public fun <T> String.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** @include [StringColsVarargColumnPathDocs] */
    public operator fun String.get(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [ColumnPath]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][KProperty.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][KProperty.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { DataSchemaType::myColumnGroup`[`[`][KProperty.cols]`"columnA", "columnB"`[`]`][KProperty.cols]` }`
     */
    private typealias KPropertiesColsVarargColumnPathDocs = Nothing

    /** @include [KPropertiesColsVarargColumnPathDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** @include [KPropertiesColsVarargColumnPathDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <T> KProperty<*>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** @include [KPropertiesColsVarargColumnPathDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun KProperty<*>.get(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [ColumnPath]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][ColumnPath.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][ColumnPath.cols]`"pathTo"["colA"], "pathTo"["colB"])`[`]`][ColumnPath.cols]` }`
     */
    private typealias ColumnPathColsVarargColumnPathDocs = Nothing

    /** @include [ColumnPathColsVarargColumnPathDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnPath.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** @include [ColumnPathColsVarargColumnPathDocs] */
    public fun <T> ColumnPath.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** @include [ColumnPathColsVarargColumnPathDocs] */
    public operator fun ColumnPath.get(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    // endregion

    // region properties

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [KProperty]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][DataFrame.select]` { this`[`[`][ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][ColumnsSelectionDsl.cols]` }`
     */
    private typealias ColumnsSelectionDslColsVarargKPropertyDocs = Nothing

    /** @include [ColumnsSelectionDslColsVarargKPropertyDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnsSelectionDsl<*>.cols(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        this.asSingleColumn().cols(firstCol, *otherCols)

    /** @include [ColumnsSelectionDslColsVarargKPropertyDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> ColumnsSelectionDsl<*>.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [KProperty]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.cols]`Type::colA, Type::colB`[`]`][SingleColumn.cols]` }`
     */
    private typealias SingleColumnColsVarargKPropertyDocs = Nothing

    /** @include [SingleColumnColsVarargKPropertyDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> SingleColumn<DataRow<*>>.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = colsInternal(listOf(firstCol, *otherCols).map { pathOf(it.name) }).cast()

    /** @include [SingleColumnColsVarargKPropertyDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> SingleColumn<DataRow<*>>.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [KProperty]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { "myColumnGroup".`[`cols`][String.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][DataFrame.select]` { "myColumnGroup"`[`[`][String.cols]`Type::colA, Type::colB`[`]`][String.cols]` }`
     */
    private typealias StringColsVarargKPropertyDocs = Nothing

    /** @include [StringColsVarargKPropertyDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> String.cols(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        columnGroup(this).cols(firstCol, *otherCols)

    /** @include [StringColsVarargKPropertyDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> String.get(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [KProperty]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][SingleColumn.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup`[`[`][SingleColumn.cols]`Type::colA, Type::colB`[`]`][SingleColumn.cols]` }`
     */
    private typealias KPropertyColsVarargKPropertyDocs = Nothing

    /** @include [KPropertyColsVarargKPropertyDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.cols(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        columnGroup(this).cols(firstCol, *otherCols)

    /** @include [KPropertyColsVarargKPropertyDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> KProperty<*>.get(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        cols(firstCol, *otherCols)

    /**
     * @include [CommonColsDocs.Vararg] {@set [CommonColsDocs.Vararg.AccessorType] [KProperty]}
     * @set [CommonColsDocs.Examples]
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][ColumnPath.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][ColumnPath.cols]`Type::colA, Type::colB`[`]`][ColumnPath.cols]` }`
     */
    private typealias ColumnPathColsVarargKPropertyDocs = Nothing

    /** @include [ColumnPathColsVarargKPropertyDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnPath.cols(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        columnGroup(this).cols(firstCol, *otherCols)

    /** @include [ColumnPathColsVarargKPropertyDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> ColumnPath.get(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        cols(firstCol, *otherCols)

    // endregion

    // region indices

    /**
     * @include [CommonColsIndicesDocs]
     * @set [CommonColsIndicesDocs.EXAMPLE]
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][SingleColumn.colsOf]`<`[`Int`][Int]`>().`[`cols`][ColumnSet.cols]`(1, 3) }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`all`][ColumnsSelectionDsl.all]`()`[`[`][ColumnSet.cols]`5, 1`[`]`][ColumnSet.cols]` }`
     */
    private typealias ColumnSetColsIndicesDocs = Nothing

    /** @include [ColumnSetColsIndicesDocs] */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<C> =
        colsInternal(headPlusArray(firstIndex, otherIndices)) as ColumnSet<C>

    /** @include [ColumnSetColsIndicesDocs] */
    public operator fun <C> ColumnSet<C>.get(firstIndex: Int, vararg otherIndices: Int): ColumnSet<C> =
        cols(firstIndex, *otherIndices)

    /**
     * @include [CommonColsIndicesDocs]
     * @set [CommonColsIndicesDocs.EXAMPLE]
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`(1, 3) }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`<`[`String`][String]`>(1, 3) }`
     */
    private typealias ColumnsSelectionDslColsIndicesDocs = Nothing

    /** @include [ColumnsSelectionDslColsIndicesDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnsSelectionDsl<*>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<*> =
        cols<Any?>(firstIndex, *otherIndices)

    /** @include [ColumnsSelectionDslColsIndicesDocs] */
    public fun <T> ColumnsSelectionDsl<*>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<T> =
        this.asSingleColumn().colsInternal(headPlusArray(firstIndex, otherIndices)).cast()

    /**
     * @include [CommonColsIndicesDocs]
     * @set [CommonColsIndicesDocs.EXAMPLE]
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`(1, 3) }`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`<`[`String`][String]`>(3, 4) }`
     */
    private typealias SingleColumnColsIndicesDocs = Nothing

    /** @include [SingleColumnColsIndicesDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun SingleColumn<DataRow<*>>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<*> =
        cols<Any?>(firstIndex, *otherIndices)

    /** @include [SingleColumnColsIndicesDocs] */
    public fun <T> SingleColumn<DataRow<*>>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<T> =
        this.ensureIsColumnGroup().colsInternal(headPlusArray(firstIndex, otherIndices)).cast()

    /**
     * @include [CommonColsIndicesDocs]
     * @set [CommonColsIndicesDocs.EXAMPLE]
     *
     * `df.`[`select`][DataFrame.select]` { "myColumnGroup".`[`cols`][String.cols]`(5, 3, 1) }`
     *
     * `df.`[`select`][DataFrame.select]` { "myColumnGroup".`[`cols`][String.cols]`<`[`String`][String]`>(5, 3, 1) }`
     */
    private typealias StringColsIndicesDocs = Nothing

    /** @include [StringColsIndicesDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun String.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<*> =
        cols<Any?>(firstIndex, *otherIndices)

    /** @include [StringColsIndicesDocs] */
    public fun <T> String.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<T> =
        columnGroup(this).cols(firstIndex, *otherIndices).cast()

    /**
     * @include [CommonColsIndicesDocs]
     * @set [CommonColsIndicesDocs.EXAMPLE]
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][SingleColumn.cols]`(5, 4) }`
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][SingleColumn.cols]`<`[`String`][String]`>(5, 4) }`
     */
    private typealias KPropertyColsIndicesDocs = Nothing

    /** @include [KPropertyColsIndicesDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<*> =
        cols<Any?>(firstIndex, *otherIndices)

    /** @include [KPropertyColsIndicesDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <T> KProperty<*>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<T> =
        columnGroup(this).cols(firstIndex, *otherIndices).cast()

    /**
     * @include [CommonColsIndicesDocs]
     * @set [CommonColsIndicesDocs.EXAMPLE]
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["myColGroup"].`[`cols`][ColumnPath.cols]`(0, 1) }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["myColGroup"].`[`cols`][ColumnPath.cols]`<`[`String`][String]`>(0, 1) }`
     */
    private typealias ColumnPathColsIndicesDocs = Nothing

    /** @include [ColumnPathColsIndicesDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnPath.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<*> =
        cols<Any?>(firstIndex, *otherIndices)

    /** @include [ColumnPathColsIndicesDocs] */
    public fun <T> ColumnPath.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<T> =
        columnGroup(this).cols(firstIndex, *otherIndices).cast()

    // endregion

    // region ranges

    /**
     * @include [CommonColsRangeDocs]
     * @set [CommonColsRangeDocs.EXAMPLE]
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][SingleColumn.colsOf]`<`[`Int`][Int]`>().`[`cols`][ColumnSet.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`all`][all]`()`[`[`][ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][ColumnSet.cols]` }`
     */
    private typealias ColumnSetColsRangeDocs = Nothing

    /** @include [ColumnSetColsRangeDocs] */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(range: IntRange): ColumnSet<C> = colsInternal(range) as ColumnSet<C>

    /** @include [ColumnSetColsRangeDocs] */
    public operator fun <C> ColumnSet<C>.get(range: IntRange): ColumnSet<C> = cols(range)

    /**
     * @include [CommonColsRangeDocs]
     * @set [CommonColsRangeDocs.EXAMPLE]
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`<`[`String`][String]`>(1`[`..`][Int.rangeTo]`3) }`
     */
    private typealias ColumnsSelectionDslColsRangeDocs = Nothing

    /** @include [ColumnsSelectionDslColsRangeDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnsSelectionDsl<*>.cols(range: IntRange): ColumnSet<*> = cols<Any?>(range)

    /** @include [ColumnsSelectionDslColsRangeDocs] */
    public fun <T> ColumnsSelectionDsl<*>.cols(range: IntRange): ColumnSet<T> =
        this.asSingleColumn().colsInternal(range).cast()

    /**
     * @include [CommonColsRangeDocs]
     * @set [CommonColsRangeDocs.EXAMPLE]
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`<`[`String`][String]`>(1`[`..`][Int.rangeTo]`3) }`
     */
    private typealias SingleColumnColsRangeDocs = Nothing

    /** @include [SingleColumnColsRangeDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun SingleColumn<DataRow<*>>.cols(range: IntRange): ColumnSet<*> = cols<Any?>(range)

    /** @include [SingleColumnColsRangeDocs] */
    public fun <T> SingleColumn<DataRow<*>>.cols(range: IntRange): ColumnSet<T> =
        this.ensureIsColumnGroup().colsInternal(range).cast()

    /**
     * @include [CommonColsRangeDocs]
     * @set [CommonColsRangeDocs.EXAMPLE]
     *
     * `df.`[`select`][DataFrame.select]` { "myColGroup".`[`cols`][String.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][DataFrame.select]` { "myColGroup".`[`cols`][String.cols]`<`[`String`][String]`>(1`[`..`][Int.rangeTo]`3) }`
     */
    private typealias StringColsRangeDocs = Nothing

    /** @include [StringColsRangeDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun String.cols(range: IntRange): ColumnSet<*> = cols<Any?>(range)

    /** @include [StringColsRangeDocs] */
    public fun <T> String.cols(range: IntRange): ColumnSet<T> = columnGroup(this).cols(range).cast()

    /**
     * @include [CommonColsRangeDocs]
     * @set [CommonColsRangeDocs.EXAMPLE]
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][SingleColumn.cols]`<`[`String`][String]`>(1`[`..`][Int.rangeTo]`3) }`
     *
     */
    private typealias KPropertyColsRangeDocs = Nothing

    /** @include [KPropertyColsRangeDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.cols(range: IntRange): ColumnSet<*> = cols<Any?>(range)

    /** @include [KPropertyColsRangeDocs] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <T> KProperty<*>.cols(range: IntRange): ColumnSet<T> = columnGroup(this).cols(range).cast()

    /**
     * @include [CommonColsRangeDocs]
     * @set [CommonColsRangeDocs.EXAMPLE]
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["myColGroup"].`[`cols`][ColumnPath.cols]`(0`[`..`][Int.rangeTo]`1) }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["myColGroup"].`[`cols`][ColumnPath.cols]`<`[`String`][String]`>(0`[`..`][Int.rangeTo]`1) }`
     */
    private typealias ColumnPathColsRangeDocs = Nothing

    /** @include [ColumnPathColsRangeDocs] */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnPath.cols(range: IntRange): ColumnSet<*> = cols<Any?>(range)

    /** @include [ColumnPathColsRangeDocs] */
    public fun <T> ColumnPath.cols(range: IntRange): ColumnSet<T> = columnGroup(this).cols(range).cast()

    // endregion
}

internal fun SingleColumn<DataRow<*>>.colsInternal(refs: Iterable<ColumnReference<*>>): ColumnSet<*> =
    ensureIsColumnGroup().transformSingle { col ->
        refs.map {
            col.getCol(it) ?: throw IllegalArgumentException(
                "Column at ${col.path.plus(it.path()).joinToString()} was not found.",
            )
        }
    }

/**
 * If this [ColumnsResolver] is a [SingleColumn], it
 * returns a new [ColumnSet] containing the columns inside of this [SingleColumn] that
 * match the given [predicate].
 *
 * Else, it returns a new [ColumnSet] containing all columns in this [ColumnsResolver] that
 * match the given [predicate].
 */
@PublishedApi
internal inline fun ColumnsResolver<*>.colsInternal(crossinline predicate: ColumnFilter<*>): TransformableColumnSet<*> =
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
