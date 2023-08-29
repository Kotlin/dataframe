package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.ColumnGroupReference
import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.Usage.ColumnGroupName
import org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.Usage.ColumnSetName
import org.jetbrains.kotlinx.dataframe.api.ColGroupColumnsSelectionDsl.Usage.PlainDslName
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.getAt
import org.jetbrains.kotlinx.dataframe.impl.columns.onResolve
import org.jetbrains.kotlinx.dataframe.impl.columns.singleImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_GROUP
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_GROUP_REPLACE
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

public interface ColGroupColumnsSelectionDsl {

    /**
     * ## Col Group Usage
     *
     * @include [UsageTemplate]
     * {@setArg [UsageTemplate.DefinitionsArg]
     *  {@include [UsageTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.IndexDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnTypeDef]}
     * }
     *
     * {@setArg [UsageTemplate.PlainDslFunctionsArg]
     *  {@include [PlainDslName]}`[`**`<`**{@include [UsageTemplate.ColumnTypeRef]}**`>`**`]`**`(`**{@include [UsageTemplate.ColumnRef]}` | `{@include [UsageTemplate.IndexRef]}**`)`**
     * }
     *
     * {@setArg [UsageTemplate.ColumnSetFunctionsArg]
     *  {@include [Indent]}{@include [ColumnSetName]}**`(`**{@include [UsageTemplate.IndexRef]}**`)`**
     * }
     *
     * {@setArg [UsageTemplate.ColumnGroupFunctionsArg]
     *  {@include [Indent]}{@include [ColumnGroupName]}`[`**`<`**{@include [UsageTemplate.ColumnTypeRef]}**`>`**`]`**`(`**{@include [UsageTemplate.ColumnRef]}` | `{@include [UsageTemplate.IndexRef]}**`)`**
     * }
     */
    public interface Usage {

        /** [**colGroup**][ColumnsSelectionDsl.colGroup] */
        public interface PlainDslName

        /** .[**colGroup**][ColumnsSelectionDsl.colGroup] */
        public interface ColumnSetName

        /** .[**colGroup**][ColumnsSelectionDsl.colGroup] */
        public interface ColumnGroupName
    }

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
     * {@include [LineBreak]}
     * {@getArg [CommonColGroupDocs.Note]}
     *
     * Check out [Usage] for how to use [colGroup].
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
     * {@setArg [CommonColGroupDocs.Note]}
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
     * @include [CommonColGroupDocs]
     * {@setArg [CommonColGroupDocs.Arg] columnGroupA}
     * {@setArg [CommonColGroupDocs.ExampleArg] {@include [CommonColGroupDocs.SingleExample]}}
     * @param [col\] The [ColumnAccessor] pointing to the value column.
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    private interface ColGroupReferenceDocs

    /**
     * @include [ColGroupReferenceDocs] {@setArg [CommonColGroupDocs.ReceiverArg]}
     */
    public fun <C> colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        colGroup.ensureIsColumnGroup()

    /**
     * @include [ColGroupReferenceDocs] {@setArg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> SingleColumn<DataRow<*>>.colGroup(colGroup: ColumnAccessor<DataRow<C>>): SingleColumn<DataRow<C>> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getChild(colGroup)
                ?: throw IllegalStateException("ColumnGroup '${colGroup.path()}' not found in column group '${it.path}'")
            child.data.ensureIsColumnGroup()
            listOf(child)
        }.singleImpl()

    /**
     * @include [ColGroupReferenceDocs] {@setArg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> AnyColumnGroupAccessor.colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        this.ensureIsColumnGroup().columnGroup<C>(colGroup.path()).ensureIsColumnGroup()

    /**
     * @include [ColGroupReferenceDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     */
    public fun <C> String.colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        columnGroup(this).ensureIsColumnGroup().columnGroup<C>(colGroup.path()).ensureIsColumnGroup()

    /**
     * @include [ColGroupReferenceDocs] {@setArg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     */
    public fun <C> KProperty<*>.colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        columnGroup(this).ensureIsColumnGroup().columnGroup<C>(colGroup.path()).ensureIsColumnGroup()

    /**
     * @include [ColGroupReferenceDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    public fun <C> ColumnPath.colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        columnGroup(this).ensureIsColumnGroup().columnGroup<C>(colGroup.path()).ensureIsColumnGroup()

    // endregion

    // region name

    /**
     * @include [CommonColGroupDocs]
     * {@setArg [CommonColGroupDocs.Arg] "columnGroupName"}
     * {@setArg [CommonColGroupDocs.ExampleArg] {@include [CommonColGroupDocs.DoubleExample]}}
     * @param [name\] The name of the value column.
     */
    private interface ColGroupNameDocs

    /**
     * @include [ColGroupNameDocs] {@setArg [CommonColGroupDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun colGroup(name: String): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(name).ensureIsColumnGroup()

    /**
     * @include [ColGroupNameDocs] {@setArg [CommonColGroupDocs.ReceiverArg]}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> colGroup(name: String): ColumnAccessor<DataRow<C>> = columnGroup<C>(name).ensureIsColumnGroup()

    /**
     * @include [ColGroupNameDocs] {@setArg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun SingleColumn<DataRow<*>>.colGroup(name: String): SingleColumn<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * @include [ColGroupNameDocs] {@setArg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.colGroup(name: String): SingleColumn<DataRow<C>> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getChild(name)?.cast<DataRow<C>>()
                ?: throw IllegalStateException("Column group '$name' not found in column group '${it.path}'")
            child.data.ensureIsColumnGroup()
            listOf(child)
        }.singleImpl()

    /**
     * @include [ColGroupNameDocs] {@setArg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun AnyColumnGroupAccessor.colGroup(name: String): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * @include [ColGroupNameDocs] {@setArg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> AnyColumnGroupAccessor.colGroup(name: String): ColumnAccessor<DataRow<C>> =
        this.ensureIsColumnGroup().columnGroup<C>(name).ensureIsColumnGroup()

    /**
     * @include [ColGroupNameDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun String.colGroup(name: String): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * @include [ColGroupNameDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> String.colGroup(name: String): ColumnAccessor<DataRow<C>> =
        columnGroup(this).ensureIsColumnGroup().columnGroup<C>(name).ensureIsColumnGroup()

    /**
     * @include [ColGroupNameDocs] {@setArg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun KProperty<*>.colGroup(name: String): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * @include [ColGroupNameDocs] {@setArg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> KProperty<*>.colGroup(name: String): ColumnAccessor<DataRow<C>> =
        columnGroup(this).ensureIsColumnGroup().columnGroup<C>(name).ensureIsColumnGroup()

    /**
     * @include [ColGroupNameDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun ColumnPath.colGroup(name: String): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * @include [ColGroupNameDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> ColumnPath.colGroup(name: String): ColumnAccessor<DataRow<C>> =
        columnGroup(this).ensureIsColumnGroup().columnGroup<C>(name).ensureIsColumnGroup()

    // endregion

    // region path

    /**
     * @include [CommonColGroupDocs]
     * {@setArg [CommonColGroupDocs.Arg] "pathTo"["columnGroupName"\] }
     * {@setArg [CommonColGroupDocs.ExampleArg] {@include [CommonColGroupDocs.DoubleExample]}}
     * @param [path\] The path to the value column.
     */
    private interface ColGroupPathDocs

    /**
     * @include [ColGroupPathDocs] {@setArg [CommonColGroupDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(path).ensureIsColumnGroup()

    /**
     * @include [ColGroupPathDocs] {@setArg [CommonColGroupDocs.ReceiverArg]}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> = columnGroup<C>(path).ensureIsColumnGroup()

    /**
     * @include [ColGroupPathDocs] {@setArg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun SingleColumn<DataRow<*>>.colGroup(path: ColumnPath): SingleColumn<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * @include [ColGroupPathDocs] {@setArg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.colGroup(path: ColumnPath): SingleColumn<DataRow<C>> =
        this.ensureIsColumnGroup()
            .transformSingle {
                val child = it.getChild(path)?.cast<DataRow<C>>()
                    ?: throw IllegalStateException("Column group '$path' not found in column group '${it.path}'")
                child.data.ensureIsColumnGroup()
                listOf(child)
            }.singleImpl()

    /**
     * @include [ColGroupPathDocs] {@setArg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun AnyColumnGroupAccessor.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * @include [ColGroupPathDocs] {@setArg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> AnyColumnGroupAccessor.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        this.ensureIsColumnGroup().columnGroup<C>(path).ensureIsColumnGroup()

    /**
     * @include [ColGroupPathDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun String.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * @include [ColGroupPathDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> String.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        columnGroup(this).ensureIsColumnGroup().columnGroup<C>(path).ensureIsColumnGroup()

    /**
     * @include [ColGroupPathDocs] {@setArg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun KProperty<*>.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * @include [ColGroupPathDocs] {@setArg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> KProperty<*>.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        columnGroup(this).ensureIsColumnGroup().columnGroup<C>(path).ensureIsColumnGroup()

    /**
     * @include [ColGroupPathDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun ColumnPath.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * @include [ColGroupPathDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> ColumnPath.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        columnGroup(this).ensureIsColumnGroup().columnGroup<C>(path).ensureIsColumnGroup()

    // endregion

    // region property

    /**
     * @include [CommonColGroupDocs]
     * {@setArg [CommonColGroupDocs.Arg] Type::columnGroupA}
     * {@setArg [CommonColGroupDocs.ExampleArg] {@include [CommonColGroupDocs.SingleExample]}}
     * @param [property\] The [KProperty] reference to the value column.
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    private interface ColGroupKPropertyDocs

    /**
     * @include [ColGroupKPropertyDocs] {@setArg [CommonColGroupDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> colGroup(property: KProperty<DataRow<C>>): SingleColumn<DataRow<C>> =
        columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@setArg [CommonColGroupDocs.ReceiverArg]}
     */
    public fun <C> colGroup(property: KProperty<C>): SingleColumn<DataRow<C>> =
        columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@setArg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> SingleColumn<DataRow<*>>.colGroup(property: KProperty<DataRow<C>>): SingleColumn<DataRow<C>> =
        colGroup<C>(property.name)

    /**
     * @include [ColGroupKPropertyDocs] {@setArg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> SingleColumn<DataRow<*>>.colGroup(property: KProperty<C>): SingleColumn<DataRow<C>> =
        colGroup<C>(property.name)

    /**
     * @include [ColGroupKPropertyDocs] {@setArg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> AnyColumnGroupAccessor.colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        this.ensureIsColumnGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@setArg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> AnyColumnGroupAccessor.colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> =
        this.ensureIsColumnGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> String.colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        columnGroup(this).ensureIsColumnGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     */
    public fun <C> String.colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> =
        columnGroup(this).ensureIsColumnGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@setArg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> KProperty<*>.colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        columnGroup(this).ensureIsColumnGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@setArg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     */
    public fun <C> KProperty<*>.colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> =
        columnGroup(this).ensureIsColumnGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> ColumnPath.colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        columnGroup(this).ensureIsColumnGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    public fun <C> ColumnPath.colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> =
        columnGroup(this).ensureIsColumnGroup().columnGroup(property).ensureIsColumnGroup()

    // endregion

    // region index

    /**
     * @include [CommonColGroupDocs]
     * {@setArg [CommonColGroupDocs.Arg] 0}
     * {@setArg [CommonColGroupDocs.ExampleArg] {@include [CommonColGroupDocs.DoubleExample]}}
     * @param [index\] The index of the value column.
     * @throws [IndexOutOfBoundsException\] if the index is out of bounds.
     */
    private interface ColGroupIndexDocs

    /**
     * @include [ColGroupIndexDocs] {@setArg [CommonColGroupDocs.ReceiverArg] `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     * {@setArg [CommonColGroupDocs.ExampleArg] {@include [CommonColGroupDocs.SingleExample]}}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("ColumnSetDataRowColGroupIndex")
    public fun <C> ColumnSet<DataRow<C>>.colGroup(index: Int): SingleColumn<DataRow<C>> =
        getAt(index).ensureIsColumnGroup()

    /**
     * @include [ColGroupIndexDocs] {@setArg [CommonColGroupDocs.ReceiverArg] `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     * {@setArg [CommonColGroupDocs.ExampleArg] {@include [CommonColGroupDocs.SingleExample]}}
     */
    public fun ColumnSet<*>.colGroup(index: Int): SingleColumn<DataRow<*>> =
        getAt(index).cast<DataRow<*>>().ensureIsColumnGroup()

    /**
     * @include [ColGroupIndexDocs] {@setArg [CommonColGroupDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun ColumnsSelectionDsl<*>.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * @include [ColGroupIndexDocs] {@setArg [CommonColGroupDocs.ReceiverArg]}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> ColumnsSelectionDsl<*>.colGroup(index: Int): SingleColumn<DataRow<C>> =
        asSingleColumn().colGroup<C>(index)

    /**
     * @include [ColGroupIndexDocs] {@setArg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun SingleColumn<DataRow<*>>.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * @include [ColGroupIndexDocs] {@setArg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.colGroup(index: Int): SingleColumn<DataRow<C>> =
        this.ensureIsColumnGroup()
            .allColumnsInternal()
            .getAt(index)
            .cast<DataRow<C>>()
            .ensureIsColumnGroup()

    /**
     * @include [ColGroupIndexDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun String.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * @include [ColGroupIndexDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> String.colGroup(index: Int): SingleColumn<DataRow<C>> =
        columnGroup(this).colGroup<C>(index)

    /**
     * @include [ColGroupIndexDocs] {@setArg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun KProperty<*>.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * @include [ColGroupIndexDocs] {@setArg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> KProperty<*>.colGroup(index: Int): SingleColumn<DataRow<C>> =
        columnGroup(this).colGroup<C>(index)

    /**
     * @include [ColGroupIndexDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun ColumnPath.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * @include [ColGroupIndexDocs] {@setArg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> ColumnPath.colGroup(index: Int): SingleColumn<DataRow<C>> =
        columnGroup(this).colGroup<C>(index)

    // endregion

    // region deprecated

    @Deprecated(COL_SELECT_DSL_GROUP, ReplaceWith(COL_SELECT_DSL_GROUP_REPLACE), DeprecationLevel.WARNING)
    public fun ColumnsContainer<*>.group(name: String): ColumnGroupReference = name.toColumnOf()

    // endregion
}

/**
 * Checks the validity of this [SingleColumn],
 * by adding a check to see it's a [ColumnGroup] (so, a [SingleColumn]<*>)
 * and throwing an [IllegalArgumentException] if it's not.
 */
@PublishedApi
internal fun <C> SingleColumn<DataRow<C>>.ensureIsColumnGroup(): SingleColumn<DataRow<C>> =
    onResolve { col: ColumnWithPath<*>? ->
        require(col?.isColumnGroup() != false) {
            "Column at ${col?.path} is not a ColumnGroup, but a ${col?.kind()}."
        }
    }

/** @include [SingleColumn.ensureIsColumnGroup] */
internal fun <C> ColumnAccessor<DataRow<C>>.ensureIsColumnGroup(): ColumnAccessor<DataRow<C>> =
    onResolve { col: ColumnWithPath<*>? ->
        require(col?.isColumnGroup() != false) {
            "Column at ${col?.path} is not a ColumnGroup, but a ${col?.kind()}."
        }
    }

// endregion
