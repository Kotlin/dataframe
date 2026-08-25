package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.ColumnGroupReference
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.Issues
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.getAt
import org.jetbrains.kotlinx.dataframe.impl.columns.singleImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.util.COL_REPLACE
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.IDENTITY_FUNCTION
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Col [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 * @param _UNUSED [#KT-68546](https://youtrack.jetbrains.com/issue/KT-68546/Conflicting-overloads-in-non-generic-interface-K2-2.0.0)
 */
public interface ColColumnsSelectionDsl<out _UNUSED> {

    /**
     * ## Col Grammar
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
     *  `T: Column type`
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
     *  [<code>**`col`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`[`**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`col`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]**`(`**[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**`  |  `[<code>**`[`**</code>][ColumnsSelectionDsl.col][`index`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef][**`]`**][ColumnsSelectionDsl.col]
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`col`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`[`**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**
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

        /** [<code>**`col`**</code>][ColumnsSelectionDsl.col] */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`col`**</code>][ColumnsSelectionDsl.col] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`col`**</code>][ColumnsSelectionDsl.col] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] (or [<code>SingleColumn</code>][SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>col</code>][col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>col</code>][col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>col</code>][col]`(1) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * To create a [<code>ColumnAccessor</code>][ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     */
    private interface CommonColDocs {

        // Example argument, can be either {@include [SingleExample]} or {@include [DoubleExample]}
        typealias EXAMPLE = Nothing

        /**
         * `df.`[<code>select</code>][DataFrame.select]` { `[<code>col</code>][col]`() }`
         */
        typealias SingleExample = Nothing

        /**
         * `df.`[<code>select</code>][DataFrame.select]` { `[<code>col</code>][col]`() }`
         *
         * `df.`[<code>select</code>][DataFrame.select]` { `[<code>col</code>][col]`<`[<code>String</code>][String]`>() }`
         */
        typealias DoubleExample = Nothing

        // Receiver argument for the example(s)
        typealias RECEIVER = Nothing

        // Argument for the example(s)
        typealias ARG = Nothing

        // Optional note
        typealias NOTE = Nothing

        /** @param [C] The type of the column. */
        typealias ColumnTypeParam = Nothing
    }

    // region reference

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(columnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][ColumnAccessor] pointing to the column.
     * @param [C] The type of the column.
     */
    private typealias ColReferenceDocs = Nothing

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: This overload is an identity function and can be omitted.
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(columnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the column.
     * @param [C] The type of the column.
     *
     */
    @Deprecated(IDENTITY_FUNCTION, ReplaceWith(COL_REPLACE))
    @AccessApiOverload
    public fun <C> col(col: ColumnAccessor<C>): ColumnAccessor<C> = col

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(columnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> SingleColumn<DataRow<*>>.col(col: ColumnAccessor<C>): SingleColumn<C> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getCol(col)
                ?: throw IllegalStateException("Column '${col.path()}' not found in column group '${it.path}'")
            listOf(child)
        }.singleImpl()

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(columnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> AnyColumnGroupAccessor.col(col: ColumnAccessor<C>): ColumnAccessor<C> =
        this.ensureIsColumnGroup().column(col.path())

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(columnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> String.col(col: ColumnAccessor<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().column(col.path())

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(columnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.col(col: ColumnAccessor<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().column(col.path())

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(columnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnPath.col(col: ColumnAccessor<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().column(col.path())

    // endregion

    // region name

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("columnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [name] The name of the column.
     */
    private typealias ColNameDocs = Nothing

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("columnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [name] The name of the column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    @Interpretable("ColByStringUntyped")
    public fun col(name: String): ColumnAccessor<*> = column<Any?>(name)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("columnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [name] The name of the column.
     * @param [C] The type of the column.
     */
    @Interpretable("ColByString")
    public fun <C> col(name: String): ColumnAccessor<C> = column(name)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("columnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [name] The name of the column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    @Interpretable("SingleColumnNestedColUntyped")
    public fun SingleColumn<DataRow<*>>.col(name: String): SingleColumn<*> = col<Any?>(name)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("columnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [name] The name of the column.
     * @param [C] The type of the column.
     */
    @Interpretable("SingleColumnNestedCol")
    public fun <C> SingleColumn<DataRow<*>>.col(name: String): SingleColumn<C> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getCol(name)?.cast<C>()
                ?: throw IllegalStateException("Column '$name' not found in column group '${it.path}'")
            listOf(child)
        }.singleImpl()

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("columnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [name] The name of the column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun AnyColumnGroupAccessor.col(name: String): ColumnAccessor<*> = col<Any?>(name)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("columnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [name] The name of the column.
     * @param [C] The type of the column.
     */
    public fun <C> AnyColumnGroupAccessor.col(name: String): ColumnAccessor<C> = this.ensureIsColumnGroup().column(name)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("columnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [name] The name of the column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    @Interpretable("StringNestedColUntyped")
    public fun String.col(name: String): ColumnAccessor<*> = col<Any?>(name)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("columnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [name] The name of the column.
     * @param [C] The type of the column.
     */
    @Interpretable("StringNestedCol")
    public fun <C> String.col(name: String): ColumnAccessor<C> =
        columnGroup(this)
            .ensureIsColumnGroup()
            .column(name)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("columnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [name] The name of the column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.col(name: String): ColumnAccessor<*> = col<Any?>(name)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("columnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [name] The name of the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.col(name: String): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().column(name)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("columnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [name] The name of the column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    @Interpretable("ColumnPathColUntyped")
    public fun ColumnPath.col(name: String): ColumnAccessor<*> = col<Any?>(name)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("columnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [name] The name of the column.
     * @param [C] The type of the column.
     */
    @Interpretable("ColumnPathCol")
    public fun <C> ColumnPath.col(name: String): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().column(name)

    // endregion

    // region path

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [path] The path to the column.
     */
    private typealias ColPathDocs = Nothing

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [path] The path to the column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun col(path: ColumnPath): ColumnAccessor<*> = column<Any?>(path)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [path] The path to the column.
     * @param [C] The type of the column.
     */
    public fun <C> col(path: ColumnPath): ColumnAccessor<C> = column(path)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [path] The path to the column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun SingleColumn<DataRow<*>>.col(path: ColumnPath): SingleColumn<*> = col<Any?>(path)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [path] The path to the column.
     * @param [C] The type of the column.
     */
    public fun <C> SingleColumn<DataRow<*>>.col(path: ColumnPath): SingleColumn<C> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getCol(path)?.cast<C>()
                ?: throw IllegalStateException("Column '$path' not found in column group '${it.path}'")
            listOf(child)
        }.singleImpl()

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [path] The path to the column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun AnyColumnGroupAccessor.col(path: ColumnPath): ColumnAccessor<*> = col<Any?>(path)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [path] The path to the column.
     * @param [C] The type of the column.
     */
    public fun <C> AnyColumnGroupAccessor.col(path: ColumnPath): ColumnAccessor<C> =
        this.ensureIsColumnGroup().column(path)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [path] The path to the column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun String.col(path: ColumnPath): ColumnAccessor<*> = col<Any?>(path)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [path] The path to the column.
     * @param [C] The type of the column.
     */
    public fun <C> String.col(path: ColumnPath): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().column(path)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [path] The path to the column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.col(path: ColumnPath): ColumnAccessor<*> = col<Any?>(path)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [path] The path to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.col(path: ColumnPath): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().column(path)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [path] The path to the column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun ColumnPath.col(path: ColumnPath): ColumnAccessor<*> = col<Any?>(path)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [path] The path to the column.
     * @param [C] The type of the column.
     */
    public fun <C> ColumnPath.col(path: ColumnPath): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().column(path)

    // endregion

    // region property

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(Type::columnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the column.
     * @param [C] The type of the column.
     */
    private typealias ColKPropertyDocs = Nothing

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(Type::columnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> col(property: KProperty<C>): SingleColumn<C> = column(property)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(Type::columnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> SingleColumn<DataRow<*>>.col(property: KProperty<C>): SingleColumn<C> = col<C>(property.name)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(Type::columnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> AnyColumnGroupAccessor.col(property: KProperty<C>): ColumnAccessor<C> =
        this.ensureIsColumnGroup().column(property)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(Type::columnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> String.col(property: KProperty<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().column(property)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(Type::columnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.col(property: KProperty<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().column(property)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(Type::columnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnPath.col(property: KProperty<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().column(property)

    // endregion

    // region index

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [index] The index of the column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    private typealias ColIndexDocs = Nothing

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: You can use the get-[<code></code>][] operator on [<code>ColumnSets</code>][ColumnSet] as well!
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<`[<code>String</code>][String]`>()`[<code>`[`</code>][col]`1`[<code>`]`</code>][col]` }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [index] The index of the column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the column.
     *
     *
     */
    public fun <C> ColumnSet<C>.col(index: Int): SingleColumn<C> = getAt(index)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<`[<code>String</code>][String]`>()`[<code>`[`</code>][col]`1`[<code>`]`</code>][col]` }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [index] The index of the column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the column.
     *
     */
    public operator fun <C> ColumnSet<C>.get(index: Int): SingleColumn<C> = col(index)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [index] The index of the column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    @Interpretable("ColByIndexUntyped")
    public fun ColumnsSelectionDsl<*>.col(index: Int): SingleColumn<*> = col<Any?>(index)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [index] The index of the column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the column.
     */
    @Interpretable("ColByIndex")
    public fun <C> ColumnsSelectionDsl<*>.col(index: Int): SingleColumn<C> = asSingleColumn().col<C>(index)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [index] The index of the column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun SingleColumn<DataRow<*>>.col(index: Int): SingleColumn<*> = col<Any?>(index)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [index] The index of the column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the column.
     */
    public fun <C> SingleColumn<DataRow<*>>.col(index: Int): SingleColumn<C> =
        this.ensureIsColumnGroup()
            .allColumnsInternal()
            .getAt(index)
            .cast()

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [index] The index of the column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun String.col(index: Int): SingleColumn<*> = col<Any?>(index)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [index] The index of the column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the column.
     */
    public fun <C> String.col(index: Int): SingleColumn<C> = columnGroup(this).col<C>(index)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [index] The index of the column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.col(index: Int): SingleColumn<*> = col<Any?>(index)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [index] The index of the column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.col(index: Int): SingleColumn<C> = columnGroup(this).col<C>(index)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [index] The index of the column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun ColumnPath.col(index: Int): SingleColumn<*> = col<Any?>(index)

    /**
     * ## Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>column</code>][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `col` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>("colA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [index] The index of the column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the column.
     */
    public fun <C> ColumnPath.col(index: Int): SingleColumn<C> = columnGroup(this).col<C>(index)

    // endregion
}

// endregion
