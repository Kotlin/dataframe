package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnFilter
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
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
 * ## Cols Of [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface ColsOfColumnsSelectionDsl {

    /**
     * ## Cols Of Grammar
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
     *  `singleColumn: `[<code>`SingleColumn`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[<code>`DataRow`</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<*>>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroupReference: `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `column: `[<code>`ColumnAccessor`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `condition: `[<code>`ColumnFilter`</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `T: Column type`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `kType: `[<code>`KType`</code>][kotlin.reflect.KType]
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
     *  [<code>**colsOf**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`  [  `**`(`**[<code>`kType`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.KTypeDef]**`)`**`  ] [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`colsOf`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`  [  `**`(`**[<code>`kType`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.KTypeDef]**`)`**`  ] [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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
     *  [<code>`singleColumn`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.SingleColumnDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`colsOf`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`  [  `**`(`**[<code>`kType`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.KTypeDef]**`)`**`  ] [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     *  [<code>`columnGroupReference`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupNoSingleColumnDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`colsOf`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>(`**[<code>`kType`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.KTypeDef]**`)`**`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
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

        /** [<code>**colsOf**</code>][ColumnsSelectionDsl.colsOf] */
        public typealias PlainDslName = Nothing

        /** __`.`__[<code>**`colsOf`**</code>][ColumnsSelectionDsl.colsOf] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`colsOf`**</code>][ColumnsSelectionDsl.colsOf] */
        public typealias ColumnGroupName = Nothing
    }

    /**
     * ## Cols Of
     *
     * Returns a [<code>ColumnSet</code>][ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
     * by [filter].
     *
     * This function operates solely on columns at the top-level.
     *
     * __NOTE:__ Null-filled columns of type [<code>Nothing?</code>][Nothing] will be included when selecting [<code>`colsOf`</code>][colsOf]`<T?>()`.
     *   This is because [<code>Nothing</code>][Nothing] is considered a subtype of all other types in Kotlin.
     *   To exclude these columns, call `.`[<code>filter</code>][ColumnsSelectionDsl.filter]` { !it.`[<code>allNulls</code>][DataColumn.allNulls]`() }`
     *   after it.
     *
     * For more information: [See `colsOf` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][DataColumn.size]` > 10 } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Alternatively, [<code>colsOf</code>][colsOf] can also be called on existing columns:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>() }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup"<Type>().`[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][DataColumn.size]` > 10 } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Finally, [<code>colsOf</code>][colsOf] can also take a [<code>KType</code>][KType] argument instead of a reified type.
     * This is useful when the type is not known at compile time or when the API function cannot be inlined.
     *
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>colsOf</code>][ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) { it: `[<code>DataColumn</code>][DataColumn]`<`[<code>Int</code>][Int]`> -> it.`[<code>size</code>][DataColumn.size]` > 10 } }`
     *
     * #### Examples for this overload:
     */
    private interface CommonColsOfDocs {

        /** @return A [<code>ColumnSet</code>][ColumnSet] containing the columns of given type that were included by [filter]. */
        typealias Return = Nothing

        /** @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included. */
        typealias FilterParam = Nothing
    }

    /**
     * ## Cols Of
     *
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
     * by [filter].
     *
     * This function operates solely on columns at the top-level.
     *
     * __NOTE:__ Null-filled columns of type [<code>Nothing?</code>][Nothing] will be included when selecting [<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf]`<T?>()`.
     *   This is because [<code>Nothing</code>][Nothing] is considered a subtype of all other types in Kotlin.
     *   To exclude these columns, call `.`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>allNulls</code>][org.jetbrains.kotlinx.dataframe.DataColumn.allNulls]`() }`
     *   after it.
     *
     * For more information: [See `colsOf` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Alternatively, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Finally, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [<code>KType</code>][KType] argument instead of a reified type.
     * This is useful when the type is not known at compile time or when the API function cannot be inlined.
     *
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) { it: `[<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[<code>Int</code>][Int]`> -> it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>colsOf</code>][String.colsOf]`(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColumnGroup".`[<code>colsOf</code>][String.colsOf]`(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) { it: `[<code>DataColumn</code>][DataColumn]`<`[<code>Int</code>][Int]`> -> it.`[<code>size</code>][DataColumn.size]` > 10 } }`
     *
     * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
     */
    public fun <C> String.colsOf(type: KType, filter: (ColumnWithPath<C>) -> Boolean = { true }): ColumnSet<*> =
        columnGroup(this).colsOf(type, filter)

    /**
     * ## Cols Of
     *
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
     * by [filter].
     *
     * This function operates solely on columns at the top-level.
     *
     * __NOTE:__ Null-filled columns of type [<code>Nothing?</code>][Nothing] will be included when selecting [<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf]`<T?>()`.
     *   This is because [<code>Nothing</code>][Nothing] is considered a subtype of all other types in Kotlin.
     *   To exclude these columns, call `.`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>allNulls</code>][org.jetbrains.kotlinx.dataframe.DataColumn.allNulls]`() }`
     *   after it.
     *
     * For more information: [See `colsOf` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Alternatively, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Finally, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [<code>KType</code>][KType] argument instead of a reified type.
     * This is useful when the type is not known at compile time or when the API function cannot be inlined.
     *
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) { it: `[<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[<code>Int</code>][Int]`> -> it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { DataSchemaType::myColumnGroup.`[<code>colsOf</code>][KProperty.colsOf]`(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColumnGroup.`[<code>colsOf</code>][KProperty.colsOf]`(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) { it: `[<code>DataColumn</code>][DataColumn]`<`[<code>Int</code>][Int]`> -> it.`[<code>size</code>][DataColumn.size]` > 10 } }`
     *
     * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<*>.colsOf(type: KType, filter: (ColumnWithPath<C>) -> Boolean = { true }): ColumnSet<*> =
        columnGroup(this).colsOf(type, filter)

    /**
     * ## Cols Of
     *
     * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
     * by [filter].
     *
     * This function operates solely on columns at the top-level.
     *
     * __NOTE:__ Null-filled columns of type [<code>Nothing?</code>][Nothing] will be included when selecting [<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf]`<T?>()`.
     *   This is because [<code>Nothing</code>][Nothing] is considered a subtype of all other types in Kotlin.
     *   To exclude these columns, call `.`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>allNulls</code>][org.jetbrains.kotlinx.dataframe.DataColumn.allNulls]`() }`
     *   after it.
     *
     * For more information: [See `colsOf` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Alternatively, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>() }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Finally, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [<code>KType</code>][KType] argument instead of a reified type.
     * This is useful when the type is not known at compile time or when the API function cannot be inlined.
     *
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) { it: `[<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[<code>Int</code>][Int]`> -> it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>colsOf</code>][ColumnPath.colsOf]`(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) }`
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColumnGroup"].`[<code>colsOf</code>][ColumnPath.colsOf]`(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) { it: `[<code>DataColumn</code>][DataColumn]`<`[<code>Int</code>][Int]`> -> it.`[<code>size</code>][DataColumn.size]` > 10 } }`
     *
     * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
     */
    public fun <C> ColumnPath.colsOf(type: KType, filter: (ColumnWithPath<C>) -> Boolean = { true }): ColumnSet<*> =
        columnGroup(this).colsOf(type, filter)
}

/**
 * ## Cols Of
 *
 * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
 * by [filter].
 *
 * This function operates solely on columns at the top-level.
 *
 * __NOTE:__ Null-filled columns of type [<code>Nothing?</code>][Nothing] will be included when selecting [<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf]`<T?>()`.
 *   This is because [<code>Nothing</code>][Nothing] is considered a subtype of all other types in Kotlin.
 *   To exclude these columns, call `.`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>allNulls</code>][org.jetbrains.kotlinx.dataframe.DataColumn.allNulls]`() }`
 *   after it.
 *
 * For more information: [See `colsOf` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of)
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
 *
 * #### For example:
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>() }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>() }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [<code>KType</code>][KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) { it: `[<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[<code>Int</code>][Int]`> -> it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * #### Examples for this overload:
 *
 * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { .. }.`[<code>colsOf</code>][ColumnSet.colsOf]`(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) }`
 *
 * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { .. }.`[<code>colsOf</code>][ColumnSet.colsOf]`(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) { it: `[<code>DataColumn</code>][DataColumn]`<`[<code>Int</code>][Int]`> -> it.`[<code>size</code>][DataColumn.size]` > 10 } }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
 */
public fun <C> ColumnSet<*>.colsOf(type: KType, filter: (ColumnWithPath<C>) -> Boolean = { true }): ColumnSet<C> =
    colsOfInternal(type, filter)

/**
 * ## Cols Of
 *
 * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
 * by [filter].
 *
 * This function operates solely on columns at the top-level.
 *
 * __NOTE:__ Null-filled columns of type [<code>Nothing?</code>][Nothing] will be included when selecting [<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf]`<T?>()`.
 *   This is because [<code>Nothing</code>][Nothing] is considered a subtype of all other types in Kotlin.
 *   To exclude these columns, call `.`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>allNulls</code>][org.jetbrains.kotlinx.dataframe.DataColumn.allNulls]`() }`
 *   after it.
 *
 * For more information: [See `colsOf` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of)
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
 *
 * #### For example:
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>() }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>() }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [<code>KType</code>][KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) { it: `[<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[<code>Int</code>][Int]`> -> it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * #### Examples for this overload:
 *
 * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { .. }.`[<code>colsOf</code>][ColumnSet.colsOf]`<`[<code>Int</code>][Int]`>() }`
 *
 * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][ColumnsSelectionDsl.cols]` { .. }.`[<code>colsOf</code>][ColumnSet.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][DataColumn.size]` > 10 } }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
 */
@Interpretable("ColsOf1")
public inline fun <reified C> ColumnSet<*>.colsOf(
    noinline filter: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<C> = colsOf(typeOf<C>(), filter)

/**
 * ## Cols Of
 *
 * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
 * by [filter].
 *
 * This function operates solely on columns at the top-level.
 *
 * __NOTE:__ Null-filled columns of type [<code>Nothing?</code>][Nothing] will be included when selecting [<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf]`<T?>()`.
 *   This is because [<code>Nothing</code>][Nothing] is considered a subtype of all other types in Kotlin.
 *   To exclude these columns, call `.`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>allNulls</code>][org.jetbrains.kotlinx.dataframe.DataColumn.allNulls]`() }`
 *   after it.
 *
 * For more information: [See `colsOf` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of)
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
 *
 * #### For example:
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>() }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>() }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [<code>KType</code>][KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) { it: `[<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[<code>Int</code>][Int]`> -> it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * #### Examples for this overload:
 *
 * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
 */
public fun <C> ColumnsSelectionDsl<*>.colsOf(
    type: KType,
    filter: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<C> = asSingleColumn().colsOf(type, filter)

/**
 * ## Cols Of
 *
 * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
 * by [filter].
 *
 * This function operates solely on columns at the top-level.
 *
 * __NOTE:__ Null-filled columns of type [<code>Nothing?</code>][Nothing] will be included when selecting [<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf]`<T?>()`.
 *   This is because [<code>Nothing</code>][Nothing] is considered a subtype of all other types in Kotlin.
 *   To exclude these columns, call `.`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>allNulls</code>][org.jetbrains.kotlinx.dataframe.DataColumn.allNulls]`() }`
 *   after it.
 *
 * For more information: [See `colsOf` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of)
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
 *
 * #### For example:
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>() }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>() }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [<code>KType</code>][KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) { it: `[<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[<code>Int</code>][Int]`> -> it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * #### Examples for this overload:
 *
 * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>() }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
 */
@Interpretable("ColsOf0")
public inline fun <reified C> ColumnsSelectionDsl<*>.colsOf(
    noinline filter: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<C> = asSingleColumn().colsOf(typeOf<C>(), filter)

/**
 * ## Cols Of
 *
 * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
 * by [filter].
 *
 * This function operates solely on columns at the top-level.
 *
 * __NOTE:__ Null-filled columns of type [<code>Nothing?</code>][Nothing] will be included when selecting [<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf]`<T?>()`.
 *   This is because [<code>Nothing</code>][Nothing] is considered a subtype of all other types in Kotlin.
 *   To exclude these columns, call `.`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>allNulls</code>][org.jetbrains.kotlinx.dataframe.DataColumn.allNulls]`() }`
 *   after it.
 *
 * For more information: [See `colsOf` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of)
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
 *
 * #### For example:
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>() }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>() }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [<code>KType</code>][KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) { it: `[<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[<code>Int</code>][Int]`> -> it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * #### Examples for this overload:
 *
 * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) { it: `[<code>DataColumn</code>][DataColumn]`<`[<code>Int</code>][Int]`> -> it.`[<code>size</code>][DataColumn.size]` > 10 } }`
 *
 * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
 */
public fun <C> SingleColumn<DataRow<*>>.colsOf(
    type: KType,
    filter: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<C> = ensureIsColumnGroup().colsOfInternal(type, filter)

/**
 * ## Cols Of
 *
 * Returns a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] of columns from [this] that are a subtype of the given type [C], optionally filtered
 * by [filter].
 *
 * This function operates solely on columns at the top-level.
 *
 * __NOTE:__ Null-filled columns of type [<code>Nothing?</code>][Nothing] will be included when selecting [<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf]`<T?>()`.
 *   This is because [<code>Nothing</code>][Nothing] is considered a subtype of all other types in Kotlin.
 *   To exclude these columns, call `.`[<code>filter</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]` { !it.`[<code>allNulls</code>][org.jetbrains.kotlinx.dataframe.DataColumn.allNulls]`() }`
 *   after it.
 *
 * For more information: [See `colsOf` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#cols-of)
 *
 * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.Grammar]
 *
 * #### For example:
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>() }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Alternatively, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also be called on existing columns:
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>() }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup"<Type>().`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { Type::myColumnGroup.`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * Finally, [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColsOfColumnsSelectionDsl.colsOf] can also take a [<code>KType</code>][KType] argument instead of a reified type.
 * This is useful when the type is not known at compile time or when the API function cannot be inlined.
 *
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) }`
 *
 * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "myColumnGroup".`[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Int</code>][Int]`>(`[<code>typeOf</code>][typeOf]`<`[<code>Int</code>][Int]`>()) { it: `[<code>DataColumn</code>][org.jetbrains.kotlinx.dataframe.DataColumn]`<`[<code>Int</code>][Int]`> -> it.`[<code>size</code>][org.jetbrains.kotlinx.dataframe.DataColumn.size]` > 10 } }`
 *
 * #### Examples for this overload:
 *
 * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>Int</code>][Int]`> { it.`[<code>size</code>][DataColumn.size]` > 10 } }`
 *
 * `df.`[<code>select</code>][DataFrame.select]` { myColumnGroup.`[<code>colsOf</code>][SingleColumn.colsOf]`<`[<code>Int</code>][Int]`>() }`
 *
 * @param [filter] an optional filter function that takes a column of type [C] and returns `true` if the column should be included.
 * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns of given type that were included by [filter].
 */
@Interpretable("ColsOf2")
public inline fun <reified C> SingleColumn<DataRow<*>>.colsOf(
    noinline filter: (ColumnWithPath<C>) -> Boolean = { true },
): ColumnSet<C> = colsOf(typeOf<C>(), filter)

/**
 * If this [<code>ColumnsResolver</code>][ColumnsResolver] is a [<code>SingleColumn</code>][SingleColumn], it
 * returns a new [<code>ColumnSet</code>][ColumnSet] containing the columns inside of this [<code>SingleColumn</code>][SingleColumn] that
 * match the given [<code>filter</code>][filter] and are the given [<code>type</code>][type].
 *
 * Else, it returns a new [<code>ColumnSet</code>][ColumnSet] containing all columns in this [<code>ColumnsResolver</code>][ColumnsResolver] that
 * match the given [<code>filter</code>][filter] and are the given [<code>type</code>][type].
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
