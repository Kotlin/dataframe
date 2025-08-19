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
        public interface PlainDslName

        /** __`.`__[**`single`**][ColumnsSelectionDsl.single] */
        public interface ColumnSetName

        /** __`.`__[**`singleCol`**][ColumnsSelectionDsl.singleCol] */
        public interface ColumnGroupName
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
        interface Examples
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
    public fun <C> ColumnSet<C>.single(condition: ColumnFilter<C> = { true }): SingleColumn<C> =
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
    public fun ColumnsSelectionDsl<*>.single(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
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
    public fun SingleColumn<DataRow<*>>.singleCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        this.ensureIsColumnGroup().asColumnSet().single(condition)

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
    public fun String.singleCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
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
    public fun KProperty<*>.singleCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
        columnGroup(this).singleCol(condition)

    /**
     * @include [CommonSingleDocs]
     * @set [CommonSingleDocs.Examples]
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[singleCol][ColumnPath.singleCol]` { it.`[name][ColumnReference.name]`().`[startsWith][String.startsWith]`("year") } }`
     */
    public fun ColumnPath.singleCol(condition: ColumnFilter<*> = { true }): SingleColumn<*> =
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
