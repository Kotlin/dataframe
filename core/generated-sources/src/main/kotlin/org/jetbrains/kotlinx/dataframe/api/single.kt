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
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.asColumnSet
import org.jetbrains.kotlinx.dataframe.columns.values
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
 * ## Single (Col) [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface SingleColumnsSelectionDsl {

    /**
     * ## Single (Col) Grammar
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
     *  [**`single`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`()`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`single`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`()`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`singleCol`**][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.singleCol]`()`
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

        /** [**`single`**][ColumnsSelectionDsl.single] */
        public interface PlainDslName

        /** __`.`__[**`single`**][ColumnsSelectionDsl.single] */
        public interface ColumnSetName

        /** __`.`__[**`singleCol`**][ColumnsSelectionDsl.singleCol] */
        public interface ColumnGroupName
    }

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [NoSuchElementException] is thrown.
     * If there are multiple columns, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][DataFrame.select]`  { `[nameStartsWith][ColumnsSelectionDsl.nameStartsWith]`("order").`[single][ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsNameStartsWith][ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[singleCol][String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     *
     *
     * @return A [SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    private interface CommonSingleDocs {

        /** Examples key */
        interface Examples
    }

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [NoSuchElementException] is thrown.
     * If there are multiple columns, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[nameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsNameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[singleCol][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[single][ColumnSet.single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[single][ColumnSet.single]`() }`
     *
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    @Interpretable("Single0")
    @Deprecated(
        message = SINGLE,
        replaceWith = ReplaceWith(SINGLE_SET_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun <C> ColumnSet<C>.single(condition: ColumnFilter<C> = { true }): TransformableSingleColumn<C> =
        singleInternal(condition)

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [NoSuchElementException] is thrown.
     * If there are multiple columns, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[nameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsNameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[singleCol][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().nameStartsWith("year").`[single][ColumnSet.single]`() }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[single][ColumnSet.single]`() }`
     *
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    @Interpretable("Single0")
    public fun <C> ColumnSet<C>.single(): TransformableSingleColumn<C> = singleInternal { true }

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [NoSuchElementException] is thrown.
     * If there are multiple columns, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[nameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsNameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[singleCol][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[single][ColumnsSelectionDsl.single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    @Interpretable("Single1")
    @Deprecated(
        message = SINGLE,
        replaceWith = ReplaceWith(SINGLE_PLAIN_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun ColumnsSelectionDsl<*>.single(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        asSingleColumn().singleCol(condition)

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [NoSuchElementException] is thrown.
     * If there are multiple columns, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[nameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsNameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[singleCol][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]`  { nameStartsWith("year").`[single][ColumnsSelectionDsl.single]`() }`
     *
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    @Interpretable("Single1")
    public fun ColumnsSelectionDsl<*>.single(): TransformableSingleColumn<*> = asSingleColumn().singleCol { true }

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [NoSuchElementException] is thrown.
     * If there are multiple columns, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[nameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsNameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[singleCol][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[singleCol][SingleColumn.singleCol]`() }`
     *
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    @Interpretable("Single2")
    @Deprecated(
        message = SINGLE,
        replaceWith = ReplaceWith(SINGLE_COL_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun SingleColumn<DataRow<*>>.singleCol(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        this.ensureIsColumnGroup().asColumnSet().single(condition)

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [NoSuchElementException] is thrown.
     * If there are multiple columns, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[nameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsNameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[singleCol][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[singleCol][SingleColumn.singleCol]`() }`
     *
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    @Interpretable("Single2")
    public fun SingleColumn<DataRow<*>>.singleCol(): TransformableSingleColumn<*> =
        this.ensureIsColumnGroup().asColumnSet().single()

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [NoSuchElementException] is thrown.
     * If there are multiple columns, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[nameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsNameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[singleCol][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[singleCol][String.singleCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    @Deprecated(
        message = SINGLE,
        replaceWith = ReplaceWith(SINGLE_COL_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun String.singleCol(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        columnGroup(this).singleCol(condition)

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [NoSuchElementException] is thrown.
     * If there are multiple columns, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[nameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsNameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[singleCol][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsNameStartsWith][ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("year").`[singleCol][String.singleCol]`() }`
     *
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    public fun String.singleCol(): TransformableSingleColumn<*> = columnGroup(this).singleCol()

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [NoSuchElementException] is thrown.
     * If there are multiple columns, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[nameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsNameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[singleCol][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[singleCol][SingleColumn.singleCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[singleCol][KProperty.singleCol]`() }`
     *
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.singleCol(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
        columnGroup(this).singleCol(condition)

    /**
     * ## Single (Col)
     * Returns the single column from [this].
     * If there is no column, [NoSuchElementException] is thrown.
     * If there are multiple columns, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { `[nameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`("order").`[single][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsNameStartsWith][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("order").`[singleCol][kotlin.String.singleCol]`() }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[singleCol][ColumnPath.singleCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * @return A [SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] containing the single column.
     * @throws [NoSuchElementException] if there are no columns in [this].
     * @throws [IllegalArgumentException] if there is more than one column in [this].
     */
    public fun ColumnPath.singleCol(condition: ColumnFilter<*> = { true }): TransformableSingleColumn<*> =
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
