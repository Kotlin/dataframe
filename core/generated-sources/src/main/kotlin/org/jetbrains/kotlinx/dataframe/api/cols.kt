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
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
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
 * ## Cols [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 * @param _UNUSED [#KT-68546](https://youtrack.jetbrains.com/issue/KT-68546/Conflicting-overloads-in-non-generic-interface-K2-2.0.0)
 */
public interface ColsColumnsSelectionDsl<out _UNUSED> {

    /**
     * ## Cols Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[<code>`ColumnSet`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[<code>`SingleColumn`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[<code>`DataRow`</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `column: `[<code>`ColumnAccessor`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `index: `[<code>`Int`</code>][Int]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[<code>`ColumnFilter`</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `T: Column type`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `indexRange: `[<code>`IntRange`</code>][IntRange]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called directly in the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>**`cols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`[`**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**`  .. |  `[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`,`**`  .. |  `[<code>`indexRange`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexRangeDef]**`)`**
     *
     *  `| `[<code>**`cols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  [  `**`  {  `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**`  }  `**`]`
     *
     *  `| `**`this`**`/`**`it `**[<code>**`[`**</code>][cols]**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**[<code>**`]`**</code>][cols]
     *
     *  `| `**`this`**`/`**`it `**[<code>**`[`**</code>][cols][`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**`  ..  `[<code>**`]`**</code>][cols]
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`cols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]**`(`**[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`,`**`  .. |  `[<code>`indexRange`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexRangeDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`cols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  [  `**`  {  `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**`  }  `**`]`
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `[<code>**`[`**</code>][cols]**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**[<code>**`]`**</code>][cols]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `[<code>**`[`**</code>][cols][`index`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`,`**`  .. |  `[<code>`indexRange`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexRangeDef][**`]`**][cols]`
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>Column Group (reference)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnGroup`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`cols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`[`**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**`  .. |  `[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`,`**`  .. |  `[<code>`indexRange`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexRangeDef]**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`cols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  [  `**`  {  `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**`  }  `**`]`
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `[<code>**`[`**</code>][cols]**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**[<code>**`]`**</code>][cols]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `[<code>**`[`**</code>][cols][`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**` ..`[<code>**`]`**</code>][cols]
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

        /** [<code>**`cols`**</code>][ColumnsSelectionDsl.cols] */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`cols`**</code>][ColumnsSelectionDsl.cols] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`cols`**</code>][ColumnsSelectionDsl.cols] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][cols] directly, you can also use the [<code>`get`</code>][ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][DataFrame.remove]`  {  `[<code>`cols`</code>][ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`colsOf`</code>][ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     *
     *
     */
    private interface CommonColsDocs {

        /**
         * ## Cols
         * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
         *
         * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
         * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
         * a column name, -path, or index (range)).
         *
         * This function operates solely on columns at the top-level.
         *
         * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
         *
         * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
         *
         * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
         *
         * #### For example:
         * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
         *
         * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
         *
         * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
         *
         * #### Examples for this overload:
         *
         *
         *
         *
         * #### Filter vs. Cols:
         * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][ColumnsSelectionDsl.filter].
         * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][ColumnSet] and
         * `cols {}` on the rest.
         *
         * @param [predicate] A [<code>ColumnFilter function</code>][ColumnFilter] that takes a [<code>ColumnReference</code>][ColumnReference] and returns a [<code>Boolean</code>][Boolean].
         * @return A [<code>ColumnSet</code>][ColumnSet] containing the columns that match the given [predicate].
         * @see [ColumnsSelectionDsl.filter]
         * @see [ColumnsSelectionDsl.colsOfKind]
         * @see [ColumnsSelectionDsl.valueCols]
         * @see [ColumnsSelectionDsl.frameCols]
         * @see [ColumnsSelectionDsl.colGroups]
         */
        typealias Predicate = Nothing

        /**
         * ## Cols
         * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
         *
         * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
         * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
         * a column name, -path, or index (range)).
         *
         * This function operates solely on columns at the top-level.
         *
         * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
         *
         * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
         *
         * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
         *
         * #### For example:
         * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
         *
         * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
         *
         * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
         *
         * #### Examples for this overload:
         *
         *
         *
         *
         * @param [firstCol] A  that points to a relative column.
         * @param [otherCols] Optional additional s that point to relative columns.
         * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][ColumnReference]s point to a column that doesn't
         *   exist.
         * @return A [<code>ColumnSet</code>][ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
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
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`cols`</code>][SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`colsOf`</code>][ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][SingleColumn.get]`5, 1, 2`[<code>`]`</code>][SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][ColumnSet] containing the columns found at the given indices.
     */
    private interface CommonColsIndicesDocs {

        /** Example argument */
        typealias EXAMPLE = Nothing
    }

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`cols`</code>][SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`colsOf`</code>][ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][ColumnSet] containing the columns found at the given indices.
     */
    private interface CommonColsRangeDocs {

        /** Example argument */
        typealias EXAMPLE = Nothing
    }

    // region predicate

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `// although these can be shortened to just the `[<code>`colsOf<>{ }`</code>][ColumnsSelectionDsl.colsOf]` call`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`colsOf`</code>][ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>().`[<code>`cols`</code>][ColumnSet.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][ColumnPath.name]`() } }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`colsOf`</code>][ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][ColumnSet.cols]`{ it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } }`[<code>`]`</code>][ColumnSet.cols]` }`
     *
     * `// identity call, same as `[<code>`all`</code>][ColumnsSelectionDsl.all]`()`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`colsOf`</code>][ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>().`[<code>`cols`</code>][ColumnSet.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
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
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `// although these can be shortened to just the `[<code>`colsOf<>{ }`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]` call`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>().`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`{ it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } }`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `// identity call, same as `[<code>`all`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`()`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>().`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.all]
     * @see [ColumnsSelectionDsl.filter] */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(predicate: (ColumnWithPath<C>) -> Boolean): ColumnSet<C> =
        colsInternal(predicate as ColumnFilter<*>).cast()

    @Deprecated(COLS_TO_ALL, ReplaceWith(COLS_TO_ALL_REPLACE), DeprecationLevel.ERROR)
    public fun <C> ColumnSet<C>.cols(): ColumnSet<C> = cols { true }

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `// although these can be shortened to just the `[<code>`colsOf<>{ }`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]` call`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>().`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`{ it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } }`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `// identity call, same as `[<code>`all`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`()`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>().`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.all]
     * @see [ColumnsSelectionDsl.filter] */
    public operator fun <C> ColumnSet<C>.get(predicate: (ColumnWithPath<C>) -> Boolean = { true }): ColumnSet<C> =
        cols(predicate)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`cols`</code>][ColumnsSelectionDsl.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][ColumnPath.name]`() } }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { this`[<code>`[`</code>][ColumnsSelectionDsl.cols]`{ it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } }`[<code>`]`</code>][ColumnsSelectionDsl.cols]` }`
     *
     * `// same as `[<code>`all`</code>][ColumnsSelectionDsl.all]`()`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`cols`</code>][ColumnsSelectionDsl.cols]`() }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.all]
     */
    private typealias ColumnsSelectionDslColsPredicateDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`{ it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } }`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `// same as `[<code>`all`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`()`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.all] */
    public fun ColumnsSelectionDsl<*>.cols(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        this.asSingleColumn().colsInternal(predicate)

    @Deprecated(COLS_TO_ALL, ReplaceWith(COLS_TO_ALL_REPLACE), DeprecationLevel.ERROR)
    public fun ColumnsSelectionDsl<*>.cols(): ColumnSet<*> = cols { true }

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`{ it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } }`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     * `// same as `[<code>`all`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`()`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`() }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.all] */
    public operator fun ColumnsSelectionDsl<*>.get(predicate: (ColumnWithPath<*>) -> Boolean = { true }): ColumnSet<*> =
        cols(predicate)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myColumnGroup`.[<code>`cols`</code>][SingleColumn.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][ColumnPath.name]`() } }`
     *
     * `// same as `[<code>`allCols`</code>][ColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][SingleColumn.cols]`() }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myColumnGroup`[<code>`[`</code>][SingleColumn.cols]`{ ... }`[<code>`]`</code>][SingleColumn.cols]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.allCols]
     */
    private typealias SingleColumnAnyRowColsPredicateDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`.[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `// same as `[<code>`allCols`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`() }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`{ ... }`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.allCols] */
    public fun SingleColumn<DataRow<*>>.cols(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal(predicate)

    @Deprecated(COLS_TO_ALL_COLS, ReplaceWith(COLS_TO_ALL_COLS_REPLACE), DeprecationLevel.ERROR)
    public fun SingleColumn<DataRow<*>>.cols(): ColumnSet<*> = cols { true }

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`.[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `// same as `[<code>`allCols`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`() }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`{ ... }`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.allCols]
     */
    public operator fun SingleColumn<DataRow<*>>.get(
        predicate: (ColumnWithPath<*>) -> Boolean = { true },
    ): ColumnSet<*> = cols(predicate)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "myGroupCol".`[<code>`cols`</code>][String.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][ColumnPath.name]`() } }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "myGroupCol"`[<code>`[`</code>][String.cols]`{ it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } }`[<code>`]`</code>][String.cols]` }`
     *
     * `// same as `[<code>`allCols`</code>][ColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "myGroupCol".`[<code>`cols`</code>][String.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     */
    private typealias StringColsPredicateDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol".`[<code>`cols`</code>][kotlin.String.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol"`[<code>`[`</code>][kotlin.String.cols]`{ it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } }`[<code>`]`</code>][kotlin.String.cols]` }`
     *
     * `// same as `[<code>`allCols`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol".`[<code>`cols`</code>][kotlin.String.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     */
    public fun String.cols(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> = columnGroup(this).cols(predicate)

    @Deprecated(COLS_TO_ALL_COLS, ReplaceWith(COLS_TO_ALL_COLS_REPLACE), DeprecationLevel.ERROR)
    public fun String.cols(): ColumnSet<*> = cols { true }

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol".`[<code>`cols`</code>][kotlin.String.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol"`[<code>`[`</code>][kotlin.String.cols]`{ it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } }`[<code>`]`</code>][kotlin.String.cols]` }`
     *
     * `// same as `[<code>`allCols`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myGroupCol".`[<code>`cols`</code>][kotlin.String.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     */
    public operator fun String.get(predicate: (ColumnWithPath<*>) -> Boolean = { true }): ColumnSet<*> = cols(predicate)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { Type::columnGroup.`[<code>`cols`</code>][KProperty.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][ColumnPath.name]`() } }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { Type::columnGroup`[<code>`[`</code>][SingleColumn.cols]`{ it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } }`[<code>`]`</code>][SingleColumn.cols]` }`
     *
     * `// same as `[<code>`allCols`</code>][ColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { Type::columnGroup.`[<code>`cols`</code>][SingleColumn.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.allCols]
     */
    private typealias KPropertyColsPredicateDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnGroup.`[<code>`cols`</code>][kotlin.reflect.KProperty.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnGroup`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`{ it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } }`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     * `// same as `[<code>`allCols`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.allCols] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.cols(predicate: (ColumnWithPath<*>) -> Boolean = { true }): ColumnSet<*> =
        columnGroup(this).cols(predicate)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnGroup.`[<code>`cols`</code>][kotlin.reflect.KProperty.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnGroup`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`{ it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } }`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     * `// same as `[<code>`allCols`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`()`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::columnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`() }`
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     * @see [ColumnsSelectionDsl.allCols] */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun KProperty<*>.get(predicate: (ColumnWithPath<*>) -> Boolean = { true }): ColumnSet<*> =
        cols(predicate)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>`cols`</code>][ColumnPath.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][ColumnPath.name]`() } }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["myGroupCol"]`[<code>`[`</code>][ColumnPath.cols]`{ it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } }`[<code>`]`</code>][ColumnPath.cols]` }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>`cols`</code>][ColumnPath.cols]`() } // identity call, same as `[<code>`allCols`</code>][ColumnsSelectionDsl.allCols]
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     */
    private typealias ColumnPathPredicateDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"]`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`{ it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } }`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`() } // identity call, same as `[<code>`allCols`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     */
    public fun ColumnPath.cols(predicate: (ColumnWithPath<*>) -> Boolean): ColumnSet<*> =
        columnGroup(this).cols(predicate)

    @Deprecated(COLS_TO_ALL_COLS, ReplaceWith(COLS_TO_ALL_COLS_REPLACE), DeprecationLevel.ERROR)
    public fun ColumnPath.cols(): ColumnSet<*> = cols { true }

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`  { "e"  `[`in`][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.name]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"]`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`{ it.`[<code>`any`</code>][ColumnWithPath.any]` { it == "Alice" } }`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`() } // identity call, same as `[<code>`allCols`</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]
     *
     *
     * #### Filter vs. Cols:
     * If used with a [predicate], `cols {}` functions exactly like [<code>`filter {}`</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter].
     * This is intentional, however; it is recommended to use `filter {}` on [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] and
     * `cols {}` on the rest.
     *
     * @param [predicate] A [<code>ColumnFilter function</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter] that takes a [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] and returns a [<code>Boolean</code>][Boolean].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that match the given [predicate].
     * @see [ColumnsSelectionDsl.filter]
     * @see [ColumnsSelectionDsl.colsOfKind]
     * @see [ColumnsSelectionDsl.valueCols]
     * @see [ColumnsSelectionDsl.frameCols]
     * @see [ColumnsSelectionDsl.colGroups]
     */
    public operator fun ColumnPath.get(predicate: (ColumnWithPath<*>) -> Boolean = { true }): ColumnSet<*> =
        cols(predicate)

    // endregion

    // region references

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`cols`</code>][ColumnsSelectionDsl.cols]`(colGroup.columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { this`[<code>`[`</code>][ColumnsSelectionDsl.cols]`colGroup.columnA, columnB`[<code>`]`</code>][ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnReference</code>][ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnReference</code>][ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias ColumnsSelectionDslColsVarargColumnReferenceDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup.columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`colGroup.columnA, columnB`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Interpretable("Cols0")
    public fun <C> ColumnsSelectionDsl<*>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = asSingleColumn().cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup.columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`colGroup.columnA, columnB`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun <C> ColumnsSelectionDsl<*>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myColumnGroup`[<code>`[`</code>][SingleColumn.cols]`columnA, columnB`[<code>`]`</code>][SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnReference</code>][ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnReference</code>][ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias SingleColumnColsVarargColumnReferenceDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`columnA, columnB`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <C> SingleColumn<DataRow<*>>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = colsInternal(listOf(firstCol, *otherCols)).cast()

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`columnA, columnB`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun <C> SingleColumn<DataRow<*>>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][String.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "myColumnGroup"`[<code>`[`</code>][String.cols]`columnA, columnB`[<code>`]`</code>][String.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnReference</code>][ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnReference</code>][ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias StringColsVarargColumnReferenceDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"`[<code>`[`</code>][kotlin.String.cols]`columnA, columnB`[<code>`]`</code>][kotlin.String.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <C> String.cols(firstCol: ColumnReference<C>, vararg otherCols: ColumnReference<C>): ColumnSet<C> =
        columnGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"`[<code>`[`</code>][kotlin.String.cols]`columnA, columnB`[<code>`]`</code>][kotlin.String.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun <C> String.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { DataSchemaType::myColumnGroup`[<code>`[`</code>][KProperty.cols]`"pathTo"["colA"], "pathTo"["colB"]`[<code>`]`</code>][KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnReference</code>][ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnReference</code>][ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias KPropertyColsVarargColumnReferenceDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup`[<code>`[`</code>][kotlin.reflect.KProperty.cols]`"pathTo"["colA"], "pathTo"["colB"]`[<code>`]`</code>][kotlin.reflect.KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.cols(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = columnGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup`[<code>`[`</code>][kotlin.reflect.KProperty.cols]`"pathTo"["colA"], "pathTo"["colB"]`[<code>`]`</code>][kotlin.reflect.KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> KProperty<*>.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][ColumnPath.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["columnGroup"]`[<code>`[`</code>][ColumnPath.cols]`columnA, columnB`[<code>`]`</code>][ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnReference</code>][ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnReference</code>][ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias ColumnPathColsVarargColumnReferenceDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`columnA, columnB`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <C> ColumnPath.cols(firstCol: ColumnReference<C>, vararg otherCols: ColumnReference<C>): ColumnSet<C> =
        columnGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"]) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`columnA, columnB`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun <C> ColumnPath.get(
        firstCol: ColumnReference<C>,
        vararg otherCols: ColumnReference<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    // endregion

    // region names

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`cols`</code>][ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`cols`</code>][ColumnsSelectionDsl.cols]`<`[<code>`String`</code>][String]`>("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { this`[<code>`[`</code>][ColumnsSelectionDsl.cols]`"columnA", "columnB"`[<code>`]`</code>][ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias ColumnsSelectionDslVarargStringDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[<code>`String`</code>][String]`>("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnsSelectionDsl<*>.cols(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[<code>`String`</code>][String]`>("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <T> ColumnsSelectionDsl<*>.cols(firstCol: String, vararg otherCols: String): ColumnSet<T> =
        this.asSingleColumn().cols(firstCol, *otherCols).cast()

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[<code>`String`</code>][String]`>("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"columnA", "columnB"`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun ColumnsSelectionDsl<*>.get(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][SingleColumn.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myColumnGroup`[<code>`[`</code>][SingleColumn.cols]`"columnA", "columnB"`[<code>`]`</code>][SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias SingleColumnColsVarargStringDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`"columnA", "columnB"`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun SingleColumn<DataRow<*>>.cols(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`"columnA", "columnB"`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <T> SingleColumn<DataRow<*>>.cols(firstCol: String, vararg otherCols: String): ColumnSet<T> =
        colsInternal(listOf(firstCol, *otherCols).map { pathOf(it) }).cast()

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`"columnA", "columnB"`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun SingleColumn<DataRow<*>>.get(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "columnGroup".`[<code>`cols`</code>][String.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "columnGroup"`[<code>`[`</code>][String.cols]`"columnA", "columnB"`[<code>`]`</code>][String.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias StringColsVarargStringDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[<code>`cols`</code>][kotlin.String.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup"`[<code>`[`</code>][kotlin.String.cols]`"columnA", "columnB"`[<code>`]`</code>][kotlin.String.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun String.cols(firstCol: String, vararg otherCols: String): ColumnSet<*> = cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[<code>`cols`</code>][kotlin.String.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup"`[<code>`[`</code>][kotlin.String.cols]`"columnA", "columnB"`[<code>`]`</code>][kotlin.String.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <T> String.cols(firstCol: String, vararg otherCols: String): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[<code>`cols`</code>][kotlin.String.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup"`[<code>`[`</code>][kotlin.String.cols]`"columnA", "columnB"`[<code>`]`</code>][kotlin.String.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun String.get(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][KProperty.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { DataSchemaType::myColumnGroup`[<code>`[`</code>][KProperty.cols]`"columnA", "columnB"`[<code>`]`</code>][KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias KPropertiesColsVarargStringDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][kotlin.reflect.KProperty.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup`[<code>`[`</code>][kotlin.reflect.KProperty.cols]`"columnA", "columnB"`[<code>`]`</code>][kotlin.reflect.KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.cols(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][kotlin.reflect.KProperty.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup`[<code>`[`</code>][kotlin.reflect.KProperty.cols]`"columnA", "columnB"`[<code>`]`</code>][kotlin.reflect.KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <T> KProperty<*>.cols(firstCol: String, vararg otherCols: String): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][kotlin.reflect.KProperty.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup`[<code>`[`</code>][kotlin.reflect.KProperty.cols]`"columnA", "columnB"`[<code>`]`</code>][kotlin.reflect.KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun KProperty<*>.get(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][ColumnPath.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["columnGroup"]`[<code>`[`</code>][ColumnPath.cols]`"columnA", "columnB"`[<code>`]`</code>][ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias ColumnPathColsVarargStringDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`"columnA", "columnB"`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnPath.cols(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`"columnA", "columnB"`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <T> ColumnPath.cols(firstCol: String, vararg otherCols: String): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`("columnA", "columnB") }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`"columnA", "columnB"`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun ColumnPath.get(firstCol: String, vararg otherCols: String): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    // endregion

    // region paths

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`cols`</code>][ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`cols`</code>][ColumnsSelectionDsl.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { this`[<code>`[`</code>][ColumnsSelectionDsl.cols]`"pathTo"["colA"], "pathTo"["colB"])`[<code>`]`</code>][ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias ColumnsSelectionDslVarargColumnPathDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"pathTo"["colA"], "pathTo"["colB"])`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnsSelectionDsl<*>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"pathTo"["colA"], "pathTo"["colB"])`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <T> ColumnsSelectionDsl<*>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<T> =
        asSingleColumn().cols<T>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`"pathTo"["colA"], "pathTo"["colB"])`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [<code>String</code>][String] that points to a relative column.
     * @param [otherCols] Optional additional [<code>String</code>][String]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun ColumnsSelectionDsl<*>.get(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][SingleColumn.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][SingleColumn.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myColumnGroup`[<code>`[`</code>][SingleColumn.cols]`"pathTo"["colA"], "pathTo"["colB"])`[<code>`]`</code>][SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnPath</code>][ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnPath</code>][ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias SingleColumnColsVarargColumnPathDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`"pathTo"["colA"], "pathTo"["colB"])`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun SingleColumn<DataRow<*>>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`"pathTo"["colA"], "pathTo"["colB"])`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <T> SingleColumn<DataRow<*>>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<T> =
        colsInternal(listOf(firstCol, *otherCols)).cast()

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`"pathTo"["colA"], "pathTo"["colB"])`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun SingleColumn<DataRow<*>>.get(
        firstCol: ColumnPath,
        vararg otherCols: ColumnPath,
    ): ColumnSet<*> = cols<Any?>(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "columnGroup".`[<code>`cols`</code>][String.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "columnGroup".`[<code>`cols`</code>][String.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "columnGroup"`[<code>`[`</code>][String.cols]`""pathTo"["colA"], "pathTo"["colB"])`[<code>`]`</code>][ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnPath</code>][ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnPath</code>][ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias StringColsVarargColumnPathDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[<code>`cols`</code>][kotlin.String.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[<code>`cols`</code>][kotlin.String.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup"`[<code>`[`</code>][kotlin.String.cols]`""pathTo"["colA"], "pathTo"["colB"])`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun String.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[<code>`cols`</code>][kotlin.String.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[<code>`cols`</code>][kotlin.String.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup"`[<code>`[`</code>][kotlin.String.cols]`""pathTo"["colA"], "pathTo"["colB"])`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <T> String.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[<code>`cols`</code>][kotlin.String.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup".`[<code>`cols`</code>][kotlin.String.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "columnGroup"`[<code>`[`</code>][kotlin.String.cols]`""pathTo"["colA"], "pathTo"["colB"])`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun String.get(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][KProperty.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][KProperty.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { DataSchemaType::myColumnGroup`[<code>`[`</code>][KProperty.cols]`"columnA", "columnB"`[<code>`]`</code>][KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnPath</code>][ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnPath</code>][ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias KPropertiesColsVarargColumnPathDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][kotlin.reflect.KProperty.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][kotlin.reflect.KProperty.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup`[<code>`[`</code>][kotlin.reflect.KProperty.cols]`"columnA", "columnB"`[<code>`]`</code>][kotlin.reflect.KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][kotlin.reflect.KProperty.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][kotlin.reflect.KProperty.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup`[<code>`[`</code>][kotlin.reflect.KProperty.cols]`"columnA", "columnB"`[<code>`]`</code>][kotlin.reflect.KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <T> KProperty<*>.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][kotlin.reflect.KProperty.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][kotlin.reflect.KProperty.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { DataSchemaType::myColumnGroup`[<code>`[`</code>][kotlin.reflect.KProperty.cols]`"columnA", "columnB"`[<code>`]`</code>][kotlin.reflect.KProperty.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun KProperty<*>.get(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][ColumnPath.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["columnGroup"]`[<code>`[`</code>][ColumnPath.cols]`"pathTo"["colA"], "pathTo"["colB"])`[<code>`]`</code>][ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnPath</code>][ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnPath</code>][ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias ColumnPathColsVarargColumnPathDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`"pathTo"["colA"], "pathTo"["colB"])`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnPath.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`"pathTo"["colA"], "pathTo"["colB"])`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public fun <T> ColumnPath.cols(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<T> =
        columnGroup(this).cols(firstCol, *otherCols).cast()

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`<`[<code>`String`</code>][String]`>("pathTo"["colA"], "pathTo"["colB"])) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`"pathTo"["colA"], "pathTo"["colB"])`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] that points to a relative column.
     * @param [otherCols] Optional additional [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    public operator fun ColumnPath.get(firstCol: ColumnPath, vararg otherCols: ColumnPath): ColumnSet<*> =
        cols<Any?>(firstCol, *otherCols)

    // endregion

    // region properties

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`cols`</code>][ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { this`[<code>`[`</code>][ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[<code>`]`</code>][ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [<code>KProperty</code>][KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [<code>KProperty</code>][KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias ColumnsSelectionDslColsVarargKPropertyDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [<code>KProperty</code>][KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [<code>KProperty</code>][KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnsSelectionDsl<*>.cols(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        this.asSingleColumn().cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { this`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`Type::colA, Type::colB`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` }`
     *
     *
     * @param [firstCol] A [<code>KProperty</code>][KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [<code>KProperty</code>][KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> ColumnsSelectionDsl<*>.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][SingleColumn.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myColumnGroup`[<code>`[`</code>][SingleColumn.cols]`Type::colA, Type::colB`[<code>`]`</code>][SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>KProperty</code>][KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [<code>KProperty</code>][KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias SingleColumnColsVarargKPropertyDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`Type::colA, Type::colB`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>KProperty</code>][KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [<code>KProperty</code>][KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> SingleColumn<DataRow<*>>.cols(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = colsInternal(listOf(firstCol, *otherCols).map { pathOf(it.name) }).cast()

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`Type::colA, Type::colB`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>KProperty</code>][KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [<code>KProperty</code>][KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> SingleColumn<DataRow<*>>.get(
        firstCol: KProperty<C>,
        vararg otherCols: KProperty<C>,
    ): ColumnSet<C> = cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][String.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "myColumnGroup"`[<code>`[`</code>][String.cols]`Type::colA, Type::colB`[<code>`]`</code>][String.cols]` }`
     *
     *
     * @param [firstCol] A [<code>KProperty</code>][KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [<code>KProperty</code>][KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias StringColsVarargKPropertyDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"`[<code>`[`</code>][kotlin.String.cols]`Type::colA, Type::colB`[<code>`]`</code>][kotlin.String.cols]` }`
     *
     *
     * @param [firstCol] A [<code>KProperty</code>][KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [<code>KProperty</code>][KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> String.cols(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        columnGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"`[<code>`[`</code>][kotlin.String.cols]`Type::colA, Type::colB`[<code>`]`</code>][kotlin.String.cols]` }`
     *
     *
     * @param [firstCol] A [<code>KProperty</code>][KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [<code>KProperty</code>][KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> String.get(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][SingleColumn.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { Type::myColumnGroup`[<code>`[`</code>][SingleColumn.cols]`Type::colA, Type::colB`[<code>`]`</code>][SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>KProperty</code>][KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [<code>KProperty</code>][KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias KPropertyColsVarargKPropertyDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`Type::colA, Type::colB`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>KProperty</code>][KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [<code>KProperty</code>][KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.cols(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        columnGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`Type::colA, Type::colB`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]` }`
     *
     *
     * @param [firstCol] A [<code>KProperty</code>][KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [<code>KProperty</code>][KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C> KProperty<*>.get(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        cols(firstCol, *otherCols)

    /**
     * ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][ColumnPath.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["columnGroup"]`[<code>`[`</code>][ColumnPath.cols]`Type::colA, Type::colB`[<code>`]`</code>][ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>KProperty</code>][KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [<code>KProperty</code>][KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    private typealias ColumnPathColsVarargKPropertyDocs = Nothing

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`Type::colA, Type::colB`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>KProperty</code>][KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [<code>KProperty</code>][KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnPath.cols(firstCol: KProperty<C>, vararg otherCols: KProperty<C>): ColumnSet<C> =
        columnGroup(this).cols(firstCol, *otherCols)

    /** ## Cols
     * Creates a subset of columns ([<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]) from [this].
     *
     * You can use either a [<code>ColumnFilter</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter], or any of the `vararg` overloads for any
     * [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]. The function can be both typed and untyped (in case you're supplying
     * a column name, -path, or index (range)).
     *
     * This function operates solely on columns at the top-level.
     *
     * Aside from calling [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.cols] directly, you can also use the [<code>`get`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.get] operator in most cases.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     * `df.`[<code>`remove`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.remove]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[<code>`hasNulls`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.hasNulls]`() } }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myGroupCol.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(columnA, columnB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`String`</code>][String]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1, 3, 5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`(Type::colA, Type::colB) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["columnGroup"]`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`Type::colA, Type::colB`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]` }`
     *
     *
     * @param [firstCol] A [<code>KProperty</code>][KProperty] that points to a relative column.
     * @param [otherCols] Optional additional [<code>KProperty</code>][KProperty]s that point to relative columns.
     * @throws [IllegalArgumentException] if any of the given [<code>ColumnReference</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnReference]s point to a column that doesn't
     *   exist.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns that [firstCol] and [otherCols] point to.
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
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`colsOf`</code>][SingleColumn.colsOf]`<`[<code>`Int`</code>][Int]`>().`[<code>`cols`</code>][ColumnSet.cols]`(1, 3) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`all`</code>][ColumnsSelectionDsl.all]`()`[<code>`[`</code>][ColumnSet.cols]`5, 1`[<code>`]`</code>][ColumnSet.cols]` }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias ColumnSetColsIndicesDocs = Nothing

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`Int`</code>][Int]`>().`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`(1, 3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`all`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`5, 1`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<C> =
        colsInternal(headPlusArray(firstIndex, otherIndices)) as ColumnSet<C>

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`Int`</code>][Int]`>().`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`(1, 3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`all`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`5, 1`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public operator fun <C> ColumnSet<C>.get(firstIndex: Int, vararg otherIndices: Int): ColumnSet<C> =
        cols(firstIndex, *otherIndices)

    /**
     * ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`cols`</code>][ColumnsSelectionDsl.cols]`(1, 3) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`cols`</code>][ColumnsSelectionDsl.cols]`<`[<code>`String`</code>][String]`>(1, 3) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias ColumnsSelectionDslColsIndicesDocs = Nothing

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1, 3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[<code>`String`</code>][String]`>(1, 3) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnsSelectionDsl<*>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<*> =
        cols<Any?>(firstIndex, *otherIndices)

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1, 3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[<code>`String`</code>][String]`>(1, 3) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public fun <T> ColumnsSelectionDsl<*>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<T> =
        this.asSingleColumn().colsInternal(headPlusArray(firstIndex, otherIndices)).cast()

    /**
     * ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][SingleColumn.cols]`(1, 3) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][SingleColumn.cols]`<`[<code>`String`</code>][String]`>(3, 4) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias SingleColumnColsIndicesDocs = Nothing

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[<code>`String`</code>][String]`>(3, 4) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun SingleColumn<DataRow<*>>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<*> =
        cols<Any?>(firstIndex, *otherIndices)

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[<code>`String`</code>][String]`>(3, 4) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public fun <T> SingleColumn<DataRow<*>>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<T> =
        this.ensureIsColumnGroup().colsInternal(headPlusArray(firstIndex, otherIndices)).cast()

    /**
     * ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][String.cols]`(5, 3, 1) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][String.cols]`<`[<code>`String`</code>][String]`>(5, 3, 1) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias StringColsIndicesDocs = Nothing

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(5, 3, 1) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`<`[<code>`String`</code>][String]`>(5, 3, 1) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun String.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<*> =
        cols<Any?>(firstIndex, *otherIndices)

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(5, 3, 1) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`<`[<code>`String`</code>][String]`>(5, 3, 1) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public fun <T> String.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<T> =
        columnGroup(this).cols(firstIndex, *otherIndices).cast()

    /**
     * ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][SingleColumn.cols]`(5, 4) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][SingleColumn.cols]`<`[<code>`String`</code>][String]`>(5, 4) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias KPropertyColsIndicesDocs = Nothing

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(5, 4) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[<code>`String`</code>][String]`>(5, 4) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<*> =
        cols<Any?>(firstIndex, *otherIndices)

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(5, 4) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[<code>`String`</code>][String]`>(5, 4) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <T> KProperty<*>.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<T> =
        columnGroup(this).cols(firstIndex, *otherIndices).cast()

    /**
     * ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["myColGroup"].`[<code>`cols`</code>][ColumnPath.cols]`(0, 1) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["myColGroup"].`[<code>`cols`</code>][ColumnPath.cols]`<`[<code>`String`</code>][String]`>(0, 1) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias ColumnPathColsIndicesDocs = Nothing

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`(0, 1) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`<`[<code>`String`</code>][String]`>(0, 1) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnPath.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<*> =
        cols<Any?>(firstIndex, *otherIndices)

    /** ## Cols: Columns by Indices
     *
     * Retrieves one or multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by their indices.
     * If any of the indices are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1, 3, 2) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]`5, 1, 2`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.get]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0, 2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`(0, 1) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`<`[<code>`String`</code>][String]`>(0, 1) }`
     *
     * @throws [IndexOutOfBoundsException] If any index is out of bounds.
     * @param [firstIndex] The index of the first column to retrieve.
     * @param [otherIndices] The other indices of the columns to retrieve.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public fun <T> ColumnPath.cols(firstIndex: Int, vararg otherIndices: Int): ColumnSet<T> =
        columnGroup(this).cols(firstIndex, *otherIndices).cast()

    // endregion

    // region ranges

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`colsOf`</code>][SingleColumn.colsOf]`<`[<code>`Int`</code>][Int]`>().`[<code>`cols`</code>][ColumnSet.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`all`</code>][all]`()`[<code>`[`</code>][ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][ColumnSet.cols]` }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias ColumnSetColsRangeDocs = Nothing

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`Int`</code>][Int]`>().`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`all`</code>][org.jetbrains.kotlinx.dataframe.api.all]`()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.cols(range: IntRange): ColumnSet<C> = colsInternal(range) as ColumnSet<C>

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`Int`</code>][Int]`>().`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`all`</code>][org.jetbrains.kotlinx.dataframe.api.all]`()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public operator fun <C> ColumnSet<C>.get(range: IntRange): ColumnSet<C> = cols(range)

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`cols`</code>][ColumnsSelectionDsl.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  {  `[<code>`cols`</code>][ColumnsSelectionDsl.cols]`<`[<code>`String`</code>][String]`>(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias ColumnsSelectionDslColsRangeDocs = Nothing

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[<code>`String`</code>][String]`>(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnsSelectionDsl<*>.cols(range: IntRange): ColumnSet<*> = cols<Any?>(range)

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`<`[<code>`String`</code>][String]`>(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public fun <T> ColumnsSelectionDsl<*>.cols(range: IntRange): ColumnSet<T> =
        this.asSingleColumn().colsInternal(range).cast()

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][SingleColumn.cols]`<`[<code>`String`</code>][String]`>(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias SingleColumnColsRangeDocs = Nothing

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[<code>`String`</code>][String]`>(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun SingleColumn<DataRow<*>>.cols(range: IntRange): ColumnSet<*> = cols<Any?>(range)

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[<code>`String`</code>][String]`>(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public fun <T> SingleColumn<DataRow<*>>.cols(range: IntRange): ColumnSet<T> =
        this.ensureIsColumnGroup().colsInternal(range).cast()

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][String.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][String.cols]`<`[<code>`String`</code>][String]`>(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias StringColsRangeDocs = Nothing

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`<`[<code>`String`</code>][String]`>(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun String.cols(range: IntRange): ColumnSet<*> = cols<Any?>(range)

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`<`[<code>`String`</code>][String]`>(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    public fun <T> String.cols(range: IntRange): ColumnSet<T> = columnGroup(this).cols(range).cast()

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][SingleColumn.cols]`<`[<code>`String`</code>][String]`>(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias KPropertyColsRangeDocs = Nothing

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[<code>`String`</code>][String]`>(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.cols(range: IntRange): ColumnSet<*> = cols<Any?>(range)

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`<`[<code>`String`</code>][String]`>(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <T> KProperty<*>.cols(range: IntRange): ColumnSet<T> = columnGroup(this).cols(range).cast()

    /**
     * ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["myColGroup"].`[<code>`cols`</code>][ColumnPath.cols]`(0`[<code>`..`</code>][Int.rangeTo]`1) }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` { "pathTo"["myColGroup"].`[<code>`cols`</code>][ColumnPath.cols]`<`[<code>`String`</code>][String]`>(0`[<code>`..`</code>][Int.rangeTo]`1) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    private typealias ColumnPathColsRangeDocs = Nothing

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`(0`[<code>`..`</code>][Int.rangeTo]`1) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`<`[<code>`String`</code>][String]`>(0`[<code>`..`</code>][Int.rangeTo]`1) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colsUnTyped")
    public fun ColumnPath.cols(range: IntRange): ColumnSet<*> = cols<Any?>(range)

    /** ## Cols: Columns by Index Range
     *
     * Retrieves multiple columns from [this] in the form of a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] by a [range] of indices.
     * If any of the indices in the [range] are out of bounds, an [<code>IndexOutOfBoundsException</code>][IndexOutOfBoundsException] is thrown.
     *
     * For more information: [See `cols` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.cols]`(1`[<code>`..`</code>][Int.rangeTo]`3) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>`Int`</code>][Int]`>()`[<code>`[`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]`1`[<code>`..`</code>][Int.rangeTo]`5`[<code>`]`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.cols]` }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColGroup".`[<code>`cols`</code>][kotlin.String.cols]`(0`[<code>`..`</code>][Int.rangeTo]`2) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`(0`[<code>`..`</code>][Int.rangeTo]`1) }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColGroup"].`[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.cols]`<`[<code>`String`</code>][String]`>(0`[<code>`..`</code>][Int.rangeTo]`1) }`
     *
     * @throws [IndexOutOfBoundsException] if any of the indices in the [range] are out of bounds.
     * @throws [IllegalArgumentException] if the [range] is empty.
     * @param [range] The range of indices to retrieve in the form of an [<code>IntRange</code>][IntRange].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns found at the given indices.
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
 * If this [<code>ColumnsResolver</code>][ColumnsResolver] is a [<code>SingleColumn</code>][SingleColumn], it
 * returns a new [<code>ColumnSet</code>][ColumnSet] containing the columns inside of this [<code>SingleColumn</code>][SingleColumn] that
 * match the given [<code>predicate</code>][predicate].
 *
 * Else, it returns a new [<code>ColumnSet</code>][ColumnSet] containing all columns in this [<code>ColumnsResolver</code>][ColumnsResolver] that
 * match the given [<code>predicate</code>][predicate].
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
