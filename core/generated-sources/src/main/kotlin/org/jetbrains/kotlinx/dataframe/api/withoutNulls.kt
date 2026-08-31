package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## Without Nulls [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface WithoutNullsColumnsSelectionDsl {

    /**
     * ## (Cols) Without Nulls Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[<code>`ColumnSet`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[<code>`SingleColumn`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[<code>`DataRow`</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called directly in the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>**`withoutNulls`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]**`()`**
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`withoutNulls`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]**`()`**
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>Column Group (reference)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnGroup`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`colsWithoutNulls`**</code>][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.colsWithoutNulls]**`()`**
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

        /** [<code>**`withoutNulls`**</code>][ColumnsSelectionDsl.withoutNulls] */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`withoutNulls`**</code>][ColumnsSelectionDsl.withoutNulls] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`colsWithoutNulls`**</code>][ColumnsSelectionDsl.colsWithoutNulls] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## (Cols) Without Nulls
     * Returns a new [<code>ColumnSet</code>][ColumnSet] that contains only columns in [this] that do not have `null` values.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * For more information: [See (Cols) Without Nulls on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-without-nulls)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>all</code>][ColumnsSelectionDsl.all]`().`[<code>nameContains</code>][ColumnsSelectionDsl.colsNameContains]`("middleName").`[<code>withoutNulls</code>][ColumnSet.withoutNulls]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>withoutNulls</code>][ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::userData.`[<code>colsWithoutNulls</code>][SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @return A [<code>ColumnSet</code>][ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    private interface CommonWithoutNullsDocs {

        typealias EXAMPLE = Nothing
    }

    /**
     * ## (Cols) Without Nulls
     * Returns a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns in [this] that do not have `null` values.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * For more information: [See (Cols) Without Nulls on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-without-nulls)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[<code>withoutNulls</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>withoutNulls</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[<code>colsWithoutNulls</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    @Suppress("UNCHECKED_CAST")
    @Interpretable("WithoutNulls0")
    public fun <C> ColumnSet<C?>.withoutNulls(): ColumnSet<C & Any> =
        transform { cols -> cols.filter { !it.hasNulls() } } as ColumnSet<C & Any>

    /**
     * ## (Cols) Without Nulls
     * Returns a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns in [this] that do not have `null` values.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * For more information: [See (Cols) Without Nulls on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-without-nulls)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[<code>withoutNulls</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>withoutNulls</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[<code>colsWithoutNulls</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    @Interpretable("WithoutNulls1")
    public fun ColumnsSelectionDsl<*>.withoutNulls(): ColumnSet<Any> = asSingleColumn().colsWithoutNulls()

    /**
     * ## (Cols) Without Nulls
     * Returns a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns in [this] that do not have `null` values.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * For more information: [See (Cols) Without Nulls on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-without-nulls)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[<code>withoutNulls</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>withoutNulls</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[<code>colsWithoutNulls</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    @Interpretable("WithoutNulls2")
    public fun SingleColumn<DataRow<*>>.colsWithoutNulls(): ColumnSet<Any> =
        ensureIsColumnGroup().allColumnsInternal().withoutNulls()

    /**
     * ## (Cols) Without Nulls
     * Returns a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns in [this] that do not have `null` values.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * For more information: [See (Cols) Without Nulls on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-without-nulls)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[<code>withoutNulls</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>withoutNulls</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[<code>colsWithoutNulls</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun String.colsWithoutNulls(): ColumnSet<Any> = columnGroup(this).colsWithoutNulls()

    /**
     * ## (Cols) Without Nulls
     * Returns a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns in [this] that do not have `null` values.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * For more information: [See (Cols) Without Nulls on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-without-nulls)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[<code>withoutNulls</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>withoutNulls</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[<code>colsWithoutNulls</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.colsWithoutNulls(): ColumnSet<Any> = columnGroup(this).colsWithoutNulls()

    /**
     * ## (Cols) Without Nulls
     * Returns a new [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains only columns in [this] that do not have `null` values.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], `withoutNulls` is named `colsWithoutNulls` to avoid confusion.
     *
     * For more information: [See (Cols) Without Nulls on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-without-nulls)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.Grammar]
     *
     * #### For Example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`().`[<code>nameContains</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]`("middleName").`[<code>withoutNulls</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.withoutNulls]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>withoutNulls</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::userData.`[<code>colsWithoutNulls</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsWithoutNulls]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing only columns that do not contain `null`s and are thus non-nullable.
     */
    public fun ColumnPath.colsWithoutNulls(): ColumnSet<Any> = columnGroup(this).colsWithoutNulls()
}

// endregion
