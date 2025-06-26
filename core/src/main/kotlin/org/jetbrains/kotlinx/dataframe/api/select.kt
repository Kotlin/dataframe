package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.AnyColumnReference
import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.annotations.Refine
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.changePath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region DataFrame

/**
 * ## The Select Operation
 *
 * Returns a new [DataFrame] with only the columns selected by [columns\].
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsMention]
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
    interface SelectSelectingOptions
}

/** {@set [SelectingColumns.OPERATION] [select][select]} */
@ExcludeFromSources
private interface SetSelectOperationArg

/**
 * {@include [Select]}
 * ### This Select Overload
 */
@ExcludeFromSources
private interface CommonSelectDocs

/**
 * @include [CommonSelectDocs]
 * @include [SelectingColumns.Dsl.WithExample] {@include [SetSelectOperationArg]}
 * @param [columns] The [Columns Selector][ColumnsSelector] used to select the columns of this [DataFrame].
 */
@Refine
@Interpretable("Select0")
public fun <T> DataFrame<T>.select(columns: ColumnsSelector<T, *>): DataFrame<T> = get(columns).toDataFrame().cast()

/**
 * @include [CommonSelectDocs]
 * @include [SelectingColumns.KProperties.WithExample] {@include [SetSelectOperationArg]}
 * @param [columns] The [KProperties][KProperty] used to select the columns of this [DataFrame].
 */
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
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
@Deprecated(DEPRECATED_ACCESS_API)
@AccessApiOverload
public fun <T> DataFrame<T>.select(vararg columns: AnyColumnReference): DataFrame<T> = select { columns.toColumnSet() }

// endregion

// region ColumnsSelectionDsl
// NOTE: invoke overloads are inside ColumnsSelectionDsl.kt due to conflicts

/**
 * ## Select {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface SelectColumnsSelectionDsl {

    /**
     * ## Select from [ColumnGroup] Grammar
     * {@include [DslGrammarTemplate]}
     *
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnsSelectorDef]}
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_GROUP_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnGroupName]}**`  {  `**{@include [DslGrammarTemplate.ColumnsSelectorRef]}**` \}`**
     *
     *  {@include [Indent]}`| `[**`{`**][ColumnsSelectionDsl.select]` `{@include [DslGrammarTemplate.ColumnsSelectorRef]}` `[**`\}`**][ColumnsSelectionDsl.select]
     * }
     * {@set [DslGrammarTemplate.PLAIN_DSL_PART]}
     * {@set [DslGrammarTemplate.COLUMN_SET_PART]}
     */
    public interface Grammar {

        /** __`.`__[**`select`**][ColumnsSelectionDsl.select] */
        public interface ColumnGroupName
    }

    /**
     * ## Select from [ColumnGroup]
     *
     * Perform a selection of columns using the {@include [ColumnsSelectionDslLink]} on
     * any [ColumnGroup]. This is more powerful than the [cols][ColumnsSelectionDsl.cols] filter, because now all
     * operations of the DSL are at your disposal.
     *
     * The scope of the new DSL instance is relative to
     * the [ColumnGroup] you are selecting from.
     *
     * The [invoke][ColumnsSelectionDsl.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[select][SingleColumn.select]`  { someCol  `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]`  { "myGroupCol"  `[{][String.select]`  "colA" and  `[expr][ColumnsSelectionDsl.expr]`  { 0 }  `[}][String.select]` }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myGroupCol"].`[select][ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[select][DataFrame.select]` { it["myGroupCol"].`[asColumnGroup][DataColumn.asColumnGroup]`()`[() {][SingleColumn.select]`  "colA" and "colB"  `[}][SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * {@get [CommonSelectDocs.EXAMPLE]}
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

        interface EXAMPLE
    }

    /**
     * @include [CommonSelectDocs]
     * @set [CommonSelectDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[select][SingleColumn.select]`  { someCol  `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]`  { myColGroup  `[`{`][SingleColumn.select]`  colA  `[and][ColumnsSelectionDsl.and]`  colB  `[`}`][SingleColumn.select]` }`
     */
    @Interpretable("NestedSelect")
    public fun <C, R> SingleColumn<DataRow<C>>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        selectInternal(selector)

    /**
     * @include [CommonSelectDocs]
     * @set [CommonSelectDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[select][KProperty.select]`  { someCol  `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]`  { DataSchemaType::myColGroup  `[`{`][KProperty.select]`  colA  `[and][ColumnsSelectionDsl.and]`  colB  `[`}`][KProperty.select]` }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C, R> KProperty<C>.select(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /**
     * @include [SelectColumnsSelectionDsl.CommonSelectDocs]
     * @set [SelectColumnsSelectionDsl.CommonSelectDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[select][String.select]`  { someCol  `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]`  { "myColGroup"  `[`{`][String.select]`  colA  `[and][ColumnsSelectionDsl.and]`  colB  `[`}`][String.select]` }`
     */
    public fun <R> String.select(selector: ColumnsSelector<*, R>): ColumnSet<R> = columnGroup(this).select(selector)

    /**
     * @include [CommonSelectDocs]
     * @set [CommonSelectDocs.EXAMPLE]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[select][ColumnPath.select]`  { someCol  `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]`  { "pathTo"["myColGroup"]  `[`{`][ColumnPath.select]`  colA  `[and][ColumnsSelectionDsl.and]`  colB  `[`}`][ColumnPath.select]` }`
     *
     * `df.`[select][DataFrame.select]`  {  `[pathOf][pathOf]`("pathTo", "myColGroup").`[select][ColumnPath.select]`  { someCol  `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[pathOf][pathOf]`("pathTo", "myColGroup")`[`() {`][ColumnPath.select]`  someCol  `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() `[`}`][ColumnPath.select]` }`
     */
    public fun <R> ColumnPath.select(selector: ColumnsSelector<*, R>): ColumnSet<R> = columnGroup(this).select(selector)
}

internal fun <C, R> SingleColumn<DataRow<C>>.selectInternal(selector: ColumnsSelector<C, R>): ColumnSet<R> =
    createColumnSet { context ->
        this.ensureIsColumnGroup().resolveSingle(context)?.let { col ->
            require(col.isColumnGroup()) {
                "Column ${col.path} is not a ColumnGroup and can thus not be selected from."
            }

            col.getColumnsWithPaths(selector as ColumnsSelector<*, R>)
                .map { it.changePath(col.path + it.path) }
        } ?: emptyList()
    }
// endregion
