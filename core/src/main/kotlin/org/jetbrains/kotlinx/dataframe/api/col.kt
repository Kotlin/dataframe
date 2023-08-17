package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnGroupAccessor
import org.jetbrains.kotlinx.dataframe.ColumnGroupReference
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Usage
import org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Usage.ColumnGroupName
import org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Usage.ColumnSetName
import org.jetbrains.kotlinx.dataframe.api.ColColumnsSelectionDsl.Usage.PlainDslName
import org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.getAt
import org.jetbrains.kotlinx.dataframe.impl.columns.singleImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.util.COL_REPLACE
import org.jetbrains.kotlinx.dataframe.util.IDENTITY_FUNCTION
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl
/** See [Usage] */
public interface ColColumnsSelectionDsl {

    /**
     * ## Col Usage
     *
     * @include [UsageTemplate]
     * {@setArg [UsageTemplate.DefinitionsArg]
     *   {@include [UsageTemplate.ColumnSetDef]}
     *   {@include [LineBreak]}
     *   {@include [UsageTemplate.ColumnGroupDef]}
     *   {@include [LineBreak]}
     *   {@include [UsageTemplate.ColumnRefDef]}
     *   {@include [LineBreak]}
     *   {@include [UsageTemplate.IndexDef]}
     *   {@include [LineBreak]}
     *   {@include [UsageTemplate.ColumnTypeDef]}
     * }
     *
     * {@setArg [UsageTemplate.PlainDslFunctionsArg]
     *   {@include [PlainDslName]}`[`**`<`**{@include [UsageTemplate.ColumnType]}**`>`**`]`**`(`**{@include [UsageTemplate.ColumnRef]}` | `{@include [UsageTemplate.Index]}**`)`**
     * }
     *
     * {@setArg [UsageTemplate.ColumnSetFunctionsArg]
     *   {@include [Indent]}{@include [ColumnSetName]}**`(`**{@include [UsageTemplate.Index]}**`)`**
     *   `|` [**`[`**][ColumnsSelectionDsl.col]{@include [UsageTemplate.Index]}[**`]`**][ColumnsSelectionDsl.col]
     * }
     *
     * {@setArg [UsageTemplate.ColumnGroupFunctionsArg]
     *   {@include [Indent]}{@include [ColumnGroupName]}`[`**`<`**{@include [UsageTemplate.ColumnType]}**`>`**`]`**`(`**{@include [UsageTemplate.ColumnRef]}` | `{@include [UsageTemplate.Index]}**`)`**
     * }
     */
    public interface Usage {

        /** [**col**][ColumnsSelectionDsl.col] */
        public interface PlainDslName

        /** .[**col**][ColumnsSelectionDsl.col] */
        public interface ColumnSetName

        /** .[**col**][ColumnsSelectionDsl.col] */
        public interface ColumnGroupName
    }

    /**
     * ## Col
     *
     * Creates a [ColumnAccessor] (or [SingleColumn]) for a column with the given argument which can be either
     * an index ([Int]) or a reference to a column ([String], [ColumnPath], [KProperty], or [ColumnAccessor]).
     *
     * This is a DSL-shorthand for [column] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index).
     * The function can also be called on [ColumnGroups][ColumnGroupReference] to create
     * an accessor for a column inside a [ColumnGroup].
     * {@include [LineBreak]}
     * {@getArg [CommonColDocs.Note]}
     *
     * Check out [Usage] for how to use [col].
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[col][col]`<`[String][String]`>("colA") }`
     *
     * `df.`[select][DataFrame.select]` { `[col][col]`(SomeType::colB) }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[col][col]`(1) }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonColDocs.ExampleArg]}
     *
     * To create a [ColumnAccessor] for a specific kind of column with runtime checks, take a look at the functions
     * [valueCol][ColumnsSelectionDsl.valueCol],
     * [colGroup][ColumnsSelectionDsl.colGroup],
     * and [frameCol][ColumnsSelectionDsl.frameCol].
     *
     * @return A [ColumnAccessor] for the column with the given argument if possible, else a [SingleColumn].
     * @throws [IllegalStateException\] if the column with the given argument does not exist.
     *
     * @see [column\]
     * @see [ColumnsSelectionDsl.colGroup\]
     * @see [ColumnsSelectionDsl.frameCol\]
     * @see [ColumnsSelectionDsl.valueCol\]
     * {@setArg [CommonColDocs.Note]}
     */
    private interface CommonColDocs {

        /* Example argument, can be either {@include [SingleExample]} or {@include [DoubleExample]} */
        interface ExampleArg

        /**
         * `df.`[select][DataFrame.select]` { {@getArg [CommonColDocs.ReceiverArg]}`[col][col]`({@getArg [CommonColDocs.Arg]}) \\\\}`
         */
        interface SingleExample

        /**
         * `df.`[select][DataFrame.select]` { {@getArg [CommonColDocs.ReceiverArg]}`[col][col]`({@getArg [CommonColDocs.Arg]}) \\\\}`
         *
         * `df.`[select][DataFrame.select]` { {@getArg [CommonColDocs.ReceiverArg]}`[col][col]`<`[String][String]`>({@getArg [CommonColDocs.Arg]}) \\\\}`
         */
        interface DoubleExample

        /* Receiver argument for the example(s) */
        interface ReceiverArg

        /* Argument for the example(s) */
        interface Arg

        /* Optional note */
        interface Note

        /** @param [C\] The type of the column. */
        interface ColumnTypeParam
    }

    // region reference

    /**
     * @include [CommonColDocs]
     * {@setArg [CommonColDocs.Arg] columnA}
     * {@setArg [CommonColDocs.ExampleArg] {@include [CommonColDocs.SingleExample]}}
     * @param [col\] The [ColumnAccessor] pointing to the column.
     * @include [CommonColDocs.ColumnTypeParam]
     */
    private interface ColReferenceDocs

    /**
     * @include [ColReferenceDocs] {@setArg [CommonColDocs.ReceiverArg]}
     * {@setArg [CommonColDocs.Note] NOTE: This overload is an identity function and can be omitted.}
     */
    @Deprecated(IDENTITY_FUNCTION, ReplaceWith(COL_REPLACE))
    public fun <C> col(col: ColumnAccessor<C>): ColumnAccessor<C> = col

    /**
     * @include [ColReferenceDocs] {@setArg [CommonColDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> SingleColumn<DataRow<*>>.col(col: ColumnAccessor<C>): SingleColumn<C> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getChild(col)
                ?: throw IllegalStateException("Column '${col.path()}' not found in column group '${it.path}'")
            listOf(child)
        }.singleImpl()

    /**
     * @include [ColReferenceDocs] {@setArg [CommonColDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> AnyColumnGroupAccessor.col(col: ColumnAccessor<C>): ColumnAccessor<C> =
        this.ensureIsColumnGroup().column(col.path())

    /**
     * @include [ColReferenceDocs] {@setArg [CommonColDocs.ReceiverArg] "myColumnGroup".}
     */
    public fun <C> String.col(col: ColumnAccessor<C>): ColumnAccessor<C> =
        asColumnGroup().ensureIsColumnGroup().column(col.path())

    /**
     * @include [ColReferenceDocs] {@setArg [CommonColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    public fun <C> KProperty<*>.col(col: ColumnAccessor<C>): ColumnAccessor<C> =
        asColumnGroup().ensureIsColumnGroup().column(col.path())

    /**
     * @include [ColReferenceDocs] {@setArg [CommonColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colKPropertyDataRow")
    public fun <C> KProperty<DataRow<*>>.col(col: ColumnAccessor<C>): ColumnAccessor<C> =
        asColumnGroup().ensureIsColumnGroup().column(col.path())

    /**
     * @include [ColReferenceDocs] {@setArg [CommonColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    public fun <C> ColumnPath.col(col: ColumnAccessor<C>): ColumnAccessor<C> =
        asColumnGroup().ensureIsColumnGroup().column(col.path())

    // endregion

    // region name

    /**
     * @include [CommonColDocs]
     * {@setArg [CommonColDocs.Arg] "columnName"}
     * {@setArg [CommonColDocs.ExampleArg] {@include [CommonColDocs.DoubleExample]}}
     * @param [name\] The name of the column.
     */
    private interface ColNameDocs

    /**
     * @include [ColNameDocs] {@setArg [CommonColDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun col(name: String): ColumnAccessor<*> = column<Any?>(name)

    /**
     * @include [ColNameDocs] {@setArg [CommonColDocs.ReceiverArg]}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> col(name: String): ColumnAccessor<C> = column(name)

    /**
     * @include [ColNameDocs] {@setArg [CommonColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun SingleColumn<DataRow<*>>.col(name: String): SingleColumn<*> =
        col<Any?>(name)

    /**
     * @include [ColNameDocs] {@setArg [CommonColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.col(name: String): SingleColumn<C> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getChild(name)?.cast<C>()
                ?: throw IllegalStateException("Column '$name' not found in column group '${it.path}'")
            listOf(child)
        }.singleImpl()

    /**
     * @include [ColNameDocs] {@setArg [CommonColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun AnyColumnGroupAccessor.col(name: String): ColumnAccessor<*> =
        col<Any?>(name)

    /**
     * @include [ColNameDocs] {@setArg [CommonColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> AnyColumnGroupAccessor.col(name: String): ColumnAccessor<C> =
        this.ensureIsColumnGroup().column(name)

    /**
     * @include [ColNameDocs] {@setArg [CommonColDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun String.col(name: String): ColumnAccessor<*> =
        col<Any?>(name)

    /**
     * @include [ColNameDocs] {@setArg [CommonColDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> String.col(name: String): ColumnAccessor<C> =
        asColumnGroup()
            .ensureIsColumnGroup()
            .column(name)

    /**
     * @include [ColNameDocs] {@setArg [CommonColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun KProperty<*>.col(name: String): ColumnAccessor<*> =
        col<Any?>(name)

    /**
     * @include [ColNameDocs] {@setArg [CommonColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> KProperty<*>.col(name: String): ColumnAccessor<C> =
        asColumnGroup().ensureIsColumnGroup().column(name)

    /**
     * @include [ColNameDocs] {@setArg [CommonColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTypedKPropertyDataRow")
    public fun KProperty<DataRow<*>>.col(name: String): ColumnAccessor<*> =
        col<Any?>(name)

    /**
     * @include [ColNameDocs] {@setArg [CommonColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colKPropertyDataRow")
    public fun <C> KProperty<DataRow<*>>.col(name: String): ColumnAccessor<C> =
        asColumnGroup().ensureIsColumnGroup().column(name)

    /**
     * @include [ColNameDocs] {@setArg [CommonColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun ColumnPath.col(name: String): ColumnAccessor<*> =
        col<Any?>(name)

    /**
     * @include [ColNameDocs] {@setArg [CommonColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> ColumnPath.col(name: String): ColumnAccessor<C> =
        asColumnGroup().ensureIsColumnGroup().column(name)

    // endregion

    // region path

    /**
     * @include [CommonColDocs]
     * {@setArg [CommonColDocs.Arg] "pathTo"["columnName"\] }
     * {@setArg [CommonColDocs.ExampleArg] {@include [CommonColDocs.DoubleExample]}}
     * @param [path\] The path to the column.
     */
    private interface ColPathDocs

    /**
     * @include [ColPathDocs] {@setArg [CommonColDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun col(path: ColumnPath): ColumnAccessor<*> = column<Any?>(path)

    /**
     * @include [ColPathDocs] {@setArg [CommonColDocs.ReceiverArg]}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> col(path: ColumnPath): ColumnAccessor<C> = column(path)

    /**
     * @include [ColPathDocs] {@setArg [CommonColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun SingleColumn<DataRow<*>>.col(path: ColumnPath): SingleColumn<*> =
        col<Any?>(path)

    /**
     * @include [ColPathDocs] {@setArg [CommonColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.col(path: ColumnPath): SingleColumn<C> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getChild(path)?.cast<C>()
                ?: throw IllegalStateException("Column '$path' not found in column group '${it.path}'")
            listOf(child)
        }.singleImpl()

    /**
     * @include [ColPathDocs] {@setArg [CommonColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun AnyColumnGroupAccessor.col(path: ColumnPath): ColumnAccessor<*> =
        col<Any?>(path)

    /**
     * @include [ColPathDocs] {@setArg [CommonColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> AnyColumnGroupAccessor.col(path: ColumnPath): ColumnAccessor<C> =
        this.ensureIsColumnGroup().column(path)

    /**
     * @include [ColPathDocs] {@setArg [CommonColDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun String.col(path: ColumnPath): ColumnAccessor<*> =
        col<Any?>(path)

    /**
     * @include [ColPathDocs] {@setArg [CommonColDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> String.col(path: ColumnPath): ColumnAccessor<C> =
        asColumnGroup().ensureIsColumnGroup().column(path)

    /**
     * @include [ColPathDocs] {@setArg [CommonColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun KProperty<*>.col(path: ColumnPath): ColumnAccessor<*> =
        col<Any?>(path)

    /**
     * @include [ColPathDocs] {@setArg [CommonColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> KProperty<*>.col(path: ColumnPath): ColumnAccessor<C> =
        asColumnGroup().ensureIsColumnGroup().column(path)

    /**
     * @include [ColPathDocs] {@setArg [CommonColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTypedKPropertyDataRow")
    public fun KProperty<DataRow<*>>.col(path: ColumnPath): ColumnAccessor<*> =
        col<Any?>(path)

    /**
     * @include [ColPathDocs] {@setArg [CommonColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colKPropertyDataRow")
    public fun <C> KProperty<DataRow<*>>.col(path: ColumnPath): ColumnAccessor<C> =
        asColumnGroup().ensureIsColumnGroup().column(path)

    /**
     * @include [ColPathDocs] {@setArg [CommonColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun ColumnPath.col(path: ColumnPath): ColumnAccessor<*> =
        col<Any?>(path)

    /**
     * @include [ColPathDocs] {@setArg [CommonColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> ColumnPath.col(path: ColumnPath): ColumnAccessor<C> =
        asColumnGroup().ensureIsColumnGroup().column(path)

    // endregion

    // region property

    /**
     * @include [CommonColDocs]
     * {@setArg [CommonColDocs.Arg] Type::columnA}
     * {@setArg [CommonColDocs.ExampleArg] {@include [CommonColDocs.SingleExample]}}
     * @param [property\] The [KProperty] reference to the column.
     * @include [CommonColDocs.ColumnTypeParam]
     */
    private interface ColKPropertyDocs

    /**
     * @include [ColKPropertyDocs] {@setArg [CommonColDocs.ReceiverArg]}
     */
    public fun <C> col(property: KProperty<C>): SingleColumn<C> = column(property)

    /**
     * @include [ColKPropertyDocs] {@setArg [CommonColDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> SingleColumn<DataRow<*>>.col(property: KProperty<C>): SingleColumn<C> =
        col<C>(property.name)

    /**
     * @include [ColKPropertyDocs] {@setArg [CommonColDocs.ReceiverArg] myColumnGroup.}
     */
    public fun <C> AnyColumnGroupAccessor.col(property: KProperty<C>): ColumnAccessor<C> =
        this.ensureIsColumnGroup().column(property)

    /**
     * @include [ColKPropertyDocs] {@setArg [CommonColDocs.ReceiverArg] "myColumnGroup".}
     */
    public fun <C> String.col(property: KProperty<C>): ColumnAccessor<C> =
        asColumnGroup().ensureIsColumnGroup().column(property)

    /**
     * @include [ColKPropertyDocs] {@setArg [CommonColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    public fun <C> KProperty<*>.col(property: KProperty<C>): ColumnAccessor<C> =
        asColumnGroup().ensureIsColumnGroup().column(property)

    /**
     * @include [ColKPropertyDocs] {@setArg [CommonColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    public fun <C> ColumnPath.col(property: KProperty<C>): ColumnAccessor<C> =
        asColumnGroup().ensureIsColumnGroup().column(property)

    // endregion

    // region index

    /**
     * @include [CommonColDocs]
     * {@setArg [CommonColDocs.Arg] 0}
     * {@setArg [CommonColDocs.ExampleArg] {@include [CommonColDocs.DoubleExample]}}
     * @param [index\] The index of the column.
     * @throws [IndexOutOfBoundsException\] if the index is out of bounds.
     */
    private interface ColIndexDocs

    /**
     * @include [ColIndexDocs] {@setArg [CommonColDocs.ReceiverArg] `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().}
     * @include [CommonColDocs.ColumnTypeParam]
     * {@setArg [CommonColDocs.ExampleArg]
     * {@include [CommonColDocs.SingleExample]}
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][col]`1`[`]`][col]` \\}`
     * }
     * {@setArg [CommonColDocs.Note] NOTE: You can use the get-[] operator on [ColumnSets][ColumnSet] as well!}
     */
    public fun <C> ColumnSet<C>.col(index: Int): SingleColumn<C> = getAt(index)

    /**
     * @include [ColIndexDocs] {@setArg [CommonColDocs.ReceiverArg] `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().}
     * @include [CommonColDocs.ColumnTypeParam]
     * {@setArg [CommonColDocs.ExampleArg]
     * {@include [CommonColDocs.SingleExample]}
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[String][String]`>()`[`[`][col]`1`[`]`][col]` \\}`
     * }
     */
    public operator fun <C> ColumnSet<C>.get(index: Int): SingleColumn<C> = col(index)

    /**
     * @include [ColIndexDocs] {@setArg [CommonColDocs.ReceiverArg]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun ColumnsSelectionDsl<*>.col(index: Int): SingleColumn<*> =
        col<Any?>(index)

    /**
     * @include [ColIndexDocs] {@setArg [CommonColDocs.ReceiverArg]}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> ColumnsSelectionDsl<*>.col(index: Int): SingleColumn<C> =
        asSingleColumn().col<C>(index)

    /**
     * @include [ColIndexDocs] {@setArg [CommonColDocs.ReceiverArg] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun SingleColumn<DataRow<*>>.col(index: Int): SingleColumn<*> =
        col<Any?>(index)

    /**
     * @include [ColIndexDocs] {@setArg [CommonColDocs.ReceiverArg] myColumnGroup.}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.col(index: Int): SingleColumn<C> =
        this.ensureIsColumnGroup()
            .allColumnsInternal()
            .getAt(index)
            .cast()

    /**
     * @include [ColIndexDocs] {@setArg [CommonColDocs.ReceiverArg] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun String.col(index: Int): SingleColumn<*> =
        col<Any?>(index)

    /**
     * @include [ColIndexDocs] {@setArg [CommonColDocs.ReceiverArg] "myColumnGroup".}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> String.col(index: Int): SingleColumn<C> =
        asColumnGroup().col<C>(index)

    /**
     * @include [ColIndexDocs] {@setArg [CommonColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun KProperty<*>.col(index: Int): SingleColumn<*> =
        col<Any?>(index)

    /**
     * @include [ColIndexDocs] {@setArg [CommonColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> KProperty<*>.col(index: Int): SingleColumn<C> =
        asColumnGroup().col<C>(index)

    /**
     * @include [ColIndexDocs] {@setArg [CommonColDocs.ReceiverArg] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTypedKPropertyDataRow")
    public fun KProperty<DataRow<*>>.col(index: Int): SingleColumn<*> =
        col<Any?>(index)

    /**
     * @include [ColIndexDocs] {@setArg [CommonColDocs.ReceiverArg] Type::myColumnGroup.}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colKPropertyDataRow")
    public fun <C> KProperty<DataRow<*>>.col(index: Int): SingleColumn<C> =
        asColumnGroup().col<C>(index)

    /**
     * @include [ColIndexDocs] {@setArg [CommonColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("colUnTyped")
    public fun ColumnPath.col(index: Int): SingleColumn<*> =
        col<Any?>(index)

    /**
     * @include [ColIndexDocs] {@setArg [CommonColDocs.ReceiverArg] "pathTo"["myColumnGroup"].}
     * @include [CommonColDocs.ColumnTypeParam]
     */
    public fun <C> ColumnPath.col(index: Int): SingleColumn<C> =
        asColumnGroup().col<C>(index)

    // endregion
}

// endregion
