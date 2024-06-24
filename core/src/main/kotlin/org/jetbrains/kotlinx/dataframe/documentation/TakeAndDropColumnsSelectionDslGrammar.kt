@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate

/**
 * ## {@get [TitleArg]} (Last) (Cols) (While) Grammar
 *
 * @include [DslGrammarTemplate]
 * {@set [DslGrammarTemplate.DefinitionsArg]
 *  {@include [DslGrammarTemplate.ColumnSetDef]}
 *  {@include [LineBreak]}
 *  {@include [DslGrammarTemplate.ColumnGroupDef]}
 *  {@include [LineBreak]}
 *  {@include [DslGrammarTemplate.ConditionDef]}
 *  {@include [LineBreak]}
 *  {@include [DslGrammarTemplate.NumberDef]}
 * }
 *
 * {@set [DslGrammarTemplate.PlainDslFunctionsArg]
 *  {@include [PlainDslName]}**`(`**{@include [DslGrammarTemplate.NumberRef]}**`)`**
 *
 *  `| `{@include [PlainDslWhileName]}**`  {  `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**
 * }
 *
 * {@set [DslGrammarTemplate.ColumnSetFunctionsArg]
 *  {@include [Indent]}{@include [ColumnSetName]}**`(`**{@include [DslGrammarTemplate.NumberRef]}**`)`**
 *
 *  {@include [Indent]}`| `{@include [ColumnSetWhileName]}**`  {  `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**
 * }
 *
 * {@set [DslGrammarTemplate.ColumnGroupFunctionsArg]
 *  {@include [Indent]}{@include [ColumnGroupName]}**`(`**{@include [DslGrammarTemplate.NumberRef]}**`)`**
 *
 *  {@include [Indent]}`| `{@include [ColumnGroupWhileName]}**`  {  `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**
 * }
 */
internal interface TakeAndDropColumnsSelectionDslGrammar {
    // Like "Take"/"Drop"
    interface TitleArg

    // Operation, like "take"/"drop"
    interface OperationArg

    /** [**\`{@get [OperationArg]}\`**][ColumnsSelectionDsl.{@get [OperationArg]}]`(`[**`Last`**][ColumnsSelectionDsl.{@get [OperationArg]}Last]`)` */
    interface PlainDslName

    /** __`.`__[**\`{@get [OperationArg]}\`**][ColumnsSelectionDsl.{@get [OperationArg]}]`(`[**`Last`**][ColumnSet.{@get [OperationArg]}Last]`)` */
    interface ColumnSetName

    /** __`.`__[**\`{@get [OperationArg]}\`**][ColumnsSelectionDsl.{@get [OperationArg]}Cols]`(`[**`Last`**][ColumnsSelectionDsl.{@get [OperationArg]}LastCols]`)`[**`Cols`**][ColumnsSelectionDsl.{@get [OperationArg]}Cols] */
    interface ColumnGroupName

    /** [**\`{@get [OperationArg]}\`**][ColumnsSelectionDsl.{@get [OperationArg]}While]`(`[**`Last`**][ColumnsSelectionDsl.{@get [OperationArg]}LastWhile]`)`[**`While`**][ColumnsSelectionDsl.{@get [OperationArg]}While] */
    interface PlainDslWhileName

    /** __`.`__[**\`{@get [OperationArg]}\`**][ColumnsSelectionDsl.{@get [OperationArg]}While]`(`[**`Last`**][ColumnsSelectionDsl.{@get [OperationArg]}LastWhile]`)`[**`While`**][ColumnsSelectionDsl.{@get [OperationArg]}While] */
    interface ColumnSetWhileName

    /** __`.`__[**\`{@get [OperationArg]}\`**][ColumnsSelectionDsl.{@get [OperationArg]}ColsWhile]`(`[**`Last`**][ColumnsSelectionDsl.{@get [OperationArg]}LastColsWhile]`)`[**`ColsWhile`**][ColumnsSelectionDsl.{@get [OperationArg]}ColsWhile] */
    interface ColumnGroupWhileName
}
