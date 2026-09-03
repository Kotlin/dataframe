package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowExpression
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.columns.values
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableSingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.singleOrNullWithTransformerImpl
import org.jetbrains.kotlinx.dataframe.impl.columns.transform
import org.jetbrains.kotlinx.dataframe.nrow
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.SINGLE
import org.jetbrains.kotlinx.dataframe.util.SINGLE_COL_REPLACE
import org.jetbrains.kotlinx.dataframe.util.SINGLE_PLAIN_REPLACE
import org.jetbrains.kotlinx.dataframe.util.SINGLE_SET_REPLACE
import kotlin.reflect.KProperty

// region DataColumn

public fun <C> DataColumn<C>.single(): C = values.single()

// endregion

// region DataFrame

public fun <T> DataFrame<T>.single(): DataRow<T> =
    when (nrow) {
        0 -> throw NoSuchElementException("DataFrame has no rows. Use `singleOrNull`.")
        1 -> get(0)
        else -> throw IllegalArgumentException("DataFrame has more than one row.")
    }

public fun <T> DataFrame<T>.singleOrNull(): DataRow<T>? = rows().singleOrNull()

public inline fun <T> DataFrame<T>.single(predicate: RowExpression<T, Boolean>): DataRow<T> =
    rows().single { predicate(it, it) }

public inline fun <T> DataFrame<T>.singleOrNull(predicate: RowExpression<T, Boolean>): DataRow<T>? =
    rows().singleOrNull { predicate(it, it) }

// endregion

// region ColumnsSelectionDsl

/**
 * ## Single (Col) [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface SingleColumnsSelectionDsl {

    /**
     * ## Single (Col) Grammar
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
     *  [<code>**`single`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`()`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`single`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`()`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`singleCol`**</code>][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.singleCol]`()`
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

        /** [<code>**`single`**</code>][ColumnsSelectionDsl.single] */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`single`**</code>][ColumnsSelectionDsl.single] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`singleCol`**</code>][ColumnsSelectionDsl.singleCol] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     * If there are multiple columns, [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  { `[<code>nameStartsWith</code>][ColumnsSelectionDsl.nameStartsWith]`("order").`[<code>single</code>][ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>colsNameStartsWith</code>][ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[<code>singleCol</code>][String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @return A [<code>SingleColumn</code>][SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    private interface CommonSingleDocs {

        /** Examples key */
        typealias Examples = Nothing
    }

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     * If there are multiple columns, [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[<code>nameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsNameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[<code>singleCol</code>][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>().`[<code>single</code>][ColumnSet.single]` { it.`[<code>name</code>][ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>single</code>][ColumnSet.single]`() }`
     *
     * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    @Interpretable("Single0")
    @Deprecated(
        message = SINGLE,
        replaceWith = ReplaceWith(SINGLE_SET_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun <C> ColumnSet<C>.single(condition: (ColumnWithPath<C>) -> Boolean = { true }): SingleColumn<C> =
        singleInternal(condition)

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     * If there are multiple columns, [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[<code>nameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsNameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[<code>singleCol</code>][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>String</code>][String]`>().nameStartsWith("year").`[<code>single</code>][ColumnSet.single]`() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>().`[<code>single</code>][ColumnSet.single]`() }`
     *
     * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    @Interpretable("Single0")
    public fun <C> ColumnSet<C>.single(): SingleColumn<C> = singleInternal { true }

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     * If there are multiple columns, [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[<code>nameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsNameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[<code>singleCol</code>][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>single</code>][ColumnsSelectionDsl.single]` { it.`[<code>name</code>][ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    @Interpretable("Single1")
    @Deprecated(
        message = SINGLE,
        replaceWith = ReplaceWith(SINGLE_PLAIN_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun ColumnsSelectionDsl<*>.single(condition: (ColumnWithPath<*>) -> Boolean = { true }): SingleColumn<*> =
        asSingleColumn().singleCol(condition)

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     * If there are multiple columns, [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[<code>nameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsNameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[<code>singleCol</code>][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  { nameStartsWith("year").`[<code>single</code>][ColumnsSelectionDsl.single]`() }`
     *
     * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    @Interpretable("Single1")
    public fun ColumnsSelectionDsl<*>.single(): SingleColumn<*> = asSingleColumn().singleCol { true }

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     * If there are multiple columns, [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[<code>nameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsNameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[<code>singleCol</code>][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>singleCol</code>][SingleColumn.singleCol]`() }`
     *
     * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    @Interpretable("Single2")
    @Deprecated(
        message = SINGLE,
        replaceWith = ReplaceWith(SINGLE_COL_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun SingleColumn<DataRow<*>>.singleCol(
        condition: (ColumnWithPath<*>) -> Boolean = { true },
    ): SingleColumn<*> = this.ensureIsColumnGroup().asColumnSet().single(condition)

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     * If there are multiple columns, [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[<code>nameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsNameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[<code>singleCol</code>][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>singleCol</code>][SingleColumn.singleCol]`() }`
     *
     * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    @Interpretable("Single2")
    public fun SingleColumn<DataRow<*>>.singleCol(): SingleColumn<*> = this.ensureIsColumnGroup().asColumnSet().single()

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     * If there are multiple columns, [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[<code>nameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsNameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[<code>singleCol</code>][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>singleCol</code>][String.singleCol]` { it.`[<code>name</code>][ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    @Deprecated(
        message = SINGLE,
        replaceWith = ReplaceWith(SINGLE_COL_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun String.singleCol(condition: (ColumnWithPath<*>) -> Boolean = { true }): SingleColumn<*> =
        columnGroup(this).singleCol(condition)

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     * If there are multiple columns, [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[<code>nameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsNameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[<code>singleCol</code>][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>colsNameStartsWith</code>][ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("year").`[<code>singleCol</code>][String.singleCol]`() }`
     *
     * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    public fun String.singleCol(): SingleColumn<*> = columnGroup(this).singleCol()

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     * If there are multiple columns, [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[<code>nameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsNameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[<code>singleCol</code>][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>singleCol</code>][SingleColumn.singleCol]` { it.`[<code>name</code>][ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::myColumnGroup.`[<code>singleCol</code>][KProperty.singleCol]`() }`
     *
     * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.singleCol(condition: (ColumnWithPath<*>) -> Boolean = { true }): SingleColumn<*> =
        columnGroup(this).singleCol(condition)

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [<code>NoSuchElementException</code>][NoSuchElementException] is thrown.
     * If there are multiple columns, [<code>IllegalArgumentException</code>][IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [<code>column groups</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * For more information: [See First (Col), Last (Col), Single (Col) on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#first-col-last-col-single-col)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[<code>nameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[<code>single</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsNameStartsWith</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[<code>singleCol</code>][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>singleCol</code>][ColumnPath.singleCol]` { it.`[<code>name</code>][ColumnReference.name]`().`[<code>startsWith</code>][String.startsWith]`("year") } }`
     *
     * @return A [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    public fun ColumnPath.singleCol(condition: (ColumnWithPath<*>) -> Boolean = { true }): SingleColumn<*> =
        columnGroup(this).singleCol(condition)
}

@Suppress("UNCHECKED_CAST")
internal fun <C> ColumnsResolver<C>.singleInternal(
    condition: ColumnFilter<C> = { true },
): TransformableSingleColumn<C> =
    (allColumnsInternal() as TransformableColumnSet<C>)
        .transform { listOf(it.single(condition)) }
        .singleOrNullWithTransformerImpl()

// endregion
