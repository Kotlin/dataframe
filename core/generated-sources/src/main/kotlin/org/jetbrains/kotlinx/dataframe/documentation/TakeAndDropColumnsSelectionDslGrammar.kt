package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate

/**
 * ## {@getArg [TitleArg]} (Last) (Cols) (While) Grammar
 *
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 *  ### Definitions:
 *  `columnSet: `[ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
 *  
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 *  `columnGroup: `[SingleColumn][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[String][String]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * `| `[KProperty][kotlin.reflect.KProperty]`<* | `[DataRow][org.jetbrains.kotlinx.dataframe.DataRow]`<*>>` | `[ColumnPath][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
 *  
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 *  `condition: `[ColumnFilter][org.jetbrains.kotlinx.dataframe.ColumnFilter]
 *  
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 *  `number: `[Int][Int]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 *  ### What can be called directly in the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
 *
 *  
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 *  [**{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}**][ColumnsSelectionDsl.{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}]`(`[**Last**][ColumnsSelectionDsl.{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}Last]`)`**`(`**[number][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
 *
 *  `|` [**{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}**][ColumnsSelectionDsl.{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}While]`(`[**Last**][ColumnsSelectionDsl.{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}LastWhile]`)`[**While**][ColumnsSelectionDsl.{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}While]**` { `**[condition][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` \
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 *  ### What can be called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
 *
 *  
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 *  [columnSet][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
 *
 *  &nbsp;&nbsp;&nbsp;&nbsp;.[**{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}**][ColumnsSelectionDsl.{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}]`(`[**Last**][ColumnSet.{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}Last]`)`**`(`**[number][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
 *
 *  &nbsp;&nbsp;&nbsp;&nbsp;`|` .[**{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}**][ColumnsSelectionDsl.{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}While]`(`[**Last**][ColumnsSelectionDsl.{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}LastWhile]`)`[**While**][ColumnsSelectionDsl.{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}While]**` { `**[condition][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` \
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 *  ### What can be called on a [Column Group (reference)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]:
 *
 *  
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 *  [columnGroup][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
 *
 *  &nbsp;&nbsp;&nbsp;&nbsp;.[**{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}**][ColumnsSelectionDsl.{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}Cols]`(`[**Last**][ColumnsSelectionDsl.{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}LastCols]`)`[**Cols**][ColumnsSelectionDsl.{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}Cols]**`(`**[number][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
 *
 *  &nbsp;&nbsp;&nbsp;&nbsp;`|` .[**{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}**][ColumnsSelectionDsl.{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}ColsWhile]`(`[**Last**][ColumnsSelectionDsl.{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}LastColsWhile]`)`[**ColsWhile**][ColumnsSelectionDsl.{@getArg [OperationArg][org.jetbrains.kotlinx.dataframe.documentation.TakeAndDropColumnsSelectionDslGrammar.OperationArg]}ColsWhile]**` { `**[condition][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` \
 *
 *
 *
 *
 *
 *
 *
 *
 * `**
 * }
 *
 * `**
 * }
 *
 * `**
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
