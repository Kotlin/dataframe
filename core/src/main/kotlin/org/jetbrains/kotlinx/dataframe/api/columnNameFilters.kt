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
public interface ColumnNameFiltersColumnsSelectionDsl {
    // region nameContains

    /**
     * ## (Children) Name Contains
     * Returns a ([transformable][TransformableColumnSet]) [ColumnSet] containing
     * all columns containing {@includeArg [CommonNameContainsDocs.ArgumentArg]} in their name.
     *
     * NOTE: For [column groups][ColumnGroup], `nameContains` is named `childrenNameContains` to avoid confusion.
     *
     * This function is a shorthand for [cols][ColumnsSelectionDsl.cols]` { `{@includeArg [ArgumentArg]}{@includeArg [ArgumentArg]}` `[in][String.contains]` it.`[name][DataColumn.name]` }`.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[nameContains][SingleColumn.childrenNameContains]`("my").`[recursively][ColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[nameContains][String.childrenNameContains]`(`[Regex][Regex]`("my[a-zA-Z][a-zA-Z0-9]*")) }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[nameContains][SingleColumn.childrenNameContains]`("my") }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [ExampleArg]}
     *
     * @param {@includeArg [ArgumentArg]} what the column name should contain to be included in the result.
     * @return A ([transformable][TransformableColumnSet]) [ColumnSet] containing
     *   all columns containing {@includeArg [CommonNameContainsDocs.ArgumentArg]} in their name.
     */
    private interface CommonNameContainsDocs {
        interface ExampleArg

        /** [text\] or [regex\] */
        interface ArgumentArg
    }

    /**
     * @include [CommonNameContainsDocs]
     * @arg [CommonNameContainsDocs.ArgumentArg] [text\] */
    private interface NameContainsTextDocs

    /**
     * @include [NameContainsTextDocs]
     * @arg [CommonNameContainsDocs.ExampleArg]
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
     * @arg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[nameContains][ColumnsSelectionDsl.childrenNameContains]`("my") }`
     */
    public fun ColumnsSelectionDsl<*>.nameContains(text: CharSequence): TransformableColumnSet<*> =
        asSingleColumn().childrenNameContains(text)

    /**
     * @include [NameContainsTextDocs]
     * @arg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[childrenNameContains][SingleColumn.childrenNameContains]`("my").`[rec][ColumnsSelectionDsl.rec]`() }`
     */
    public fun SingleColumn<DataRow<*>>.childrenNameContains(text: CharSequence): TransformableColumnSet<*> =
        ensureIsColGroup().colsInternal { it.name.contains(text) }

    /**
     * @include [NameContainsTextDocs]
     * @arg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameContains][String.childrenNameContains]`("my").`[rec][ColumnsSelectionDsl.rec]`() }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameContains][String.childrenNameContains]`("my") }`
     *
     */
    public fun String.childrenNameContains(text: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(text)

    /**
     * @include [NameContainsTextDocs]
     * @arg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameContains][SingleColumn.childrenNameContains]`("my") }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[childrenNameContains][KProperty.childrenNameContains]`("my").`[rec][ColumnsSelectionDsl.rec]`() }`
     */
    public fun KProperty<DataRow<*>>.childrenNameContains(text: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(text)

    /**
     * @include [NameContainsTextDocs]
     * @arg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameContains][ColumnPath.childrenNameContains]`("my") }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameContains][ColumnPath.childrenNameContains]`("my").`[rec][ColumnsSelectionDsl.rec]`() }`
     */
    public fun ColumnPath.childrenNameContains(text: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(text)

    /**
     * @include [CommonNameContainsDocs]
     * @arg [CommonNameContainsDocs.ArgumentArg] [regex\] */
    private interface NameContainsRegexDocs

    /**
     * @include [NameContainsRegexDocs]
     * @arg [CommonNameContainsDocs.ExampleArg]
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
     * @arg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[nameContains][ColumnsSelectionDsl.nameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    public fun ColumnsSelectionDsl<*>.nameContains(regex: Regex): TransformableColumnSet<*> =
        asSingleColumn().childrenNameContains(regex)

    /**
     * @include [NameContainsRegexDocs]
     * @arg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[childrenNameContains][SingleColumn.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")).`[rec][ColumnsSelectionDsl.rec]`() }`
     */
    public fun SingleColumn<DataRow<*>>.childrenNameContains(regex: Regex): TransformableColumnSet<*> =
        ensureIsColGroup().colsInternal { it.name.contains(regex) }

    /**
     * @include [NameContainsRegexDocs]
     * @arg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameContains][String.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")).`[rec][ColumnsSelectionDsl.rec]`() }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameContains][String.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     */
    public fun String.childrenNameContains(regex: Regex): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(regex)

    /**
     * @include [NameContainsRegexDocs]
     * @arg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameContains][SingleColumn.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[childrenNameContains][KProperty.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")).`[rec][ColumnsSelectionDsl.rec]`() }`
     */
    public fun KProperty<DataRow<*>>.childrenNameContains(regex: Regex): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(regex)

    /**
     * @include [NameContainsRegexDocs]
     * @arg [CommonNameContainsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameContains][ColumnPath.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameContains][ColumnPath.childrenNameContains]`(`[Regex][Regex]`("order-[0-9]+")).`[rec][ColumnsSelectionDsl.rec]`() }`
     */
    public fun ColumnPath.childrenNameContains(regex: Regex): TransformableColumnSet<*> =
        columnGroup(this).childrenNameContains(regex)

    // endregion

    /**
     * ## (Children) Name {@includeArg [CommonNameStartsEndsDocs.CapitalTitle]} With
     * Returns a ([transformable][TransformableColumnSet]) [ColumnSet] containing
     * all columns {@includeArg [CommonNameStartsEndsDocs.Noun]} with {@includeArg [CommonNameStartsEndsDocs.ArgumentArg]} in their name.
     *
     * If [this\] is a [SingleColumn] containing a [ColumnGroup], the function runs on the children of the [ColumnGroup].
     * Else, if [this\] is a [ColumnSet], the function runs on the [ColumnSet] itself.
     *
     * This function is a shorthand for [cols][ColumnsSelectionDsl.cols]` { it.`[name][DataColumn.name]`.`[{@includeArg [OperationName]}][String.{@includeArg [OperationName]}]`(`{@includeArg [ArgumentArg]}{@includeArg [ArgumentArg]}`) }`.
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[{@includeArg [NameOperationName]}][ColumnsSelectionDsl.{@includeArg [NameOperationName]}]`("order").`[recursively][ColumnsSelectionDsl.recursively]`() }`
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[{@includeArg [ChildrenNameOperationName]}][String.{@includeArg [ChildrenNameOperationName]}]`("b") }`
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[{@includeArg [ChildrenNameOperationName]}][SingleColumn.{@includeArg [ChildrenNameOperationName]}]`("a") }`
     *
     * #### Examples for this overload:
     *
     * {@includeArg [ExampleArg]}
     *
     * @param {@includeArg [ArgumentArg]} Columns {@includeArg [CommonNameStartsEndsDocs.Noun]} with this {@includeArg [CommonNameStartsEndsDocs.ArgumentArg]} in their name will be returned.
     * @return A ([transformable][TransformableColumnSet]) [ColumnSet] containing
     *   all columns {@includeArg [CommonNameStartsEndsDocs.Noun]} with {@includeArg [CommonNameStartsEndsDocs.ArgumentArg]} in their name.
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
     * @arg [CommonNameStartsEndsDocs.CapitalTitle] Starts
     * @arg [CommonNameStartsEndsDocs.Noun] starting
     * @arg [CommonNameStartsEndsDocs.OperationName] startsWith
     * @arg [CommonNameStartsEndsDocs.NameOperationName] nameStartsWith
     * @arg [CommonNameStartsEndsDocs.ChildrenNameOperationName] childrenNameStartsWith
     * @arg [CommonNameStartsEndsDocs.ArgumentArg] [prefix\]
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
     * @arg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameStartsWith][ColumnSet.nameStartsWith]`("order-") }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameStartsWith(prefix: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.startsWith(prefix) } as TransformableColumnSet<C>

    /**
     * @include [CommonNameStartsWithDocs]
     * @arg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order-") }`
     */
    public fun ColumnsSelectionDsl<*>.nameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        asSingleColumn().childrenNameStartsWith(prefix)

    /**
     * @include [CommonNameStartsWithDocs]
     * @arg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[childrenNameStartsWith][SingleColumn.childrenNameStartsWith]`("order-") }`
     */
    public fun SingleColumn<DataRow<*>>.childrenNameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        ensureIsColGroup().colsInternal { it.name.startsWith(prefix) }

    /**
     * @include [CommonNameStartsWithDocs]
     * @arg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameStartsWith][String.childrenNameStartsWith]`("order-") }`
     */
    public fun String.childrenNameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameStartsWith(prefix)

    /**
     * @include [CommonNameStartsWithDocs]
     * @arg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameStartsWith][SingleColumn.childrenNameStartsWith]`("order-") }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[childrenNameStartsWith][KProperty.childrenNameStartsWith]`("order-") }`
     */
    public fun KProperty<DataRow<*>>.childrenNameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameStartsWith(prefix)

    /**
     * @include [CommonNameStartsWithDocs]
     * @arg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameStartsWith][ColumnPath.childrenNameStartsWith]`("order-") }`
     */
    public fun ColumnPath.childrenNameStartsWith(prefix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameStartsWith(prefix)

    // endregion

    // region nameEndsWith

    /**
     * @include [CommonNameStartsEndsDocs]
     * @arg [CommonNameStartsEndsDocs.CapitalTitle] Ends
     * @arg [CommonNameStartsEndsDocs.Noun] ending
     * @arg [CommonNameStartsEndsDocs.OperationName] endsWith
     * @arg [CommonNameStartsEndsDocs.NameOperationName] nameEndsWith
     * @arg [CommonNameStartsEndsDocs.ChildrenNameOperationName] childrenNameEndsWith
     * @arg [CommonNameStartsEndsDocs.ArgumentArg] [suffix\]
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
        ensureIsColGroup().colsInternal { it.name.endsWith(suffix) }

    /**
     * @include [CommonNameEndsWithDocs]
     * @arg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[nameEndsWith][ColumnSet.nameEndsWith]`("-order") }`
     */
    @Suppress("UNCHECKED_CAST")
    public fun <C> ColumnSet<C>.nameEndsWith(suffix: CharSequence): TransformableColumnSet<C> =
        colsInternal { it.name.endsWith(suffix) } as TransformableColumnSet<C>

    /**
     * @include [CommonNameEndsWithDocs]
     * @arg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[nameEndsWith][ColumnsSelectionDsl.nameEndsWith]`("-order") }`
     */
    public fun ColumnsSelectionDsl<*>.nameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        asSingleColumn().childrenNameEndsWith(suffix)

    /**
     * @include [CommonNameEndsWithDocs]
     * @arg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { someGroupCol.`[childrenNameEndsWith][SingleColumn.childrenNameEndsWith]`("-order") }`
     */
    public fun SingleColumn<DataRow<*>>.childrenNameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        ensureIsColGroup().colsInternal { it.name.endsWith(suffix) }

    /**
     * @include [CommonNameEndsWithDocs]
     * @arg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "someGroupCol".`[childrenNameEndsWith][String.childrenNameEndsWith]`("-order") }`
     */
    public fun String.childrenNameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameEndsWith(suffix)

    /**
     * @include [CommonNameEndsWithDocs]
     * @arg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { `[colGroup][ColumnsSelectionDsl.colGroup]`(Type::someGroupCol).`[childrenNameEndsWith][SingleColumn.childrenNameEndsWith]`("-order") }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::someGroupCol.`[childrenNameEndsWith][KProperty.childrenNameEndsWith]`("-order") }`
     */
    public fun KProperty<DataRow<*>>.childrenNameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameEndsWith(suffix)

    /**
     * @include [CommonNameEndsWithDocs]
     * @arg [CommonNameStartsEndsDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["someGroupCol"].`[childrenNameEndsWith][ColumnPath.childrenNameEndsWith]`("-order") }`
     */
    public fun ColumnPath.childrenNameEndsWith(suffix: CharSequence): TransformableColumnSet<*> =
        columnGroup(this).childrenNameEndsWith(suffix)

    // endregion
}
// endregion
