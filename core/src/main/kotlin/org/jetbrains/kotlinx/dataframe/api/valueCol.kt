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
 * ## Value Col {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 * @param _UNUSED {@include [Issues.ConflictingOverloadsK2Link]}
 */
public interface ValueColColumnsSelectionDsl<out _UNUSED> {

    /**
     * ## Value Col Grammar
     *
     * @include [DslGrammarTemplate]
     * {@set [DslGrammarTemplate.DEFINITIONS]
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
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [PlainDslName]}`[`**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>`**`]`**`(`**{@include [DslGrammarTemplate.ColumnRef]}`  |  `{@include [DslGrammarTemplate.IndexRef]}**`)`**
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnSetName]}**`(`**{@include [DslGrammarTemplate.IndexRef]}**`)`**
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_GROUP_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnGroupName]}`[`**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>`**`]`**`(`**{@include [DslGrammarTemplate.ColumnRef]}`  |  `{@include [DslGrammarTemplate.IndexRef]}**`)`**
     * }
     */
    public interface Grammar {

        /** [**`valueCol`**][ColumnsSelectionDsl.valueCol] */
        public typealias PlainDslName = Nothing

        /** __`.`__[**`valueCol`**][ColumnsSelectionDsl.valueCol] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[**`valueCol`**][ColumnsSelectionDsl.valueCol] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Value Col
     *
     * Creates a [ColumnAccessor] (or [SingleColumn]) for a value column with the given argument which can be either
     * an index ([Int]) or a reference to a column
     * ([String], [ColumnPath], [KProperty], or [ColumnAccessor]; any {@include [AccessApiLink]}).
     *
     * This is a DSL-shorthand for [valueColumn] and can be both typed and untyped (in case you're supplying
     * a column name, -path, or index). In addition, extra runtime checks are in place to ensure that the column
     * you specify is actually a value column.
     * The function can also be called on [ColumnGroups][ColumnGroupReference] to create
     * an accessor for a value column inside a [ColumnGroup].
     * {@include [LineBreak]}
     * {@get [CommonValueColDocs.NOTE]}
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]`  {  `[valueCol][valueCol]`<`[String][String]`>("valueColA") }`
     *
     * `df.`[select][DataFrame.select]`  {  `[valueCol][valueCol]`(SomeType::valueColB) }`
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[valueCol][valueCol]`(1) }`
     *
     * #### Examples for this overload:
     *
     * {@get [CommonValueColDocs.EXAMPLE]}
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
     */
    private interface CommonValueColDocs {

        // Example argument, can be either {@include [SingleExample]} or {@include [DoubleExample]}
        typealias EXAMPLE = Nothing

        /**
         * `df.`[select][DataFrame.select]` { {@get [CommonValueColDocs.RECEIVER]}`[valueCol][valueCol]`({@get [CommonValueColDocs.ARG]}) \}`
         */
        typealias SingleExample = Nothing

        /**
         * `df.`[select][DataFrame.select]` { {@get [CommonValueColDocs.RECEIVER]}`[valueCol][valueCol]`({@get [CommonValueColDocs.ARG]}) \}`
         *
         * `df.`[select][DataFrame.select]` { {@get [CommonValueColDocs.RECEIVER]}`[valueCol][valueCol]`<`[String][String]`>({@get [CommonValueColDocs.ARG]}) \}`
         */
        typealias DoubleExample = Nothing

        // Receiver argument for the example(s)
        typealias RECEIVER = Nothing

        // Argument for the example(s)
        typealias ARG = Nothing

        // Optional note
        typealias NOTE = Nothing

        /** @param [C\] The type of the value column. */
        typealias ValueColumnTypeParam = Nothing
    }

    // region reference

    /**
     * @include [CommonValueColDocs]
     * {@set [CommonValueColDocs.ARG] valueColumnA}
     * {@set [CommonValueColDocs.EXAMPLE] {@include [CommonValueColDocs.SingleExample]}}
     * @param [col\] The [ColumnAccessor] pointing to the value column.
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    private typealias ValueColReferenceDocs = Nothing

    /**
     * @include [ValueColReferenceDocs] {@set [CommonValueColDocs.RECEIVER]}
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> = valueCol.ensureIsValueColumn()

    /**
     * @include [ValueColReferenceDocs] {@set [CommonValueColDocs.RECEIVER] myColumnGroup.}
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
     * @include [ValueColReferenceDocs] {@set [CommonValueColDocs.RECEIVER] myColumnGroup.}
     */
    public fun <C> AnyColumnGroupAccessor.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        this.ensureIsColumnGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    /**
     * @include [ValueColReferenceDocs] {@set [CommonValueColDocs.RECEIVER] "myColumnGroup".}
     */
    public fun <C> String.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    /**
     * @include [ValueColReferenceDocs] {@set [CommonValueColDocs.RECEIVER] Type::myColumnGroup.}
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    /**
     * @include [ValueColReferenceDocs] {@set [CommonValueColDocs.RECEIVER] "pathTo"["myColumnGroup"].}
     */
    public fun <C> ColumnPath.valueCol(valueCol: ColumnAccessor<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(valueCol.path()).ensureIsValueColumn()

    // endregion

    // region name

    /**
     * @include [CommonValueColDocs]
     * {@set [CommonValueColDocs.ARG] "valueColumnName"}
     * {@set [CommonValueColDocs.EXAMPLE] {@include [CommonValueColDocs.DoubleExample]}}
     * @param [name\] The name of the value column.
     */
    private typealias ValueColNameDocs = Nothing

    /**
     * @include [ValueColNameDocs] {@set [CommonValueColDocs.RECEIVER]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun valueCol(name: String): ColumnAccessor<*> = valueColumn<Any?>(name).ensureIsValueColumn()

    /**
     * @include [ValueColNameDocs] {@set [CommonValueColDocs.RECEIVER]}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> valueCol(name: String): ColumnAccessor<C> = valueColumn<C>(name).ensureIsValueColumn()

    /**
     * @include [ValueColNameDocs] {@set [CommonValueColDocs.RECEIVER] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun SingleColumn<DataRow<*>>.valueCol(name: String): SingleColumn<*> = valueCol<Any?>(name)

    /**
     * @include [ValueColNameDocs] {@set [CommonValueColDocs.RECEIVER] myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(name: String): SingleColumn<C> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getCol(name)?.cast<C>()
                ?: throw IllegalStateException("Value column '$name' not found in column group '${it.path}'")
            child.data.ensureIsValueColumn()
            listOf(child)
        }.singleImpl()

    /**
     * @include [ValueColNameDocs] {@set [CommonValueColDocs.RECEIVER] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun AnyColumnGroupAccessor.valueCol(name: String): ColumnAccessor<*> = valueCol<Any?>(name)

    /**
     * @include [ValueColNameDocs] {@set [CommonValueColDocs.RECEIVER] myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> AnyColumnGroupAccessor.valueCol(name: String): ColumnAccessor<C> =
        this.ensureIsColumnGroup().valueColumn<C>(name).ensureIsValueColumn()

    /**
     * @include [ValueColNameDocs] {@set [CommonValueColDocs.RECEIVER] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun String.valueCol(name: String): ColumnAccessor<*> = valueCol<Any?>(name)

    /**
     * @include [ValueColNameDocs] {@set [CommonValueColDocs.RECEIVER] "myColumnGroup".}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> String.valueCol(name: String): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(name).ensureIsValueColumn()

    /**
     * @include [ValueColNameDocs] {@set [CommonValueColDocs.RECEIVER] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.valueCol(name: String): ColumnAccessor<*> = valueCol<Any?>(name)

    /**
     * @include [ValueColNameDocs] {@set [CommonValueColDocs.RECEIVER] Type::myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.valueCol(name: String): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(name).ensureIsValueColumn()

    /**
     * @include [ValueColNameDocs] {@set [CommonValueColDocs.RECEIVER] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun ColumnPath.valueCol(name: String): ColumnAccessor<*> = valueCol<Any?>(name)

    /**
     * @include [ValueColNameDocs] {@set [CommonValueColDocs.RECEIVER] "pathTo"["myColumnGroup"].}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> ColumnPath.valueCol(name: String): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(name).ensureIsValueColumn()

    // endregion

    // region path

    /**
     * @include [CommonValueColDocs]
     * {@set [CommonValueColDocs.ARG] "pathTo"["valueColumnName"\] }
     * {@set [CommonValueColDocs.EXAMPLE] {@include [CommonValueColDocs.DoubleExample]}}
     * @param [path\] The path to the value column.
     */
    private typealias ValueColPathDocs = Nothing

    /**
     * @include [ValueColPathDocs] {@set [CommonValueColDocs.RECEIVER]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun valueCol(path: ColumnPath): ColumnAccessor<*> = valueColumn<Any?>(path).ensureIsValueColumn()

    /**
     * @include [ValueColPathDocs] {@set [CommonValueColDocs.RECEIVER]}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> valueCol(path: ColumnPath): ColumnAccessor<C> = valueColumn<C>(path).ensureIsValueColumn()

    /**
     * @include [ValueColPathDocs] {@set [CommonValueColDocs.RECEIVER] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun SingleColumn<DataRow<*>>.valueCol(path: ColumnPath): SingleColumn<*> = valueCol<Any?>(path)

    /**
     * @include [ValueColPathDocs] {@set [CommonValueColDocs.RECEIVER] myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(path: ColumnPath): SingleColumn<C> =
        this.ensureIsColumnGroup().transformSingle {
            val child = it.getCol(path)?.cast<C>()
                ?: throw IllegalStateException("Value column '$path' not found in column group '${it.path}'")
            child.data.ensureIsValueColumn()
            listOf(child)
        }.singleImpl()

    /**
     * @include [ValueColPathDocs] {@set [CommonValueColDocs.RECEIVER] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun AnyColumnGroupAccessor.valueCol(path: ColumnPath): ColumnAccessor<*> = valueCol<Any?>(path)

    /**
     * @include [ValueColPathDocs] {@set [CommonValueColDocs.RECEIVER] myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> AnyColumnGroupAccessor.valueCol(path: ColumnPath): ColumnAccessor<C> =
        this.ensureIsColumnGroup().valueColumn<C>(path).ensureIsValueColumn()

    /**
     * @include [ValueColPathDocs] {@set [CommonValueColDocs.RECEIVER] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun String.valueCol(path: ColumnPath): ColumnAccessor<*> = valueCol<Any?>(path)

    /**
     * @include [ValueColPathDocs] {@set [CommonValueColDocs.RECEIVER] "myColumnGroup".}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> String.valueCol(path: ColumnPath): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(path).ensureIsValueColumn()

    /**
     * @include [ValueColPathDocs] {@set [CommonValueColDocs.RECEIVER] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.valueCol(path: ColumnPath): ColumnAccessor<*> = valueCol<Any?>(path)

    /**
     * @include [ValueColPathDocs] {@set [CommonValueColDocs.RECEIVER] Type::myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.valueCol(path: ColumnPath): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(path).ensureIsValueColumn()

    /**
     * @include [ValueColPathDocs] {@set [CommonValueColDocs.RECEIVER] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun ColumnPath.valueCol(path: ColumnPath): ColumnAccessor<*> = valueCol<Any?>(path)

    /**
     * @include [ValueColPathDocs] {@set [CommonValueColDocs.RECEIVER] "pathTo"["myColumnGroup"].}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> ColumnPath.valueCol(path: ColumnPath): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn<C>(path).ensureIsValueColumn()

    // endregion

    // region property

    /**
     * @include [CommonValueColDocs]
     * {@set [CommonValueColDocs.ARG] Type::valueColumnA}
     * {@set [CommonValueColDocs.EXAMPLE] {@include [CommonValueColDocs.SingleExample]}}
     * @param [property\] The [KProperty] reference to the value column.
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    private typealias ValueColKPropertyDocs = Nothing

    /**
     * @include [ValueColKPropertyDocs] {@set [CommonValueColDocs.RECEIVER]}
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> valueCol(property: KProperty<C>): SingleColumn<C> = valueColumn(property).ensureIsValueColumn()

    /**
     * @include [ValueColKPropertyDocs] {@set [CommonValueColDocs.RECEIVER] myColumnGroup.}
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> SingleColumn<DataRow<*>>.valueCol(property: KProperty<C>): SingleColumn<C> =
        valueCol<C>(property.name)

    /**
     * @include [ValueColKPropertyDocs] {@set [CommonValueColDocs.RECEIVER] myColumnGroup.}
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> AnyColumnGroupAccessor.valueCol(property: KProperty<C>): ColumnAccessor<C> =
        this.ensureIsColumnGroup().valueColumn(property).ensureIsValueColumn()

    /**
     * @include [ValueColKPropertyDocs] {@set [CommonValueColDocs.RECEIVER] "myColumnGroup".}
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> String.valueCol(property: KProperty<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn(property).ensureIsValueColumn()

    /**
     * @include [ValueColKPropertyDocs] {@set [CommonValueColDocs.RECEIVER] Type::myColumnGroup.}
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.valueCol(property: KProperty<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn(property).ensureIsValueColumn()

    /**
     * @include [ValueColKPropertyDocs] {@set [CommonValueColDocs.RECEIVER] "pathTo"["myColumnGroup"].}
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnPath.valueCol(property: KProperty<C>): ColumnAccessor<C> =
        columnGroup(this).ensureIsColumnGroup().valueColumn(property).ensureIsValueColumn()

    // endregion

    // region index

    /**
     * @include [CommonValueColDocs]
     * {@set [CommonValueColDocs.ARG] 0}
     * {@set [CommonValueColDocs.EXAMPLE] {@include [CommonValueColDocs.DoubleExample]}}
     * @param [index\] The index of the value column.
     * @throws [IndexOutOfBoundsException\] if the index is out of bounds.
     */
    private typealias ValueColIndexDocs = Nothing

    /**
     * @include [ValueColIndexDocs] {@set [CommonValueColDocs.RECEIVER] `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>().}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     * {@set [CommonValueColDocs.EXAMPLE] {@include [CommonValueColDocs.SingleExample]}}
     */
    public fun <C> ColumnSet<C>.valueCol(index: Int): SingleColumn<C> = getAt(index).ensureIsValueColumn()

    /**
     * @include [ValueColIndexDocs] {@set [CommonValueColDocs.RECEIVER]}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun ColumnsSelectionDsl<*>.valueCol(index: Int): SingleColumn<*> = valueCol<Any?>(index)

    /**
     * @include [ValueColIndexDocs] {@set [CommonValueColDocs.RECEIVER]}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> ColumnsSelectionDsl<*>.valueCol(index: Int): SingleColumn<C> = asSingleColumn().valueCol<C>(index)

    /**
     * @include [ValueColIndexDocs] {@set [CommonValueColDocs.RECEIVER] myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun SingleColumn<DataRow<*>>.valueCol(index: Int): SingleColumn<*> = valueCol<Any?>(index)

    /**
     * @include [ValueColIndexDocs] {@set [CommonValueColDocs.RECEIVER] myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> SingleColumn<DataRow<*>>.valueCol(index: Int): SingleColumn<C> =
        this.ensureIsColumnGroup()
            .allColumnsInternal()
            .getAt(index)
            .ensureIsValueColumn()
            .cast()

    /**
     * @include [ValueColIndexDocs] {@set [CommonValueColDocs.RECEIVER] "myColumnGroup".}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun String.valueCol(index: Int): SingleColumn<*> = valueCol<Any?>(index)

    /**
     * @include [ValueColIndexDocs] {@set [CommonValueColDocs.RECEIVER] "myColumnGroup".}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> String.valueCol(index: Int): SingleColumn<C> = columnGroup(this).valueCol<C>(index)

    /**
     * @include [ValueColIndexDocs] {@set [CommonValueColDocs.RECEIVER] Type::myColumnGroup.}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.valueCol(index: Int): SingleColumn<*> = valueCol<Any?>(index)

    /**
     * @include [ValueColIndexDocs] {@set [CommonValueColDocs.RECEIVER] Type::myColumnGroup.}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.valueCol(index: Int): SingleColumn<C> = columnGroup(this).valueCol<C>(index)

    /**
     * @include [ValueColIndexDocs] {@set [CommonValueColDocs.RECEIVER] "pathTo"["myColumnGroup"].}
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("valueColUnTyped")
    public fun ColumnPath.valueCol(index: Int): SingleColumn<*> = valueCol<Any?>(index)

    /**
     * @include [ValueColIndexDocs] {@set [CommonValueColDocs.RECEIVER] "pathTo"["myColumnGroup"].}
     * @include [CommonValueColDocs.ValueColumnTypeParam]
     */
    public fun <C> ColumnPath.valueCol(index: Int): SingleColumn<C> = columnGroup(this).valueCol<C>(index)

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
