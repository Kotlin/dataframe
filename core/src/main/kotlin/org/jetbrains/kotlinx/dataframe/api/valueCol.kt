package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.ColumnGroupReference
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Usage.ColumnGroupName
import org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Usage.ColumnSetName
import org.jetbrains.kotlinx.dataframe.api.ValueColColumnsSelectionDsl.Usage.PlainDslName
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.ValueColumn
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.getAt
import org.jetbrains.kotlinx.dataframe.impl.columns.onResolve
import org.jetbrains.kotlinx.dataframe.impl.columns.singleImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

public interface ValueColColumnsSelectionDsl {

    /**
     * ## Value Col Usage
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

        /** [**valueCol**][ColumnsSelectionDsl.valueCol] */
        public interface PlainDslName

        /** .[**valueCol**][ColumnsSelectionDsl.valueCol] */
        public interface ColumnSetName

        /** .[**valueCol**][ColumnsSelectionDsl.valueCol] */
        public interface ColumnGroupName
    }

    /**
     * ## Value Col
     *
     * Creates a [ColumnAccessor] (or [SingleColumn]) for a value column with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath], [KProperty], or [ColumnAccessor]).
     *
     * This is a DSL-shorthand for [valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [ColumnGroups][ColumnGroupReference] to create
     * an accessor for a value column inside a [ColumnGroup].
     * {@include [LineBreak]}
     * {@getArg [CommonValueColDocs.Note]}
     *
     * Check out [Usage] for how to use [valueCol].
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
     * {@getArg [CommonValueColDocs.ExampleArg]}
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
     * {@setArg [CommonValueColDocs.Note]}
     */
    private interface CommonValueColDocs {

        /* Example argument, can be either {@include [SingleExample]} or {@include [DoubleExample]} */
        interface ExampleArg

        /**
         * `df.`[select][DataFrame.select]` { {@getArg [CommonValueColDocs.ReceiverArg]}`[valueCol][valueCol]`({@getArg [CommonValueColDocs.Arg]}) \\\\}`
         */
        interface SingleExample

        /**
         * `df.`[select][DataFrame.select]` { {@getArg [CommonValueColDocs.ReceiverArg]}`[valueCol][valueCol]`({@getArg [CommonValueColDocs.Arg]}) \\\\}`
         *
         * `df.`[select][DataFrame.select]` { {@getArg [CommonValueColDocs.ReceiverArg]}`[valueCol][valueCol]`<`[String][String]`>({@getArg [CommonValueColDocs.Arg]}) \\\\}`
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
     * {@setArg [CommonValueColDocs.Arg] valueColumnA}
     * {@setArg [CommonValueColDocs.ExampleArg] {@include [CommonValueColDocs.SingleExample]}}
     * @param [col\] The [ColumnAccessor] pointing to the value column.
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    private interface ValueColReferenceDocs

    /**
     * @include [ValueColReferenceDocs] {@setArg [CommonValueColDocs.ReceiverArg]}
     */
    public fun <C> valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> = valueCol.ensureIsValueColumn()

    /**
     * @include [ValueColReferenceDocs] {@setArg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(valueCol: ColumnAccessor<C>): SingleColumn<C> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getChild(valueCol)
                ?: throw IllegalStateException("ValueColumn '${valueCol.path()}' not found in column group '${it.path}'")
            child.data.ensureIsValueColumn()
            listOf(child)
        }.singleImpl()

    /**
     * @include [ValueColReferenceDocs] {@setArg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> AnyColumnGroupAccessor.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        this.ensureIsColumnGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    /**
     * @include [ValueColReferenceDocs] {@setArg [CommonValueColDocs.ReceiverArg] "myColumnGroup".}
     */
    public fun <C> String.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    /**
     * @include [ValueColReferenceDocs] {@setArg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    public fun <C> KProperty<*>.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    /**
     * @include [ValueColReferenceDocs] {@setArg [CommonValueColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    public fun <C> ColumnPath.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    // endregion

    // region name

    /**
     * @include [CommonValueColDocs]
     * {@setArg [CommonValueColDocs.Arg] "valueColumnName"}
     * {@setArg [CommonValueColDocs.ExampleArg] {@include [CommonValueColDocs.DoubleExample]}}
     * @param [name\] The name of the value column.
     */
    private interface ValueColNameDocs

    /**
     * @include [ValueColNameDocs] {@setArg [CommonValueColDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun valueCol(name: String): ColumnAccessor<*> = valueColumn<Any?>(name).ensureIsValueColumn()

    /**
     * @include [ValueColNameDocs] {@setArg [CommonValueColDocs.ReceiverArg]}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> valueCol(name: String): ColumnAccessor<C> = valueColumn<C>(name).ensureIsValueColumn()

    /**
     * @include [ValueColNameDocs] {@setArg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun SingleColumn<DataRow<*>>.valueCol(name: String): SingleColumn<*> =
        valueCol<Any?>(name)

    /**
     * @include [ValueColNameDocs] {@setArg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(name: String): SingleColumn<C> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getChild(name)?.cast<C>()
                ?: throw IllegalStateException("Value column '$name' not found in column group '${it.path}'")
            child.data.ensureIsValueColumn()
            listOf(child)
        }.singleImpl()

    /**
     * @include [ValueColNameDocs] {@setArg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun AnyColumnGroupAccessor.valueCol(name: String): ColumnAccessor<*> =
        valueCol<Any?>(name)

    /**
     * @include [ValueColNameDocs] {@setArg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> AnyColumnGroupAccessor.valueCol(name: String): ColumnAccessor<C> =
        this.ensureIsColumnGroup().valueColumn<C>(name).ensureIsValueColumn()

    /**
     * @include [ValueColNameDocs] {@setArg [CommonValueColDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun String.valueCol(name: String): ColumnAccessor<*> =
        valueCol<Any?>(name)

    /**
     * @include [ValueColNameDocs] {@setArg [CommonValueColDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> String.valueCol(name: String): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(name).ensureIsValueColumn()

    /**
     * @include [ValueColNameDocs] {@setArg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun KProperty<*>.valueCol(name: String): ColumnAccessor<*> =
        valueCol<Any?>(name)

    /**
     * @include [ValueColNameDocs] {@setArg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> KProperty<*>.valueCol(name: String): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(name).ensureIsValueColumn()

    /**
     * @include [ValueColNameDocs] {@setArg [CommonValueColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun ColumnPath.valueCol(name: String): ColumnAccessor<*> =
        valueCol<Any?>(name)

    /**
     * @include [ValueColNameDocs] {@setArg [CommonValueColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> ColumnPath.valueCol(name: String): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(name).ensureIsValueColumn()

    // endregion

    // region path

    /**
     * @include [CommonValueColDocs]
     * {@setArg [CommonValueColDocs.Arg] "pathTo"["valueColumnName"\] }
     * {@setArg [CommonValueColDocs.ExampleArg] {@include [CommonValueColDocs.DoubleExample]}}
     * @param [path\] The path to the value column.
     */
    private interface ValueColPathDocs

    /**
     * @include [ValueColPathDocs] {@setArg [CommonValueColDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun valueCol(path: ColumnPath): ColumnAccessor<*> = valueColumn<Any?>(path).ensureIsValueColumn()

    /**
     * @include [ValueColPathDocs] {@setArg [CommonValueColDocs.ReceiverArg]}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> valueCol(path: ColumnPath): ColumnAccessor<C> = valueColumn<C>(path).ensureIsValueColumn()

    /**
     * @include [ValueColPathDocs] {@setArg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun SingleColumn<DataRow<*>>.valueCol(path: ColumnPath): SingleColumn<*> =
        valueCol<Any?>(path)

    /**
     * @include [ValueColPathDocs] {@setArg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(path: ColumnPath): SingleColumn<C> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getChild(path)?.cast<C>()
                ?: throw IllegalStateException("Value column '$path' not found in column group '${it.path}'")
            child.data.ensureIsValueColumn()
            listOf(child)
        }.singleImpl()

    /**
     * @include [ValueColPathDocs] {@setArg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun AnyColumnGroupAccessor.valueCol(path: ColumnPath): ColumnAccessor<*> =
        valueCol<Any?>(path)

    /**
     * @include [ValueColPathDocs] {@setArg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> AnyColumnGroupAccessor.valueCol(path: ColumnPath): ColumnAccessor<C> =
        this.ensureIsColumnGroup().valueColumn<C>(path).ensureIsValueColumn()

    /**
     * @include [ValueColPathDocs] {@setArg [CommonValueColDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun String.valueCol(path: ColumnPath): ColumnAccessor<*> =
        valueCol<Any?>(path)

    /**
     * @include [ValueColPathDocs] {@setArg [CommonValueColDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> String.valueCol(path: ColumnPath): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(path).ensureIsValueColumn()

    /**
     * @include [ValueColPathDocs] {@setArg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun KProperty<*>.valueCol(path: ColumnPath): ColumnAccessor<*> =
        valueCol<Any?>(path)

    /**
     * @include [ValueColPathDocs] {@setArg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> KProperty<*>.valueCol(path: ColumnPath): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(path).ensureIsValueColumn()

    /**
     * @include [ValueColPathDocs] {@setArg [CommonValueColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun ColumnPath.valueCol(path: ColumnPath): ColumnAccessor<*> =
        valueCol<Any?>(path)

    /**
     * @include [ValueColPathDocs] {@setArg [CommonValueColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> ColumnPath.valueCol(path: ColumnPath): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(path).ensureIsValueColumn()

    // endregion

    // region property

    /**
     * @include [CommonValueColDocs]
     * {@setArg [CommonValueColDocs.Arg] Type::valueColumnA}
     * {@setArg [CommonValueColDocs.ExampleArg] {@include [CommonValueColDocs.SingleExample]}}
     * @param [property\] The [KProperty] reference to the value column.
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    private interface ValueColKPropertyDocs

    /**
     * @include [ValueColKPropertyDocs] {@setArg [CommonValueColDocs.ReceiverArg]}
     */
    public fun <C> valueCol(property: KProperty<C>): SingleColumn<C> = valueColumn(property).ensureIsValueColumn()

    /**
     * @include [ValueColKPropertyDocs] {@setArg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(property: KProperty<C>): SingleColumn<C> =
        valueCol<C>(property.name)

    /**
     * @include [ValueColKPropertyDocs] {@setArg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> AnyColumnGroupAccessor.valueCol(property: KProperty<C>): ColumnAccessor<C> =
        this.ensureIsColumnGroup().valueColumn(property).ensureIsValueColumn()

    /**
     * @include [ValueColKPropertyDocs] {@setArg [CommonValueColDocs.ReceiverArg] "myColumnGroup".}
     */
    public fun <C> String.valueCol(property: KProperty<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn(property).ensureIsValueColumn()

    /**
     * @include [ValueColKPropertyDocs] {@setArg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    public fun <C> KProperty<*>.valueCol(property: KProperty<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn(property).ensureIsValueColumn()

    /**
     * @include [ValueColKPropertyDocs] {@setArg [CommonValueColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    public fun <C> ColumnPath.valueCol(property: KProperty<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn(property).ensureIsValueColumn()

    // endregion

    // region index

    /**
     * @include [CommonValueColDocs]
     * {@setArg [CommonValueColDocs.Arg] 0}
     * {@setArg [CommonValueColDocs.ExampleArg] {@include [CommonValueColDocs.DoubleExample]}}
     * @param [index\] The index of the value column.
     * @throws [IndexOutOfBoundsException\] if the index is out of bounds.
     */
    private interface ValueColIndexDocs

    /**
     * @include [ValueColIndexDocs] {@setArg [CommonValueColDocs.ReceiverArg] `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     * {@setArg [CommonValueColDocs.ExampleArg] {@include [CommonValueColDocs.SingleExample]}}
     */
    public fun <C> ColumnSet<C>.valueCol(index: Int): SingleColumn<C> = getAt(index).ensureIsValueColumn()

    /**
     * @include [ValueColIndexDocs] {@setArg [CommonValueColDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun ColumnsSelectionDsl<*>.valueCol(index: Int): SingleColumn<*> =
        valueCol<Any?>(index)

    /**
     * @include [ValueColIndexDocs] {@setArg [CommonValueColDocs.ReceiverArg]}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> ColumnsSelectionDsl<*>.valueCol(index: Int): SingleColumn<C> =
        asSingleColumn().valueCol<C>(index)

    /**
     * @include [ValueColIndexDocs] {@setArg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun SingleColumn<DataRow<*>>.valueCol(index: Int): SingleColumn<*> =
        valueCol<Any?>(index)

    /**
     * @include [ValueColIndexDocs] {@setArg [CommonValueColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(index: Int): SingleColumn<C> =
        this.ensureIsColumnGroup()
            .allColumnsInternal()
            .getAt(index)
            .ensureIsValueColumn()
            .cast()

    /**
     * @include [ValueColIndexDocs] {@setArg [CommonValueColDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun String.valueCol(index: Int): SingleColumn<*> =
        valueCol<Any?>(index)

    /**
     * @include [ValueColIndexDocs] {@setArg [CommonValueColDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> String.valueCol(index: Int): SingleColumn<C> =
        columnGroup(this).valueCol<C>(index)

    /**
     * @include [ValueColIndexDocs] {@setArg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun KProperty<*>.valueCol(index: Int): SingleColumn<*> =
        valueCol<Any?>(index)

    /**
     * @include [ValueColIndexDocs] {@setArg [CommonValueColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> KProperty<*>.valueCol(index: Int): SingleColumn<C> =
        columnGroup(this).valueCol<C>(index)

    /**
     * @include [ValueColIndexDocs] {@setArg [CommonValueColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun ColumnPath.valueCol(index: Int): SingleColumn<*> =
        valueCol<Any?>(index)

    /**
     * @include [ValueColIndexDocs] {@setArg [CommonValueColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> ColumnPath.valueCol(index: Int): SingleColumn<C> =
        columnGroup(this).valueCol<C>(index)

    // endregion
}

/**
 * Checks the validity of this [SingleColumn],
 * by adding a check to see it's a [ValueColumn] (so, a [SingleColumn]<*>)
 * and throwing an [IllegalArgumentException] if it's not.
 */
internal fun <C> SingleColumn<C>.ensureIsValueColumn(): SingleColumn<C> =
    onResolve { col: ColumnWithPath<*>? ->
        require(col?.isValueColumn() != false) {
            "Column at ${col?.path} is not a ValueColumn, but a ${col?.kind()}."
        }
    }

/** @include [SingleColumn.ensureIsValueColumn] */
internal fun <C> ColumnAccessor<C>.ensureIsValueColumn(): ColumnAccessor<C> =
    onResolve { col: ColumnWithPath<*>? ->
        require(col?.isValueColumn() != false) {
            "Column at ${col?.path} is not a ValueColumn, but a ${col?.kind()}."
        }
    }

// endregion
