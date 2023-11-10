package org.jetbrains.kotlinx.dataframe.api

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
     * @include [UsageTemplate]
     *
     * {@setArg [UsageTemplate.DefinitionsArg]
     *  {@include [UsageTemplate.NameDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.InferDef]}
     *  {@include [LineBreak]}
     *  {@include [UsageTemplate.ColumnExpressionDef]}
     * }
     * {@setArg [UsageTemplate.PlainDslFunctionsArg]
     *  {@include [PlainDslName]}**`(`**`[`{@include [UsageTemplate.NameRef]}`,][`{@include [UsageTemplate.InferRef]}`]`**`)`** **`{ `**{@include [UsageTemplate.ColumnExpressionRef]}**` \\}`**
     * }
     * {@setArg [UsageTemplate.ColumnSetPart]}
     * {@setArg [UsageTemplate.ColumnGroupPart]}
     */
    public interface Usage {

        /** [**expr**][ColumnsSelectionDsl.expr] */
        public interface PlainDslName
    }
}

/**
 * @include [ColumnExpression.CommonDocs]
 *
 * See [Usage][ExprColumnsSelectionDsl.Usage] for how to use [expr].
 *
 * #### For example:
 *
 * `df.`[groupBy][DataFrame.groupBy]` { `[expr][ColumnsSelectionDsl.expr]` { firstName.`[length][String.length]` + lastName.`[length][String.length]` } `[named][named]` "nameLength" }`
 *
 * `df.`[sortBy][DataFrame.sortBy]` { `[expr][ColumnsSelectionDsl.expr]` { name.`[length][String.length]` }.`[desc][SortDsl.desc]`() }`
 *
 * @param [name] The name the temporary column. Will be empty by default.
 * @include [Infer.Param] By default: [Nulls][Infer.Nulls].
 * @param [expression] An [AddExpression] to define what each new row of the temporary column should contain.
 */
public inline fun <T, reified R> ColumnsSelectionDsl<T>.expr(
    name: String = "",
    infer: Infer = Infer.Nulls,
    noinline expression: AddExpression<T, R>,
): DataColumn<R> = mapToColumn(name, infer, expression)

// endregion
