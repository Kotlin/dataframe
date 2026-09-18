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
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
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
 * ## Frame Col [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 * @param _UNUSED [#KT-68546](https://youtrack.jetbrains.com/issue/KT-68546/Conflicting-overloads-in-non-generic-interface-K2-2.0.0)
 */
public interface FrameColColumnsSelectionDsl<out _UNUSED> {

    /**
     * ## Frame Col Grammar
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
     *  [<code>**`frameCol`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`[`**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`frameCol`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]**`(`**[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`frameCol`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`[`**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**
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

        /** [<code>**`frameCol`**</code>][ColumnsSelectionDsl.frameCol] */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`frameCol`**</code>][ColumnsSelectionDsl.frameCol] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`frameCol`**</code>][ColumnsSelectionDsl.frameCol] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Frame Col
     *
     * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] (or [<code>SingleColumn</code>][SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>frameCol</code>][frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>frameCol</code>][frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * To create a [<code>ColumnAccessor</code>][ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][SingleColumn].
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
        typealias EXAMPLE = Nothing

        /**
         * `df.`[<code>select</code>][DataFrame.select]` { `[<code>frameCol</code>][frameCol]`() }`
         */
        typealias SingleExample = Nothing

        /**
         * `df.`[<code>select</code>][DataFrame.select]` { `[<code>frameCol</code>][frameCol]`() }`
         *
         * `df.`[<code>select</code>][DataFrame.select]` { `[<code>frameCol</code>][frameCol]`<`[<code>String</code>][String]`>() }`
         */
        typealias DoubleExample = Nothing

        // Receiver argument for the example(s)
        typealias RECEIVER = Nothing

        // Argument for the example(s)
        typealias ARG = Nothing

        // Optional note
        typealias NOTE = Nothing

        /** @param [C] The type of the frame column. */
        typealias FrameColumnTypeParam = Nothing
    }

    // region reference

    /**
     * ## Frame Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][ColumnAccessor] pointing to the frame column.
     * @param [C] The type of the frame column.
     */
    private typealias FrameColReferenceDocs = Nothing

    /**
     * ## Frame Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the frame column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> frameCol(frameCol: ColumnAccessor<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        frameCol.ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the frame column.
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the frame column.
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the frame column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> String.frameCol(frameCol: ColumnAccessor<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(frameCol.path()).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the frame column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.frameCol(frameCol: ColumnAccessor<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(frameCol.path()).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the frame column.
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
    private typealias FrameColNameDocs = Nothing

    /**
     * ## Frame Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("frameColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
    private typealias FrameColPathDocs = Nothing

    /**
     * ## Frame Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`("pathTo"["frameColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("pathTo"["frameColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    private typealias FrameColKPropertyDocs = Nothing

    /**
     * ## Frame Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> frameCol(property: KProperty<List<C>>): SingleColumn<DataFrame<C>> =
        frameColumn(property).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> SingleColumn<DataRow<*>>.frameCol(property: KProperty<List<C>>): SingleColumn<DataFrame<C>> =
        frameCol<C>(property.name)

    /**
     * ## Frame Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> AnyColumnGroupAccessor.frameCol(property: KProperty<List<C>>): ColumnAccessor<DataFrame<C>> =
        this.ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> String.frameCol(property: KProperty<List<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
     * @param [C] The type of the frame column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.frameCol(property: KProperty<List<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * ## Frame Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(Type::frameColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
    private typealias FrameColIndexDocs = Nothing

    /**
     * ## Frame Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a frame column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>frameColumn</code>][org.jetbrains.kotlinx.dataframe.api.frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a frame column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `frameCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>("frameColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.frameCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the frame column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
 * Checks the validity of this [<code>SingleColumn</code>][SingleColumn],
 * by adding a check to see it's a [<code>FrameColumn</code>][FrameColumn] (so, a [<code>SingleColumn</code>][SingleColumn]<*>)
 * and throwing an [<code>IllegalArgumentException</code>][IllegalArgumentException] if it's not.
 */
internal fun <C> SingleColumn<DataFrame<C>>.ensureIsFrameColumn(): SingleColumn<DataFrame<C>> =
    onResolve { col: ColumnWithPath<*>? ->
        require(col?.isFrameColumn() != false) {
            "Column at ${col?.path} is not a FrameColumn, but a ${col?.kind()}."
        }
    }

/** Checks the validity of this [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn],
 * by adding a check to see it's a [<code>FrameColumn</code>][org.jetbrains.kotlinx.dataframe.columns.FrameColumn] (so, a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]<*>)
 * and throwing an [<code>IllegalArgumentException</code>][IllegalArgumentException] if it's not. */
internal fun <C> ColumnAccessor<DataFrame<C>>.ensureIsFrameColumn(): ColumnAccessor<DataFrame<C>> =
    onResolve { col: ColumnWithPath<*>? ->
        require(col?.isFrameColumn() != false) {
            "Column at ${col?.path} is not a FrameColumn, but a ${col?.kind()}."
        }
    }

// endregion
