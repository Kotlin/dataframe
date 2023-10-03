package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.documentation.UsageTemplateColumnsSelectionDsl.UsageTemplate

/**
 * ## {@getArg [TitleArg]} (Last) (Cols) (While) Usage
 *
 * @include [UsageTemplate]
 * {@setArg [UsageTemplate.DefinitionsArg]
 *  {@include [UsageTemplate.ColumnSetDef]}
 *  {@include [LineBreak]}
 *  {@include [UsageTemplate.ColumnGroupDef]}
 *  {@include [LineBreak]}
 *  {@include [UsageTemplate.ConditionDef]}
 *  {@include [LineBreak]}
 *  {@include [UsageTemplate.NumberDef]}
 * }
 *
 * {@setArg [UsageTemplate.PlainDslFunctionsArg]
 *  {@include [PlainDslName]}**`(`**{@include [UsageTemplate.NumberRef]}**`)`**
 *
 *  `|` {@include [PlainDslWhileName]}**` { `**{@include [UsageTemplate.ConditionRef]}**` \\\\}`**
 * }
 *
 * {@setArg [UsageTemplate.ColumnSetFunctionsArg]
 *  {@include [Indent]}{@include [ColumnSetName]}**`(`**{@include [UsageTemplate.NumberRef]}**`)`**
 *
 *  {@include [Indent]}`|` {@include [ColumnSetWhileName]}**` { `**{@include [UsageTemplate.ConditionRef]}**` \\\\}`**
 * }
 *
 * {@setArg [UsageTemplate.ColumnGroupFunctionsArg]
 *  {@include [Indent]}{@include [ColumnGroupName]}**`(`**{@include [UsageTemplate.NumberRef]}**`)`**
 *
 *  {@include [Indent]}`|` {@include [ColumnGroupWhileName]}**` { `**{@include [UsageTemplate.ConditionRef]}**` \\\\}`**
 * }
 */
internal interface TakeAndDropColumnsSelectionDslUsage {

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
