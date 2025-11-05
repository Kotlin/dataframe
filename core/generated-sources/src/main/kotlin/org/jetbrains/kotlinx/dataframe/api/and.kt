package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnReference
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnListImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## And [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface AndColumnsSelectionDsl {

    /**
     * ## And Operator Grammar
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
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `column: `[`ColumnAccessor`][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnOrSet: `[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[`columnSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
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
     *  [`columnOrSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]` `[**`and`**][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]`  [  `**`{`**`  ]  `[`columnOrSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]`  [  `**`}`**` ]`
     *
     *  `| `[`columnOrSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]__`.`__[**`and`**][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]**` (`**`|`**`{ `**[`columnOrSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]**` }`**`|`**`)`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`and`**][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]**` (`**`|`**`{ `**[`columnOrSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]**` }`**`|`**`)`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`and`**][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]**` (`**`|`**`{ `**[`columnOrSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]**` }`**`|`**`)`**
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

        /** [**`and`**][ColumnsSelectionDsl.and] */
        public typealias InfixName = Nothing

        /** __`.`__[**`and`**][ColumnsSelectionDsl.and] */
        public typealias Name = Nothing
    }

    /**
     * ## And Operator
     * The [and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Check out: [Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][DataFrame.groupBy]`  { "colA"  `[`and`][String.and]` colB }`
     *
     * `df.`[`select`][DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[`colsOf`][SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[`colsAtAnyDepth`][ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[`select`][DataFrame.select]`  { "colC"  `[`and`][String.and]`  Type::colB  `[`and`][KProperty.and]`  "pathTo"["colC"]  `[`and`][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     *
     *
     * @return A [ColumnSet] that contains all the columns from the [ColumnsResolvers][ColumnsResolver] on the left
     *   and right side of the [and] operator.
     */
    private interface CommonAndDocs {

        typealias EXAMPLE = Nothing
    }

    // region ColumnsResolver

    /**
     * ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[`and`][kotlin.String.and]` colB }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[`and`][kotlin.String.and]`  Type::colB  `[`and`][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][DataFrame.select]`  {  `[`cols`][ColumnsSelectionDsl.cols]`  { ... }  `[`and`][ColumnsResolver.and]` `<code></code>` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    private interface ColumnsResolverAndDocs {

        typealias Argument = Nothing
    }

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[`and`][kotlin.String.and]` colB }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[`and`][kotlin.String.and]`  Type::colB  `[`and`][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  { ... }  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` `<code>[`colsOf`][SingleColumn.colsOf]`<`[`Int`][Int]`>()`</code>` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    @Interpretable("And0")
    public infix fun <C> ColumnsResolver<C>.and(other: ColumnsResolver<C>): ColumnSet<C> = ColumnListImpl(this, other)

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[`and`][kotlin.String.and]` colB }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[`and`][kotlin.String.and]`  Type::colB  `[`and`][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  { ... }  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` `<code>`{ colA `[`/`][DataColumn.div]`  2.0  `[`named`][ColumnReference.named]` "half colA" }`</code>` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsResolver<C>.and(other: () -> ColumnsResolver<C>): ColumnSet<C> = this and other()

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[`and`][kotlin.String.and]` colB }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[`and`][kotlin.String.and]`  Type::colB  `[`and`][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  { ... }  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` `<code>`"colB"`</code>` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsResolver<C>.and(other: String): ColumnSet<*> = this and other.toColumnAccessor()

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[`and`][kotlin.String.and]` colB }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[`and`][kotlin.String.and]`  Type::colB  `[`and`][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[`cols`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  { ... }  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` `<code>`Type::colB`</code>` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> ColumnsResolver<C>.and(other: KProperty<C>): ColumnSet<C> = this and other.toColumnAccessor()

    // endregion

    // region String

    /**
     * ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[`and`][kotlin.String.and]` colB }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[`and`][kotlin.String.and]`  Type::colB  `[`and`][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][DataFrame.select]`  { "colA"  `[`and`][String.and]` `<code></code>` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    private interface StringAndDocs {

        typealias Argument = Nothing
    }

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[`and`][kotlin.String.and]` colB }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[`and`][kotlin.String.and]`  Type::colB  `[`and`][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[`and`][kotlin.String.and]` `<code>[`colsOf`][SingleColumn.colsOf]`<`[`Int`][Int]`>()`</code>` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> String.and(other: ColumnsResolver<C>): ColumnSet<*> = toColumnAccessor() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[`and`][kotlin.String.and]` colB }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[`and`][kotlin.String.and]`  Type::colB  `[`and`][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[`and`][kotlin.String.and]` `<code>`{ colA `[`/`][DataColumn.div]`  2.0  `[`named`][ColumnReference.named]` "half colA" }`</code>` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> String.and(other: () -> ColumnsResolver<C>): ColumnSet<*> = toColumnAccessor() and other()

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[`and`][kotlin.String.and]` colB }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[`and`][kotlin.String.and]`  Type::colB  `[`and`][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[`and`][kotlin.String.and]` `<code>`"colB"`</code>` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun String.and(other: String): ColumnSet<*> = toColumnAccessor() and other.toColumnAccessor()

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[`and`][kotlin.String.and]` colB }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[`and`][kotlin.String.and]`  Type::colB  `[`and`][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[`and`][kotlin.String.and]` `<code>`Type::colB`</code>` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> String.and(other: KProperty<C>): ColumnSet<*> = toColumnAccessor() and other

    // endregion

    // region KProperty

    /**
     * ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[`and`][kotlin.String.and]` colB }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[`and`][kotlin.String.and]`  Type::colB  `[`and`][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][DataFrame.select]`  { Type::colA  `[`and`][KProperty.and]` `<code></code>` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    private interface KPropertyAndDocs {

        typealias Argument = Nothing
    }

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[`and`][kotlin.String.and]` colB }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[`and`][kotlin.String.and]`  Type::colB  `[`and`][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::colA  `[`and`][kotlin.reflect.KProperty.and]` `<code>[`colsOf`][SingleColumn.colsOf]`<`[`Int`][Int]`>()`</code>` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.and(other: ColumnsResolver<C>): ColumnSet<C> = toColumnAccessor() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[`and`][kotlin.String.and]` colB }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[`and`][kotlin.String.and]`  Type::colB  `[`and`][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::colA  `[`and`][kotlin.reflect.KProperty.and]` `<code>`{ colA `[/][DataColumn.div]`  2.0  `[`named`][ColumnReference.named]` "half colA" }`</code>` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.and(other: () -> ColumnsResolver<C>): ColumnSet<C> =
        toColumnAccessor() and other()

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[`and`][kotlin.String.and]` colB }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[`and`][kotlin.String.and]`  Type::colB  `[`and`][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::colA  `[`and`][kotlin.reflect.KProperty.and]` `<code>`"colB"`</code>` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.and(other: String): ColumnSet<*> = toColumnAccessor() and other

    /** ## And Operator
     * The [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi]!
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[`groupBy`][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[`and`][kotlin.String.and]` colB }`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[`colsOf`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[`String`][String]`>() `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[`in`][String.contains]` it.`[`name`][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[`select`][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[`and`][kotlin.String.and]`  Type::colB  `[`and`][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[`and`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::colA  `[`and`][kotlin.reflect.KProperty.and]` `<code>`Type::colB`</code>` }`
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [ColumnsResolvers][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.and(other: KProperty<C>): ColumnSet<C> =
        toColumnAccessor() and other.toColumnAccessor()

    // endregion
}

// endregion
