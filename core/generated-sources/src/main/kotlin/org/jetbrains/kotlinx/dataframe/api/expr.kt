package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import kotlin.reflect.typeOf

// region ColumnsSelectionDsl

/**
 * ## Expr [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ExprColumnsSelectionDsl {

    /**
     * ## Expr Grammar
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
     *  `name: `[`String`][String]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `infer: `[`Infer`][org.jetbrains.kotlinx.dataframe.api.Infer]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `expression: `[Column Expression][org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression]
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
     *  [**`expr`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]**`(`**`[`[`name`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NameDef]**`,`**`][`[`infer`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.InferDef]`]`**`) { `**[`expression`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnExpressionDef]**` }`**
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

        /** [**`expr`**][ColumnsSelectionDsl.expr] */
        public typealias PlainDslName = Nothing
    }
}

/**
 * ## Column Expression
 * Create a temporary new column by defining an expression to fill up each row.
 *
 * See [Column Expression][org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression] for more information.
 *
 * This function is essentially a shortcut for [ColumnsContainer.mapToColumn].
 *
 * ### Check out: [Usage][ExprColumnsSelectionDsl.Grammar]
 *
 * #### For example:
 *
 * `df.`[groupBy][DataFrame.groupBy]`  {  `[`expr`][ColumnsSelectionDsl.expr]` { firstName.`[`length`][String.length]` + lastName.`[`length`][String.length]`  }  `[`named`][named]` "nameLength" }`
 *
 * `df.`[sortBy][DataFrame.sortBy]`  {  `[`expr`][ColumnsSelectionDsl.expr]` { name.`[`length`][String.length]` }.`[`desc`][SortDsl.desc]`() }`
 *
 * @param [name] The name the temporary column. Is empty by default ("untitled" in the DataFrame).
 * @param [infer] [An enum][org.jetbrains.kotlinx.dataframe.api.Infer.Infer] that indicates how [DataColumn.type][org.jetbrains.kotlinx.dataframe.DataColumn.type] should be calculated.
 * Either [None][org.jetbrains.kotlinx.dataframe.api.Infer.None], [Nulls][org.jetbrains.kotlinx.dataframe.api.Infer.Nulls], or [Type][org.jetbrains.kotlinx.dataframe.api.Infer.Type]. By default: [Nulls][Infer.Nulls].
 * @param [expression] An [AddExpression] to define what each new row of the temporary column should contain.
 * @see [ColumnsContainer.mapToColumn]
 */
@Interpretable("Expr0")
public inline fun <T, reified R> ColumnsSelectionDsl<T>.expr(
    name: String = "",
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(name, typeOf<R>(), infer, expression)

// endregion
