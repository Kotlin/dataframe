package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Without Nulls [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface WithoutNullsColumnsSelectionDsl {

    /**
     * ## (Cols) Without Nulls Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[`ColumnSet`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[`SingleColumn`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called directly in the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [**`withoutNulls`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]**`()`**
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`withoutNulls`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]**`()`**
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [Column Group (reference)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnGroup`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`colsWithoutNulls`**][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.colsWithoutNulls]**`()`**
     *
     *
     *
     *
     *
     *
     *
     *
     */
    public interface Grammar {

        /** [**`withoutNulls`**][ColumnsSelectionDsl.withoutNulls] */
        public interface PlainDslName

        /** __`.`__[**`withoutNulls`**][ColumnsSelectionDsl.withoutNulls] */
        public interface ColumnSetName

        /** __`.`__[**`colsWithoutNulls`**][ColumnsSelectionDsl.colsWithoutNulls] */
        public interface ColumnGroupName
    }

    /**
     * ## (Cols) Without Nulls
     * Returns a new [ColumnSet] that contains only columns in [this] that do not have `null` values.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * ### Check out: [Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][DataFrame.select]`  {  `[all][ColumnsSelectionDsl.all]`().`[nameContains][ColumnsSelectionDsl.colsNameContains]`("middleName").`[withoutNulls][ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][DataFrame.select]`  {  `[withoutNulls][ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][DataFrame.select]` { Type::userData.`[colsWithoutNulls][SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @return A [ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    private interface CommonWithoutNullsDocs {

        interface EXAMPLE
    }

    /**
     * ## (Cols) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns in [this] that do not have `null` values.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[colsWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    @Suppress("UNCHECKED_CAST")
    @Interpretable("WithoutNulls0")
    public fun <C> ColumnSet<C?>.withoutNulls(): ColumnSet<C & Any> =
        transform { cols -> cols.filter { !it.hasNulls() } } as ColumnSet<C & Any>

    /**
     * ## (Cols) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns in [this] that do not have `null` values.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[colsWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    @Interpretable("WithoutNulls1")
    public fun ColumnsSelectionDsl<*>.withoutNulls(): ColumnSet<Any> = asSingleColumn().colsWithoutNulls()

    /**
     * ## (Cols) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns in [this] that do not have `null` values.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[colsWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    @Interpretable("WithoutNulls2")
    public fun SingleColumn<DataRow<*>>.colsWithoutNulls(): ColumnSet<Any> =
        ensureIsColumnGroup().allColumnsInternal().withoutNulls()

    /**
     * ## (Cols) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns in [this] that do not have `null` values.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[colsWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun String.colsWithoutNulls(): ColumnSet<Any> = columnGroup(this).colsWithoutNulls()

    /**
     * ## (Cols) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns in [this] that do not have `null` values.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[colsWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsWithoutNulls(): ColumnSet<Any> = columnGroup(this).colsWithoutNulls()

    /**
     * ## (Cols) Without Nulls
     * Returns a new [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns in [this] that do not have `null` values.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[nameContains][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[withoutNulls][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[withoutNulls][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[colsWithoutNulls][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun ColumnPath.colsWithoutNulls(): ColumnSet<Any> = columnGroup(this).colsWithoutNulls()
}

// endregion
