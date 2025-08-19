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
import org.jetbrains.kotlinx.dataframe.impl.columns.TransformableColumnSet
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.typeOf

// region ColumnsSelectionDsl

/**
 * ## Cols Of [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ColsOfColumnsSelectionDsl {

    /**
     * ## Cols Of Grammar
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
     *  `singleColumn: `[`SingleColumn`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroupReference: `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `column: `[`ColumnAccessor`][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[`ColumnFilter`][org.jetbrains.kotlinx.dataframe.ColumnFilter]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `T: Column type`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `kType: `[`KType`][kotlin.reflect.KType]
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
     *  [**colsOf**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]**`<`**[`T`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`  [  `**`(`**[`kType`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.KTypeDef]**`)`**`  ] [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`colsOf`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]**`<`**[`T`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`  [  `**`(`**[`kType`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.KTypeDef]**`)`**`  ] [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### On a column group reference:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *  [`singleColumn`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.SingleColumnDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`colsOf`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]**`<`**[`T`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`  [  `**`(`**[`kType`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.KTypeDef]**`)`**`  ] [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *  [`columnGroupReference`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupNoSingleColumnDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`colsOf`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]**`<`**[`T`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>(`**[`kType`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.KTypeDef]**`)`**`  [  `**`{ `**[`condition`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
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
     * Returns a [ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
     * by [filter].
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar]
     *
     * #### For example:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Alternatively, [colsOf] can also be called on existing columns:
     *
     * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsOf][ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Finally, [colsOf] can also take a [KType] argument instead of a reified type.
     * This is useful when the type is not known at compile time or when the API function cannot be inlined.
     *
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * #### Examples for this overload:
     */
    private interface CommonColsOfDocs {

        /** @return A [ColumnSet] containing the columns of given type that were included by [filter]. */
        interface Return

        /** @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included. */
        interface FilterParam
    }

    /**
     * ## Cols Of
     *
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
     * by [filter].
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [KType] argument instead of a reified type.
     * This is useful when the type is not known at compile time or when the API function cannot be inlined.
     *
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][String.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { "myColumnGroup".`[colsOf][String.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
     */
    public fun <C> String.colsOf(type: KType, filter: ColumnFilter<C> = { true }): ColumnSet<*> =
        columnGroup(this).colsOf(type, filter)

    /**
     * ## Cols Of
     *
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
     * by [filter].
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [KType] argument instead of a reified type.
     * This is useful when the type is not known at compile time or when the API function cannot be inlined.
     *
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColumnGroup.`[colsOf][KProperty.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { Type::myColumnGroup.`[colsOf][KProperty.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.colsOf(type: KType, filter: ColumnFilter<C> = { true }): ColumnSet<*> =
        columnGroup(this).colsOf(type, filter)

    /**
     * ## Cols Of
     *
     * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
     * by [filter].
     *
     * This function operates solely on columns at the top-level.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [KType] argument instead of a reified type.
     * This is useful when the type is not known at compile time or when the API function cannot be inlined.
     *
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsOf][ColumnPath.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColumnGroup"].`[colsOf][ColumnPath.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
     *
     * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
     */
    public fun <C> ColumnPath.colsOf(type: KType, filter: ColumnFilter<C> = { true }): ColumnSet<*> =
        columnGroup(this).colsOf(type, filter)
}

/**
 * ## Cols Of
 *
 * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
 * by [filter].
 *
 * This function operates solely on columns at the top-level.
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
 *
 * #### For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * #### Examples for this overload:
 *
 * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
 */
public fun <C> ColumnSet<*>.colsOf(type: KType, filter: ColumnFilter<C> = { true }): ColumnSet<C> =
    colsOfInternal(type, filter)

/**
 * ## Cols Of
 *
 * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
 * by [filter].
 *
 * This function operates solely on columns at the top-level.
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
 *
 * #### For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * #### Examples for this overload:
 *
 * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]` { .. }.`[colsOf][ColumnSet.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
 */
@Interpretable("ColsOf1")
public inline fun <reified C> ColumnSet<*>.colsOf(noinline filter: ColumnFilter<C> = { true }): ColumnSet<C> =
    colsOf(typeOf<C>(), filter)

/**
 * ## Cols Of
 *
 * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
 * by [filter].
 *
 * This function operates solely on columns at the top-level.
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
 *
 * #### For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * #### Examples for this overload:
 *
 * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
 */
public fun <C> ColumnsSelectionDsl<*>.colsOf(type: KType, filter: ColumnFilter<C> = { true }): ColumnSet<C> =
    asSingleColumn().colsOf(type, filter)

/**
 * ## Cols Of
 *
 * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
 * by [filter].
 *
 * This function operates solely on columns at the top-level.
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
 *
 * #### For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * #### Examples for this overload:
 *
 * `df.`[select][DataFrame.select]`  {  `[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
 */
@Interpretable("ColsOf0")
public inline fun <reified C> ColumnsSelectionDsl<*>.colsOf(
    noinline filter: ColumnFilter<C> = { true },
): ColumnSet<C> = asSingleColumn().colsOf(typeOf<C>(), filter)

/**
 * ## Cols Of
 *
 * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
 * by [filter].
 *
 * This function operates solely on columns at the top-level.
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
 *
 * #### For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * #### Examples for this overload:
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][DataColumn]`<`[Int][Int]`> -> it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
 */
public fun <C> SingleColumn<DataRow<*>>.colsOf(type: KType, filter: ColumnFilter<C> = { true }): ColumnSet<C> =
    ensureIsColumnGroup().colsOfInternal(type, filter)

/**
 * ## Cols Of
 *
 * Returns a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
 * by [filter].
 *
 * This function operates solely on columns at the top-level.
 *
 * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
 *
 * #### For example:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[colsOf][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Double][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [colsOf][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) }`
 *
 * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Int][Int]`>(`[typeOf][typeOf]`<`[Int][Int]`>()) { it: `[DataColumn][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[Int][Int]`> -> it.`[size][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * #### Examples for this overload:
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`> { it.`[size][DataColumn.size]` > 10 } }`
 *
 * `df.`[select][DataFrame.select]` { myColumnGroup.`[colsOf][SingleColumn.colsOf]`<`[Int][Int]`>() }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
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
