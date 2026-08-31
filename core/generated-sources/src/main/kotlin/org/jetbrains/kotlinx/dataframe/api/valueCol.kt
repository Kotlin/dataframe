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
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
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
 * ## Value Col [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 * @param _UNUSED [#KT-68546](https://youtrack.jetbrains.com/issue/KT-68546/Conflicting-overloads-in-non-generic-interface-K2-2.0.0)
 */
public interface ValueColColumnsSelectionDsl<out _UNUSED> {

    /**
     * ## Value Col Grammar
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
     *  [<code>**`valueCol`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol]`[`**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`valueCol`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol]**`(`**[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`valueCol`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol]`[`**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**
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

        /** [<code>**`valueCol`**</code>][ColumnsSelectionDsl.valueCol] */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`valueCol`**</code>][ColumnsSelectionDsl.valueCol] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`valueCol`**</code>][ColumnsSelectionDsl.valueCol] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][ColumnAccessor] (or [<code>SingleColumn</code>][SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>valueCol</code>][valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>valueCol</code>][valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * To create a [<code>ColumnAccessor</code>][ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     */
    private interface CommonValueColDocs {

        // Example argument, can be either {@include [SingleExample]} or {@include [DoubleExample]}
        typealias EXAMPLE = Nothing

        /**
         * `df.`[<code>select</code>][DataFrame.select]` { `[<code>valueCol</code>][valueCol]`() }`
         */
        typealias SingleExample = Nothing

        /**
         * `df.`[<code>select</code>][DataFrame.select]` { `[<code>valueCol</code>][valueCol]`() }`
         *
         * `df.`[<code>select</code>][DataFrame.select]` { `[<code>valueCol</code>][valueCol]`<`[<code>String</code>][String]`>() }`
         */
        typealias DoubleExample = Nothing

        // Receiver argument for the example(s)
        typealias RECEIVER = Nothing

        // Argument for the example(s)
        typealias ARG = Nothing

        // Optional note
        typealias NOTE = Nothing

        /** @param [C] The type of the value column. */
        typealias ValueColumnTypeParam = Nothing
    }

    // region reference

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(valueColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][ColumnAccessor] pointing to the value column.
     * @param [C] The type of the value column.
     */
    private typealias ValueColReferenceDocs = Nothing

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(valueColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the value column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> = valueCol.ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(valueColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(valueCol: ColumnAccessor<C>): SingleColumn<C> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getCol(valueCol)
                ?: throw IllegalStateException(
                    "ValueColumn '${valueCol.path()}' not found in column group '${it.path}'",
                )
            child.data.ensureIsValueColumn()
            listOf(child)
        }.singleImpl()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(valueColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> AnyColumnGroupAccessor.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        this.ensureIsColumnGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(valueColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> String.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(valueColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the value column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(valueColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [col] The [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> ColumnPath.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    // endregion

    // region name

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("valueColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     */
    private typealias ValueColNameDocs = Nothing

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("valueColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun valueCol(name: String): ColumnAccessor<*> = valueColumn<Any?>(name).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("valueColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> valueCol(name: String): ColumnAccessor<C> = valueColumn<C>(name).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("valueColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun SingleColumn<DataRow<*>>.valueCol(name: String): SingleColumn<*> = valueCol<Any?>(name)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("valueColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(name: String): SingleColumn<C> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getCol(name)?.cast<C>()
                ?: throw IllegalStateException("Value column '$name' not found in column group '${it.path}'")
            child.data.ensureIsValueColumn()
            listOf(child)
        }.singleImpl()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("valueColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun AnyColumnGroupAccessor.valueCol(name: String): ColumnAccessor<*> = valueCol<Any?>(name)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("valueColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> AnyColumnGroupAccessor.valueCol(name: String): ColumnAccessor<C> =
        this.ensureIsColumnGroup().valueColumn<C>(name).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("valueColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun String.valueCol(name: String): ColumnAccessor<*> = valueCol<Any?>(name)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("valueColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> String.valueCol(name: String): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(name).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("valueColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.valueCol(name: String): ColumnAccessor<*> = valueCol<Any?>(name)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("valueColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     * @param [C] The type of the value column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.valueCol(name: String): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(name).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("valueColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun ColumnPath.valueCol(name: String): ColumnAccessor<*> = valueCol<Any?>(name)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("valueColumnName") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColumnName") }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [name] The name of the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> ColumnPath.valueCol(name: String): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(name).ensureIsValueColumn()

    // endregion

    // region path

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("pathTo"["valueColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("pathTo"["valueColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     */
    private typealias ValueColPathDocs = Nothing

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("pathTo"["valueColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("pathTo"["valueColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun valueCol(path: ColumnPath): ColumnAccessor<*> = valueColumn<Any?>(path).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("pathTo"["valueColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("pathTo"["valueColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> valueCol(path: ColumnPath): ColumnAccessor<C> = valueColumn<C>(path).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("pathTo"["valueColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("pathTo"["valueColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun SingleColumn<DataRow<*>>.valueCol(path: ColumnPath): SingleColumn<*> = valueCol<Any?>(path)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("pathTo"["valueColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("pathTo"["valueColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(path: ColumnPath): SingleColumn<C> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getCol(path)?.cast<C>()
                ?: throw IllegalStateException("Value column '$path' not found in column group '${it.path}'")
            child.data.ensureIsValueColumn()
            listOf(child)
        }.singleImpl()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("pathTo"["valueColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("pathTo"["valueColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun AnyColumnGroupAccessor.valueCol(path: ColumnPath): ColumnAccessor<*> = valueCol<Any?>(path)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("pathTo"["valueColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("pathTo"["valueColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> AnyColumnGroupAccessor.valueCol(path: ColumnPath): ColumnAccessor<C> =
        this.ensureIsColumnGroup().valueColumn<C>(path).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("pathTo"["valueColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("pathTo"["valueColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun String.valueCol(path: ColumnPath): ColumnAccessor<*> = valueCol<Any?>(path)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("pathTo"["valueColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("pathTo"["valueColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> String.valueCol(path: ColumnPath): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(path).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("pathTo"["valueColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("pathTo"["valueColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.valueCol(path: ColumnPath): ColumnAccessor<*> = valueCol<Any?>(path)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("pathTo"["valueColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("pathTo"["valueColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     * @param [C] The type of the value column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.valueCol(path: ColumnPath): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(path).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("pathTo"["valueColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("pathTo"["valueColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun ColumnPath.valueCol(path: ColumnPath): ColumnAccessor<*> = valueCol<Any?>(path)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`("pathTo"["valueColumnName"] ) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("pathTo"["valueColumnName"] ) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [path] The path to the value column.
     * @param [C] The type of the value column.
     */
    public fun <C> ColumnPath.valueCol(path: ColumnPath): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(path).ensureIsValueColumn()

    // endregion

    // region property

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(Type::valueColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
     * @param [C] The type of the value column.
     */
    private typealias ValueColKPropertyDocs = Nothing

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(Type::valueColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
     * @param [C] The type of the value column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> valueCol(property: KProperty<C>): SingleColumn<C> = valueColumn(property).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(Type::valueColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
     * @param [C] The type of the value column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> SingleColumn<DataRow<*>>.valueCol(property: KProperty<C>): SingleColumn<C> =
        valueCol<C>(property.name)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(Type::valueColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
     * @param [C] The type of the value column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> AnyColumnGroupAccessor.valueCol(property: KProperty<C>): ColumnAccessor<C> =
        this.ensureIsColumnGroup().valueColumn(property).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(Type::valueColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
     * @param [C] The type of the value column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> String.valueCol(property: KProperty<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn(property).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(Type::valueColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
     * @param [C] The type of the value column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.valueCol(property: KProperty<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn(property).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(Type::valueColumnA) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [property] The [<code>KProperty</code>][KProperty] reference to the value column.
     * @param [C] The type of the value column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnPath.valueCol(property: KProperty<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn(property).ensureIsValueColumn()

    // endregion

    // region index

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    private typealias ValueColIndexDocs = Nothing

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the value column.
     *
     */
    public fun <C> ColumnSet<C>.valueCol(index: Int): SingleColumn<C> = getAt(index).ensureIsValueColumn()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun ColumnsSelectionDsl<*>.valueCol(index: Int): SingleColumn<*> = valueCol<Any?>(index)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the value column.
     */
    public fun <C> ColumnsSelectionDsl<*>.valueCol(index: Int): SingleColumn<C> = asSingleColumn().valueCol<C>(index)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun SingleColumn<DataRow<*>>.valueCol(index: Int): SingleColumn<*> = valueCol<Any?>(index)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the value column.
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(index: Int): SingleColumn<C> =
        this.ensureIsColumnGroup()
            .allColumnsInternal()
            .getAt(index)
            .ensureIsValueColumn()
            .cast()

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun String.valueCol(index: Int): SingleColumn<*> = valueCol<Any?>(index)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the value column.
     */
    public fun <C> String.valueCol(index: Int): SingleColumn<C> = columnGroup(this).valueCol<C>(index)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.valueCol(index: Int): SingleColumn<*> = valueCol<Any?>(index)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the value column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.valueCol(index: Int): SingleColumn<C> = columnGroup(this).valueCol<C>(index)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun ColumnPath.valueCol(index: Int): SingleColumn<*> = valueCol<Any?>(index)

    /**
     * ## Value Col
     *
     * Creates a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a value column with the given argument which can be either
     * an index ([<code>Int</code>][Int]) or a reference to a column
     * ([<code>String</code>][String], [<code>ColumnPath</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [<code>KProperty</code>][KProperty], or [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]).
     *
     * This is a DSL-shorthand for [<code>valueColumn</code>][org.jetbrains.kotlinx.dataframe.api.valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a value column inside a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * For more information: [See `valueCol` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#value-col-frame-col-col-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>("valueColA") }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`(0) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>valueCol</code>][org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.valueCol]`<`[<code>String</code>][String]`>(0) }`
     *
     * To create a [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [<code>col</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [<code>colGroup</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [<code>frameCol</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [<code>ColumnAccessor</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the value column with the given argument if possible, else a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a value column.
     *
     * @see [valueColumn]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds.
     * @param [C] The type of the value column.
     */
    public fun <C> ColumnPath.valueCol(index: Int): SingleColumn<C> = columnGroup(this).valueCol<C>(index)

    // endregion
}

/**
 * Checks the validity of this [<code>SingleColumn</code>][SingleColumn],
 * by adding a check to see it's a [<code>ValueColumn</code>][ValueColumn] (so, a [<code>SingleColumn</code>][SingleColumn]<*>)
 * and throwing an [<code>IllegalArgumentException</code>][IllegalArgumentException] if it's not.
 */
internal fun <C> SingleColumn<C>.ensureIsValueColumn(): SingleColumn<C> =
    onResolve { col: ColumnWithPath<*>? ->
        require(col?.isValueColumn() != false) {
            "Column at ${col?.path} is not a ValueColumn, but a ${col?.kind()}."
        }
    }

/** Checks the validity of this [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn],
 * by adding a check to see it's a [<code>ValueColumn</code>][org.jetbrains.kotlinx.dataframe.columns.ValueColumn] (so, a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]<*>)
 * and throwing an [<code>IllegalArgumentException</code>][IllegalArgumentException] if it's not. */
internal fun <C> ColumnAccessor<C>.ensureIsValueColumn(): ColumnAccessor<C> =
    onResolve { col: ColumnWithPath<*>? ->
        require(col?.isValueColumn() != false) {
            "Column at ${col?.path} is not a ValueColumn, but a ${col?.kind()}."
        }
    }

// endregion
