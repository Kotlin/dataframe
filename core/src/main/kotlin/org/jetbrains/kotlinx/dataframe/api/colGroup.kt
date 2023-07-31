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
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][ColumnGroupReference] to create
     * an accessor for a column group inside a [ColumnGroup].
     * {@include [LineBreak]}
     * {@includeArg [CommonColGroupDocs.Note]}
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
     * {@includeArg [CommonColGroupDocs.ExampleArg]}
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
     * {@arg [CommonColGroupDocs.Note]}
     */
    private interface CommonColGroupDocs {

        /* Example argument, can be either {@include [SingleExample]} or {@include [DoubleExample]} */
        interface ExampleArg

        /**
         * `df.`[select][DataFrame.select]` { {@includeArg [CommonColGroupDocs.ReceiverArg]}`[colGroup][colGroup]`({@includeArg [CommonColGroupDocs.Arg]}) \\\\}`
         */
        interface SingleExample

        /**
         * `df.`[select][DataFrame.select]` { {@includeArg [CommonColGroupDocs.ReceiverArg]}`[colGroup][colGroup]`({@includeArg [CommonColGroupDocs.Arg]}) \\\\}`
         *
         * `df.`[select][DataFrame.select]` { {@includeArg [CommonColGroupDocs.ReceiverArg]}`[colGroup][colGroup]`<`[String][String]`>({@includeArg [CommonColGroupDocs.Arg]}) \\\\}`
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
     * {@arg [CommonColGroupDocs.Arg] columnGroupA}
     * {@arg [CommonColGroupDocs.ExampleArg] {@include [CommonColGroupDocs.SingleExample]}}
     * @param [col\] The [ColumnAccessor] pointing to the value column.
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    private interface ColGroupReferenceDocs

    /**
     * @include [ColGroupReferenceDocs] {@arg [CommonColGroupDocs.ReceiverArg]}
     */
    public fun <C> colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        colGroup.ensureIsColumnGroup()

    /**
     * @include [ColGroupReferenceDocs] {@arg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
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
     * @include [ColGroupReferenceDocs] {@arg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> AnyColumnGroupAccessor.colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        ensureIsColGroup().columnGroup<C>(colGroup.path()).ensureIsColumnGroup()

    /**
     * @include [ColGroupReferenceDocs] {@arg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     */
    public fun <C> String.colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(colGroup.path()).ensureIsColumnGroup()

    /**
     * @include [ColGroupReferenceDocs] {@arg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     */
    public fun <C> KProperty<*>.colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(colGroup.path()).ensureIsColumnGroup()

    /**
     * @include [ColGroupReferenceDocs] {@arg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupKPropertyDataRow")
    public fun <C> KProperty<DataRow<*>>.colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(colGroup.path()).ensureIsColumnGroup()

    /**
     * @include [ColGroupReferenceDocs] {@arg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    public fun <C> ColumnPath.colGroup(colGroup: ColumnAccessor<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(colGroup.path()).ensureIsColumnGroup()

    // endregion

    // region name

    /**
     * @include [CommonColGroupDocs]
     * {@arg [CommonColGroupDocs.Arg] "columnGroupName"}
     * {@arg [CommonColGroupDocs.ExampleArg] {@include [CommonColGroupDocs.DoubleExample]}}
     * @param [name\] The name of the value column.
     */
    private interface ColGroupNameDocs

    /**
     * @include [ColGroupNameDocs] {@arg [CommonColGroupDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun colGroup(name: String): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(name).ensureIsColumnGroup()

    /**
     * @include [ColGroupNameDocs] {@arg [CommonColGroupDocs.ReceiverArg]}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> colGroup(name: String): ColumnAccessor<DataRow<C>> = columnGroup<C>(name).ensureIsColumnGroup()

    /**
     * @include [ColGroupNameDocs] {@arg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun SingleColumn<DataRow<*>>.colGroup(name: String): SingleColumn<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * @include [ColGroupNameDocs] {@arg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
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
     * @include [ColGroupNameDocs] {@arg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun AnyColumnGroupAccessor.colGroup(name: String): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * @include [ColGroupNameDocs] {@arg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> AnyColumnGroupAccessor.colGroup(name: String): ColumnAccessor<DataRow<C>> =
        ensureIsColGroup().columnGroup<C>(name).ensureIsColumnGroup()

    /**
     * @include [ColGroupNameDocs] {@arg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun String.colGroup(name: String): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * @include [ColGroupNameDocs] {@arg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> String.colGroup(name: String): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(name).ensureIsColumnGroup()

    /**
     * @include [ColGroupNameDocs] {@arg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun KProperty<*>.colGroup(name: String): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * @include [ColGroupNameDocs] {@arg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> KProperty<*>.colGroup(name: String): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(name).ensureIsColumnGroup()

    /**
     * @include [ColGroupNameDocs] {@arg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTypedKPropertyDataRow")
    public fun KProperty<DataRow<*>>.colGroup(name: String): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * @include [ColGroupNameDocs] {@arg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupKPropertyDataRow")
    public fun <C> KProperty<DataRow<*>>.colGroup(name: String): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(name).ensureIsColumnGroup()

    /**
     * @include [ColGroupNameDocs] {@arg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun ColumnPath.colGroup(name: String): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(name)

    /**
     * @include [ColGroupNameDocs] {@arg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> ColumnPath.colGroup(name: String): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(name).ensureIsColumnGroup()

    // endregion

    // region path

    /**
     * @include [CommonColGroupDocs]
     * {@arg [CommonColGroupDocs.Arg] "pathTo"["columnGroupName"\] }
     * {@arg [CommonColGroupDocs.ExampleArg] {@include [CommonColGroupDocs.DoubleExample]}}
     * @param [path\] The path to the value column.
     */
    private interface ColGroupPathDocs

    /**
     * @include [ColGroupPathDocs] {@arg [CommonColGroupDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> = columnGroup<Any?>(path).ensureIsColumnGroup()

    /**
     * @include [ColGroupPathDocs] {@arg [CommonColGroupDocs.ReceiverArg]}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> = columnGroup<C>(path).ensureIsColumnGroup()

    /**
     * @include [ColGroupPathDocs] {@arg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun SingleColumn<DataRow<*>>.colGroup(path: ColumnPath): SingleColumn<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * @include [ColGroupPathDocs] {@arg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
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
     * @include [ColGroupPathDocs] {@arg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun AnyColumnGroupAccessor.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * @include [ColGroupPathDocs] {@arg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> AnyColumnGroupAccessor.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        ensureIsColGroup().columnGroup<C>(path).ensureIsColumnGroup()

    /**
     * @include [ColGroupPathDocs] {@arg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun String.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * @include [ColGroupPathDocs] {@arg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> String.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(path).ensureIsColumnGroup()

    /**
     * @include [ColGroupPathDocs] {@arg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun KProperty<*>.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * @include [ColGroupPathDocs] {@arg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> KProperty<*>.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(path).ensureIsColumnGroup()

    /**
     * @include [ColGroupPathDocs] {@arg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTypedKPropertyDataRow")
    public fun KProperty<DataRow<*>>.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * @include [ColGroupPathDocs] {@arg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupKPropertyDataRow")
    public fun <C> KProperty<DataRow<*>>.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(path).ensureIsColumnGroup()

    /**
     * @include [ColGroupPathDocs] {@arg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun ColumnPath.colGroup(path: ColumnPath): ColumnAccessor<DataRow<*>> =
        colGroup<Any?>(path)

    /**
     * @include [ColGroupPathDocs] {@arg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> ColumnPath.colGroup(path: ColumnPath): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup<C>(path).ensureIsColumnGroup()

    // endregion

    // region property

    /**
     * @include [CommonColGroupDocs]
     * {@arg [CommonColGroupDocs.Arg] Type::columnGroupA}
     * {@arg [CommonColGroupDocs.ExampleArg] {@include [CommonColGroupDocs.SingleExample]}}
     * @param [property\] The [KProperty] reference to the value column.
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    private interface ColGroupKPropertyDocs

    /**
     * @include [ColGroupKPropertyDocs] {@arg [CommonColGroupDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> colGroup(property: KProperty<DataRow<C>>): SingleColumn<DataRow<C>> =
        columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@arg [CommonColGroupDocs.ReceiverArg]}
     */
    public fun <C> colGroup(property: KProperty<C>): SingleColumn<DataRow<C>> =
        columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@arg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> SingleColumn<DataRow<*>>.colGroup(property: KProperty<DataRow<C>>): SingleColumn<DataRow<C>> =
        colGroup<C>(property.name)

    /**
     * @include [ColGroupKPropertyDocs] {@arg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> SingleColumn<DataRow<*>>.colGroup(property: KProperty<C>): SingleColumn<DataRow<C>> =
        colGroup<C>(property.name)

    /**
     * @include [ColGroupKPropertyDocs] {@arg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> AnyColumnGroupAccessor.colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        ensureIsColGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@arg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> AnyColumnGroupAccessor.colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> =
        ensureIsColGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@arg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> String.colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@arg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     */
    public fun <C> String.colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@arg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> KProperty<*>.colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@arg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     */
    public fun <C> KProperty<*>.colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@arg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupDataRowKProperty")
    public fun <C> ColumnPath.colGroup(property: KProperty<DataRow<C>>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup(property).ensureIsColumnGroup()

    /**
     * @include [ColGroupKPropertyDocs] {@arg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    public fun <C> ColumnPath.colGroup(property: KProperty<C>): ColumnAccessor<DataRow<C>> =
        asColumnGroup().ensureIsColGroup().columnGroup(property).ensureIsColumnGroup()

    // endregion

    // region index

    /**
     * @include [CommonColGroupDocs]
     * {@arg [CommonColGroupDocs.Arg] 0}
     * {@arg [CommonColGroupDocs.ExampleArg] {@include [CommonColGroupDocs.DoubleExample]}}
     * @param [index\] The index of the value column.
     * @throws [IndexOutOfBoundsException\] if the index is out of bounds.
     */
    private interface ColGroupIndexDocs

    /**
     * @include [ColGroupIndexDocs] {@arg [CommonColGroupDocs.ReceiverArg] `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     * {@arg [CommonColGroupDocs.ExampleArg] {@include [CommonColGroupDocs.SingleExample]}}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("ColumnSetDataRowColGroupIndex")
    public fun <C> ColumnSet<DataRow<C>>.colGroup(index: Int): SingleColumn<DataRow<C>> =
        getAt(index).ensureIsColumnGroup()

    /**
     * @include [ColGroupIndexDocs] {@arg [CommonColGroupDocs.ReceiverArg] `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     * {@arg [CommonColGroupDocs.ExampleArg] {@include [CommonColGroupDocs.SingleExample]}}
     */
    public fun ColumnSet<*>.colGroup(index: Int): SingleColumn<DataRow<*>> =
        getAt(index).cast<DataRow<*>>().ensureIsColumnGroup()

    /**
     * @include [ColGroupIndexDocs] {@arg [CommonColGroupDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun ColumnsSelectionDsl<*>.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * @include [ColGroupIndexDocs] {@arg [CommonColGroupDocs.ReceiverArg]}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> ColumnsSelectionDsl<*>.colGroup(index: Int): SingleColumn<DataRow<C>> =
        asSingleColumn().colGroup<C>(index)

    /**
     * @include [ColGroupIndexDocs] {@arg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun SingleColumn<DataRow<*>>.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * @include [ColGroupIndexDocs] {@arg [CommonColGroupDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.colGroup(index: Int): SingleColumn<DataRow<C>> =
        ensureIsColGroup()
            .allColumnsInternal()
            .getAt(index)
            .cast<DataRow<C>>()
            .ensureIsColumnGroup()

    /**
     * @include [ColGroupIndexDocs] {@arg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun String.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * @include [ColGroupIndexDocs] {@arg [CommonColGroupDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> String.colGroup(index: Int): SingleColumn<DataRow<C>> =
        asColumnGroup().colGroup<C>(index)

    /**
     * @include [ColGroupIndexDocs] {@arg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun KProperty<*>.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * @include [ColGroupIndexDocs] {@arg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    public fun <C> KProperty<*>.colGroup(index: Int): SingleColumn<DataRow<C>> =
        asColumnGroup().colGroup<C>(index)

    /**
     * @include [ColGroupIndexDocs] {@arg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTypedKPropertyDataRow")
    public fun KProperty<DataRow<*>>.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * @include [ColGroupIndexDocs] {@arg [CommonColGroupDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupKPropertyDataRow")
    public fun <C> KProperty<DataRow<*>>.colGroup(index: Int): SingleColumn<DataRow<C>> =
        asColumnGroup().colGroup<C>(index)

    /**
     * @include [ColGroupIndexDocs] {@arg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colGroupUnTyped")
    public fun ColumnPath.colGroup(index: Int): SingleColumn<DataRow<*>> =
        colGroup<Any?>(index)

    /**
     * @include [ColGroupIndexDocs] {@arg [CommonColGroupDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonColGroupDocs.ColumnGroupTypeParam]
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

/** @include [SingleColumn.ensureIsColumnGroup] */
internal fun <C> ColumnAccessor<DataRow<C>>.ensureIsColumnGroup(): ColumnAccessor<DataRow<C>> =
    performCheck { col: ColumnWithPath<*>? ->
        require(col?.isColumnGroup() != false) {
            "Attempted to perform a ColumnGroup operation on ${col?.kind()} ${col?.path}."
        }
    }

// endregion
