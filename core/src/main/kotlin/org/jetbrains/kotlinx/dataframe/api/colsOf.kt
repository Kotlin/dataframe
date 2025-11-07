package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region ColumnsSelectionDsl

/**
 * ## Cols Of {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ColsOfColumnsSelectionDsl {

    /**
     * ## Cols Of Grammar
     *
     * @include [DslGrammarTemplate]
     *
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.SingleColumnDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnGroupNoSingleColumnDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ConditionDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnTypeDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.KTypeDef]}
     * }
     *
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [PlainDslName]}**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>`**`  [  `**`(`**{@include [DslGrammarTemplate.KTypeRef]}**`)`**`  ] [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     *
     * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
     *  {@include [Indent]}{@include [ColumnSetName]}**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>`**`  [  `**`(`**{@include [DslGrammarTemplate.KTypeRef]}**`)`**`  ] [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     *
     * {@comment We need to deviate from the template here, since we have overload discrepancies.}
     * {@set [DslGrammarTemplate.COLUMN_GROUP_PART]
     *  {@include [LineBreak]}
     *  ### On a column group reference:
     *
     *  {@include [LineBreak]}
     *
     *  {@include [DslGrammarTemplate.SingleColumnRef]}
     *
     *  {@include [Indent]}{@include [ColumnGroupName]}**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>`**`  [  `**`(`**{@include [DslGrammarTemplate.KTypeRef]}**`)`**`  ] [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     *
     *  {@include [LineBreak]}
     *
     *  {@include [DslGrammarTemplate.ColumnGroupNoSingleColumnRef]}
     *
     *  {@include [Indent]}{@include [ColumnGroupName]}**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>(`**{@include [DslGrammarTemplate.KTypeRef]}**`)`**`  [  `**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**` ]`
     * }
     */
    public interface Grammar {

        /** [**colsOf**][ColumnsSelectionDsl.colsOf] */
        public interface PlainDslName

        /** __`.`__[**`colsOf`**][ColumnsSelectionDsl.colsOf] */
        public interface ColumnSetName

        /** __`.`__[**`colsOf`**][ColumnsSelectionDsl.colsOf] */
        public interface ColumnGroupName
    }

    /**
     * ## Cols Of
     *
     * Returns a [ColumnSet] of columns from [this\] that are a subtype of the given type [C\], optionally filtered
     * by [filter\].
     *
     * This function operates solely on columns at the top-level.
     *
     * __NOTE:__ Null-filled columns of type [Nothing?][Nothing] will be included when selecting [`colsOf`][colsOf]`<T?>()`.
     *   This is because [Nothing][Nothing] is considered a subtype of all other types in Kotlin.
     *   To exclude these columns, call `.`[filter][ColumnsSelectionDsl.filter]` { !it.`[allNulls][DataColumn.allNulls]`() }`
     *   after it.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
     * {@include [LineBreak]}
     * Alternatively, [colsOf] can also be called on existing columns:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsOf][ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
     * {@include [LineBreak]}
     * Finally, [colsOf] can also take a [KType] argument instead of a reified type.
     * This is useful when the type is not known at compile time or when the API function cannot be inlined.
     * {@comment TODO: [Issue: #325, context receiver support](https://github.com/Kotlin/dataframe/issues/325) }
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * #### Examples for this overload:
     */
    private interface CommonColsOfDocs {

        /** @return A [ColumnSet] containing the columns of given type that were included by [filter\]. */
        interface Return

        /** @param [filter\] an optional filter function that takes a column of type [C\] and returns `true` if the column should be included. */
        interface FilterParam
    }

    /**
     * @include [CommonColsOfDocs]
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][String.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][String.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @include [CommonColsOfDocs.FilterParam]
     * @include [CommonColsOfDocs.Return]
     */
    public fun <C> String.colsOf(type: KType, filter: ColumnFilter<C> = { true }): ColumnSet<*> =
        columnGroup(this).colsOf(type, filter)

    /**
     * @include [CommonColsOfDocs]
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[colsOf][KProperty.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsOf][KProperty.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @include [CommonColsOfDocs.FilterParam]
     * @include [CommonColsOfDocs.Return]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.colsOf(type: KType, filter: ColumnFilter<C> = { true }): ColumnSet<*> =
        columnGroup(this).colsOf(type, filter)

    /**
     * @include [CommonColsOfDocs]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsOf][ColumnPath.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsOf][ColumnPath.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @include [CommonColsOfDocs.FilterParam]
     * @include [CommonColsOfDocs.Return]
     */
    public fun <C> ColumnPath.colsOf(type: KType, filter: ColumnFilter<C> = { true }): ColumnSet<*> =
        columnGroup(this).colsOf(type, filter)
}

/**
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs]
 *
 * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
 *
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.FilterParam]
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.Return]
 */
public fun <C> ColumnSet<*>.colsOf(type: KType, filter: ColumnFilter<C> = { true }): ColumnSet<C> =
    colsOfInternal(type, filter)

/**
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs]
 *
 * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 *
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.FilterParam]
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.Return]
 */
@Interpretable("ColsOf1")
public inline fun <reified C> ColumnSet<*>.colsOf(noinline filter: ColumnFilter<C> = { true }): ColumnSet<C> =
    colsOf(typeOf<C>(), filter)

/**
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs]
 *
 * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.FilterParam]
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.Return]
 */
public fun <C> ColumnsSelectionDsl<*>.colsOf(type: KType, filter: ColumnFilter<C> = { true }): ColumnSet<C> =
    asSingleColumn().colsOf(type, filter)

/**
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs]
 *
 * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.FilterParam]
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.Return]
 */
@Interpretable("ColsOf0")
public inline fun <reified C> ColumnsSelectionDsl<*>.colsOf(
    noinline filter: ColumnFilter<C> = { true },
): ColumnSet<C> = asSingleColumn().colsOf(typeOf<C>(), filter)

/**
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs]
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.FilterParam]
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.Return]
 */
public fun <C> SingleColumn<DataRow<*>>.colsOf(type: KType, filter: ColumnFilter<C> = { true }): ColumnSet<C> =
    ensureIsColumnGroup().colsOfInternal(type, filter)

/**
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs]
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.FilterParam]
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.Return]
 */
@Interpretable("ColsOf2")
public inline fun <reified C> SingleColumn<DataRow<*>>.colsOf(
    noinline filter: ColumnFilter<C> = { true },
): ColumnSet<C> = colsOf(typeOf<C>(), filter)

/**
 * If this [ColumnsResolver] is a [SingleColumn], it
 * returns a new [ColumnSet] containing the columns inside of this [SingleColumn] that
 * match the given [filter] and are the given [type].
 *
 * Else, it returns a new [ColumnSet] containing all columns in this [ColumnsResolver] that
 * match the given [filter] and are the given [type].
 */
@Suppress("UNCHECKED_CAST")
internal inline fun <C> ColumnsResolver<*>.colsOfInternal(
    type: KType,
    crossinline filter: ColumnFilter<C>,
): TransformableColumnSet<C> =
    colsInternal {
        it.isSubtypeOf(type) && filter(it.cast())
    } as TransformableColumnSet<C>

/* TODO: [Issue: #325, context receiver support](https://github.com/Kotlin/dataframe/issues/325)
context(ColumnsSelectionDsl)
public inline fun <reified C> KProperty<*>.colsOf(noinline filter: (DataColumn<C>) -> Boolean = { true }): ColumnSet<*> =
    colsOf(typeOf<C>(), filter)

context(ColumnsSelectionDsl)
public inline fun <reified C> String.colsOf(noinline filter: (DataColumn<C>) -> Boolean = { true }): ColumnSet<*> =
    colsOf(typeOf<C>(), filter)

 */

// endregion
