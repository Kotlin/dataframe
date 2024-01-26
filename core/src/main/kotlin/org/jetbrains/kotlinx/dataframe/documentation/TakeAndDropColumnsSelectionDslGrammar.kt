package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate

/**
 * ## {@getArg [TitleArg]} (Last) (Cols) (While) Grammar
 *
 * @include [DslGrammarTemplate]
 * {@setArg [DslGrammarTemplate.DefinitionsArg]
 *  {@include [DslGrammarTemplate.ColumnSetDef]}
 *  {@include [LineBreak]}
 *  {@include [DslGrammarTemplate.ColumnGroupDef]}
 *  {@include [LineBreak]}
 *  {@include [DslGrammarTemplate.ConditionDef]}
 *  {@include [LineBreak]}
 *  {@include [DslGrammarTemplate.NumberDef]}
 * }
 *
 * {@setArg [DslGrammarTemplate.PlainDslFunctionsArg]
 *  {@include [PlainDslName]}**`(`**{@include [DslGrammarTemplate.NumberRef]}**`)`**
 *
 *  `|` {@include [PlainDslWhileName]}**` { `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**
 * }
 *
 * {@setArg [DslGrammarTemplate.ColumnSetFunctionsArg]
 *  {@include [Indent]}{@include [ColumnSetName]}**`(`**{@include [DslGrammarTemplate.NumberRef]}**`)`**
 *
 *  {@include [Indent]}`|` {@include [ColumnSetWhileName]}**` { `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**
 * }
 *
 * {@setArg [DslGrammarTemplate.ColumnGroupFunctionsArg]
 *  {@include [Indent]}{@include [ColumnGroupName]}**`(`**{@include [DslGrammarTemplate.NumberRef]}**`)`**
 *
 *  {@include [Indent]}`|` {@include [ColumnGroupWhileName]}**` { `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**
 * }
 */
internal interface TakeAndDropColumnsSelectionDslGrammar {

    /** Like "Take"/"Drop" */
    interface TitleArg

    /** Operation, like "take"/"drop" */
    interface OperationArg

    /** [**{@getArg [OperationArg]}**][ColumnsSelectionDsl.{@getArg [OperationArg]}]`(`[**Last**][ColumnsSelectionDsl.{@getArg [OperationArg]}Last]`)` */
    interface PlainDslName

    /** .[**{@getArg [OperationArg]}**][ColumnsSelectionDsl.{@getArg [OperationArg]}]`(`[**Last**][ColumnSet.{@getArg [OperationArg]}Last]`)` */
    interface ColumnSetName

    /** .[**{@getArg [OperationArg]}**][ColumnsSelectionDsl.{@getArg [OperationArg]}Cols]`(`[**Last**][ColumnsSelectionDsl.{@getArg [OperationArg]}LastCols]`)`[**Cols**][ColumnsSelectionDsl.{@getArg [OperationArg]}Cols] */
    interface ColumnGroupName

    /** [**{@getArg [OperationArg]}**][ColumnsSelectionDsl.{@getArg [OperationArg]}While]`(`[**Last**][ColumnsSelectionDsl.{@getArg [OperationArg]}LastWhile]`)`[**While**][ColumnsSelectionDsl.{@getArg [OperationArg]}While] */
    interface PlainDslWhileName

    /** .[**{@getArg [OperationArg]}**][ColumnsSelectionDsl.{@getArg [OperationArg]}While]`(`[**Last**][ColumnsSelectionDsl.{@getArg [OperationArg]}LastWhile]`)`[**While**][ColumnsSelectionDsl.{@getArg [OperationArg]}While] */
    interface ColumnSetWhileName

    /** .[**{@getArg [OperationArg]}**][ColumnsSelectionDsl.{@getArg [OperationArg]}ColsWhile]`(`[**Last**][ColumnsSelectionDsl.{@getArg [OperationArg]}LastColsWhile]`)`[**ColsWhile**][ColumnsSelectionDsl.{@getArg [OperationArg]}ColsWhile] */
    interface ColumnGroupWhileName
}
