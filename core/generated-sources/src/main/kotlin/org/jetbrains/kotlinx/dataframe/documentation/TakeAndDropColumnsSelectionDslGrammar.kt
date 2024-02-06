package org.jetbrains.kotlinx.dataframe.documentation

import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate

/**
 * ##  (Last) (Cols) (While) Grammar
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
 *  [****][ColumnsSelectionDsl.]`(`[**Last**][ColumnsSelectionDsl.Last]`)`**`(`**[number][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
 *
 *  `|` [****][ColumnsSelectionDsl.While]`(`[**Last**][ColumnsSelectionDsl.LastWhile]`)`[**While**][ColumnsSelectionDsl.While]**` { `**[condition][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
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
 *  &nbsp;&nbsp;&nbsp;&nbsp;.[****][ColumnsSelectionDsl.]`(`[**Last**][ColumnSet.Last]`)`**`(`**[number][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
 *
 *  &nbsp;&nbsp;&nbsp;&nbsp;`|` .[****][ColumnsSelectionDsl.While]`(`[**Last**][ColumnsSelectionDsl.LastWhile]`)`[**While**][ColumnsSelectionDsl.While]**` { `**[condition][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
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
 *  &nbsp;&nbsp;&nbsp;&nbsp;.[****][ColumnsSelectionDsl.Cols]`(`[**Last**][ColumnsSelectionDsl.LastCols]`)`[**Cols**][ColumnsSelectionDsl.Cols]**`(`**[number][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
 *
 *  &nbsp;&nbsp;&nbsp;&nbsp;`|` .[****][ColumnsSelectionDsl.ColsWhile]`(`[**Last**][ColumnsSelectionDsl.LastColsWhile]`)`[**ColsWhile**][ColumnsSelectionDsl.ColsWhile]**` { `**[condition][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
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
internal interface TakeAndDropColumnsSelectionDslGrammar {

    /* Like "Take"/"Drop" */
    interface TitleArg

    /* Operation, like "take"/"drop" */
    interface OperationArg

    /** [****][ColumnsSelectionDsl.]`(`[**Last**][ColumnsSelectionDsl.Last]`)` */
    interface PlainDslName

    /** .[****][ColumnsSelectionDsl.]`(`[**Last**][ColumnSet.Last]`)` */
    interface ColumnSetName

    /** .[****][ColumnsSelectionDsl.Cols]`(`[**Last**][ColumnsSelectionDsl.LastCols]`)`[**Cols**][ColumnsSelectionDsl.Cols] */
    interface ColumnGroupName

    /** [****][ColumnsSelectionDsl.While]`(`[**Last**][ColumnsSelectionDsl.LastWhile]`)`[**While**][ColumnsSelectionDsl.While] */
    interface PlainDslWhileName

    /** .[****][ColumnsSelectionDsl.While]`(`[**Last**][ColumnsSelectionDsl.LastWhile]`)`[**While**][ColumnsSelectionDsl.While] */
    interface ColumnSetWhileName

    /** .[****][ColumnsSelectionDsl.ColsWhile]`(`[**Last**][ColumnsSelectionDsl.LastColsWhile]`)`[**ColsWhile**][ColumnsSelectionDsl.ColsWhile] */
    interface ColumnGroupWhileName
}
