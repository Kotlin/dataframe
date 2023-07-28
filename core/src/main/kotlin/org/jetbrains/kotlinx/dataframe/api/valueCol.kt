package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.ColumnGroupReference
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.getAt
import org.jetbrains.kotlinx.dataframe.impl.columns.singleImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

public interface ValueColColumnsSelectionDsl {

    /**
     * ## Value Col
     *
     * Creates a [ColumnAccessor] (or [SingleColumn]) for a value column with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath], [KProperty], or [ColumnAccessor]).
     *
     * This is a DSL-shorthand for [valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][ColumnGroupReference] to create
     * an accessor for a value column inside a [ColumnGroup].
     * {@include [LineBreak]}
     * {@includeArg [CommonValueColDocs.Note]}
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[valueCol][valueCol]`<`[String][String]`>("valueColA") }`
     *
     * `df.`[select][DataFrame.select]` { `[valueCol][valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[valueCol][valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [CommonValueColDocs.ExampleArg]}
     *
     * To create a [ColumnAccessor] for another kind of column, take a look at the functions
     * [col][ColumnsSelectionDsl.col],
     * [colGroup][ColumnsSelectionDsl.colGroup],
     * and [frameCol][ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor] for the value column with the given argument if possible, else a [SingleColumn].
     * @throws [IllegalStateException\] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException\] if the column with the given argument is not a value column.
     *
     * @see [valueColumn\]
     * @see [ColumnsSelectionDsl.colGroup\]
     * @see [ColumnsSelectionDsl.frameCol\]
     * @see [ColumnsSelectionDsl.col\]
     * {@arg [CommonValueColDocs.Note]}
     */
    private interface CommonValueColDocs {

        /* Example argument, can be either {@include [SingleExample]} or {@include [DoubleExample]} */
        interface ExampleArg

        /**
         * `df.`[select][DataFrame.select]` { {@includeArg [CommonValueColDocs.ReceiverArg]}`[valueCol][valueCol]`({@includeArg [CommonValueColDocs.Arg]}) \\\\}`
         */
        interface SingleExample

        /**
         * `df.`[select][DataFrame.select]` { {@includeArg [CommonValueColDocs.ReceiverArg]}`[valueCol][valueCol]`({@includeArg [CommonValueColDocs.Arg]}) \\\\}`
         *
         * `df.`[select][DataFrame.select]` { {@includeArg [CommonValueColDocs.ReceiverArg]}`[valueCol][valueCol]`<`[String][String]`>({@includeArg [CommonValueColDocs.Arg]}) \\\\}`
         */
        interface DoubleExample

        /* Receiver argument for the example(s) */
        interface ReceiverArg

        /* Argument for the example(s) */
        interface Arg

        /* Optional note */
        interface Note

        /** @param [C\] The type of the value column. */
        interface ValueColumnTypeParam
    }

    // region reference

    /**
     * @include [CommonValueColDocs]
     * {@arg [CommonValueColDocs.Arg] valueColumnA}
     * {@arg [CommonValueColDocs.ExampleArg] {@include [CommonValueColDocs.SingleExample]}}
     * @param [col\] The [ColumnAccessor] pointing to the value column.
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    private interface ValueColReferenceDocs

    /**
     * @include [ValueColReferenceDocs] {@arg [CommonValueColDocs.ReceiverArg]}
     */
    public fun <C> valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> = valueCol.ensureIsValueColumn()

    /**
     * @include [ValueColReferenceDocs] {@arg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(valueCol: ColumnAccessor<C>): SingleColumn<C> =
        ensureIsColGroup()
            .transformSingle {
                it.getChild(valueCol)
                    ?.cast<C>()
                    ?.also { it.data.ensureIsValueColumn() }
                    ?.let(::listOf)
                    ?: throw IllegalStateException("ValueColumn '${valueCol.path()}' not found in column group '${it.path}'")
            }.singleImpl()

    /**
     * @include [ValueColReferenceDocs] {@arg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> AnyColumnGroupAccessor.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        ensureIsColGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    /**
     * @include [ValueColReferenceDocs] {@arg [CommonValueColDocs.ReceiverArg] "myColumnGroup".}
     */
    public fun <C> String.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        asColumnGroup().ensureIsColGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    /**
     * @include [ValueColReferenceDocs] {@arg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    public fun <C> KProperty<*>.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        asColumnGroup().ensureIsColGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    /**
     * @include [ValueColReferenceDocs] {@arg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColKPropertyDataRow")
    public fun <C> KProperty<DataRow<*>>.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        asColumnGroup().ensureIsColGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    /**
     * @include [ValueColReferenceDocs] {@arg [CommonValueColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    public fun <C> ColumnPath.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        asColumnGroup().ensureIsColGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    // endregion

    // region name

    /**
     * @include [CommonValueColDocs]
     * {@arg [CommonValueColDocs.Arg] "valueColumnName"}
     * {@arg [CommonValueColDocs.ExampleArg] {@include [CommonValueColDocs.DoubleExample]}}
     * @param [name\] The name of the value column.
     */
    private interface ValueColNameDocs

    /**
     * @include [ValueColNameDocs] {@arg [CommonValueColDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun valueCol(name: String): ColumnAccessor<*> = valueColumn<Any?>(name).ensureIsValueColumn()

    /**
     * @include [ValueColNameDocs] {@arg [CommonValueColDocs.ReceiverArg]}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> valueCol(name: String): ColumnAccessor<C> = valueColumn<C>(name).ensureIsValueColumn()

    /**
     * @include [ValueColNameDocs] {@arg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun SingleColumn<DataRow<*>>.valueCol(name: String): SingleColumn<*> =
        valueCol<Any?>(name)

    /**
     * @include [ValueColNameDocs] {@arg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(name: String): SingleColumn<C> =
        ensureIsColGroup()
            .transformSingle {
                it.getChild(name)
                    ?.cast<C>()
                    ?.also { it.data.ensureIsValueColumn() }
                    ?.let(::listOf)
                    ?: throw IllegalStateException("Value column '$name' not found in column group '${it.path}'")
            }.singleImpl()

    /**
     * @include [ValueColNameDocs] {@arg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun AnyColumnGroupAccessor.valueCol(name: String): ColumnAccessor<*> =
        valueCol<Any?>(name)

    /**
     * @include [ValueColNameDocs] {@arg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> AnyColumnGroupAccessor.valueCol(name: String): ColumnAccessor<C> =
        ensureIsColGroup().valueColumn<C>(name).ensureIsValueColumn()

    /**
     * @include [ValueColNameDocs] {@arg [CommonValueColDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun String.valueCol(name: String): ColumnAccessor<*> =
        valueCol<Any?>(name)

    /**
     * @include [ValueColNameDocs] {@arg [CommonValueColDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> String.valueCol(name: String): ColumnAccessor<C> =
        asColumnGroup().ensureIsColGroup().valueColumn<C>(name).ensureIsValueColumn()

    /**
     * @include [ValueColNameDocs] {@arg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun KProperty<*>.valueCol(name: String): ColumnAccessor<*> =
        valueCol<Any?>(name)

    /**
     * @include [ValueColNameDocs] {@arg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> KProperty<*>.valueCol(name: String): ColumnAccessor<C> =
        asColumnGroup().ensureIsColGroup().valueColumn<C>(name).ensureIsValueColumn()

    /**
     * @include [ValueColNameDocs] {@arg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTypedKPropertyDataRow")
    public fun KProperty<DataRow<*>>.valueCol(name: String): ColumnAccessor<*> =
        valueCol<Any?>(name)

    /**
     * @include [ValueColNameDocs] {@arg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColKPropertyDataRow")
    public fun <C> KProperty<DataRow<*>>.valueCol(name: String): ColumnAccessor<C> =
        asColumnGroup().ensureIsColGroup().valueColumn<C>(name).ensureIsValueColumn()

    /**
     * @include [ValueColNameDocs] {@arg [CommonValueColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun ColumnPath.valueCol(name: String): ColumnAccessor<*> =
        valueCol<Any?>(name)

    /**
     * @include [ValueColNameDocs] {@arg [CommonValueColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> ColumnPath.valueCol(name: String): ColumnAccessor<C> =
        asColumnGroup().ensureIsColGroup().valueColumn<C>(name).ensureIsValueColumn()

    // endregion

    // region path

    /**
     * @include [CommonValueColDocs]
     * {@arg [CommonValueColDocs.Arg] "pathTo"["valueColumnName"\] }
     * {@arg [CommonValueColDocs.ExampleArg] {@include [CommonValueColDocs.DoubleExample]}}
     * @param [path\] The path to the value column.
     */
    private interface ValueColPathDocs

    /**
     * @include [ValueColPathDocs] {@arg [CommonValueColDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun valueCol(path: ColumnPath): ColumnAccessor<*> = valueColumn<Any?>(path).ensureIsValueColumn()

    /**
     * @include [ValueColPathDocs] {@arg [CommonValueColDocs.ReceiverArg]}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> valueCol(path: ColumnPath): ColumnAccessor<C> = valueColumn<C>(path).ensureIsValueColumn()

    /**
     * @include [ValueColPathDocs] {@arg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun SingleColumn<DataRow<*>>.valueCol(path: ColumnPath): SingleColumn<*> =
        valueCol<Any?>(path)

    /**
     * @include [ValueColPathDocs] {@arg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(path: ColumnPath): SingleColumn<C> =
        ensureIsColGroup()
            .transformSingle {
                it.getChild(path)
                    ?.cast<C>()
                    ?.also { it.data.ensureIsValueColumn() }
                    ?.let(::listOf)
                    ?: throw IllegalStateException("Value column '$path' not found in column group '${it.path}'")
            }.singleImpl()

    /**
     * @include [ValueColPathDocs] {@arg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun AnyColumnGroupAccessor.valueCol(path: ColumnPath): ColumnAccessor<*> =
        valueCol<Any?>(path)

    /**
     * @include [ValueColPathDocs] {@arg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> AnyColumnGroupAccessor.valueCol(path: ColumnPath): ColumnAccessor<C> =
        ensureIsColGroup().valueColumn<C>(path).ensureIsValueColumn()

    /**
     * @include [ValueColPathDocs] {@arg [CommonValueColDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun String.valueCol(path: ColumnPath): ColumnAccessor<*> =
        valueCol<Any?>(path)

    /**
     * @include [ValueColPathDocs] {@arg [CommonValueColDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> String.valueCol(path: ColumnPath): ColumnAccessor<C> =
        asColumnGroup().ensureIsColGroup().valueColumn<C>(path).ensureIsValueColumn()

    /**
     * @include [ValueColPathDocs] {@arg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun KProperty<*>.valueCol(path: ColumnPath): ColumnAccessor<*> =
        valueCol<Any?>(path)

    /**
     * @include [ValueColPathDocs] {@arg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> KProperty<*>.valueCol(path: ColumnPath): ColumnAccessor<C> =
        asColumnGroup().ensureIsColGroup().valueColumn<C>(path).ensureIsValueColumn()

    /**
     * @include [ValueColPathDocs] {@arg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTypedKPropertyDataRow")
    public fun KProperty<DataRow<*>>.valueCol(path: ColumnPath): ColumnAccessor<*> =
        valueCol<Any?>(path)

    /**
     * @include [ValueColPathDocs] {@arg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColKPropertyDataRow")
    public fun <C> KProperty<DataRow<*>>.valueCol(path: ColumnPath): ColumnAccessor<C> =
        asColumnGroup().ensureIsColGroup().valueColumn<C>(path).ensureIsValueColumn()

    /**
     * @include [ValueColPathDocs] {@arg [CommonValueColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun ColumnPath.valueCol(path: ColumnPath): ColumnAccessor<*> =
        valueCol<Any?>(path)

    /**
     * @include [ValueColPathDocs] {@arg [CommonValueColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> ColumnPath.valueCol(path: ColumnPath): ColumnAccessor<C> =
        asColumnGroup().ensureIsColGroup().valueColumn<C>(path).ensureIsValueColumn()

    // endregion

    // region property

    /**
     * @include [CommonValueColDocs]
     * {@arg [CommonValueColDocs.Arg] Type::valueColumnA}
     * {@arg [CommonValueColDocs.ExampleArg] {@include [CommonValueColDocs.SingleExample]}}
     * @param [property\] The [KProperty] reference to the value column.
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    private interface ValueColKPropertyDocs

    /**
     * @include [ValueColKPropertyDocs] {@arg [CommonValueColDocs.ReceiverArg]}
     */
    public fun <C> valueCol(property: KProperty<C>): SingleColumn<C> = valueColumn(property).ensureIsValueColumn()

    /**
     * @include [ValueColKPropertyDocs] {@arg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(property: KProperty<C>): SingleColumn<C> =
        valueCol<C>(property.name)

    /**
     * @include [ValueColKPropertyDocs] {@arg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> AnyColumnGroupAccessor.valueCol(property: KProperty<C>): ColumnAccessor<C> =
        ensureIsColGroup().valueColumn(property).ensureIsValueColumn()

    /**
     * @include [ValueColKPropertyDocs] {@arg [CommonValueColDocs.ReceiverArg] "myColumnGroup".}
     */
    public fun <C> String.valueCol(property: KProperty<C>): ColumnAccessor<C> =
        asColumnGroup().ensureIsColGroup().valueColumn(property).ensureIsValueColumn()

    /**
     * @include [ValueColKPropertyDocs] {@arg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    public fun <C> KProperty<*>.valueCol(property: KProperty<C>): ColumnAccessor<C> =
        asColumnGroup().ensureIsColGroup().valueColumn(property).ensureIsValueColumn()

    /**
     * @include [ValueColKPropertyDocs] {@arg [CommonValueColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    public fun <C> ColumnPath.valueCol(property: KProperty<C>): ColumnAccessor<C> =
        asColumnGroup().ensureIsColGroup().valueColumn(property).ensureIsValueColumn()

    // endregion

    // region index

    /**
     * @include [CommonValueColDocs]
     * {@arg [CommonValueColDocs.Arg] 0}
     * {@arg [CommonValueColDocs.ExampleArg] {@include [CommonValueColDocs.DoubleExample]}}
     * @param [index\] The index of the value column.
     * @throws [IndexOutOfBoundsException\] if the index is out of bounds.
     */
    private interface ValueColIndexDocs

    /**
     * @include [ValueColIndexDocs] {@arg [CommonValueColDocs.ReceiverArg] `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     * {@arg [CommonValueColDocs.ExampleArg] {@include [CommonValueColDocs.SingleExample]}}
     */
    public fun <C> ColumnSet<C>.valueCol(index: Int): SingleColumn<C> = getAt(index).ensureIsValueColumn()

    /**
     * @include [ValueColIndexDocs] {@arg [CommonValueColDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun ColumnsSelectionDsl<*>.valueCol(index: Int): SingleColumn<*> =
        valueCol<Any?>(index)

    /**
     * @include [ValueColIndexDocs] {@arg [CommonValueColDocs.ReceiverArg]}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> ColumnsSelectionDsl<*>.valueCol(index: Int): SingleColumn<C> =
        asSingleColumn().valueCol<C>(index)

    /**
     * @include [ValueColIndexDocs] {@arg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun SingleColumn<DataRow<*>>.valueCol(index: Int): SingleColumn<*> =
        valueCol<Any?>(index)

    /**
     * @include [ValueColIndexDocs] {@arg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(index: Int): SingleColumn<C> =
        ensureIsColGroup()
            .allColumnsInternal()
            .getAt(index)
            .ensureIsValueColumn()
            .cast()


    /**
     * @include [ValueColIndexDocs] {@arg [CommonValueColDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun String.valueCol(index: Int): SingleColumn<*> =
        valueCol<Any?>(index)

    /**
     * @include [ValueColIndexDocs] {@arg [CommonValueColDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> String.valueCol(index: Int): SingleColumn<C> =
        asColumnGroup().valueCol<C>(index)

    /**
     * @include [ValueColIndexDocs] {@arg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun KProperty<*>.valueCol(index: Int): SingleColumn<*> =
        valueCol<Any?>(index)

    /**
     * @include [ValueColIndexDocs] {@arg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> KProperty<*>.valueCol(index: Int): SingleColumn<C> =
        asColumnGroup().valueCol<C>(index)

    /**
     * @include [ValueColIndexDocs] {@arg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTypedKPropertyDataRow")
    public fun KProperty<DataRow<*>>.valueCol(index: Int): SingleColumn<*> =
        valueCol<Any?>(index)

    /**
     * @include [ValueColIndexDocs] {@arg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColKPropertyDataRow")
    public fun <C> KProperty<DataRow<*>>.valueCol(index: Int): SingleColumn<C> =
        asColumnGroup().valueCol<C>(index)

    /**
     * @include [ValueColIndexDocs] {@arg [CommonValueColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun ColumnPath.valueCol(index: Int): SingleColumn<*> =
        valueCol<Any?>(index)

    /**
     * @include [ValueColIndexDocs] {@arg [CommonValueColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> ColumnPath.valueCol(index: Int): SingleColumn<C> =
        asColumnGroup().valueCol<C>(index)

    // endregion
}

// endregion
