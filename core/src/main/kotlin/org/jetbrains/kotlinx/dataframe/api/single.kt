package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.RowFilter
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
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
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.documentation.SelectingRows
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

/**
 * Returns the single value in this [DataColumn].
 *
 * For more information: {@include [DocumentationUrls.SingleOnColumn]}
 *
 * See also [firstOrNull][DataColumn.firstOrNull], that returns `null` instead of throwing
 * when the [DataColumn] is empty,
 * and [first][DataColumn.first], [last][DataColumn.last], [take][DataColumn.take],
 * [takeLast][DataColumn.takeLast], that do not require the [DataColumn]
 * to contain exactly one value.
 *
 * @return The single value in this [DataColumn].
 * It can be `null` if the [DataColumn] contains exactly one value and that value is `null`.
 *
 * @throws [NoSuchElementException] if the [DataColumn] is empty.
 * @throws [IllegalArgumentException] if the [DataColumn] contains more than one value.
 */
public fun <C> DataColumn<C>.single(): C = values.single()

// endregion

// region DataFrame

/**
 * Returns the single [row][DataRow] in this [DataFrame].
 *
 * For more information: {@include [DocumentationUrls.Single]}
 *
 * See also [singleOrNull][DataFrame.singleOrNull], that returns `null` instead of throwing,
 * and [first][DataFrame.first], [last][DataFrame.last],
 * that do not require the [DataFrame] to contain exactly one matching row.
 *
 * @return A [DataRow] containing the single row in this [DataFrame].
 *
 * @throws [NoSuchElementException] if the [DataFrame] contains no rows.
 * @throws [IllegalArgumentException] if the [DataFrame] contains more than one row.
 */
public fun <T> DataFrame<T>.single(): DataRow<T> =
    when (nrow) {
        0 -> throw NoSuchElementException("DataFrame has no rows. Use `singleOrNull`.")
        1 -> get(0)
        else -> throw IllegalArgumentException("DataFrame has more than one row.")
    }

/**
 * Returns the single [row][DataRow] in this [DataFrame].
 * Returns `null` if the [DataFrame] contains no rows or contains more than one row.
 *
 * For more information: {@include [DocumentationUrls.SingleOrNull]}
 *
 * See also [single][DataFrame.single], that throws instead of returning `null`,
 * and [firstOrNull][DataFrame.firstOrNull], [lastOrNull][DataFrame.lastOrNull],
 * that do not require the [DataFrame] to contain exactly one matching row.
 *
 * @return A [DataRow] containing the single row in this [DataFrame],
 * or `null` if the [DataFrame] contains no rows or contains more than one row.
 */
public fun <T> DataFrame<T>.singleOrNull(): DataRow<T>? = rows().singleOrNull()

/**
 * Returns the single [row][DataRow] in this [DataFrame] that satisfies the given [predicate].
 *
 * @include [SelectingRows.RowFilterSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // find the only transaction made with the given id
 * df.single { id == 137 }
 * ```
 *
 * For more information: {@include [DocumentationUrls.Single]}
 *
 * See also [singleOrNull][DataFrame.singleOrNull], that returns `null` instead of throwing,
 * and [first][DataFrame.first], [last][DataFrame.last],
 * that do not require the [DataFrame] to contain exactly one matching row.
 *
 * @param [predicate] A [row filter][RowFilter] used to get the single row
 * that satisfies a condition specified in this filter.
 *
 * @return A [DataRow] containing the single row that matches the given [predicate].
 *
 * @throws [NoSuchElementException] if the [DataFrame] contains no rows matching the [predicate].
 * @throws [IllegalArgumentException] if the [DataFrame] contains more than one row
 * matching the [predicate].
 */
public inline fun <T> DataFrame<T>.single(predicate: RowFilter<T>): DataRow<T> = rows().single { predicate(it, it) }

/**
 * Returns the single [row][DataRow] in this [DataFrame] that satisfies the given [predicate].
 * Returns `null` if the [DataFrame] contains no rows matching the [predicate]
 * (including the case when the [DataFrame] is empty)
 * or contains more than one row matching the [predicate].
 *
 * @include [SelectingRows.RowFilterSnippet]
 *
 * @include [SelectingColumns.ColumnGroupsAndNestedColumnsSnippet]
 *
 * ### Example
 * ```kotlin
 * // In a DataFrame of financial transactions,
 * // find the only transaction made with the given id,
 * // or 'null' if there is no such transaction or there is more than one
 * df.singleOrNull { id == 137 }
 * ```
 *
 * For more information: {@include [DocumentationUrls.SingleOrNull]}
 *
 * See also [single][DataFrame.single], that throws instead of returning `null`,
 * and [firstOrNull][DataFrame.firstOrNull], [lastOrNull][DataFrame.lastOrNull],
 * that do not require the [DataFrame] to contain exactly one matching row.
 *
 * @param [predicate] A [row filter][RowFilter] used to get the single row
 * that satisfies a condition specified in this filter.
 *
 * @return A [DataRow] containing the single row that matches the given [predicate],
 * or `null` if there is no such row or there is more than one.
 */
public inline fun <T> DataFrame<T>.singleOrNull(predicate: RowFilter<T>): DataRow<T>? =
    rows().singleOrNull { predicate(it, it) }

// endregion

// region ColumnsSelectionDsl

/**
 * ## Single (Col) {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface SingleColumnsSelectionDsl {

    /**
     * ## Single (Col) Grammar
     *
     * @include [DslGrammarTemplate]
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     * }
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [PlainDslName]}`()`
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnSetName]}`()`
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_GROUP_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnGroupName]}`()`
     * }
     */
    public interface Grammar {

        /** [**`single`**][ColumnsSelectionDsl.single] */
        public typealias PlainDslName = Nothing

        /** __`.`__[**`single`**][ColumnsSelectionDsl.single] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[**`singleCol`**][ColumnsSelectionDsl.singleCol] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Single (Col)
     * Returns the single column from [this\].
     * If there is no column, [NoSuchElementException] is thrown.
     * If there are multiple columns, [IllegalArgumentException] is thrown.
     *
     * This function operates solely on columns at the top-level.
     *
     * NOTE: For [column groups][ColumnsSelectionDsl], `single` is named `singleCol` instead to avoid confusion.
     *
     * For more information: {@include [DocumentationUrls.FirstLastSingleCols]}
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
     * {@get [Examples]}
     *
     * @return A [SingleColumn] containing the single column.
     * @throws [NoSuchElementException\] if there are no columns in [this\].
     * @throws [IllegalArgumentException\] if there is more than one column in [this\].
     */
    private interface CommonSingleDocs {

        /** Examples key */
        typealias Examples = Nothing
    }

    /**
     * @include [CommonSingleDocs]
     * @set [CommonSingleDocs.Examples]
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().`[single][ColumnSet.single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[single][ColumnSet.single]`() }`
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
     * @include [CommonSingleDocs]
     * @set [CommonSingleDocs.Examples]
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[String][String]`>().nameStartsWith("year").`[single][ColumnSet.single]`() }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>().`[single][ColumnSet.single]`() }`
     */
    @Interpretable("Single0")
    public fun <C> ColumnSet<C>.single(): SingleColumn<C> = singleInternal { true }

    /**
     * @include [CommonSingleDocs]
     * @set [CommonSingleDocs.Examples]
     *
     * `df.`[select][DataFrame.select]`  {  `[single][ColumnsSelectionDsl.single]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
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
     * @include [CommonSingleDocs]
     * @set [CommonSingleDocs.Examples]
     *
     * `df.`[select][DataFrame.select]`  { nameStartsWith("year").`[single][ColumnsSelectionDsl.single]`() }`
     */
    @Interpretable("Single1")
    public fun ColumnsSelectionDsl<*>.single(): SingleColumn<*> = asSingleColumn().singleCol { true }

    /**
     * @include [CommonSingleDocs]
     * @set [CommonSingleDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[singleCol][SingleColumn.singleCol]`() }`
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
     * @include [CommonSingleDocs]
     * @set [CommonSingleDocs.Examples]
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[singleCol][SingleColumn.singleCol]`() }`
     */
    @Interpretable("Single2")
    public fun SingleColumn<DataRow<*>>.singleCol(): SingleColumn<*> = this.ensureIsColumnGroup().asColumnSet().single()

    /**
     * @include [CommonSingleDocs]
     * @set [CommonSingleDocs.Examples]
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[singleCol][String.singleCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    @Deprecated(
        message = SINGLE,
        replaceWith = ReplaceWith(SINGLE_COL_REPLACE),
        level = DeprecationLevel.WARNING,
    )
    public fun String.singleCol(condition: (ColumnWithPath<*>) -> Boolean = { true }): SingleColumn<*> =
        columnGroup(this).singleCol(condition)

    /**
     * @include [CommonSingleDocs]
     * @set [CommonSingleDocs.Examples]
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsNameStartsWith][ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`("year").`[singleCol][String.singleCol]`() }`
     */
    public fun String.singleCol(): SingleColumn<*> = columnGroup(this).singleCol()

    /**
     * @include [CommonSingleDocs]
     * @set [CommonSingleDocs.Examples]
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[singleCol][SingleColumn.singleCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[singleCol][KProperty.singleCol]`() }`
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.singleCol(condition: (ColumnWithPath<*>) -> Boolean = { true }): SingleColumn<*> =
        columnGroup(this).singleCol(condition)

    /**
     * @include [CommonSingleDocs]
     * @set [CommonSingleDocs.Examples]
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[singleCol][ColumnPath.singleCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
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
