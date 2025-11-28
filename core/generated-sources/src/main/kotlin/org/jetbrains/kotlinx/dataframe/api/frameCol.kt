package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.ColumnGroupReference
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.Issues
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.getAt
import org.jetbrains.kotlinx.dataframe.impl.columns.onResolve
import org.jetbrains.kotlinx.dataframe.impl.columns.singleImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Frame Col [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 * @param _UNUSED [#KT-68546](https://youtrack.jetbrains.com/issue/KT-68546/Conflicting-overloads-in-non-generic-interface-K2-2.0.0)
 */
public interface FrameColColumnsSelectionDsl<out _UNUSED> {

    /**
     * ## Frame Col Grammar
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
     *  `T: Column type`
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
     *  [**`frameCol`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`[`**`<`**[`T`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[`index`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`frameCol`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]**`(`**[`index`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`frameCol`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`[`**`<`**[`T`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[`index`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**
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

        /** [**`frameCol`**][ColumnsSelectionDsl.frameCol] */
        public interface PlainDslName

        /** __`.`__[**`frameCol`**][ColumnsSelectionDsl.frameCol] */
        public interface ColumnSetName

        /** __`.`__[**`frameCol`**][ColumnsSelectionDsl.frameCol] */
        public interface ColumnGroupName
    }

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor] (or [SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath], [KProperty], or [ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]`  {  `[frameCol][frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][DataFrame.select]`  {  `[frameCol][frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[frameCol][frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * To create a [ColumnAccessor] for another kind of column, take a look at the functions
     * [col][ColumnsSelectionDsl.col],
     * [colGroup][ColumnsSelectionDsl.colGroup],
     * and [valueCol][ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     */
    private interface CommonFrameColDocs {

        // Example argument, can be either {@include [SingleExample]} or {@include [DoubleExample]}
        interface EXAMPLE

        /**
         * `df.`[select][DataFrame.select]` { `[frameCol][frameCol]`() }`
         */
        interface SingleExample

        /**
         * `df.`[select][DataFrame.select]` { `[frameCol][frameCol]`() }`
         *
         * `df.`[select][DataFrame.select]` { `[frameCol][frameCol]`<`[String][String]`>() }`
         */
        interface DoubleExample

        // Receiver argument for the example(s)
        interface RECEIVER

        // Argument for the example(s)
        interface ARG

        // Optional note
        interface NOTE

        /** @param [C] The type of the frame column. */
        interface FrameColumnTypeParam
    }

    // region reference

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [ColumnAccessor] pointing to the value column.
     * @param [C] The type of the frame column.
     */
    private interface FrameColReferenceDocs

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> frameCol(frameCol: ColumnAccessor<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        frameCol.ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> SingleColumn<DataRow<*>>.frameCol(
        frameCol: ColumnAccessor<DataFrame<C>>,
    ): SingleColumn<DataFrame<C>> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getCol(frameCol)
                ?: throw IllegalStateException(
                    "FrameColumn '${frameCol.path()}' not found in column group '${it.path}'",
                )
            child.data.ensureIsFrameColumn()
            listOf(child)
        }.singleImpl()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> AnyColumnGroupAccessor.frameCol(
        frameCol: ColumnAccessor<DataFrame<C>>,
    ): ColumnAccessor<DataFrame<C>> = this.ensureIsColumnGroup().frameColumn<C>(frameCol.path()).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> String.frameCol(frameCol: ColumnAccessor<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(frameCol.path()).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.frameCol(frameCol: ColumnAccessor<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(frameCol.path()).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnPath.frameCol(frameCol: ColumnAccessor<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(frameCol.path()).ensureIsFrameColumn()

    // endregion

    // region name

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColumnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     */
    private interface FrameColNameDocs

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColumnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun frameCol(name: String): ColumnAccessor<DataFrame<*>> = frameColumn<Any?>(name).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColumnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     * @param [C] The type of the frame column.
     */
    public fun <C> frameCol(name: String): ColumnAccessor<DataFrame<C>> = frameColumn<C>(name).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColumnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun SingleColumn<DataRow<*>>.frameCol(name: String): SingleColumn<DataFrame<*>> = frameCol<Any?>(name)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColumnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     * @param [C] The type of the frame column.
     */
    public fun <C> SingleColumn<DataRow<*>>.frameCol(name: String): SingleColumn<DataFrame<C>> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getCol(name)?.cast<DataFrame<C>>()
                ?: throw IllegalStateException("Frame column '$name' not found in column group '${it.path}'")
            child.data.ensureIsFrameColumn()
            listOf(child)
        }.singleImpl()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColumnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun AnyColumnGroupAccessor.frameCol(name: String): ColumnAccessor<DataFrame<*>> = frameCol<Any?>(name)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColumnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     * @param [C] The type of the frame column.
     */
    public fun <C> AnyColumnGroupAccessor.frameCol(name: String): ColumnAccessor<DataFrame<C>> =
        this.ensureIsColumnGroup().frameColumn<C>(name).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColumnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun String.frameCol(name: String): ColumnAccessor<DataFrame<*>> = frameCol<Any?>(name)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColumnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     * @param [C] The type of the frame column.
     */
    public fun <C> String.frameCol(name: String): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(name).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColumnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun KProperty<*>.frameCol(name: String): ColumnAccessor<DataFrame<*>> = frameCol<Any?>(name)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColumnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     * @param [C] The type of the frame column.
     */
    public fun <C> KProperty<*>.frameCol(name: String): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(name).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColumnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun ColumnPath.frameCol(name: String): ColumnAccessor<DataFrame<*>> = frameCol<Any?>(name)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColumnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     * @param [C] The type of the frame column.
     */
    public fun <C> ColumnPath.frameCol(name: String): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(name).ensureIsFrameColumn()

    // endregion

    // region path

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     */
    private interface FrameColPathDocs

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun frameCol(path: ColumnPath): ColumnAccessor<DataFrame<*>> = frameColumn<Any?>(path).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     * @param [C] The type of the frame column.
     */
    public fun <C> frameCol(path: ColumnPath): ColumnAccessor<DataFrame<C>> = frameColumn<C>(path).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun SingleColumn<DataRow<*>>.frameCol(path: ColumnPath): SingleColumn<DataFrame<*>> = frameCol<Any?>(path)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     * @param [C] The type of the frame column.
     */
    public fun <C> SingleColumn<DataRow<*>>.frameCol(path: ColumnPath): SingleColumn<DataFrame<C>> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getCol(path)?.cast<DataFrame<C>>()
                ?: throw IllegalStateException("Frame column '$path' not found in column group '${it.path}'")
            child.data.ensureIsFrameColumn()
            listOf(child)
        }.singleImpl()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun AnyColumnGroupAccessor.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<*>> = frameCol<Any?>(path)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     * @param [C] The type of the frame column.
     */
    public fun <C> AnyColumnGroupAccessor.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<C>> =
        this.ensureIsColumnGroup().frameColumn<C>(path).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun String.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<*>> = frameCol<Any?>(path)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     * @param [C] The type of the frame column.
     */
    public fun <C> String.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(path).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun KProperty<*>.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<*>> = frameCol<Any?>(path)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     * @param [C] The type of the frame column.
     */
    public fun <C> KProperty<*>.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(path).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun ColumnPath.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<*>> = frameCol<Any?>(path)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     * @param [C] The type of the frame column.
     */
    public fun <C> ColumnPath.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(path).ensureIsFrameColumn()

    // endregion

    // region property

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    private interface FrameColKPropertyDocs

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColDataFrameKProperty")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> frameCol(property: KProperty<DataFrame<C>>): SingleColumn<DataFrame<C>> =
        frameColumn(property).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> frameCol(property: KProperty<List<C>>): SingleColumn<DataFrame<C>> =
        frameColumn(property).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColDataFrameKProperty")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> SingleColumn<DataRow<*>>.frameCol(property: KProperty<DataFrame<C>>): SingleColumn<DataFrame<C>> =
        frameCol<C>(property.name)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> SingleColumn<DataRow<*>>.frameCol(property: KProperty<List<C>>): SingleColumn<DataFrame<C>> =
        frameCol<C>(property.name)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColDataFrameKProperty")
    public fun <C> AnyColumnGroupAccessor.frameCol(property: KProperty<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        this.ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> AnyColumnGroupAccessor.frameCol(property: KProperty<List<C>>): ColumnAccessor<DataFrame<C>> =
        this.ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColDataFrameKProperty")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> String.frameCol(property: KProperty<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> String.frameCol(property: KProperty<List<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColDataFrameKProperty")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.frameCol(property: KProperty<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.frameCol(property: KProperty<List<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColDataFrameKProperty")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnPath.frameCol(property: KProperty<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnPath.frameCol(property: KProperty<List<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    // endregion

    // region index

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    private interface FrameColIndexDocs

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the frame column.
     *
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("ColumnSetDataFrameFrameColIndex")
    public fun <C> ColumnSet<DataFrame<C>>.frameCol(index: Int): SingleColumn<DataFrame<C>> =
        getAt(index).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the frame column.
     *
     */
    public fun ColumnSet<*>.frameCol(index: Int): SingleColumn<DataFrame<*>> =
        getAt(index).cast<DataFrame<*>>().ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun ColumnsSelectionDsl<*>.frameCol(index: Int): SingleColumn<DataFrame<*>> = frameCol<Any?>(index)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the frame column.
     */
    public fun <C> ColumnsSelectionDsl<*>.frameCol(index: Int): SingleColumn<DataFrame<C>> =
        asSingleColumn().frameCol<C>(index)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun SingleColumn<DataRow<*>>.frameCol(index: Int): SingleColumn<DataFrame<*>> = frameCol<Any?>(index)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the frame column.
     */
    public fun <C> SingleColumn<DataRow<*>>.frameCol(index: Int): SingleColumn<DataFrame<C>> =
        this
            .ensureIsColumnGroup()
            .allColumnsInternal()
            .getAt(index)
            .cast<DataFrame<C>>()
            .ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun String.frameCol(index: Int): SingleColumn<DataFrame<*>> = frameCol<Any?>(index)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the frame column.
     */
    public fun <C> String.frameCol(index: Int): SingleColumn<DataFrame<C>> = columnGroup(this).frameCol<C>(index)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.frameCol(index: Int): SingleColumn<DataFrame<*>> = frameCol<Any?>(index)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.frameCol(index: Int): SingleColumn<DataFrame<C>> = columnGroup(this).frameCol<C>(index)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun ColumnPath.frameCol(index: Int): SingleColumn<DataFrame<*>> = frameCol<Any?>(index)

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [frameColumn][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[frameCol][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the frame column.
     */
    public fun <C> ColumnPath.frameCol(index: Int): SingleColumn<DataFrame<C>> = columnGroup(this).frameCol<C>(index)

    // endregion
}

/**
 * Checks the validity of this [SingleColumn],
 * by adding a check to see it's a [FrameColumn] (so, a [SingleColumn]<*>)
 * and throwing an [IllegalArgumentException] if it's not.
 */
internal fun <C> SingleColumn<DataFrame<C>>.ensureIsFrameColumn(): SingleColumn<DataFrame<C>> =
    onResolve { col: ColumnWithPath<*>? ->
        require(col?.isFrameColumn() != false) {
            "Column at ${col?.path} is not a FrameColumn, but a ${col?.kind()}."
        }
    }

/** Checks the validity of this [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn],
 * by adding a check to see it's a [FrameColumn][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] (so, a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]<*>)
 * and throwing an [IllegalArgumentException] if it's not. */
internal fun <C> ColumnAccessor<DataFrame<C>>.ensureIsFrameColumn(): ColumnAccessor<DataFrame<C>> =
    onResolve { col: ColumnWithPath<*>? ->
        require(col?.isFrameColumn() != false) {
            "Column at ${col?.path} is not a FrameColumn, but a ${col?.kind()}."
        }
    }

// endregion
