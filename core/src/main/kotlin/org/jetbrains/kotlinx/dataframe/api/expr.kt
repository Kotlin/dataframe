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
 * ## Expr {@include [ColumnsSelectionDslLink]}
 *
 * See [Grammar] for all functions in this interface.
 */
public interface ExprColumnsSelectionDsl {

    /**
     * ## Expr Grammar
     *
     * @include [DslGrammarTemplate]
     *
     * {@set [DslGrammarTemplate.DEFINITIONS]
     *  {@include [DslGrammarTemplate.NameDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.InferDef]}
     *  {@include [LineBreak]}
     *  {@include [DslGrammarTemplate.ColumnExpressionDef]}
     * }
     * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
     *  {@include [PlainDslName]}**`(`**`[`{@include [DslGrammarTemplate.NameRef]}**`,`**`][`{@include [DslGrammarTemplate.InferRef]}`]`**`) { `**{@include [DslGrammarTemplate.ColumnExpressionRef]}**` \}`**
     * }
     * {@set [DslGrammarTemplate.COLUMN_SET_PART]}
     * {@set [DslGrammarTemplate.COLUMN_GROUP_PART]}
     */
    public interface Grammar {

        /** [**`expr`**][ColumnsSelectionDsl.expr] */
        public interface PlainDslName
    }
}

/**
 * @include [ColumnExpression.CommonDocs]
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
 * @include [Infer.ParamDoc] By default: [Nulls][Infer.Nulls].
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
