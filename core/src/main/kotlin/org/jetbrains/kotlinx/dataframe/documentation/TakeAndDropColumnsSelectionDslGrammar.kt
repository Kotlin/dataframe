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
    typealias TITLE = Nothing

    // Operation, like "take"/"drop"
    typealias OPERATION = Nothing

    /** [**\`{@get [OPERATION]}\`**][ColumnsSelectionDsl.{@get [OPERATION]}]`(`[**`Last`**][ColumnsSelectionDsl.{@get [OPERATION]}Last]`)` */
    typealias PlainDslName = Nothing

    /** __`.`__[**\`{@get [OPERATION]}\`**][ColumnsSelectionDsl.{@get [OPERATION]}]`(`[**`Last`**][ColumnSet.{@get [OPERATION]}Last]`)` */
    typealias ColumnSetName = Nothing

    /** __`.`__[**\`{@get [OPERATION]}\`**][ColumnsSelectionDsl.{@get [OPERATION]}Cols]`(`[**`Last`**][ColumnsSelectionDsl.{@get [OPERATION]}LastCols]`)`[**`Cols`**][ColumnsSelectionDsl.{@get [OPERATION]}Cols] */
    typealias ColumnGroupName = Nothing

    /** [**\`{@get [OPERATION]}\`**][ColumnsSelectionDsl.{@get [OPERATION]}While]`(`[**`Last`**][ColumnsSelectionDsl.{@get [OPERATION]}LastWhile]`)`[**`While`**][ColumnsSelectionDsl.{@get [OPERATION]}While] */
    typealias PlainDslWhileName = Nothing

    /** __`.`__[**\`{@get [OPERATION]}\`**][ColumnsSelectionDsl.{@get [OPERATION]}While]`(`[**`Last`**][ColumnsSelectionDsl.{@get [OPERATION]}LastWhile]`)`[**`While`**][ColumnsSelectionDsl.{@get [OPERATION]}While] */
    typealias ColumnSetWhileName = Nothing

    /** __`.`__[**\`{@get [OPERATION]}\`**][ColumnsSelectionDsl.{@get [OPERATION]}ColsWhile]`(`[**`Last`**][ColumnsSelectionDsl.{@get [OPERATION]}LastColsWhile]`)`[**`ColsWhile`**][ColumnsSelectionDsl.{@get [OPERATION]}ColsWhile] */
    typealias ColumnGroupWhileName = Nothing
}
