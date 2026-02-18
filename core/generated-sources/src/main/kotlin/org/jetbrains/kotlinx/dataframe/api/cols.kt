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
 * ## Cols [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 * @param _UNUSED [#KT-68546](https://youtrack.jetbrains.com/issue/KT-68546/Conflicting-overloads-in-non-generic-interface-K2-2.0.0)
 */
public interface ColsColumnsSelectionDsl<out _UNUSED> {

    /**
     * ## Cols Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[`ColumnSet`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[`SingleColumn`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `column: `[`ColumnAccessor`][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `index: `[`Int`][Int]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[`ColumnFilter`][org.jetbrains.kotlinx.dataframe.ColumnFilter]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `T: Column type`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `indexRange: `[`IntRange`][IntRange]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called directly in the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [**`cols`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`[`**`<`**[`T`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**`  .. |  `[`index`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`,`**`  .. |  `[`indexRange`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexRangeDef]**`)`**
     *
     *  `| `[**`cols`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  [  `**`  {  `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**`  }  `**`]`
     *
     *  `| `**`this`**`/`**`it `**[**`[`**][cols]**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**[**`]`**][cols]
     *
     *  `| `**`this`**`/`**`it `**[**`[`**][cols][`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**`  ..  `[**`]`**][cols]
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`cols`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]**`(`**[`index`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`,`**`  .. |  `[`indexRange`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexRangeDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[**`cols`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  [  `**`  {  `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**`  }  `**`]`
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `[**`[`**][cols]**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**[**`]`**][cols]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `[**`[`**][cols][`index`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`,`**`  .. |  `[`indexRange`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexRangeDef][**`]`**][cols]`
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [Column Group (reference)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnGroup`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`cols`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`[`**`<`**[`T`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**`  .. |  `[`index`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`,`**`  .. |  `[`indexRange`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexRangeDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[**`cols`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  [  `**`  {  `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**`  }  `**`]`
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `[**`[`**][cols]**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**[**`]`**][cols]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `[**`[`**][cols][`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**` ..`[**`]`**][cols]
     *
     *
     *
     *
     *
     *
     *
     *
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
     * Creates a subset of columns ([ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
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
     *
     *
     */
    private interface CommonColsDocs {

        /**
         * ## Cols
         * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
         *
         * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
         * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
         * a column name, -path, or index (range)).
         *
         * This function operates solely on columns at the top-level.
         *
         * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
         *
         * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
         *
         * #### For example:
         * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
         *
         * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
         *
         * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
         *
         * #### Examples for this overload:
         *
         *
         *
         *
         * #### Filter vs. Cols:
         * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][ColumnsSelectionDsl.filter].
         * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][ColumnSet] and
         * `cols {}` on the rest.
         *
         * @param [predicate] A [ColumnFilter function][ColumnFilter] that takes a [ColumnReference] and returns a [Boolean].
         * @return A [ColumnSet] containing the columns that match the given [predicate].
         * @see [ColumnsSelectionDsl.filter]
         * @see [ColumnsSelectionDsl.colsOfKind]
         * @see [ColumnsSelectionDsl.valueCols]
         * @see [ColumnsSelectionDsl.frameCols]
         * @see [ColumnsSelectionDsl.colGroups]
         */
        typealias Predicate = Nothing

        /**
         * ## Cols
         * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
         *
         * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
         * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
         * a column name, -path, or index (range)).
         *
         * This function operates solely on columns at the top-level.
         *
         * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
         *
         * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
         *
         * #### For example:
         * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
         *
         * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
         *
         * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
         *
         * #### Examples for this overload:
         *
         *
         *
         *
         * @param [firstCol] A  that points to a relative column.
         * @param [otherCols] Optional additional s that point to relative columns.
         * @throws [IllegalArgumentException] if any of the given [ColumnReference]s point to a column that doesn't
         *   exist.
         * @return A [ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
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
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet] by their indices.
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
     *
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet] containing the columns found at the given indices.
     */
    private interface CommonColsIndicesDocs {

        /** Example argument */
        typealias EXAMPLE = Nothing
    }

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
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
     *
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet] containing the columns found at the given indices.
     */
    private interface CommonColsRangeDocs {

        /** Example argument */
        typealias EXAMPLE = Nothing
    }

    // region predicate

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `// although these can be shortened to just the `[`colsOf<>{ }`][ColumnsSelectionDsl.colsOf]` call`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>().`[`cols`][ColumnSet.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][ColumnPath.name]`() } }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][ColumnSet.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][ColumnSet.cols]` }`
     *
     * `// identity call, same as `[`all`][ColumnsSelectionDsl.all]`()`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>().`[`cols`][ColumnSet.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.all]
     * @see [ColumnsSelectionDsl.filter]
     */
    private typealias ColumnSetColsPredicateDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `// although these can be shortened to just the `[`colsOf<>{ }`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]` call`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>().`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `// identity call, same as `[`all`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`()`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>().`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.all]
     * @see [ColumnsSelectionDsl.filter] */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(predicate: ColumnFilter<C>): ColumnSet<C> =
        colsInternal(predicate as ColumnFilter<*>).cast()

    @Deprecated(COLS_TO_ALL, ReplaceWith(COLS_TO_ALL_REPLACE), DeprecationLevel.ERROR)
    public fun <C> ColumnSet<C>.cols(): ColumnSet<C> = cols { true }

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `// although these can be shortened to just the `[`colsOf<>{ }`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]` call`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>().`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `// identity call, same as `[`all`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`()`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>().`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.all]
     * @see [ColumnsSelectionDsl.filter] */
    public operator fun <C> ColumnSet<C>.get(predicate: ColumnFilter<C> = { true }): ColumnSet<C> = cols(predicate)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][ColumnPath.name]`() } }`
     *
     * `df.`[`select`][DataFrame.select]` { this`[`[`][ColumnsSelectionDsl.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][ColumnsSelectionDsl.cols]` }`
     *
     * `// same as `[`all`][ColumnsSelectionDsl.all]`()`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`() }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.all]
     */
    private typealias ColumnsSelectionDslColsPredicateDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `// same as `[`all`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`()`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.all] */
    public fun ColumnsSelectionDsl<*>.cols(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.asSingleColumn().colsInternal(predicate)

    @Deprecated(COLS_TO_ALL, ReplaceWith(COLS_TO_ALL_REPLACE), DeprecationLevel.ERROR)
    public fun ColumnsSelectionDsl<*>.cols(): ColumnSet<*> = cols { true }

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `// same as `[`all`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`()`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.all] */
    public operator fun ColumnsSelectionDsl<*>.get(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        cols(predicate)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup`.[`cols`][SingleColumn.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][ColumnPath.name]`() } }`
     *
     * `// same as `[`allCols`][ColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`() }`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.cols]`{ ... }`[`]`][SingleColumn.cols]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.allCols]
     */
    private typealias SingleColumnAnyRowColsPredicateDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`.[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `// same as `[`allCols`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`() }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`{ ... }`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.allCols] */
    public fun SingleColumn<DataRow<*>>.cols(predicate: ColumnFilter<*>): ColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal(predicate)

    @Deprecated(COLS_TO_ALL_COLS, ReplaceWith(COLS_TO_ALL_COLS_REPLACE), DeprecationLevel.ERROR)
    public fun SingleColumn<DataRow<*>>.cols(): ColumnSet<*> = cols { true }

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`.[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `// same as `[`allCols`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`() }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`{ ... }`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.allCols]
     */
    public operator fun SingleColumn<DataRow<*>>.get(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        cols(predicate)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { "myGroupCol".`[`cols`][String.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][ColumnPath.name]`() } }`
     *
     * `df.`[`select`][DataFrame.select]` { "myGroupCol"`[`[`][String.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][String.cols]` }`
     *
     * `// same as `[`allCols`][ColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[`select`][DataFrame.select]` { "myGroupCol".`[`cols`][String.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     */
    private typealias StringColsPredicateDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol".`[`cols`][kotlin.String.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol"`[`[`][kotlin.String.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][kotlin.String.cols]` }`
     *
     * `// same as `[`allCols`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol".`[`cols`][kotlin.String.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     */
    public fun String.cols(predicate: ColumnFilter<*>): ColumnSet<*> = columnGroup(this).cols(predicate)

    @Deprecated(COLS_TO_ALL_COLS, ReplaceWith(COLS_TO_ALL_COLS_REPLACE), DeprecationLevel.ERROR)
    public fun String.cols(): ColumnSet<*> = cols { true }

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol".`[`cols`][kotlin.String.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol"`[`[`][kotlin.String.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][kotlin.String.cols]` }`
     *
     * `// same as `[`allCols`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol".`[`cols`][kotlin.String.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     */
    public operator fun String.get(predicate: ColumnFilter<*> = { true }): ColumnSet<*> = cols(predicate)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { Type::columnGroup.`[`cols`][KProperty.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][ColumnPath.name]`() } }`
     *
     * `df.`[`select`][DataFrame.select]` { Type::columnGroup`[`[`][SingleColumn.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][SingleColumn.cols]` }`
     *
     * `// same as `[`allCols`][ColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[`select`][DataFrame.select]` { Type::columnGroup.`[`cols`][SingleColumn.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.allCols]
     */
    private typealias KPropertyColsPredicateDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnGroup.`[`cols`][kotlin.reflect.KProperty.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     * `// same as `[`allCols`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.allCols] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.cols(predicate: ColumnFilter<*> = { true }): ColumnSet<*> =
        columnGroup(this).cols(predicate)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnGroup.`[`cols`][kotlin.reflect.KProperty.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     * `// same as `[`allCols`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.allCols] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun KProperty<*>.get(predicate: ColumnFilter<*> = { true }): ColumnSet<*> = cols(predicate)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["myGroupCol"].`[`cols`][ColumnPath.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][ColumnPath.name]`() } }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["myGroupCol"]`[`[`][ColumnPath.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][ColumnPath.cols]` }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["myGroupCol"].`[`cols`][ColumnPath.cols]`() } // identity call, same as `[`allCols`][ColumnsSelectionDsl.allCols]
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     */
    private typealias ColumnPathPredicateDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"]`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`() } // identity call, same as `[`allCols`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     */
    public fun ColumnPath.cols(predicate: ColumnFilter<*>): ColumnSet<*> = columnGroup(this).cols(predicate)

    @Deprecated(COLS_TO_ALL_COLS, ReplaceWith(COLS_TO_ALL_COLS_REPLACE), DeprecationLevel.ERROR)
    public fun ColumnPath.cols(): ColumnSet<*> = cols { true }

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`  { "e"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"]`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`{ it.`[`any`][ColumnWithPath.any]` { it == "Alice" } }`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`() } // identity call, same as `[`allCols`][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [`filter {}`][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [ColumnFilter function][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [Boolean].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     */
    public operator fun ColumnPath.get(predicate: ColumnFilter<*> = { true }): ColumnSet<*> = cols(predicate)

    // endregion

    // region references

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`(colGroup.columnA, columnB) }`
     *
     * `df.`[`select`][DataFrame.select]` { this`[`[`][ColumnsSelectionDsl.cols]`colGroup.columnA, columnB`[`]`][ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias ColumnsSelectionDslColsVarargColumnReferenceDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup.columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`colGroup.columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Interpretable("Cols0")
    public fun <C> ColumnsSelectionDsl<*>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = asSingleColumn().cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup.columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`colGroup.columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun <C> ColumnsSelectionDsl<*>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.cols]`columnA, columnB`[`]`][SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias SingleColumnColsVarargColumnReferenceDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <C> SingleColumn<DataRow<*>>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = colsInternal(listOf(firstCol, *otherCols)).cast()

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun <C> SingleColumn<DataRow<*>>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { "myColumnGroup".`[`cols`][String.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][DataFrame.select]` { "myColumnGroup"`[`[`][String.cols]`columnA, columnB`[`]`][String.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias StringColsVarargColumnReferenceDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"`[`[`][kotlin.String.cols]`columnA, columnB`[`]`][kotlin.String.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <C> String.cols(firstCol: ColumnReference<C>, vararg otherCols: ColumnReference<C>): ColumnSet<C> =
        columnGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"`[`[`][kotlin.String.cols]`columnA, columnB`[`]`][kotlin.String.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun <C> String.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][DataFrame.select]` { DataSchemaType::myColumnGroup`[`[`][KProperty.cols]`"pathTo"["colA"], "pathTo"["colB"]`[`]`][KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias KPropertyColsVarargColumnReferenceDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup`[`[`][kotlin.reflect.KProperty.cols]`"pathTo"["colA"], "pathTo"["colB"]`[`]`][kotlin.reflect.KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = columnGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup`[`[`][kotlin.reflect.KProperty.cols]`"pathTo"["colA"], "pathTo"["colB"]`[`]`][kotlin.reflect.KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> KProperty<*>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][ColumnPath.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][ColumnPath.cols]`columnA, columnB`[`]`][ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias ColumnPathColsVarargColumnReferenceDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <C> ColumnPath.cols(firstCol: ColumnReference<C>, vararg otherCols: ColumnReference<C>): ColumnSet<C> =
        columnGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`columnA, columnB`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun <C> ColumnPath.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    // endregion

    // region names

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`<`[`String`][String]`>("columnA", "columnB") }`
     *
     * `df.`[`select`][DataFrame.select]` { this`[`[`][ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias ColumnsSelectionDslVarargStringDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[`String`][String]`>("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnsSelectionDsl<*>.cols(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[`String`][String]`>("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <T> ColumnsSelectionDsl<*>.cols(firstCol: String, vararg otherCols: String): ColumnSet<T> =
        this.asSingleColumn().cols(firstCol, *otherCols).cast()

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[`String`][String]`>("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun ColumnsSelectionDsl<*>.get(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.cols]`"columnA", "columnB"`[`]`][SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias SingleColumnColsVarargStringDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun SingleColumn<DataRow<*>>.cols(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <T> SingleColumn<DataRow<*>>.cols(firstCol: String, vararg otherCols: String): ColumnSet<T> =
        colsInternal(listOf(firstCol, *otherCols).map { pathOf(it) }).cast()

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun SingleColumn<DataRow<*>>.get(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { "columnGroup".`[`cols`][String.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][DataFrame.select]` { "columnGroup"`[`[`][String.cols]`"columnA", "columnB"`[`]`][String.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias StringColsVarargStringDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[`cols`][kotlin.String.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup"`[`[`][kotlin.String.cols]`"columnA", "columnB"`[`]`][kotlin.String.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun String.cols(firstCol: String, vararg otherCols: String): ColumnSet<*> = cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[`cols`][kotlin.String.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup"`[`[`][kotlin.String.cols]`"columnA", "columnB"`[`]`][kotlin.String.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <T> String.cols(firstCol: String, vararg otherCols: String): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[`cols`][kotlin.String.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup"`[`[`][kotlin.String.cols]`"columnA", "columnB"`[`]`][kotlin.String.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun String.get(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][KProperty.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][DataFrame.select]` { DataSchemaType::myColumnGroup`[`[`][KProperty.cols]`"columnA", "columnB"`[`]`][KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias KPropertiesColsVarargStringDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][kotlin.reflect.KProperty.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup`[`[`][kotlin.reflect.KProperty.cols]`"columnA", "columnB"`[`]`][kotlin.reflect.KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.cols(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][kotlin.reflect.KProperty.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup`[`[`][kotlin.reflect.KProperty.cols]`"columnA", "columnB"`[`]`][kotlin.reflect.KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <T> KProperty<*>.cols(firstCol: String, vararg otherCols: String): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][kotlin.reflect.KProperty.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup`[`[`][kotlin.reflect.KProperty.cols]`"columnA", "columnB"`[`]`][kotlin.reflect.KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun KProperty<*>.get(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][ColumnPath.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][ColumnPath.cols]`"columnA", "columnB"`[`]`][ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias ColumnPathColsVarargStringDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnPath.cols(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <T> ColumnPath.cols(firstCol: String, vararg otherCols: String): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`("columnA", "columnB") }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`"columnA", "columnB"`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun ColumnPath.get(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    // endregion

    // region paths

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { this`[`[`][ColumnsSelectionDsl.cols]`"pathTo"["colA"], "pathTo"["colB"])`[`]`][ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias ColumnsSelectionDslVarargColumnPathDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"pathTo"["colA"], "pathTo"["colB"])`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnsSelectionDsl<*>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"pathTo"["colA"], "pathTo"["colB"])`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <T> ColumnsSelectionDsl<*>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<T> =
        asSingleColumn().cols<T>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"pathTo"["colA"], "pathTo"["colB"])`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [String] that points to a relative column.
     * @param [otherCols] Optional additional [String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun ColumnsSelectionDsl<*>.get(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.cols]`"pathTo"["colA"], "pathTo"["colB"])`[`]`][SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias SingleColumnColsVarargColumnPathDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`"pathTo"["colA"], "pathTo"["colB"])`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun SingleColumn<DataRow<*>>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`"pathTo"["colA"], "pathTo"["colB"])`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <T> SingleColumn<DataRow<*>>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<T> =
        colsInternal(listOf(firstCol, *otherCols)).cast()

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`"pathTo"["colA"], "pathTo"["colB"])`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun SingleColumn<DataRow<*>>.get(
        firstCol: ColumnPath,
        vararg otherCols: ColumnPath,
    ): ColumnSet<*> = cols<Any?>(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { "columnGroup".`[`cols`][String.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { "columnGroup".`[`cols`][String.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { "columnGroup"`[`[`][String.cols]`""pathTo"["colA"], "pathTo"["colB"])`[`]`][ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias StringColsVarargColumnPathDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[`cols`][kotlin.String.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[`cols`][kotlin.String.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup"`[`[`][kotlin.String.cols]`""pathTo"["colA"], "pathTo"["colB"])`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun String.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[`cols`][kotlin.String.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[`cols`][kotlin.String.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup"`[`[`][kotlin.String.cols]`""pathTo"["colA"], "pathTo"["colB"])`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <T> String.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[`cols`][kotlin.String.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[`cols`][kotlin.String.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup"`[`[`][kotlin.String.cols]`""pathTo"["colA"], "pathTo"["colB"])`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun String.get(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][KProperty.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][KProperty.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { DataSchemaType::myColumnGroup`[`[`][KProperty.cols]`"columnA", "columnB"`[`]`][KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias KPropertiesColsVarargColumnPathDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][kotlin.reflect.KProperty.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][kotlin.reflect.KProperty.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup`[`[`][kotlin.reflect.KProperty.cols]`"columnA", "columnB"`[`]`][kotlin.reflect.KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][kotlin.reflect.KProperty.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][kotlin.reflect.KProperty.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup`[`[`][kotlin.reflect.KProperty.cols]`"columnA", "columnB"`[`]`][kotlin.reflect.KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <T> KProperty<*>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][kotlin.reflect.KProperty.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][kotlin.reflect.KProperty.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup`[`[`][kotlin.reflect.KProperty.cols]`"columnA", "columnB"`[`]`][kotlin.reflect.KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun KProperty<*>.get(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][ColumnPath.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][ColumnPath.cols]`"pathTo"["colA"], "pathTo"["colB"])`[`]`][ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias ColumnPathColsVarargColumnPathDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`"pathTo"["colA"], "pathTo"["colB"])`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnPath.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`"pathTo"["colA"], "pathTo"["colB"])`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <T> ColumnPath.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`<`[`String`][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`"pathTo"["colA"], "pathTo"["colB"])`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun ColumnPath.get(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    // endregion

    // region properties

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][DataFrame.select]` { this`[`[`][ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias ColumnsSelectionDslColsVarargKPropertyDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnsSelectionDsl<*>.cols(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        this.asSingleColumn().cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[`[`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> ColumnsSelectionDsl<*>.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup`[`[`][SingleColumn.cols]`Type::colA, Type::colB`[`]`][SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias SingleColumnColsVarargKPropertyDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> SingleColumn<DataRow<*>>.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = colsInternal(listOf(firstCol, *otherCols).map { pathOf(it.name) }).cast()

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> SingleColumn<DataRow<*>>.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { "myColumnGroup".`[`cols`][String.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][DataFrame.select]` { "myColumnGroup"`[`[`][String.cols]`Type::colA, Type::colB`[`]`][String.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias StringColsVarargKPropertyDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"`[`[`][kotlin.String.cols]`Type::colA, Type::colB`[`]`][kotlin.String.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> String.cols(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        columnGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"`[`[`][kotlin.String.cols]`Type::colA, Type::colB`[`]`][kotlin.String.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> String.get(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][SingleColumn.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup`[`[`][SingleColumn.cols]`Type::colA, Type::colB`[`]`][SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias KPropertyColsVarargKPropertyDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.cols(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        columnGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> KProperty<*>.get(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][ColumnPath.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][ColumnPath.cols]`Type::colA, Type::colB`[`]`][ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias ColumnPathColsVarargKPropertyDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnPath.cols(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        columnGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [cols][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [`get`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[`remove`][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[`hasNulls`][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`String`][String]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`Type::colA, Type::colB`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [ColumnReference][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> ColumnPath.get(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        cols(firstCol, *otherCols)

    // endregion

    // region indices

    /**
     * ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][SingleColumn.colsOf]`<`[`Int`][Int]`>().`[`cols`][ColumnSet.cols]`(1, 3) }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`all`][ColumnsSelectionDsl.all]`()`[`[`][ColumnSet.cols]`5, 1`[`]`][ColumnSet.cols]` }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias ColumnSetColsIndicesDocs = Nothing

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`Int`][Int]`>().`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`(1, 3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`all`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`5, 1`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<C> =
        colsInternal(headPlusArray(firstIndex, otherIndices)) as ColumnSet<C>

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`Int`][Int]`>().`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`(1, 3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`all`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`5, 1`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public operator fun <C> ColumnSet<C>.get(firstIndex: Int, vararg otherIndices: Int): ColumnSet<C> =
        cols(firstIndex, *otherIndices)

    /**
     * ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`(1, 3) }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`<`[`String`][String]`>(1, 3) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias ColumnsSelectionDslColsIndicesDocs = Nothing

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1, 3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[`String`][String]`>(1, 3) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnsSelectionDsl<*>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<*> =
        cols<Any?>(firstIndex, *otherIndices)

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1, 3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[`String`][String]`>(1, 3) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public fun <T> ColumnsSelectionDsl<*>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<T> =
        this.asSingleColumn().colsInternal(headPlusArray(firstIndex, otherIndices)).cast()

    /**
     * ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`(1, 3) }`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`<`[`String`][String]`>(3, 4) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias SingleColumnColsIndicesDocs = Nothing

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[`String`][String]`>(3, 4) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun SingleColumn<DataRow<*>>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<*> =
        cols<Any?>(firstIndex, *otherIndices)

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[`String`][String]`>(3, 4) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public fun <T> SingleColumn<DataRow<*>>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<T> =
        this.ensureIsColumnGroup().colsInternal(headPlusArray(firstIndex, otherIndices)).cast()

    /**
     * ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { "myColumnGroup".`[`cols`][String.cols]`(5, 3, 1) }`
     *
     * `df.`[`select`][DataFrame.select]` { "myColumnGroup".`[`cols`][String.cols]`<`[`String`][String]`>(5, 3, 1) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias StringColsIndicesDocs = Nothing

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(5, 3, 1) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`<`[`String`][String]`>(5, 3, 1) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun String.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<*> =
        cols<Any?>(firstIndex, *otherIndices)

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(5, 3, 1) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`<`[`String`][String]`>(5, 3, 1) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public fun <T> String.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<T> =
        columnGroup(this).cols(firstIndex, *otherIndices).cast()

    /**
     * ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][SingleColumn.cols]`(5, 4) }`
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][SingleColumn.cols]`<`[`String`][String]`>(5, 4) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias KPropertyColsIndicesDocs = Nothing

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(5, 4) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[`String`][String]`>(5, 4) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<*> =
        cols<Any?>(firstIndex, *otherIndices)

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(5, 4) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[`String`][String]`>(5, 4) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <T> KProperty<*>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<T> =
        columnGroup(this).cols(firstIndex, *otherIndices).cast()

    /**
     * ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["myColGroup"].`[`cols`][ColumnPath.cols]`(0, 1) }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["myColGroup"].`[`cols`][ColumnPath.cols]`<`[`String`][String]`>(0, 1) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias ColumnPathColsIndicesDocs = Nothing

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`(0, 1) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`<`[`String`][String]`>(0, 1) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnPath.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<*> =
        cols<Any?>(firstIndex, *otherIndices)

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[`]`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[`cols`][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`(0, 1) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`<`[`String`][String]`>(0, 1) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public fun <T> ColumnPath.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<T> =
        columnGroup(this).cols(firstIndex, *otherIndices).cast()

    // endregion

    // region ranges

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`colsOf`][SingleColumn.colsOf]`<`[`Int`][Int]`>().`[`cols`][ColumnSet.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`all`][all]`()`[`[`][ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][ColumnSet.cols]` }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias ColumnSetColsRangeDocs = Nothing

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`Int`][Int]`>().`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`all`][org.jetbrains.kotlinx.dataframe.api.all]`()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(range: IntRange): ColumnSet<C> = colsInternal(range) as ColumnSet<C>

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`Int`][Int]`>().`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`all`][org.jetbrains.kotlinx.dataframe.api.all]`()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public operator fun <C> ColumnSet<C>.get(range: IntRange): ColumnSet<C> = cols(range)

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`<`[`String`][String]`>(1`[`..`][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias ColumnsSelectionDslColsRangeDocs = Nothing

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[`String`][String]`>(1`[`..`][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnsSelectionDsl<*>.cols(range: IntRange): ColumnSet<*> = cols<Any?>(range)

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[`String`][String]`>(1`[`..`][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public fun <T> ColumnsSelectionDsl<*>.cols(range: IntRange): ColumnSet<T> =
        this.asSingleColumn().colsInternal(range).cast()

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][DataFrame.select]` { myColumnGroup.`[`cols`][SingleColumn.cols]`<`[`String`][String]`>(1`[`..`][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias SingleColumnColsRangeDocs = Nothing

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[`String`][String]`>(1`[`..`][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun SingleColumn<DataRow<*>>.cols(range: IntRange): ColumnSet<*> = cols<Any?>(range)

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[`String`][String]`>(1`[`..`][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public fun <T> SingleColumn<DataRow<*>>.cols(range: IntRange): ColumnSet<T> =
        this.ensureIsColumnGroup().colsInternal(range).cast()

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { "myColGroup".`[`cols`][String.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][DataFrame.select]` { "myColGroup".`[`cols`][String.cols]`<`[`String`][String]`>(1`[`..`][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias StringColsRangeDocs = Nothing

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`<`[`String`][String]`>(1`[`..`][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun String.cols(range: IntRange): ColumnSet<*> = cols<Any?>(range)

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`<`[`String`][String]`>(1`[`..`][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public fun <T> String.cols(range: IntRange): ColumnSet<T> = columnGroup(this).cols(range).cast()

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][DataFrame.select]` { Type::myColumnGroup.`[`cols`][SingleColumn.cols]`<`[`String`][String]`>(1`[`..`][Int.rangeTo]`3) }`
     *
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias KPropertyColsRangeDocs = Nothing

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[`String`][String]`>(1`[`..`][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.cols(range: IntRange): ColumnSet<*> = cols<Any?>(range)

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[`String`][String]`>(1`[`..`][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <T> KProperty<*>.cols(range: IntRange): ColumnSet<T> = columnGroup(this).cols(range).cast()

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["myColGroup"].`[`cols`][ColumnPath.cols]`(0`[`..`][Int.rangeTo]`1) }`
     *
     * `df.`[`select`][DataFrame.select]` { "pathTo"["myColGroup"].`[`cols`][ColumnPath.cols]`<`[`String`][String]`>(0`[`..`][Int.rangeTo]`1) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias ColumnPathColsRangeDocs = Nothing

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`(0`[`..`][Int.rangeTo]`1) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`<`[`String`][String]`>(0`[`..`][Int.rangeTo]`1) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnPath.cols(range: IntRange): ColumnSet<*> = cols<Any?>(range)

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [IndexOutOfBoundsException] is thrown.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[`..`][Int.rangeTo]`3) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`colsOf`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[`Int`][Int]`>()`[`[`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[`..`][Int.rangeTo]`5`[`]`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[`cols`][kotlin.String.cols]`(0`[`..`][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`(0`[`..`][Int.rangeTo]`1) }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[`cols`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`<`[`String`][String]`>(0`[`..`][Int.rangeTo]`1) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [IntRange].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
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
