package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Usage.ColumnSetName
import org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Usage.PlainDslName
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.size
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region ColumnsSelectionDsl

/**
 * See [Usage]
 */
public interface ColsOfColumnsSelectionDsl {

    /**
     * ## Cols Of Usage
     *
     * @include [UsageTemplate]
     *
     * {@setArg [UsageTemplate.DefinitionsArg]
     *  {@include [UsageTemplate.ColumnSetDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.SingleColumnDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnGroupNoSingleColumnDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ConditionDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnTypeDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.KTypeDef]}
     * }
     *
     * {@setArg [UsageTemplate.PlainDslFunctionsArg]
     *  {@include [PlainDslName]}**`<`**{@include [UsageTemplate.ColumnTypeRef]}**`>`**` [` **`(`**{@include [UsageTemplate.KTypeRef]}**`)`** `] [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `]`
     * }
     *
     * {@setArg [UsageTemplate.ColumnSetFunctionsArg]
     *  {@include [Indent]}{@include [ColumnSetName]}**`<`**{@include [UsageTemplate.ColumnTypeRef]}**`>`**` [` **`(`**{@include [UsageTemplate.KTypeRef]}**`)`** `] [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `]`
     * }
     *
     * {@comment We need to deviate from the template here, since we have overload discrepancies.}
     * {@setArg [UsageTemplate.ColumnGroupPart]
     *  {@include [LineBreak]}
     *  ### On a column group reference:
     *
     *  {@include [LineBreak]}
     *
     *  {@include [UsageTemplate.SingleColumnRef]}
     *
     *  {@include [Indent]}{@include [ColumnGroupName]}**`<`**{@include [UsageTemplate.ColumnTypeRef]}**`>`**` [` **`(`**{@include [UsageTemplate.KTypeRef]}**`)`** `] [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `]`
     *
     *  {@include [LineBreak]}
     *
     *  {@include [UsageTemplate.ColumnGroupNoSingleColumnRef]}
     *
     *  {@include [Indent]}{@include [ColumnGroupName]}**`<`**{@include [UsageTemplate.ColumnTypeRef]}**`>(`**{@include [UsageTemplate.KTypeRef]}**`)`** ` [` **`{ `**{@include [UsageTemplate.ConditionRef]}**` \\}`** `]`
     * }
     */
    public interface Usage {

        /** [**colsOf**][ColumnsSelectionDsl.colsOf] */
        public interface PlainDslName

        /** .[**colsOf**][ColumnsSelectionDsl.colsOf] */
        public interface ColumnSetName

        /** .[**colsOf**][ColumnsSelectionDsl.colsOf] */
        public interface ColumnGroupName
    }

    /**
     * ## Cols Of
     *
     * Get columns by a given type and an optional filter.
     *
     * See [Usage] for how to use [colsOf].
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
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
     * `df.`[select][DataFrame.select]` { `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
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
    public fun <C> String.colsOf(
        type: KType,
        filter: ColumnFilter<C> = { true },
    ): ColumnSet<*> = columnGroup(this).colsOf(type, filter)

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
    public fun <C> KProperty<*>.colsOf(
        type: KType,
        filter: ColumnFilter<C> = { true },
    ): ColumnSet<*> = columnGroup(this).colsOf(type, filter)

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
    public fun <C> ColumnPath.colsOf(
        type: KType,
        filter: ColumnFilter<C> = { true },
    ): ColumnSet<*> = columnGroup(this).colsOf(type, filter)
}

/**
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs]
 *
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
 *
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.FilterParam]
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.Return]
 */
public fun <C> ColumnSet<*>.colsOf(
    type: KType,
    filter: ColumnFilter<C> = { true },
): TransformableColumnSet<C> = colsOfInternal(type, filter)

/**
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs]
 *
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]` { `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 *
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.FilterParam]
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.Return]
 */
public inline fun <reified C> ColumnSet<*>.colsOf(
    noinline filter: ColumnFilter<C> = { true },
): TransformableColumnSet<C> = colsOf(typeOf<C>(), filter)

/**
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs]
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.FilterParam]
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.Return]
 */
public fun <C> ColumnsSelectionDsl<*>.colsOf(
    type: KType,
    filter: ColumnFilter<C> = { true },
): TransformableColumnSet<C> = asSingleColumn().colsOf(type, filter)

/**
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs]
 *
 * `df.`[select][DataFrame.select]` { `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.FilterParam]
 * @include [ColsOfColumnsSelectionDsl.CommonColsOfDocs.Return]
 */
public inline fun <reified C> ColumnsSelectionDsl<*>.colsOf(
    noinline filter: ColumnFilter<C> = { true },
): TransformableColumnSet<C> = asSingleColumn().colsOf(typeOf<C>(), filter)

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
public fun <C> SingleColumn<DataRow<*>>.colsOf(
    type: KType,
    filter: ColumnFilter<C> = { true },
): TransformableColumnSet<C> = ensureIsColumnGroup().colsOfInternal(type, filter)

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
public inline fun <reified C> SingleColumn<DataRow<*>>.colsOf(
    noinline filter: ColumnFilter<C> = { true },
): TransformableColumnSet<C> = colsOf(typeOf<C>(), filter)

/**
 * If this [ColumnsResolver] is a [SingleColumn], it
 * returns a new [ColumnSet] containing the columns inside of this [SingleColumn] that
 * match the given [filter] and are the given [type].
 *
 * Else, it returns a new [ColumnSet] containing all columns in this [ColumnsResolver] that
 * match the given [filter] and are the given [type].
 */
@Suppress("UNCHECKED_CAST")
internal fun <C> ColumnsResolver<*>.colsOfInternal(
    type: KType,
    filter: ColumnFilter<C>,
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
