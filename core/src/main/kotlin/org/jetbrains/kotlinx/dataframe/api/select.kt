package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumnsLink
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.changePath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_SELECT_COLS
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_SELECT_COLS_REPLACE
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty

// region DataFrame

/**
 * ## The Select Operation
 *
 * Returns a new [DataFrame] with only the columns selected by [columns].
 *
 * See [Selecting Columns][SelectSelectingOptions].
 *
 * For more information: {@include [DocumentationUrls.Select]}
 */
internal interface Select {

    /**
     * {@comment Version of [SelectingColumns] with correctly filled in examples}
     * @include [SelectingColumns] {@include [SetSelectOperationArg]}
     */
    public interface SelectSelectingOptions
}

/** {@setArg [SelectingColumns.OperationArg] [select][select]} */
private interface SetSelectOperationArg

/**
 * {@include [Select]}
 * ### This Select Overload
 */
private interface CommonSelectDocs

/**
 * @include [CommonSelectDocs]
 * @include [SelectingColumns.Dsl.WithExample] {@include [SetSelectOperationArg]}
 * @param [columns] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame].
 */
public fun <T> DataFrame<T>.select(columns: ColumnsSelector<T, *>): DataFrame<T> =
    get(columns).toDataFrame().cast()

/**
 * @include [CommonSelectDocs]
 * @include [SelectingColumns.KProperties.WithExample] {@include [SetSelectOperationArg]}
 * @param [columns] The [KProperties][KProperty] used to select the columns of this [DataFrame].
 */
public fun <T> DataFrame<T>.select(vararg columns: KProperty<*>): DataFrame<T> = select { columns.toColumnSet() }

/**
 * @include [CommonSelectDocs]
 * @include [SelectingColumns.ColumnNames.WithExample] {@include [SetSelectOperationArg]}
 * @param [columns] The [Column Names][String] used to select the columns of this [DataFrame].
 */
public fun <T> DataFrame<T>.select(vararg columns: String): DataFrame<T> = select { columns.toColumnSet() }

/**
 * @include [CommonSelectDocs]
 * @include [SelectingColumns.ColumnAccessors.WithExample] {@include [SetSelectOperationArg]}
 * @param [columns] The [Column Accessors][ColumnReference] used to select the columns of this [DataFrame].
 */
public fun <T> DataFrame<T>.select(vararg columns: AnyColumnReference): DataFrame<T> = select { columns.toColumnSet() }

// endregion

// region ColumnsSelectionDsl
// NOTE: invoke overloads are inside ColumnsSelectionDsl.kt due to conflicts
public interface SelectColumnsSelectionDsl {

    /**
     * ## Select from [ColumnGroup] Usage
     * {@include [UsageTemplate]}
     *
     * {@setArg [UsageTemplate.DefinitionsArg]
     *  {@include [UsageTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnsSelectorDef]}
     * }
     *
     * {@setArg [UsageTemplate.ColumnGroupFunctionsArg]
     *  {@include [Indent]}{@include [ColumnGroupName]}**` {`** {@include [UsageTemplate.ColumnsSelectorRef]} **`\}`**
     *
     *  {@include [Indent]}`|`[**` {`**][ColumnsSelectionDsl.select] {@include [UsageTemplate.ColumnsSelectorRef]} [**`\}`**][ColumnsSelectionDsl.select]
     * }
     * {@setArg [UsageTemplate.PlainDslPart]}
     * {@setArg [UsageTemplate.ColumnSetPart]}
     */
    public interface Usage {

        /** .[**select**][ColumnsSelectionDsl.select] */
        public interface ColumnGroupName
    }

    /**
     * ## Select from [ColumnGroup]
     *
     * Perform a selection of columns using the {@include [ColumnsSelectionDslLink]} on
     * any [ColumnGroup]. This is more powerful than [ColumnsSelectionDsl.cols], because all operations of
     * the DSL are at your disposal.
     *
     * The [invoke][ColumnsSelectionDsl.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * ### Check out: [Usage]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[select][SingleColumn.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "myGroupCol" `[{][String.select]` "colA" and `[expr][ColumnsSelectionDsl.expr]` { 0 } `[}][String.select]` }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[select][ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][DataColumn.asColumnGroup]`()`[() {][SingleColumn.select]` "colA" and "colB" `[}][SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [CommonSelectDocs.ExampleArg]}
     *
     * {@include [LineBreak]}
     *
     * See also [except][ColumnsSelectionDsl.except]/[allExcept][ColumnsSelectionDsl.allColsExcept] for the inverted operation of this function.
     *
     * @param [selector\] The [ColumnsSelector] to use for the selection.
     * @receiver The [ColumnGroup] to select from.
     * @throws [IllegalArgumentException\] If [this\] is not a [ColumnGroup].
     * @return A [ColumnSet] containing the columns selected by [selector\].
     * @see [SingleColumn.except\]
     */
    private interface CommonSelectDocs {

        interface ExampleArg
    }

    /**
     * @include [CommonSelectDocs]
     * @setArg [CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[select][SingleColumn.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup `[{][SingleColumn.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][SingleColumn.select]` }`
     */
    public fun <C, R> SingleColumn<DataRow<C>>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        selectInternal(selector)

    /**
     * @include [CommonSelectDocs]
     * @setArg [CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[select][KProperty.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup `[`{`][KProperty.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[`}`][KProperty.select]` }`
     */
    @OptIn(ExperimentalTypeInference::class)
    @OverloadResolutionByLambdaReturnType
    // TODO: [KT-64092](https://youtrack.jetbrains.com/issue/KT-64092/OVERLOADRESOLUTIONAMBIGUITY-caused-by-lambda-argument)
    public fun <C, R> KProperty<C>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /**
     * @include [CommonSelectDocs]
     * @setArg [CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[select][KProperty.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup `[`{`][KProperty.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[`}`][KProperty.select]` }`
     *
     * ## NOTE: {@comment TODO fix warning}
     * If you get a warning `CANDIDATE_CHOSEN_USING_OVERLOAD_RESOLUTION_BY_LAMBDA_ANNOTATION`, you
     * can safely ignore this. It is caused by a workaround for a bug in the Kotlin compiler
     * ([KT-64092](https://youtrack.jetbrains.com/issue/KT-64092/OVERLOADRESOLUTIONAMBIGUITY-caused-by-lambda-argument)).
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowSelect")
    public fun <C, R> KProperty<DataRow<C>>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /**
     * @include [SelectColumnsSelectionDsl.CommonSelectDocs]
     * @setArg [SelectColumnsSelectionDsl.CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[select][String.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup" `[{][String.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][String.select]` }`
     */
    public fun <R> String.select(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /**
     * @include [CommonSelectDocs]
     * @setArg [CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[select][ColumnPath.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"] `[{][ColumnPath.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][ColumnPath.select]` }`
     *
     * `df.`[select][DataFrame.select]` { `[pathOf][pathOf]`("pathTo", "myColGroup").`[select][ColumnPath.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { `[pathOf][pathOf]`("pathTo", "myColGroup")`[() {][ColumnPath.select]` someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() `[}][ColumnPath.select]` }`
     */
    public fun <R> ColumnPath.select(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    // region deprecated

    @Deprecated(
        message = COL_SELECT_DSL_SELECT_COLS,
        replaceWith = ReplaceWith(COL_SELECT_DSL_SELECT_COLS_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun SingleColumn<DataRow<*>>.select(vararg columns: String): ColumnSet<*> =
        selectInternal { columns.toColumnSet() }

    @Deprecated(
        message = COL_SELECT_DSL_SELECT_COLS,
        replaceWith = ReplaceWith(COL_SELECT_DSL_SELECT_COLS_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun <R> SingleColumn<DataRow<*>>.select(vararg columns: ColumnReference<R>): ColumnSet<R> =
        selectInternal { columns.toColumnSet() }

    @Deprecated(
        message = COL_SELECT_DSL_SELECT_COLS,
        replaceWith = ReplaceWith(COL_SELECT_DSL_SELECT_COLS_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun <R> SingleColumn<DataRow<*>>.select(vararg columns: KProperty<R>): ColumnSet<R> =
        selectInternal { columns.toColumnSet() }

    // endregion
}

internal fun <C, R> SingleColumn<DataRow<C>>.selectInternal(selector: ColumnsSelector<C, R>): ColumnSet<R> =
    createColumnSet { context ->
        this.ensureIsColumnGroup().resolveSingle(context)?.let { col ->
            require(col.isColumnGroup()) {
                "Column ${col.path} is not a ColumnGroup and can thus not be selected from."
            }

            col.asColumnGroup()
                .getColumnsWithPaths(selector as ColumnsSelector<*, R>)
                .map { it.changePath(col.path + it.path) }
        } ?: emptyList()
    }
// endregion
