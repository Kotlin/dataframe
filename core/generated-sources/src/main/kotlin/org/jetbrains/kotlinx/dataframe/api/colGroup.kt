package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.ColumnGroupReference
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.getAt
import org.jetbrains.kotlinx.dataframe.impl.columns.performCheck
import org.jetbrains.kotlinx.dataframe.impl.columns.singleImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

public interface ColGroupColumnsSelectionDsl {

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor] (or [SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath], [KProperty], or [ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[colGroup][colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonColGroupDocs.ExampleArg]}
     *
     * To create a [ColumnAccessor] for another kind of column, take a look at the functions
     * [col][ColumnsSelectionDsl.col],
     * [valueCol][ColumnsSelectionDsl.valueCol],
     * and [frameCol][ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn].
     * @throws [IllegalStateException\] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException\] if the column with the given argument is not a column group.
     *
     * @see [columnGroup\]
     * @see [ColumnsSelectionDsl.frameCol\]
     * @see [ColumnsSelectionDsl.valueCol\]
     * @see [ColumnsSelectionDsl.col\]
     *
     */
    private interface CommonColGroupDocs {

        /* Example argument, can be either {@include [SingleExample]} or {@include [DoubleExample]} */
        interface ExampleArg

        /**
         * `df.`[select][DataFrame.select]` { {@getArg [CommonColGroupDocs.ReceiverArg]}`[colGroup][colGroup]`({@getArg [CommonColGroupDocs.Arg]}) \\\\}`
         */
        interface SingleExample

        /**
         * `df.`[select][DataFrame.select]` { {@getArg [CommonColGroupDocs.ReceiverArg]}`[colGroup][colGroup]`({@getArg [CommonColGroupDocs.Arg]}) \\\\}`
         *
         * `df.`[select][DataFrame.select]` { {@getArg [CommonColGroupDocs.ReceiverArg]}`[colGroup][colGroup]`<`[String][String]`>({@getArg [CommonColGroupDocs.Arg]}) \\\\}`
         */
        interface DoubleExample

        /* Receiver argument for the example(s) */
        interface ReceiverArg

        /* Argument for the example(s) */
        interface Arg

        /* Optional note */
        interface Note

        /** @param [C\] The type of the column group. */
        interface ColumnGroupTypeParam
    }

    // region reference

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { {@getArg [CommonColGroupDocs.ReceiverArg]}`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [col\] The [ColumnAccessor] pointing to the value column.
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    private interface ColGroupReferenceDocs

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the column group. 
     */
    public fun <C> colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        colGroup.ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the column group. 
     */
    public fun <C> SingleColumn<DataRow<*>>.colGroup(colGroup: ColumnAccessor<DataRow<C>>): SingleColumn<DataRow<C>> =
        ensureIsColGroup()
            .transformSingle {
                it.getChild(colGroup)
                    ?.cast<DataRow<C>>()
                    ?.also { it.data.ensureIsColumnGroup() }
                    ?.let(::listOf)
                    ?: throw IllegalStateException("ColumnGroup '${colGroup.path()}' not found in column group '${it.path}'")
            }.singleImpl()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the column group. 
     */
    public fun <C> AnyColumnGroupAccessor.colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        ensureIsColGroup().columnGroup<C>(colGroup.path()).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the column group. 
     */
    public fun <C> String.colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(colGroup.path()).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the column group. 
     */
    public fun <C> KProperty<*>.colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(colGroup.path()).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the column group. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupKPropertyDataRow")
    public fun <C> KProperty<DataRow<*>>.colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(colGroup.path()).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [col] The [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] pointing to the value column.
     * @param [C] The type of the column group. 
     */
    public fun <C> ColumnPath.colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(colGroup.path()).ensureIsColumnGroup()

    // endregion

    // region name

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { {@getArg [CommonColGroupDocs.ReceiverArg]}`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { {@getArg [CommonColGroupDocs.ReceiverArg]}`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("columnGroupName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [name\] The name of the value column.
     */
    private interface ColGroupNameDocs

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("columnGroupName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [name] The name of the value column. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun colGroup(name: String): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(name).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("columnGroupName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [name] The name of the value column. 
     * @param [C] The type of the column group.
     */
    public fun <C> colGroup(name: String): ColumnAccessor<DataRow<C>> = columnGroup<C>(name).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("columnGroupName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [name] The name of the value column. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun SingleColumn<DataRow<*>>.colGroup(name: String): SingleColumn<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("columnGroupName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [name] The name of the value column. 
     * @param [C] The type of the column group.
     */
    public fun <C> SingleColumn<DataRow<*>>.colGroup(name: String): SingleColumn<DataRow<C>> =
        ensureIsColGroup()
            .transformSingle {
                it.getChild(name)
                    ?.cast<DataRow<C>>()
                    ?.also { it.data.ensureIsColumnGroup() }
                    ?.let(::listOf)
                    ?: throw IllegalStateException("Column group '$name' not found in column group '${it.path}'")
            }.singleImpl()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("columnGroupName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [name] The name of the value column. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun AnyColumnGroupAccessor.colGroup(name: String): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("columnGroupName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [name] The name of the value column. 
     * @param [C] The type of the column group.
     */
    public fun <C> AnyColumnGroupAccessor.colGroup(name: String): ColumnAccessor<DataRow<C>> =
        ensureIsColGroup().columnGroup<C>(name).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("columnGroupName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [name] The name of the value column. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun String.colGroup(name: String): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("columnGroupName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [name] The name of the value column. 
     * @param [C] The type of the column group.
     */
    public fun <C> String.colGroup(name: String): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(name).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("columnGroupName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [name] The name of the value column. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun KProperty<*>.colGroup(name: String): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("columnGroupName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [name] The name of the value column. 
     * @param [C] The type of the column group.
     */
    public fun <C> KProperty<*>.colGroup(name: String): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(name).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("columnGroupName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [name] The name of the value column. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTypedKPropertyDataRow")
    public fun KProperty<DataRow<*>>.colGroup(name: String): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("columnGroupName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [name] The name of the value column. 
     * @param [C] The type of the column group.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupKPropertyDataRow")
    public fun <C> KProperty<DataRow<*>>.colGroup(name: String): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(name).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("columnGroupName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [name] The name of the value column. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun ColumnPath.colGroup(name: String): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("columnGroupName") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("columnGroupName") }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [name] The name of the value column. 
     * @param [C] The type of the column group.
     */
    public fun <C> ColumnPath.colGroup(name: String): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(name).ensureIsColumnGroup()

    // endregion

    // region path

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { {@getArg [CommonColGroupDocs.ReceiverArg]}`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("pathTo"["columnGroupName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { {@getArg [CommonColGroupDocs.ReceiverArg]}`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("pathTo"["columnGroupName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [path\] The path to the value column.
     */
    private interface ColGroupPathDocs

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("pathTo"["columnGroupName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("pathTo"["columnGroupName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [path] The path to the value column. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(path).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("pathTo"["columnGroupName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("pathTo"["columnGroupName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [path] The path to the value column. 
     * @param [C] The type of the column group.
     */
    public fun <C> colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> = columnGroup<C>(path).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("pathTo"["columnGroupName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("pathTo"["columnGroupName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [path] The path to the value column. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun SingleColumn<DataRow<*>>.colGroup(path: ColumnPath): SingleColumn<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("pathTo"["columnGroupName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("pathTo"["columnGroupName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [path] The path to the value column. 
     * @param [C] The type of the column group.
     */
    public fun <C> SingleColumn<DataRow<*>>.colGroup(path: ColumnPath): SingleColumn<DataRow<C>> =
        ensureIsColGroup()
            .transformSingle {
                it.getChild(path)
                    ?.cast<DataRow<C>>()
                    ?.also { it.data.ensureIsColumnGroup() }
                    ?.let(::listOf)
                    ?: throw IllegalStateException("Column group '$path' not found in column group '${it.path}'")
            }.singleImpl()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("pathTo"["columnGroupName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("pathTo"["columnGroupName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [path] The path to the value column. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun AnyColumnGroupAccessor.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("pathTo"["columnGroupName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("pathTo"["columnGroupName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [path] The path to the value column. 
     * @param [C] The type of the column group.
     */
    public fun <C> AnyColumnGroupAccessor.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        ensureIsColGroup().columnGroup<C>(path).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("pathTo"["columnGroupName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("pathTo"["columnGroupName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [path] The path to the value column. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun String.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("pathTo"["columnGroupName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("pathTo"["columnGroupName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [path] The path to the value column. 
     * @param [C] The type of the column group.
     */
    public fun <C> String.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(path).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("pathTo"["columnGroupName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("pathTo"["columnGroupName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [path] The path to the value column. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun KProperty<*>.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("pathTo"["columnGroupName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("pathTo"["columnGroupName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [path] The path to the value column. 
     * @param [C] The type of the column group.
     */
    public fun <C> KProperty<*>.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(path).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("pathTo"["columnGroupName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("pathTo"["columnGroupName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [path] The path to the value column. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTypedKPropertyDataRow")
    public fun KProperty<DataRow<*>>.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("pathTo"["columnGroupName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("pathTo"["columnGroupName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [path] The path to the value column. 
     * @param [C] The type of the column group.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupKPropertyDataRow")
    public fun <C> KProperty<DataRow<*>>.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(path).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("pathTo"["columnGroupName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("pathTo"["columnGroupName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [path] The path to the value column. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun ColumnPath.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`("pathTo"["columnGroupName"] ) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("pathTo"["columnGroupName"] ) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [path] The path to the value column. 
     * @param [C] The type of the column group.
     */
    public fun <C> ColumnPath.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(path).ensureIsColumnGroup()

    // endregion

    // region property

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { {@getArg [CommonColGroupDocs.ReceiverArg]}`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(Type::columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [property\] The [KProperty] reference to the value column.
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    private interface ColGroupKPropertyDocs

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(Type::columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the column group. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> colGroup(property: KProperty<DataRow<C>>): SingleColumn<DataRow<C>> =
        columnGroup(property).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(Type::columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the column group. 
     */
    public fun <C> colGroup(property: KProperty<C>): SingleColumn<DataRow<C>> =
        columnGroup(property).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(Type::columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the column group. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> SingleColumn<DataRow<*>>.colGroup(property: KProperty<DataRow<C>>): SingleColumn<DataRow<C>> =
        colGroup<C>(property.name)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(Type::columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the column group. 
     */
    public fun <C> SingleColumn<DataRow<*>>.colGroup(property: KProperty<C>): SingleColumn<DataRow<C>> =
        colGroup<C>(property.name)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(Type::columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the column group. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> AnyColumnGroupAccessor.colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        ensureIsColGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(Type::columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the column group. 
     */
    public fun <C> AnyColumnGroupAccessor.colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> =
        ensureIsColGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(Type::columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the column group. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> String.colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(Type::columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the column group. 
     */
    public fun <C> String.colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(Type::columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the column group. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> KProperty<*>.colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(Type::columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the column group. 
     */
    public fun <C> KProperty<*>.colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(Type::columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the column group. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> ColumnPath.colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(Type::columnGroupA) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [property] The [KProperty] reference to the value column.
     * @param [C] The type of the column group. 
     */
    public fun <C> ColumnPath.colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup(property).ensureIsColumnGroup()

    // endregion

    // region index

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { {@getArg [CommonColGroupDocs.ReceiverArg]}`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { {@getArg [CommonColGroupDocs.ReceiverArg]}`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [index\] The index of the value column.
     * @throws [IndexOutOfBoundsException\] if the index is out of bounds.
     */
    private interface ColGroupIndexDocs

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds. 
     * @param [C] The type of the column group.
     *
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("ColumnSetDataRowColGroupIndex")
    public fun <C> ColumnSet<DataRow<C>>.colGroup(index: Int): SingleColumn<DataRow<C>> =
        getAt(index).ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds. 
     * @param [C] The type of the column group.
     *
     */
    public fun ColumnSet<*>.colGroup(index: Int): SingleColumn<DataRow<*>> =
        getAt(index).cast<DataRow<*>>().ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun ColumnsSelectionDsl<*>.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds. 
     * @param [C] The type of the column group.
     */
    public fun <C> ColumnsSelectionDsl<*>.colGroup(index: Int): SingleColumn<DataRow<C>> =
        asSingleColumn().colGroup<C>(index)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun SingleColumn<DataRow<*>>.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds. 
     * @param [C] The type of the column group.
     */
    public fun <C> SingleColumn<DataRow<*>>.colGroup(index: Int): SingleColumn<DataRow<C>> =
        ensureIsColGroup()
            .allColumnsInternal()
            .getAt(index)
            .cast<DataRow<C>>()
            .ensureIsColumnGroup()

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun String.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds. 
     * @param [C] The type of the column group.
     */
    public fun <C> String.colGroup(index: Int): SingleColumn<DataRow<C>> =
        asColumnGroup().colGroup<C>(index)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun KProperty<*>.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds. 
     * @param [C] The type of the column group.
     */
    public fun <C> KProperty<*>.colGroup(index: Int): SingleColumn<DataRow<C>> =
        asColumnGroup().colGroup<C>(index)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTypedKPropertyDataRow")
    public fun KProperty<DataRow<*>>.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds. 
     * @param [C] The type of the column group.
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupKPropertyDataRow")
    public fun <C> KProperty<DataRow<*>>.colGroup(index: Int): SingleColumn<DataRow<C>> =
        asColumnGroup().colGroup<C>(index)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds. 
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun ColumnPath.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * ## Col Group
     *
     * Creates a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] (or [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]) for a column group with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath], [KProperty], or [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]).
     *
     * This is a DSL-shorthand for [columnGroup][org.jetbrains.kotlinx.dataframe.api.columnGroup] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a column group.
     * The function can also be called on [ColumnGroups][org.jetbrains.kotlinx.dataframe.ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>("colGroupA") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(SomeType::colGroupB) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(1) }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`(0) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myColumnGroup"].`[colGroup][org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.colGroup]`<`[String][String]`>(0) }`
     *
     * To create a [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for another kind of column, take a look at the functions
     * [col][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col],
     * [valueCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol],
     * and [frameCol][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor] for the column group with the given argument if possible, else a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn].
     * @throws [IllegalStateException] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException] if the column with the given argument is not a column group.
     *
     * @see [columnGroup]
     * @see [ColumnsSelectionDsl.frameCol]
     * @see [ColumnsSelectionDsl.valueCol]
     * @see [ColumnsSelectionDsl.col]
     *
     *
     *
     * @param [index] The index of the value column.
     * @throws [IndexOutOfBoundsException] if the index is out of bounds. 
     * @param [C] The type of the column group.
     */
    public fun <C> ColumnPath.colGroup(index: Int): SingleColumn<DataRow<C>> =
        asColumnGroup().colGroup<C>(index)

    // endregion
}

/**
 * Checks the validity of this [SingleColumn],
 * by adding a check to see it's a [ColumnGroup] (so, a [SingleColumn]<*>)
 * and throwing an [IllegalArgumentException] if it's not.
 */
internal fun <C> SingleColumn<DataRow<C>>.ensureIsColumnGroup(): SingleColumn<DataRow<C>> =
    performCheck { col: ColumnWithPath<*>? ->
        require(col?.isColumnGroup() != false) {
            "Attempted to perform a ColumnGroup operation on ${col?.kind()} ${col?.path}."
        }
    }

/** Checks the validity of this [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn],
 * by adding a check to see it's a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] (so, a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]<*>)
 * and throwing an [IllegalArgumentException] if it's not. */
internal fun <C> ColumnAccessor<DataRow<C>>.ensureIsColumnGroup(): ColumnAccessor<DataRow<C>> =
    performCheck { col: ColumnWithPath<*>? ->
        require(col?.isColumnGroup() != false) {
            "Attempted to perform a ColumnGroup operation on ${col?.kind()} ${col?.path}."
        }
    }

// endregion
