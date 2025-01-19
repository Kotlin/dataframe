@file:ExcludeFromSources

package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate

/**
 * ## {@get [TITLE]} (Last) (Cols) (While) Grammar
 *
 * @include [DslGrammarTemplate]
 * {@set [DslGrammarTemplate.DEFINITIONS]
 *  {@include [DslGrammarTemplate.ColumnSetDef]}
 *  {@include [LineBreak]}
 *  {@include [DslGrammarTemplate.ColumnGroupDef]}
 *  {@include [LineBreak]}
 *  {@include [DslGrammarTemplate.ConditionDef]}
 *  {@include [LineBreak]}
 *  {@include [DslGrammarTemplate.NumberDef]}
 * }
 *
 * {@set [DslGrammarTemplate.PLAIN_DSL_FUNCTIONS]
 *  {@include [PlainDslName]}**`(`**{@include [DslGrammarTemplate.NumberRef]}**`)`**
 *
 *  `| `{@include [PlainDslWhileName]}**`  {  `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**
 * }
 *
 * {@set [DslGrammarTemplate.COLUMN_SET_FUNCTIONS]
 *  {@include [Indent]}{@include [ColumnSetName]}**`(`**{@include [DslGrammarTemplate.NumberRef]}**`)`**
 *
 *  {@include [Indent]}`| `{@include [ColumnSetWhileName]}**`  {  `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**
 * }
 *
 * {@set [DslGrammarTemplate.COLUMN_GROUP_FUNCTIONS]
 *  {@include [Indent]}{@include [ColumnGroupName]}**`(`**{@include [DslGrammarTemplate.NumberRef]}**`)`**
 *
 *  {@include [Indent]}`| `{@include [ColumnGroupWhileName]}**`  {  `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**
 * }
 */
internal interface TakeAndDropColumnsSelectionDslGrammar {

    // Like "Take"/"Drop"
    interface TITLE

    // Operation, like "take"/"drop"
    interface OPERATION

    /** [**\`{@get [OPERATION]}\`**][ColumnsSelectionDsl.{@get [OPERATION]}]`(`[**`Last`**][ColumnsSelectionDsl.{@get [OPERATION]}Last]`)` */
    interface PlainDslName

    /** __`.`__[**\`{@get [OPERATION]}\`**][ColumnsSelectionDsl.{@get [OPERATION]}]`(`[**`Last`**][ColumnSet.{@get [OPERATION]}Last]`)` */
    interface ColumnSetName

    /** __`.`__[**\`{@get [OPERATION]}\`**][ColumnsSelectionDsl.{@get [OPERATION]}Cols]`(`[**`Last`**][ColumnsSelectionDsl.{@get [OPERATION]}LastCols]`)`[**`Cols`**][ColumnsSelectionDsl.{@get [OPERATION]}Cols] */
    interface ColumnGroupName

    /** [**\`{@get [OPERATION]}\`**][ColumnsSelectionDsl.{@get [OPERATION]}While]`(`[**`Last`**][ColumnsSelectionDsl.{@get [OPERATION]}LastWhile]`)`[**`While`**][ColumnsSelectionDsl.{@get [OPERATION]}While] */
    interface PlainDslWhileName

    /** __`.`__[**\`{@get [OPERATION]}\`**][ColumnsSelectionDsl.{@get [OPERATION]}While]`(`[**`Last`**][ColumnsSelectionDsl.{@get [OPERATION]}LastWhile]`)`[**`While`**][ColumnsSelectionDsl.{@get [OPERATION]}While] */
    interface ColumnSetWhileName

    /** __`.`__[**\`{@get [OPERATION]}\`**][ColumnsSelectionDsl.{@get [OPERATION]}ColsWhile]`(`[**`Last`**][ColumnsSelectionDsl.{@get [OPERATION]}LastColsWhile]`)`[**`ColsWhile`**][ColumnsSelectionDsl.{@get [OPERATION]}ColsWhile] */
    interface ColumnGroupWhileName
}
