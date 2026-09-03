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
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DoubleIndent
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnListImpl
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## And [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface AndColumnsSelectionDsl {

    /**
     * ## And Operator Grammar
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
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `column: `[<code>`ColumnAccessor`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnOrSet: `[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[<code>`columnSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
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
     *  [<code>`columnOrSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]` `[<code>**`and`**</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]`  [  `**`{`**`  ]  `[<code>`columnOrSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]`  [  `**`}`**` ]`
     *
     *  `| `[<code>`columnOrSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]__`.`__[<code>**`and`**</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]**` (`**`|`**`{ `**[<code>`columnOrSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]**` }`**`|`**`)`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`and`**</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]**` (`**`|`**`{ `**[<code>`columnOrSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]**` }`**`|`**`)`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`and`**</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]**` (`**`|`**`{ `**[<code>`columnOrSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]**` }`**`|`**`)`**
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

        /** [<code>**`and`**</code>][ColumnsSelectionDsl.and] */
        public typealias InfixName = Nothing

        /** __`.`__[<code>**`and`**</code>][ColumnsSelectionDsl.and] */
        public typealias Name = Nothing
    }

    /**
     * ## And Operator
     * The [<code>and</code>][and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]!
     *
     * For more information: [See `and` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#and)
     *
     * ### Check out: [<code>Grammar</code>][Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>`groupBy`</code>][DataFrame.groupBy]`  { "colA"  `[<code>`and`</code>][String.and]` colB }`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsOf`</code>][SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`and`</code>][ColumnSet.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsAtAnyDepth`</code>][ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[<code>`select`</code>][DataFrame.select]`  { "colC"  `[<code>`and`</code>][String.and]`  Type::colB  `[<code>`and`</code>][KProperty.and]`  "pathTo"["colC"]  `[<code>`and`</code>][ColumnPath.and]` colD }`
     *
     * #### Example for this overload:
     *
     *
     *
     * @return A [<code>ColumnSet</code>][ColumnSet] that contains all the columns from the [<code>ColumnsResolvers</code>][ColumnsResolver] on the left
     *   and right side of the [<code>and</code>][and] operator.
     */
    private interface CommonAndDocs {

        typealias EXAMPLE = Nothing
    }

    // region ColumnsResolver

    /**
     * ## And Operator
     * The [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]!
     *
     * For more information: [See `and` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#and)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>`groupBy`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` colB }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsAtAnyDepth`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[<code>`and`</code>][kotlin.String.and]`  Type::colB  `[<code>`and`</code>][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>`cols`</code>][ColumnsSelectionDsl.cols]`  { ... }  `[<code>`and`</code>][ColumnsResolver.and]` `<code></code>` }`
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [<code>ColumnsResolvers</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    private interface ColumnsResolverAndDocs {

        typealias Argument = Nothing
    }

    /** ## And Operator
     * The [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]!
     *
     * For more information: [See `and` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#and)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>`groupBy`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` colB }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsAtAnyDepth`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[<code>`and`</code>][kotlin.String.and]`  Type::colB  `[<code>`and`</code>][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  { ... }  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` `<code>[<code>`colsOf`</code>][SingleColumn.colsOf]`<`[<code>`Int`</code>][Int]`>()`</code>` }`
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [<code>ColumnsResolvers</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    @Interpretable("And0")
    public infix fun <C> ColumnsResolver<C>.and(other: ColumnsResolver<C>): ColumnSet<C> = ColumnListImpl(this, other)

    /** ## And Operator
     * The [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]!
     *
     * For more information: [See `and` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#and)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>`groupBy`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` colB }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsAtAnyDepth`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[<code>`and`</code>][kotlin.String.and]`  Type::colB  `[<code>`and`</code>][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  { ... }  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` `<code>`{ colA `[<code>`/`</code>][DataColumn.div]`  2.0  `[<code>`named`</code>][ColumnReference.named]` "half colA" }`</code>` }`
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [<code>ColumnsResolvers</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsResolver<C>.and(other: () -> ColumnsResolver<C>): ColumnSet<C> = this and other()

    /** ## And Operator
     * The [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]!
     *
     * For more information: [See `and` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#and)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>`groupBy`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` colB }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsAtAnyDepth`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[<code>`and`</code>][kotlin.String.and]`  Type::colB  `[<code>`and`</code>][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  { ... }  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` `<code>`"colB"`</code>` }`
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [<code>ColumnsResolvers</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> ColumnsResolver<C>.and(other: String): ColumnSet<*> = this and other.toColumnAccessor()

    /** ## And Operator
     * The [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]!
     *
     * For more information: [See `and` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#and)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>`groupBy`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` colB }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsAtAnyDepth`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[<code>`and`</code>][kotlin.String.and]`  Type::colB  `[<code>`and`</code>][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>`cols`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`  { ... }  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` `<code>`Type::colB`</code>` }`
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [<code>ColumnsResolvers</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> ColumnsResolver<C>.and(other: KProperty<C>): ColumnSet<C> = this and other.toColumnAccessor()

    // endregion

    // region String

    /**
     * ## And Operator
     * The [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]!
     *
     * For more information: [See `and` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#and)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>`groupBy`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` colB }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsAtAnyDepth`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[<code>`and`</code>][kotlin.String.and]`  Type::colB  `[<code>`and`</code>][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  { "colA"  `[<code>`and`</code>][String.and]` `<code></code>` }`
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [<code>ColumnsResolvers</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    private interface StringAndDocs {

        typealias Argument = Nothing
    }

    /** ## And Operator
     * The [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]!
     *
     * For more information: [See `and` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#and)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>`groupBy`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` colB }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsAtAnyDepth`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[<code>`and`</code>][kotlin.String.and]`  Type::colB  `[<code>`and`</code>][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` `<code>[<code>`colsOf`</code>][SingleColumn.colsOf]`<`[<code>`Int`</code>][Int]`>()`</code>` }`
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [<code>ColumnsResolvers</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> String.and(other: ColumnsResolver<C>): ColumnSet<*> = toColumnAccessor() and other

    /** ## And Operator
     * The [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]!
     *
     * For more information: [See `and` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#and)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>`groupBy`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` colB }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsAtAnyDepth`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[<code>`and`</code>][kotlin.String.and]`  Type::colB  `[<code>`and`</code>][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` `<code>`{ colA `[<code>`/`</code>][DataColumn.div]`  2.0  `[<code>`named`</code>][ColumnReference.named]` "half colA" }`</code>` }`
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [<code>ColumnsResolvers</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun <C> String.and(other: () -> ColumnsResolver<C>): ColumnSet<*> = toColumnAccessor() and other()

    /** ## And Operator
     * The [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]!
     *
     * For more information: [See `and` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#and)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>`groupBy`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` colB }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsAtAnyDepth`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[<code>`and`</code>][kotlin.String.and]`  Type::colB  `[<code>`and`</code>][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` `<code>`"colB"`</code>` }`
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [<code>ColumnsResolvers</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    public infix fun String.and(other: String): ColumnSet<*> = toColumnAccessor() and other.toColumnAccessor()

    /** ## And Operator
     * The [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]!
     *
     * For more information: [See `and` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#and)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>`groupBy`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` colB }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsAtAnyDepth`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[<code>`and`</code>][kotlin.String.and]`  Type::colB  `[<code>`and`</code>][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` `<code>`Type::colB`</code>` }`
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [<code>ColumnsResolvers</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> String.and(other: KProperty<C>): ColumnSet<*> = toColumnAccessor() and other

    // endregion

    // region KProperty

    /**
     * ## And Operator
     * The [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]!
     *
     * For more information: [See `and` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#and)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>`groupBy`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` colB }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsAtAnyDepth`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[<code>`and`</code>][kotlin.String.and]`  Type::colB  `[<code>`and`</code>][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]`  { Type::colA  `[<code>`and`</code>][KProperty.and]` `<code></code>` }`
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [<code>ColumnsResolvers</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    private interface KPropertyAndDocs {

        typealias Argument = Nothing
    }

    /** ## And Operator
     * The [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]!
     *
     * For more information: [See `and` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#and)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>`groupBy`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` colB }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsAtAnyDepth`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[<code>`and`</code>][kotlin.String.and]`  Type::colB  `[<code>`and`</code>][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::colA  `[<code>`and`</code>][kotlin.reflect.KProperty.and]` `<code>[<code>`colsOf`</code>][SingleColumn.colsOf]`<`[<code>`Int`</code>][Int]`>()`</code>` }`
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [<code>ColumnsResolvers</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.and(other: ColumnsResolver<C>): ColumnSet<C> = toColumnAccessor() and other

    /** ## And Operator
     * The [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]!
     *
     * For more information: [See `and` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#and)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>`groupBy`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` colB }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsAtAnyDepth`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[<code>`and`</code>][kotlin.String.and]`  Type::colB  `[<code>`and`</code>][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::colA  `[<code>`and`</code>][kotlin.reflect.KProperty.and]` `<code>`{ colA `[<code>/</code>][DataColumn.div]`  2.0  `[<code>`named`</code>][ColumnReference.named]` "half colA" }`</code>` }`
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [<code>ColumnsResolvers</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.and(other: () -> ColumnsResolver<C>): ColumnSet<C> =
        toColumnAccessor() and other()

    /** ## And Operator
     * The [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]!
     *
     * For more information: [See `and` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#and)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>`groupBy`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` colB }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsAtAnyDepth`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[<code>`and`</code>][kotlin.String.and]`  Type::colB  `[<code>`and`</code>][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::colA  `[<code>`and`</code>][kotlin.reflect.KProperty.and]` `<code>`"colB"`</code>` }`
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [<code>ColumnsResolvers</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.and(other: String): ColumnSet<*> = toColumnAccessor() and other

    /** ## And Operator
     * The [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator allows you to combine selections of columns or simply select multiple columns at once.
     *
     * You can even mix and match any [<code>Access APIs</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis]!
     *
     * For more information: [See `and` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#and)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.Grammar]
     *
     * #### Examples:
     *
     * `df.`[<code>`groupBy`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.groupBy]`  { "colA"  `[<code>`and`</code>][kotlin.String.and]` colB }`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsOf`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` {`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[<code>`colsAtAnyDepth`</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "price"  `[<code>`in`</code>][String.contains]` it.`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]` }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`}`
     *
     * `}`
     *
     * `df.`[<code>`select`</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "colC"  `[<code>`and`</code>][kotlin.String.and]`  Type::colB  `[<code>`and`</code>][kotlin.reflect.KProperty.and]`  "pathTo"["colC"]  `[<code>`and`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver.and]` colD }`
     *
     * #### Example for this overload:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { Type::colA  `[<code>`and`</code>][kotlin.reflect.KProperty.and]` `<code>`Type::colB`</code>` }`
     *
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] that contains all the columns from the [<code>ColumnsResolvers</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver] on the left
     *   and right side of the [<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and] operator.
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> KProperty<C>.and(other: KProperty<C>): ColumnSet<C> =
        toColumnAccessor() and other.toColumnAccessor()

    // endregion
}

// endregion
