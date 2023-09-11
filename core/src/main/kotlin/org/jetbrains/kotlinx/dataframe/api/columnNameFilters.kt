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
     * `df.`[select][DataFrame.select]` { `[nameContains][SingleColumn.childrenNameContains]`("my").`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth2]`() }`
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
     * @include [CommonNameContainsDocs]
     * @setArg [CommonNameContainsDocs.ArgumentArg] [text\] */
    private interface NameContainsTextDocs

    /**
     * @include [NameContainsTextDocs]
     * @setArg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[nameContains][ColumnSet.nameContains]`("my") }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameContains][ColumnSet.nameContains]`("my") }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameContains(text: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.contains(text) } as TransformableColumnSet<C>

    /**
     * @include [NameContainsTextDocs]
     * @setArg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[nameContains][ColumnsSelectionDsl.childrenNameContains]`("my") }`
     */
    public fun ColumnsSelectionDsl<*>.nameContains(text: CharSequence): TransformableColumnSet<*> =
        asSingleColumn().childrenNameContains(text)

    /**
     * @include [NameContainsTextDocs]
     * @setArg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[childrenNameContains][SingleColumn.childrenNameContains]`("my").`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth2]`() }`
     */
    public fun SingleColumn<DataRow<*>>.childrenNameContains(text: CharSequence): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.contains(text) }

    /**
     * @include [NameContainsTextDocs]
     * @setArg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameContains][String.childrenNameContains]`("my").`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth2]`() }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameContains][String.childrenNameContains]`("my") }`
     *
     */
    public fun String.childrenNameContains(text: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(text)

    /**
     * @include [NameContainsTextDocs]
     * @setArg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameContains][SingleColumn.childrenNameContains]`("my") }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[childrenNameContains][KProperty.childrenNameContains]`("my").`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth2]`() }`
     */
    public fun KProperty<DataRow<*>>.childrenNameContains(text: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(text)

    /**
     * @include [NameContainsTextDocs]
     * @setArg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameContains][ColumnPath.childrenNameContains]`("my") }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameContains][ColumnPath.childrenNameContains]`("my").`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth2]`() }`
     */
    public fun ColumnPath.childrenNameContains(text: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(text)

    /**
     * @include [CommonNameContainsDocs]
     * @setArg [CommonNameContainsDocs.ArgumentArg] [regex\] */
    private interface NameContainsRegexDocs

    /**
     * @include [NameContainsRegexDocs]
     * @setArg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[nameContains][ColumnSet.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameContains][ColumnSet.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameContains(regex: Regex): TransformableColumnSet<C> =
        colsInternal { it.name.contains(regex) } as TransformableColumnSet<C>

    /**
     * @include [NameContainsRegexDocs]
     * @setArg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[nameContains][ColumnsSelectionDsl.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    public fun ColumnsSelectionDsl<*>.nameContains(regex: Regex): TransformableColumnSet<*> =
        asSingleColumn().childrenNameContains(regex)

    /**
     * @include [NameContainsRegexDocs]
     * @setArg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[childrenNameContains][SingleColumn.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")).`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth2]`() }`
     */
    public fun SingleColumn<DataRow<*>>.childrenNameContains(regex: Regex): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.contains(regex) }

    /**
     * @include [NameContainsRegexDocs]
     * @setArg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameContains][String.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")).`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth2]`() }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameContains][String.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    public fun String.childrenNameContains(regex: Regex): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(regex)

    /**
     * @include [NameContainsRegexDocs]
     * @setArg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameContains][SingleColumn.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[childrenNameContains][KProperty.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")).`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth2]`() }`
     */
    public fun KProperty<DataRow<*>>.childrenNameContains(regex: Regex): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(regex)

    /**
     * @include [NameContainsRegexDocs]
     * @setArg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameContains][ColumnPath.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameContains][ColumnPath.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")).`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth2]`() }`
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
     * `df.`[select][DataFrame.select]` { `[{@getArg [NameOperationName]}][ColumnsSelectionDsl.{@getArg [NameOperationName]}]`("order").`[atAnyDepth][ColumnsSelectionDsl.atAnyDepth2]`() }`
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
     * @include [CommonNameStartsEndsDocs]
     * @setArg [CommonNameStartsEndsDocs.CapitalTitle] Starts
     * @setArg [CommonNameStartsEndsDocs.Noun] starting
     * @setArg [CommonNameStartsEndsDocs.OperationName] startsWith
     * @setArg [CommonNameStartsEndsDocs.NameOperationName] nameStartsWith
     * @setArg [CommonNameStartsEndsDocs.ChildrenNameOperationName] childrenNameStartsWith
     * @setArg [CommonNameStartsEndsDocs.ArgumentArg] [prefix\]
     *
     * @see [nameEndsWith\]
     * @see [nameContains\]
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
     * @include [CommonNameStartsWithDocs]
     * @setArg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameStartsWith][ColumnSet.nameStartsWith]`("order-") }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameStartsWith(prefix: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.startsWith(prefix) } as TransformableColumnSet<C>

    /**
     * @include [CommonNameStartsWithDocs]
     * @setArg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order-") }`
     */
    public fun ColumnsSelectionDsl<*>.nameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        asSingleColumn().childrenNameStartsWith(prefix)

    /**
     * @include [CommonNameStartsWithDocs]
     * @setArg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[childrenNameStartsWith][SingleColumn.childrenNameStartsWith]`("order-") }`
     */
    public fun SingleColumn<DataRow<*>>.childrenNameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.startsWith(prefix) }

    /**
     * @include [CommonNameStartsWithDocs]
     * @setArg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameStartsWith][String.childrenNameStartsWith]`("order-") }`
     */
    public fun String.childrenNameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameStartsWith(prefix)

    /**
     * @include [CommonNameStartsWithDocs]
     * @setArg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameStartsWith][SingleColumn.childrenNameStartsWith]`("order-") }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[childrenNameStartsWith][KProperty.childrenNameStartsWith]`("order-") }`
     */
    public fun KProperty<DataRow<*>>.childrenNameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameStartsWith(prefix)

    /**
     * @include [CommonNameStartsWithDocs]
     * @setArg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameStartsWith][ColumnPath.childrenNameStartsWith]`("order-") }`
     */
    public fun ColumnPath.childrenNameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameStartsWith(prefix)

    // endregion

    // region nameEndsWith

    /**
     * @include [CommonNameStartsEndsDocs]
     * @setArg [CommonNameStartsEndsDocs.CapitalTitle] Ends
     * @setArg [CommonNameStartsEndsDocs.Noun] ending
     * @setArg [CommonNameStartsEndsDocs.OperationName] endsWith
     * @setArg [CommonNameStartsEndsDocs.NameOperationName] nameEndsWith
     * @setArg [CommonNameStartsEndsDocs.ChildrenNameOperationName] childrenNameEndsWith
     * @setArg [CommonNameStartsEndsDocs.ArgumentArg] [suffix\]
     *
     * @see [nameStartsWith\]
     * @see [nameContains\]
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
     * @include [CommonNameEndsWithDocs]
     * @setArg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameEndsWith][ColumnSet.nameEndsWith]`("-order") }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameEndsWith(suffix: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.endsWith(suffix) } as TransformableColumnSet<C>

    /**
     * @include [CommonNameEndsWithDocs]
     * @setArg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("-order") }`
     */
    public fun ColumnsSelectionDsl<*>.nameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        asSingleColumn().childrenNameEndsWith(suffix)

    /**
     * @include [CommonNameEndsWithDocs]
     * @setArg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[childrenNameEndsWith][SingleColumn.childrenNameEndsWith]`("-order") }`
     */
    public fun SingleColumn<DataRow<*>>.childrenNameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        this.ensureIsColumnGroup().colsInternal { it.name.endsWith(suffix) }

    /**
     * @include [CommonNameEndsWithDocs]
     * @setArg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameEndsWith][String.childrenNameEndsWith]`("-order") }`
     */
    public fun String.childrenNameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameEndsWith(suffix)

    /**
     * @include [CommonNameEndsWithDocs]
     * @setArg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameEndsWith][SingleColumn.childrenNameEndsWith]`("-order") }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[childrenNameEndsWith][KProperty.childrenNameEndsWith]`("-order") }`
     */
    public fun KProperty<DataRow<*>>.childrenNameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameEndsWith(suffix)

    /**
     * @include [CommonNameEndsWithDocs]
     * @setArg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameEndsWith][ColumnPath.childrenNameEndsWith]`("-order") }`
     */
    public fun ColumnPath.childrenNameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameEndsWith(suffix)

    // endregion
}
// endregion
