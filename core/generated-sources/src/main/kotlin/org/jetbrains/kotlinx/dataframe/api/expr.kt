package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsContainer
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ExprColumnsSelectionDsl.Usage.PlainDslName
import org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate

// region ColumnsSelectionDsl

/**
 * See [Usage]
 */
public interface ExprColumnsSelectionDsl {

    /**
     * ## Expr Usage
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * `name: `[String][String]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `infer: `[Infer][org.jetbrains.kotlinx.dataframe.api.Infer]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `expression: `[Column Expression][org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [**expr**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]**`(`**`[`[name][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.NameDef]`,][`[infer][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.InferDef]`]`**`)`** **`{ `**[expression][org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate.ColumnExpressionDef]**` }`**
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
     *
     *
     *
     *
     *
     */
    public interface Usage {

        /** [**expr**][ColumnsSelectionDsl.expr] */
        public interface PlainDslName
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
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * See [Usage][ExprColumnsSelectionDsl.Usage] for how to use [expr].
 *
 * #### For example:
 *
 * `df.`[groupBy][DataFrame.groupBy]` { `[expr][ColumnsSelectionDsl.expr]` { firstName.`[length][String.length]` + lastName.`[length][String.length]` } `[named][named]` "nameLength" }`
 *
 * `df.`[sortBy][DataFrame.sortBy]` { `[expr][ColumnsSelectionDsl.expr]` { name.`[length][String.length]` }.`[desc][SortDsl.desc]`() }`
 *
 * @param [name] The name the temporary column. Is empty by default ("untitled" in the DataFrame).
 * @param [infer] [An enum][org.jetbrains.kotlinx.dataframe.api.Infer.Infer] that indicates how [DataColumn.type][org.jetbrains.kotlinx.dataframe.DataColumn.type] should be calculated.
 * Either [None][org.jetbrains.kotlinx.dataframe.api.Infer.None], [Nulls][org.jetbrains.kotlinx.dataframe.api.Infer.Nulls], or [Type][org.jetbrains.kotlinx.dataframe.api.Infer.Type]. By default: [Nulls][Infer.Nulls].
 * @param [expression] An [AddExpression] to define what each new row of the temporary column should contain.
 * @see [ColumnsContainer.mapToColumn]
 */
public inline fun <T, reified R> ColumnsSelectionDsl<T>.expr(
    name: String = "",
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(name, infer, expression)

// endregion
