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
import org.jetbrains.kotlinx.dataframe.impl.columns.getAt
import org.jetbrains.kotlinx.dataframe.impl.columns.singleImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.util.COL_REPLACE
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.IDENTITY_FUNCTION
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Col [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 * @param _UNUSED [#KT-68546](https://youtrack.jetbrains.com/issue/KT-68546/Conflicting-overloads-in-non-generic-interface-K2-2.0.0)
 */
public interface ColColumnsSelectionDsl<out _UNUSED> {

    /**
     * ## Col Grammar
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
     *  [**`col`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`[`**`<`**[`T`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[`index`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`col`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]**`(`**[`index`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**`  |  `[**`[`**][ColumnsSelectionDsl.col][`index`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef][**`]`**][ColumnsSelectionDsl.col]
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`col`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`[`**`<`**[`T`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[`index`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**
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

        /** [**`col`**][ColumnsSelectionDsl.col] */
        public typealias PlainDslName = Nothing

        /** __`.`__[**`col`**][ColumnsSelectionDsl.col] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[**`col`**][ColumnsSelectionDsl.col] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor] (or [SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath], [KProperty], or [ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]`  {  `[col][col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][DataFrame.select]`  {  `[col][col]`(SomeType::colB) }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[col][col]`(1) }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * To create a [ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][ColumnsSelectionDsl.valueCol],
     * [colGroup][ColumnsSelectionDsl.colGroup],
     * and [frameCol][ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn].
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
         * `df.`[select][DataFrame.select]` { `[col][col]`() }`
         */
        typealias SingleExample = Nothing

        /**
         * `df.`[select][DataFrame.select]` { `[col][col]`() }`
         *
         * `df.`[select][DataFrame.select]` { `[col][col]`<`[String][String]`>() }`
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(columnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [col] The [ColumnAccessor] pointing to the column.
     * @param [C] The type of the column.
     */
    private typealias ColReferenceDocs = Nothing

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: This overload is an identity function and can be omitted.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(columnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the column.
     * @param [C] The type of the column.
     *
     */
    @Deprecated(IDENTITY_FUNCTION, ReplaceWith(COL_REPLACE))
    @AccessApiOverload
    public fun <C> col(col: ColumnAccessor<C>): ColumnAccessor<C> = col

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(columnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the column.
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(columnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> AnyColumnGroupAccessor.col(col: ColumnAccessor<C>): ColumnAccessor<C> =
        this.ensureIsColumnGroup().column(col.path())

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(columnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> String.col(col: ColumnAccessor<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().column(col.path())

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(columnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.col(col: ColumnAccessor<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().column(col.path())

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(columnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the column.
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("columnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("columnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
    @Interpretable("Col")
    public fun col(name: String): ColumnAccessor<*> = column<Any?>(name)

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("columnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
    @Interpretable("ColUntyped")
    public fun <C> col(name: String): ColumnAccessor<C> = column(name)

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("columnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("columnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("columnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("columnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("columnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("columnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("columnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("columnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("columnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("columnName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("columnName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`("pathTo"["columnName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("pathTo"["columnName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(Type::columnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [property] The [KProperty] reference to the column.
     * @param [C] The type of the column.
     */
    private typealias ColKPropertyDocs = Nothing

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(Type::columnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [property] The [KProperty] reference to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> col(property: KProperty<C>): SingleColumn<C> = column(property)

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(Type::columnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [property] The [KProperty] reference to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> SingleColumn<DataRow<*>>.col(property: KProperty<C>): SingleColumn<C> = col<C>(property.name)

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(Type::columnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [property] The [KProperty] reference to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> AnyColumnGroupAccessor.col(property: KProperty<C>): ColumnAccessor<C> =
        this.ensureIsColumnGroup().column(property)

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(Type::columnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [property] The [KProperty] reference to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> String.col(property: KProperty<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().column(property)

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(Type::columnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [property] The [KProperty] reference to the column.
     * @param [C] The type of the column.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.col(property: KProperty<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().column(property)

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(Type::columnA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     *
     * @see [column]
     * @see [ColumnsSelectionDsl.colGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     *
     *
     *
     * @param [property] The [KProperty] reference to the column.
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * NOTE: You can use the get-[] operator on [ColumnSets][ColumnSet] as well!
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][col]`1`[`]`][col]` }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][col]`1`[`]`][col]` }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
    public fun ColumnsSelectionDsl<*>.col(index: Int): SingleColumn<*> = col<Any?>(index)

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
    public fun <C> ColumnsSelectionDsl<*>.col(index: Int): SingleColumn<C> = asSingleColumn().col<C>(index)

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]; any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]).
     *
     * This is a DSL-shorthand for [column][org.jetbrains.kotlinx.dataframe.api.column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(SomeType::colB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[col][org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.col]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * [colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
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
