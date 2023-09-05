package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl
public interface ColumnNameFiltersColumnsSelectionDsl<out T> : ColumnsSelectionDslExtension<T> {
    // region nameContains

    /**
     * ## (Children) Name Contains
     * Returns a ([transformable][TransformableColumnSet]) [ColumnSet] containing
     * all columns containing {@getArg [CommonNameContainsDocs.ArgumentArg]} in their name.
     *
     * NOTE: For [column groups][ColumnGroup], `nameContains` is named `childrenNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][ColumnsSelectionDsl.cols]` { `{@getArg [ArgumentArg]}{@getArg [ArgumentArg]}` `[in][String.contains]` it.`[name][DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[nameContains][SingleColumn.childrenNameContains]`("my").`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[nameContains][String.childrenNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[nameContains][SingleColumn.childrenNameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [ExampleArg]}
     *
     * @param {@getArg [ArgumentArg]} what the column name should contain to be included in the result.
     * @return A ([transformable][TransformableColumnSet]) [ColumnSet] containing
     *   all columns containing {@getArg [CommonNameContainsDocs.ArgumentArg]} in their name.
     */
    private interface CommonNameContainsDocs {
        interface ExampleArg

        /** [text\] or [regex\] */
        interface ArgumentArg
    }

    /**
     * ## (Children) Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [text] in their name.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `childrenNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.childrenNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [ExampleArg][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.CommonNameContainsDocs.ExampleArg]}
     *
     * @param [text] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     */
    private interface NameContainsTextDocs

    /**
     * ## (Children) Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [text] in their name.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `childrenNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.childrenNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[nameContains][ColumnSet.nameContains]`("my") }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameContains][ColumnSet.nameContains]`("my") }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameContains(text: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.contains(text) } as TransformableColumnSet<C>

    /**
     * ## (Children) Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [text] in their name.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `childrenNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.childrenNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[nameContains][ColumnsSelectionDsl.childrenNameContains]`("my") }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     */
    public fun ColumnsSelectionDsl<*>.nameContains(text: CharSequence): TransformableColumnSet<*> =
        asSingleColumn().childrenNameContains(text)

    /**
     * ## (Children) Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [text] in their name.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `childrenNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.childrenNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[childrenNameContains][SingleColumn.childrenNameContains]`("my").`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     */
    public fun SingleColumn<DataRow<*>>.childrenNameContains(text: CharSequence): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.contains(text) }

    /**
     * ## (Children) Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [text] in their name.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `childrenNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.childrenNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameContains][String.childrenNameContains]`("my").`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameContains][String.childrenNameContains]`("my") }`
     *
     *
     * @param [text] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     */
    public fun String.childrenNameContains(text: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(text)

    /**
     * ## (Children) Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [text] in their name.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `childrenNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.childrenNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameContains][SingleColumn.childrenNameContains]`("my") }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[childrenNameContains][KProperty.childrenNameContains]`("my").`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     */
    public fun KProperty<DataRow<*>>.childrenNameContains(text: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(text)

    /**
     * ## (Children) Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [text] in their name.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `childrenNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { `[text][text]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.childrenNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameContains][ColumnPath.childrenNameContains]`("my") }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameContains][ColumnPath.childrenNameContains]`("my").`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * @param [text] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [text] in their name.
     */
    public fun ColumnPath.childrenNameContains(text: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(text)

    /**
     * ## (Children) Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [regex] in their name.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `childrenNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.childrenNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [ExampleArg][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.CommonNameContainsDocs.ExampleArg]}
     *
     * @param [regex] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     */
    private interface NameContainsRegexDocs

    /**
     * ## (Children) Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [regex] in their name.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `childrenNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.childrenNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[nameContains][ColumnSet.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameContains][ColumnSet.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameContains(regex: Regex): TransformableColumnSet<C> =
        colsInternal { it.name.contains(regex) } as TransformableColumnSet<C>

    /**
     * ## (Children) Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [regex] in their name.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `childrenNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.childrenNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[nameContains][ColumnsSelectionDsl.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     */
    public fun ColumnsSelectionDsl<*>.nameContains(regex: Regex): TransformableColumnSet<*> =
        asSingleColumn().childrenNameContains(regex)

    /**
     * ## (Children) Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [regex] in their name.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `childrenNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.childrenNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[childrenNameContains][SingleColumn.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")).`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     */
    public fun SingleColumn<DataRow<*>>.childrenNameContains(regex: Regex): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.contains(regex) }

    /**
     * ## (Children) Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [regex] in their name.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `childrenNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.childrenNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameContains][String.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")).`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameContains][String.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     */
    public fun String.childrenNameContains(regex: Regex): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(regex)

    /**
     * ## (Children) Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [regex] in their name.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `childrenNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.childrenNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameContains][SingleColumn.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[childrenNameContains][KProperty.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")).`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     */
    public fun KProperty<DataRow<*>>.childrenNameContains(regex: Regex): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(regex)

    /**
     * ## (Children) Name Contains
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns containing [regex] in their name.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `nameContains` is named `childrenNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { `[regex][regex]` `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[nameContains][kotlin.String.childrenNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[nameContains][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.childrenNameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameContains][ColumnPath.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameContains][ColumnPath.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")).`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * @param [regex] what the column name should contain to be included in the result.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns containing [regex] in their name.
     */
    public fun ColumnPath.childrenNameContains(regex: Regex): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(regex)

    // endregion

    /**
     * ## (Children) Name {@getArg [CommonNameStartsEndsDocs.CapitalTitle]} With
     * Returns a ([transformable][TransformableColumnSet]) [ColumnSet] containing
     * all columns {@getArg [CommonNameStartsEndsDocs.Noun]} with {@getArg [CommonNameStartsEndsDocs.ArgumentArg]} in their name.
     *
     * If [this\] is a [SingleColumn] containing a [ColumnGroup], the function runs on the children of the [ColumnGroup].
     * Else, if [this\] is a [ColumnSet], the function runs on the [ColumnSet] itself.
     *
     * This function is a shorthand for [cols][ColumnsSelectionDsl.cols]` { it.`[name][DataColumn.name]`.`[{@getArg [OperationName]}][String.{@getArg [OperationName]}]`(`{@getArg [ArgumentArg]}{@getArg [ArgumentArg]}`) }`.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[{@getArg [NameOperationName]}][ColumnsSelectionDsl.{@getArg [NameOperationName]}]`("order").`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[{@getArg [ChildrenNameOperationName]}][String.{@getArg [ChildrenNameOperationName]}]`("b") }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[{@getArg [ChildrenNameOperationName]}][SingleColumn.{@getArg [ChildrenNameOperationName]}]`("a") }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [ExampleArg]}
     *
     * @param {@getArg [ArgumentArg]} Columns {@getArg [CommonNameStartsEndsDocs.Noun]} with this {@getArg [CommonNameStartsEndsDocs.ArgumentArg]} in their name will be returned.
     * @return A ([transformable][TransformableColumnSet]) [ColumnSet] containing
     *   all columns {@getArg [CommonNameStartsEndsDocs.Noun]} with {@getArg [CommonNameStartsEndsDocs.ArgumentArg]} in their name.
     */
    private interface CommonNameStartsEndsDocs {

        /** "Starts" or "Ends" */
        interface CapitalTitle

        /** "starting" or "ending" */
        interface Noun

        /** "startsWith" or "endsWith" */
        interface OperationName

        /** "nameStartsWith" or "nameEndsWith" */
        interface NameOperationName

        /** "childrenNameStartsWith" or "childrenNameEndsWith" */
        interface ChildrenNameOperationName

        /** [prefix\] or [suffix\] */
        interface ArgumentArg

        interface ExampleArg
    }

    // region nameStartsWith

    /**
     * ## (Children) Name Starts With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns starting with [prefix]
     *
     * @see [nameEndsWith]
     * @see [nameContains] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix]
     *
     * @see [nameEndsWith]
     * @see [nameContains][prefix]
     *
     * @see [nameEndsWith]
     * @see [nameContains]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[childrenNameStartsWith][String.childrenNameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameStartsWith][SingleColumn.childrenNameStartsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [ExampleArg][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.CommonNameStartsEndsDocs.ExampleArg]}
     *
     * @param [prefix]
     *
     * @see [nameEndsWith]
     * @see [nameContains] Columns starting with this [prefix]
     *
     * @see [nameEndsWith]
     * @see [nameContains] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix]
     *
     * @see [nameEndsWith]
     * @see [nameContains] in their name.
     */
    private interface CommonNameStartsWithDocs

    @Deprecated("Use nameStartsWith instead", ReplaceWith("this.nameStartsWith(prefix)"))
    public fun <C> ColumnSet<C>.startsWith(prefix: CharSequence): TransformableColumnSet<C> =
        nameStartsWith(prefix)

    @Deprecated("Use nameStartsWith instead", ReplaceWith("this.nameStartsWith(prefix)"))
    public fun ColumnsSelectionDsl<*>.startsWith(prefix: CharSequence): TransformableColumnSet<*> =
        nameStartsWith(prefix)

    @Deprecated("Use childrenNameStartsWith instead", ReplaceWith("this.childrenNameStartsWith(prefix)"))
    public fun SingleColumn<DataRow<*>>.startsWith(prefix: CharSequence): TransformableColumnSet<*> =
        childrenNameStartsWith(prefix)

    /**
     * ## (Children) Name Starts With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns starting with [prefix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[childrenNameStartsWith][String.childrenNameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameStartsWith][SingleColumn.childrenNameStartsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameStartsWith][ColumnSet.nameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameStartsWith(prefix: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.startsWith(prefix) } as TransformableColumnSet<C>

    /**
     * ## (Children) Name Starts With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns starting with [prefix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[childrenNameStartsWith][String.childrenNameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameStartsWith][SingleColumn.childrenNameStartsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    public fun ColumnsSelectionDsl<*>.nameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        asSingleColumn().childrenNameStartsWith(prefix)

    /**
     * ## (Children) Name Starts With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns starting with [prefix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[childrenNameStartsWith][String.childrenNameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameStartsWith][SingleColumn.childrenNameStartsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[childrenNameStartsWith][SingleColumn.childrenNameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    public fun SingleColumn<DataRow<*>>.childrenNameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.startsWith(prefix) }

    /**
     * ## (Children) Name Starts With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns starting with [prefix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[childrenNameStartsWith][String.childrenNameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameStartsWith][SingleColumn.childrenNameStartsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameStartsWith][String.childrenNameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    public fun String.childrenNameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameStartsWith(prefix)

    /**
     * ## (Children) Name Starts With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns starting with [prefix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[childrenNameStartsWith][String.childrenNameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameStartsWith][SingleColumn.childrenNameStartsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameStartsWith][SingleColumn.childrenNameStartsWith]`("order-") }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[childrenNameStartsWith][KProperty.childrenNameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    public fun KProperty<DataRow<*>>.childrenNameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameStartsWith(prefix)

    /**
     * ## (Children) Name Starts With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns starting with [prefix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[startsWith][String.startsWith]`(`[prefix][prefix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[childrenNameStartsWith][String.childrenNameStartsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameStartsWith][SingleColumn.childrenNameStartsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameStartsWith][ColumnPath.childrenNameStartsWith]`("order-") }`
     *
     * @param [prefix] Columns starting with this [prefix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns starting with [prefix] in their name.
     * @see [nameEndsWith]
     * @see [nameContains]
     */
    public fun ColumnPath.childrenNameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameStartsWith(prefix)

    // endregion

    // region nameEndsWith

    /**
     * ## (Children) Name Ends With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns ending with [suffix]
     *
     * @see [nameStartsWith]
     * @see [nameContains] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix]
     *
     * @see [nameStartsWith]
     * @see [nameContains][suffix]
     *
     * @see [nameStartsWith]
     * @see [nameContains]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("order").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[childrenNameEndsWith][String.childrenNameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameEndsWith][SingleColumn.childrenNameEndsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * {@getArg [ExampleArg][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.CommonNameStartsEndsDocs.ExampleArg]}
     *
     * @param [suffix]
     *
     * @see [nameStartsWith]
     * @see [nameContains] Columns ending with this [suffix]
     *
     * @see [nameStartsWith]
     * @see [nameContains] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix]
     *
     * @see [nameStartsWith]
     * @see [nameContains] in their name.
     */
    private interface CommonNameEndsWithDocs

    @Deprecated("Use nameEndsWith instead", ReplaceWith("this.nameEndsWith(suffix)"))
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.endsWith(suffix: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.endsWith(suffix) } as TransformableColumnSet<C>

    @Deprecated("Use nameEndsWith instead", ReplaceWith("this.nameEndsWith(suffix)"))
    public fun ColumnsSelectionDsl<*>.endsWith(suffix: CharSequence): TransformableColumnSet<*> =
        nameEndsWith(suffix)

    @Deprecated("Use childrenNameEndsWith instead", ReplaceWith("this.childrenNameEndsWith(suffix)"))
    public fun SingleColumn<DataRow<*>>.endsWith(suffix: CharSequence): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.endsWith(suffix) }

    /**
     * ## (Children) Name Ends With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns ending with [suffix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("order").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[childrenNameEndsWith][String.childrenNameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameEndsWith][SingleColumn.childrenNameEndsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameEndsWith][ColumnSet.nameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameEndsWith(suffix: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.endsWith(suffix) } as TransformableColumnSet<C>

    /**
     * ## (Children) Name Ends With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns ending with [suffix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("order").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[childrenNameEndsWith][String.childrenNameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameEndsWith][SingleColumn.childrenNameEndsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    public fun ColumnsSelectionDsl<*>.nameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        asSingleColumn().childrenNameEndsWith(suffix)

    /**
     * ## (Children) Name Ends With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns ending with [suffix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("order").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[childrenNameEndsWith][String.childrenNameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameEndsWith][SingleColumn.childrenNameEndsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[childrenNameEndsWith][SingleColumn.childrenNameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    public fun SingleColumn<DataRow<*>>.childrenNameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.endsWith(suffix) }

    /**
     * ## (Children) Name Ends With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns ending with [suffix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("order").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[childrenNameEndsWith][String.childrenNameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameEndsWith][SingleColumn.childrenNameEndsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameEndsWith][String.childrenNameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    public fun String.childrenNameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameEndsWith(suffix)

    /**
     * ## (Children) Name Ends With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns ending with [suffix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("order").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[childrenNameEndsWith][String.childrenNameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameEndsWith][SingleColumn.childrenNameEndsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameEndsWith][SingleColumn.childrenNameEndsWith]`("-order") }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[childrenNameEndsWith][KProperty.childrenNameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    public fun KProperty<DataRow<*>>.childrenNameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameEndsWith(suffix)

    /**
     * ## (Children) Name Ends With
     * Returns a ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     * all columns ending with [suffix] in their name.
     *
     * If [this] is a [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], the function runs on the children of the [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * Else, if [this] is a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], the function runs on the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] itself.
     *
     * This function is a shorthand for [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]` { it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`.`[endsWith][String.endsWith]`(`[suffix][suffix]`) }`.
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("order").`[atAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.atAnyDepth]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "someGroupCol".`[childrenNameEndsWith][String.childrenNameEndsWith]`("b") }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { `[colGroup][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameEndsWith][SingleColumn.childrenNameEndsWith]`("a") }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameEndsWith][ColumnPath.childrenNameEndsWith]`("-order") }`
     *
     * @param [suffix] Columns ending with this [suffix] in their name will be returned.
     * @return A ([transformable][org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet]) [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing
     *   all columns ending with [suffix] in their name.
     * @see [nameStartsWith]
     * @see [nameContains]
     */
    public fun ColumnPath.childrenNameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameEndsWith(suffix)

    // endregion
}
// endregion
