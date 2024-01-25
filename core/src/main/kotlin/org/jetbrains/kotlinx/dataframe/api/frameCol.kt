package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.ColumnGroupReference
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar.ColumnGroupName
import org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar.ColumnSetName
import org.jetbrains.kotlinx.dataframe.api.FrameColColumnsSelectionDsl.Grammar.PlainDslName
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
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.getAt
import org.jetbrains.kotlinx.dataframe.impl.columns.onResolve
import org.jetbrains.kotlinx.dataframe.impl.columns.singleImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Frame Col {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface FrameColColumnsSelectionDsl {

    /**
     * ## Frame Col Grammar
     *
     * @include [DslGrammarTemplate]
     * {@setArg [DslGrammarTemplate.DefinitionsArg]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.IndexDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnTypeDef]}
     * }
     *
     * {@setArg [DslGrammarTemplate.PlainDslFunctionsArg]
     *  {@include [PlainDslName]}`[`**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>`**`]`**`(`**{@include [DslGrammarTemplate.ColumnRef]}` | `{@include [DslGrammarTemplate.IndexRef]}**`)`**
     * }
     *
     * {@setArg [DslGrammarTemplate.ColumnSetFunctionsArg]
     *  {@include [Indent]}{@include [ColumnSetName]}**`(`**{@include [DslGrammarTemplate.IndexRef]}**`)`**
     * }
     *
     * {@setArg [DslGrammarTemplate.ColumnGroupFunctionsArg]
     *  {@include [Indent]}{@include [ColumnGroupName]}`[`**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>`**`]`**`(`**{@include [DslGrammarTemplate.ColumnRef]}` | `{@include [DslGrammarTemplate.IndexRef]}**`)`**
     * }
     */
    public interface Grammar {

        /** [**frameCol**][ColumnsSelectionDsl.frameCol] */
        public interface PlainDslName

        /** .[**frameCol**][ColumnsSelectionDsl.frameCol] */
        public interface ColumnSetName

        /** .[**frameCol**][ColumnsSelectionDsl.frameCol] */
        public interface ColumnGroupName
    }

    /**
     * ## Frame Col
     *
     * Creates a [ColumnAccessor] (or [SingleColumn]) for a frame column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath], [KProperty], or [ColumnAccessor]; any {@include [AccessApiLink]}).
     *
     * This is a DSL-shorthand for [frameColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a frame column.
     * The function can also be called on [ColumnGroups][ColumnGroupReference] to create
     * an accessor for a frame column inside a [ColumnGroup].
     * {@include [LineBreak]}
     * {@getArg [CommonFrameColDocs.Note]}
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[frameCol][frameCol]`<`[String][String]`>("frameColA") }`
     *
     * `df.`[select][DataFrame.select]` { `[frameCol][frameCol]`(SomeType::frameColB) }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[frameCol][frameCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonFrameColDocs.ExampleArg]}
     *
     * To create a [ColumnAccessor] for another kind of column, take a look at the functions
     * [col][ColumnsSelectionDsl.col],
     * [colGroup][ColumnsSelectionDsl.colGroup],
     * and [valueCol][ColumnsSelectionDsl.valueCol].
     *
     * @return A [ColumnAccessor] for the frame column with the given argument if possible, else a [SingleColumn].
     * @throws [IllegalStateException\] if the column with the given argument does not exist.
     * @throws [IllegalArgumentException\] if the column with the given argument is not a frame column.
     *
     * @see [frameColumn\]
     * @see [ColumnsSelectionDsl.colGroup\]
     * @see [ColumnsSelectionDsl.valueCol\]
     * @see [ColumnsSelectionDsl.col\]
     * {@setArg [CommonFrameColDocs.Note]}
     */
    private interface CommonFrameColDocs {

        /* Example argument, can be either {@include [SingleExample]} or {@include [DoubleExample]} */
        interface ExampleArg

        /**
         * `df.`[select][DataFrame.select]` { {@getArg [CommonFrameColDocs.ReceiverArg]}`[frameCol][frameCol]`({@getArg [CommonFrameColDocs.Arg]}) \\\\}`
         */
        interface SingleExample

        /**
         * `df.`[select][DataFrame.select]` { {@getArg [CommonFrameColDocs.ReceiverArg]}`[frameCol][frameCol]`({@getArg [CommonFrameColDocs.Arg]}) \\\\}`
         *
         * `df.`[select][DataFrame.select]` { {@getArg [CommonFrameColDocs.ReceiverArg]}`[frameCol][frameCol]`<`[String][String]`>({@getArg [CommonFrameColDocs.Arg]}) \\\\}`
         */
        interface DoubleExample

        /* Receiver argument for the example(s) */
        interface ReceiverArg

        /* Argument for the example(s) */
        interface Arg

        /* Optional note */
        interface Note

        /** @param [C\] The type of the frame column. */
        interface FrameColumnTypeParam
    }

    // region reference

    /**
     * @include [CommonFrameColDocs]
     * {@setArg [CommonFrameColDocs.Arg] frameColumnA}
     * {@setArg [CommonFrameColDocs.ExampleArg] {@include [CommonFrameColDocs.SingleExample]}}
     * @param [col\] The [ColumnAccessor] pointing to the value column.
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    private interface FrameColReferenceDocs

    /**
     * @include [FrameColReferenceDocs] {@setArg [CommonFrameColDocs.ReceiverArg]}
     */
    public fun <C> frameCol(frameCol: ColumnAccessor<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        frameCol.ensureIsFrameColumn()

    /**
     * @include [FrameColReferenceDocs] {@setArg [CommonFrameColDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> SingleColumn<DataRow<*>>.frameCol(frameCol: ColumnAccessor<DataFrame<C>>): SingleColumn<DataFrame<C>> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getCol(frameCol)
                ?: throw IllegalStateException("FrameColumn '${frameCol.path()}' not found in column group '${it.path}'")
            child.data.ensureIsFrameColumn()
            listOf(child)
        }.singleImpl()

    /**
     * @include [FrameColReferenceDocs] {@setArg [CommonFrameColDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> AnyColumnGroupAccessor.frameCol(frameCol: ColumnAccessor<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        this.ensureIsColumnGroup().frameColumn<C>(frameCol.path()).ensureIsFrameColumn()

    /**
     * @include [FrameColReferenceDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "myColumnGroup".}
     */
    public fun <C> String.frameCol(frameCol: ColumnAccessor<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(frameCol.path()).ensureIsFrameColumn()

    /**
     * @include [FrameColReferenceDocs] {@setArg [CommonFrameColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    public fun <C> KProperty<*>.frameCol(frameCol: ColumnAccessor<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(frameCol.path()).ensureIsFrameColumn()

    /**
     * @include [FrameColReferenceDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    public fun <C> ColumnPath.frameCol(frameCol: ColumnAccessor<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(frameCol.path()).ensureIsFrameColumn()

    // endregion

    // region name

    /**
     * @include [CommonFrameColDocs]
     * {@setArg [CommonFrameColDocs.Arg] "frameColumnName"}
     * {@setArg [CommonFrameColDocs.ExampleArg] {@include [CommonFrameColDocs.DoubleExample]}}
     * @param [name\] The name of the value column.
     */
    private interface FrameColNameDocs

    /**
     * @include [FrameColNameDocs] {@setArg [CommonFrameColDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun frameCol(name: String): ColumnAccessor<DataFrame<*>> = frameColumn<Any?>(name).ensureIsFrameColumn()

    /**
     * @include [FrameColNameDocs] {@setArg [CommonFrameColDocs.ReceiverArg]}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> frameCol(name: String): ColumnAccessor<DataFrame<C>> = frameColumn<C>(name).ensureIsFrameColumn()

    /**
     * @include [FrameColNameDocs] {@setArg [CommonFrameColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun SingleColumn<DataRow<*>>.frameCol(name: String): SingleColumn<DataFrame<*>> =
        frameCol<Any?>(name)

    /**
     * @include [FrameColNameDocs] {@setArg [CommonFrameColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.frameCol(name: String): SingleColumn<DataFrame<C>> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getCol(name)?.cast<DataFrame<C>>()
                ?: throw IllegalStateException("Frame column '$name' not found in column group '${it.path}'")
            child.data.ensureIsFrameColumn()
            listOf(child)
        }.singleImpl()

    /**
     * @include [FrameColNameDocs] {@setArg [CommonFrameColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun AnyColumnGroupAccessor.frameCol(name: String): ColumnAccessor<DataFrame<*>> =
        frameCol<Any?>(name)

    /**
     * @include [FrameColNameDocs] {@setArg [CommonFrameColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> AnyColumnGroupAccessor.frameCol(name: String): ColumnAccessor<DataFrame<C>> =
        this.ensureIsColumnGroup().frameColumn<C>(name).ensureIsFrameColumn()

    /**
     * @include [FrameColNameDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun String.frameCol(name: String): ColumnAccessor<DataFrame<*>> =
        frameCol<Any?>(name)

    /**
     * @include [FrameColNameDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> String.frameCol(name: String): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(name).ensureIsFrameColumn()

    /**
     * @include [FrameColNameDocs] {@setArg [CommonFrameColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun KProperty<*>.frameCol(name: String): ColumnAccessor<DataFrame<*>> =
        frameCol<Any?>(name)

    /**
     * @include [FrameColNameDocs] {@setArg [CommonFrameColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> KProperty<*>.frameCol(name: String): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(name).ensureIsFrameColumn()

    /**
     * @include [FrameColNameDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun ColumnPath.frameCol(name: String): ColumnAccessor<DataFrame<*>> =
        frameCol<Any?>(name)

    /**
     * @include [FrameColNameDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> ColumnPath.frameCol(name: String): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(name).ensureIsFrameColumn()

    // endregion

    // region path

    /**
     * @include [CommonFrameColDocs]
     * {@setArg [CommonFrameColDocs.Arg] "pathTo"["frameColumnName"\] }
     * {@setArg [CommonFrameColDocs.ExampleArg] {@include [CommonFrameColDocs.DoubleExample]}}
     * @param [path\] The path to the value column.
     */
    private interface FrameColPathDocs

    /**
     * @include [FrameColPathDocs] {@setArg [CommonFrameColDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun frameCol(path: ColumnPath): ColumnAccessor<DataFrame<*>> = frameColumn<Any?>(path).ensureIsFrameColumn()

    /**
     * @include [FrameColPathDocs] {@setArg [CommonFrameColDocs.ReceiverArg]}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> frameCol(path: ColumnPath): ColumnAccessor<DataFrame<C>> = frameColumn<C>(path).ensureIsFrameColumn()

    /**
     * @include [FrameColPathDocs] {@setArg [CommonFrameColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun SingleColumn<DataRow<*>>.frameCol(path: ColumnPath): SingleColumn<DataFrame<*>> =
        frameCol<Any?>(path)

    /**
     * @include [FrameColPathDocs] {@setArg [CommonFrameColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.frameCol(path: ColumnPath): SingleColumn<DataFrame<C>> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getCol(path)?.cast<DataFrame<C>>()
                ?: throw IllegalStateException("Frame column '$path' not found in column group '${it.path}'")
            child.data.ensureIsFrameColumn()
            listOf(child)
        }.singleImpl()

    /**
     * @include [FrameColPathDocs] {@setArg [CommonFrameColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun AnyColumnGroupAccessor.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<*>> =
        frameCol<Any?>(path)

    /**
     * @include [FrameColPathDocs] {@setArg [CommonFrameColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> AnyColumnGroupAccessor.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<C>> =
        this.ensureIsColumnGroup().frameColumn<C>(path).ensureIsFrameColumn()

    /**
     * @include [FrameColPathDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun String.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<*>> =
        frameCol<Any?>(path)

    /**
     * @include [FrameColPathDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> String.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(path).ensureIsFrameColumn()

    /**
     * @include [FrameColPathDocs] {@setArg [CommonFrameColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun KProperty<*>.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<*>> =
        frameCol<Any?>(path)

    /**
     * @include [FrameColPathDocs] {@setArg [CommonFrameColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> KProperty<*>.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(path).ensureIsFrameColumn()

    /**
     * @include [FrameColPathDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun ColumnPath.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<*>> =
        frameCol<Any?>(path)

    /**
     * @include [FrameColPathDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> ColumnPath.frameCol(path: ColumnPath): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn<C>(path).ensureIsFrameColumn()

    // endregion

    // region property

    /**
     * @include [CommonFrameColDocs]
     * {@setArg [CommonFrameColDocs.Arg] Type::frameColumnA}
     * {@setArg [CommonFrameColDocs.ExampleArg] {@include [CommonFrameColDocs.SingleExample]}}
     * @param [property\] The [KProperty] reference to the value column.
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    private interface FrameColKPropertyDocs

    /**
     * @include [FrameColKPropertyDocs] {@setArg [CommonFrameColDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColDataFrameKProperty")
    public fun <C> frameCol(property: KProperty<DataFrame<C>>): SingleColumn<DataFrame<C>> =
        frameColumn(property).ensureIsFrameColumn()

    /**
     * @include [FrameColKPropertyDocs] {@setArg [CommonFrameColDocs.ReceiverArg]}
     */
    public fun <C> frameCol(property: KProperty<List<C>>): SingleColumn<DataFrame<C>> =
        frameColumn(property).ensureIsFrameColumn()

    /**
     * @include [FrameColKPropertyDocs] {@setArg [CommonFrameColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColDataFrameKProperty")
    public fun <C> SingleColumn<DataRow<*>>.frameCol(property: KProperty<DataFrame<C>>): SingleColumn<DataFrame<C>> =
        frameCol<C>(property.name)

    /**
     * @include [FrameColKPropertyDocs] {@setArg [CommonFrameColDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> SingleColumn<DataRow<*>>.frameCol(property: KProperty<List<C>>): SingleColumn<DataFrame<C>> =
        frameCol<C>(property.name)

    /**
     * @include [FrameColKPropertyDocs] {@setArg [CommonFrameColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColDataFrameKProperty")
    public fun <C> AnyColumnGroupAccessor.frameCol(property: KProperty<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        this.ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * @include [FrameColKPropertyDocs] {@setArg [CommonFrameColDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> AnyColumnGroupAccessor.frameCol(property: KProperty<List<C>>): ColumnAccessor<DataFrame<C>> =
        this.ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * @include [FrameColKPropertyDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColDataFrameKProperty")
    public fun <C> String.frameCol(property: KProperty<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * @include [FrameColKPropertyDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "myColumnGroup".}
     */
    public fun <C> String.frameCol(property: KProperty<List<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * @include [FrameColKPropertyDocs] {@setArg [CommonFrameColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColDataFrameKProperty")
    public fun <C> KProperty<*>.frameCol(property: KProperty<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * @include [FrameColKPropertyDocs] {@setArg [CommonFrameColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    public fun <C> KProperty<*>.frameCol(property: KProperty<List<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * @include [FrameColKPropertyDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColDataFrameKProperty")
    public fun <C> ColumnPath.frameCol(property: KProperty<DataFrame<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    /**
     * @include [FrameColKPropertyDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    public fun <C> ColumnPath.frameCol(property: KProperty<List<C>>): ColumnAccessor<DataFrame<C>> =
        columnGroup(this).ensureIsColumnGroup().frameColumn(property).ensureIsFrameColumn()

    // endregion

    // region index

    /**
     * @include [CommonFrameColDocs]
     * {@setArg [CommonFrameColDocs.Arg] 0}
     * {@setArg [CommonFrameColDocs.ExampleArg] {@include [CommonFrameColDocs.DoubleExample]}}
     * @param [index\] The index of the value column.
     * @throws [IndexOutOfBoundsException\] if the index is out of bounds.
     */
    private interface FrameColIndexDocs

    /**
     * @include [FrameColIndexDocs] {@setArg [CommonFrameColDocs.ReceiverArg] `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     * {@setArg [CommonFrameColDocs.ExampleArg] {@include [CommonFrameColDocs.SingleExample]}}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("ColumnSetDataFrameFrameColIndex")
    public fun <C> ColumnSet<DataFrame<C>>.frameCol(index: Int): SingleColumn<DataFrame<C>> =
        getAt(index).ensureIsFrameColumn()

    /**
     * @include [FrameColIndexDocs] {@setArg [CommonFrameColDocs.ReceiverArg] `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     * {@setArg [CommonFrameColDocs.ExampleArg] {@include [CommonFrameColDocs.SingleExample]}}
     */
    public fun ColumnSet<*>.frameCol(index: Int): SingleColumn<DataFrame<*>> =
        getAt(index).cast<DataFrame<*>>().ensureIsFrameColumn()

    /**
     * @include [FrameColIndexDocs] {@setArg [CommonFrameColDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun ColumnsSelectionDsl<*>.frameCol(index: Int): SingleColumn<DataFrame<*>> =
        frameCol<Any?>(index)

    /**
     * @include [FrameColIndexDocs] {@setArg [CommonFrameColDocs.ReceiverArg]}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> ColumnsSelectionDsl<*>.frameCol(index: Int): SingleColumn<DataFrame<C>> =
        asSingleColumn().frameCol<C>(index)

    /**
     * @include [FrameColIndexDocs] {@setArg [CommonFrameColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun SingleColumn<DataRow<*>>.frameCol(index: Int): SingleColumn<DataFrame<*>> =
        frameCol<Any?>(index)

    /**
     * @include [FrameColIndexDocs] {@setArg [CommonFrameColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.frameCol(index: Int): SingleColumn<DataFrame<C>> =
        this.ensureIsColumnGroup()
            .allColumnsInternal()
            .getAt(index)
            .cast<DataFrame<C>>()
            .ensureIsFrameColumn()

    /**
     * @include [FrameColIndexDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun String.frameCol(index: Int): SingleColumn<DataFrame<*>> =
        frameCol<Any?>(index)

    /**
     * @include [FrameColIndexDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> String.frameCol(index: Int): SingleColumn<DataFrame<C>> =
        columnGroup(this).frameCol<C>(index)

    /**
     * @include [FrameColIndexDocs] {@setArg [CommonFrameColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun KProperty<*>.frameCol(index: Int): SingleColumn<DataFrame<*>> =
        frameCol<Any?>(index)

    /**
     * @include [FrameColIndexDocs] {@setArg [CommonFrameColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> KProperty<*>.frameCol(index: Int): SingleColumn<DataFrame<C>> =
        columnGroup(this).frameCol<C>(index)

    /**
     * @include [FrameColIndexDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("frameColUnTyped")
    public fun ColumnPath.frameCol(index: Int): SingleColumn<DataFrame<*>> =
        frameCol<Any?>(index)

    /**
     * @include [FrameColIndexDocs] {@setArg [CommonFrameColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonFrameColDocs.FrameColumnTypeParam]
     */
    public fun <C> ColumnPath.frameCol(index: Int): SingleColumn<DataFrame<C>> =
        columnGroup(this).frameCol<C>(index)

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

/** @include [SingleColumn.ensureIsFrameColumn] */
internal fun <C> ColumnAccessor<DataFrame<C>>.ensureIsFrameColumn(): ColumnAccessor<DataFrame<C>> =
    onResolve { col: ColumnWithPath<*>? ->
        require(col?.isFrameColumn() != false) {
            "Column at ${col?.path} is not a FrameColumn, but a ${col?.kind()}."
        }
    }

// endregion
