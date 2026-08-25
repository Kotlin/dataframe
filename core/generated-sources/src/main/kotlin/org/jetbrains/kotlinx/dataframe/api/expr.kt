package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import kotlin.reflect.typeOf

// region ColumnsSelectionDsl

/**
 * ## Expr [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
public interface ExprColumnsSelectionDsl {

    /**
     * ## Expr Grammar
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
     *  `name: `[<code>`String`</code>][String]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `infer: `[<code>`Infer`</code>][org.jetbrains.kotlinx.dataframe.api.Infer]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `expression: `[<code>Column Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression]
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
     *  [<code>**`expr`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]**`(`**`[`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NameDef]**`,`**`][`[<code>`infer`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.InferDef]`]`**`) { `**[<code>`expression`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnExpressionDef]**` }`**
     *
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

        /** [<code>**`expr`**</code>][ColumnsSelectionDsl.expr] */
        public typealias PlainDslName = Nothing
    }
}

/**
 * Creates a temporary new column by defining an expression to fill up each row.
 *
 * See [<code>Column Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression] for more information.
 *
 * For more information: [See `expr` on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#expr-column-expression)
 *
 * ### Check out: [<code>Usage</code>][ExprColumnsSelectionDsl.Grammar]
 *
 * #### For example:
 *
 * `df.`[<code>groupBy</code>][DataFrame.groupBy]`  {  `[<code>`expr`</code>][ColumnsSelectionDsl.expr]` { firstName.`[<code>`length`</code>][String.length]` + lastName.`[<code>`length`</code>][String.length]`  }  `[<code>`named`</code>][named]` "nameLength" }`
 *
 * `df.`[<code>sortBy</code>][DataFrame.sortBy]`  {  `[<code>`expr`</code>][ColumnsSelectionDsl.expr]` { name.`[<code>`length`</code>][String.length]` }.`[<code>`desc`</code>][SortDsl.desc]`() }`
 *
 * @param [name] The name the temporary column. Is empty by default ("untitled" in the DataFrame).
 * @param [infer] [<code>An enum</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Infer] that indicates how [<code>DataColumn.type</code>][org.jetbrains.kotlinx.dataframe.DataColumn.type] should be calculated.
 * Either [<code>None</code>][org.jetbrains.kotlinx.dataframe.api.Infer.None], [<code>Nulls</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Nulls], or [<code>Type</code>][org.jetbrains.kotlinx.dataframe.api.Infer.Type]. By default: [<code>Nulls</code>][Infer.Nulls].
 * @param [expression] An [<code>AddExpression</code>][AddExpression] to define what each new row of the temporary column should contain.
 */
@Interpretable("Expr0")
public inline fun <T, reified R> ColumnsSelectionDsl<T>.expr(
    name: String = "",
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(name, typeOf<R>(), infer, expression)

// endregion
