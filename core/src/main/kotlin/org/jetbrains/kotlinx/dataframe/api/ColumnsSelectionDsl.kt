package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.DataFrameReceiver
import org.jetbrains.kotlinx.dataframe.impl.columns.ColumnsList
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_LIST_DATACOLUMN_GET
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_LIST_DATACOLUMN_GET_REPLACE
import kotlin.experimental.ExperimentalTypeInference
import kotlin.reflect.KProperty

/** [Columns Selection DSL][ColumnsSelectionDsl] */
internal interface ColumnsSelectionDslLink

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <T> ColumnsSelectionDsl<T>.asSingleColumn(): SingleColumn<DataRow<T>> = this as SingleColumn<DataRow<T>>

/**
 * [DslMarker] for [ColumnsSelectionDsl] to prevent accessors being used across scopes for nested
 * [ColumnsSelectionDsl.select] calls.
 */
@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
public annotation class ColumnsSelectionDslMarker

/**
 * ## Columns Selection DSL
 * {@include [SelectingColumns.Dsl.WithExample]}
 * {@set [SelectingColumns.OperationArg] [select][DataFrame.select]}
 *
 * @comment This interface be safely cast to [SingleColumn] across the library because it's always
 * implemented in combination with [DataFrameReceiver] which is a [SingleColumn] itself.
 * It does not directly implement [SingleColumn] for DSL purposes.
 */
@ColumnsSelectionDslMarker
public interface ColumnsSelectionDsl<out T> : /* SingleColumn<DataRow<T>> */
    ColumnSelectionDsl<T>,

    // first {}, firstCol()
    FirstColumnsSelectionDsl,
    // last {}, lastCol()
    LastColumnsSelectionDsl,
    // single {}, singleCol()
    SingleColumnsSelectionDsl,

    // col(name), col(5), [5]
    ColColumnsSelectionDsl,
    // valueCol(name), valueCol(5)
    ValueColColumnsSelectionDsl,
    // frameCol(name), frameCol(5)
    FrameColColumnsSelectionDsl,
    // colGroup(name), colGroup(5)
    ColGroupColumnsSelectionDsl,

    // cols {}, cols(), cols(colA, colB), cols(1, 5), cols(1..5), [{}]
    ColsColumnsSelectionDsl,

    // colA.."colB"
    ColumnRangeColumnsSelectionDsl,

    // valueCols {}, valueCols()
    ValueColsColumnsSelectionDsl,
    // frameCols {}, frameCols()
    FrameColsColumnsSelectionDsl,
    // colGroups {}, colGroups()
    ColGroupsColumnsSelectionDsl,
    // colsOfKind(Value, Frame) {}, colsOfKind(Value, Frame)
    ColsOfKindColumnsSelectionDsl,

    // all(Cols), allAfter(colA), allBefore(colA), allFrom(colA), allUpTo(colA)
    AllColumnsSelectionDsl,
    // colsAtAnyDepth {}, colsAtAnyDepth()
    ColsAtAnyDepthColumnsSelectionDsl,
    // colsInGroups {}, colsInGroups()
    ColsInGroupsColumnsSelectionDsl,
    // take(5), takeLastCols(2), takeLastWhile {}, takeColsWhile {}
    TakeColumnsSelectionDsl,
    // drop(5), dropLastCols(2), dropLastWhile {}, dropColsWhile {}
    DropColumnsSelectionDsl,

    // select {}, TODO due to String.invoke conflict this cannot be moved out of ColumnsSelectionDsl
    SelectColumnsSelectionDsl,
    // except(), allExcept {}, allColsExcept {}
    AllExceptColumnsSelectionDsl,

    // nameContains(""), colsNameContains(""), nameStartsWith(""), childrenNameEndsWith("")
    ColumnNameFiltersColumnsSelectionDsl,
    // withoutNulls(), colsWithoutNulls()
    WithoutNullsColumnsSelectionDsl,
    // distinct()
    DistinctColumnsSelectionDsl,
    // none()
    NoneColumnsSelectionDsl,
    // colsOf<>(), colsOf<> {}
    ColsOfColumnsSelectionDsl,
    // simplify()
    SimplifyColumnsSelectionDsl,
    // filter {}
    FilterColumnsSelectionDsl,
    // colSet and colB
    AndColumnsSelectionDsl,
    // colA named "colB", colA into "colB"
    RenameColumnsSelectionDsl,
    // expr {}
    ExprColumnsSelectionDsl {

    /**
     * ## {@include [ColumnsSelectionDslLink]} Grammar
     *
     * @include [DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate]
     *
     * {@set [DslGrammarTemplate.DefinitionsArg]
     *  {@include [DslGrammarTemplate.ColumnGroupNoSingleColumnDef]}
     *
     *  {@include [DslGrammarTemplate.ColumnSelectorDef]}
     *
     *  {@include [DslGrammarTemplate.ColumnsSelectorDef]}
     *
     *  {@include [DslGrammarTemplate.ColumnDef]}
     *
     *  {@include [DslGrammarTemplate.ColumnGroupDef]}
     *
     *  {@include [DslGrammarTemplate.ColumnNoAccessorDef]}
     *
     *  {@include [DslGrammarTemplate.ColumnOrColumnSetDef]}
     *
     *  {@include [DslGrammarTemplate.ColumnSetDef]}
     *
     *  {@include [DslGrammarTemplate.ColumnsResolverDef]}
     *
     *  {@include [DslGrammarTemplate.ConditionDef]}
     *
     *  {@include [DslGrammarTemplate.ColumnExpressionDef]}
     *
     *  {@include [DslGrammarTemplate.IgnoreCaseDef]}
     *
     *  {@include [DslGrammarTemplate.IndexDef]}
     *
     *  {@include [DslGrammarTemplate.IndexRangeDef]}
     *
     *  {@include [DslGrammarTemplate.InferDef]}
     *
     *  {@include [DslGrammarTemplate.ColumnKindDef]}
     *
     *  {@include [DslGrammarTemplate.KTypeDef]}
     *
     *  {@include [DslGrammarTemplate.NameDef]}
     *
     *  {@include [DslGrammarTemplate.NumberDef]}
     *
     *  {@include [DslGrammarTemplate.RegexDef]}
     *
     *  {@include [DslGrammarTemplate.SingleColumnDef]}
     *
     *  {@include [DslGrammarTemplate.ColumnTypeDef]}
     *
     *  {@include [DslGrammarTemplate.TextDef]}
     * }
     * {@comment Plain DSL: -------------------------------------------------------------------------------------------- }
     * {@set [DslGrammarTemplate.PlainDslFunctionsArg]
     *
     * {@include [DslGrammarTemplate.ColumnRef]} {@include [ColumnRangeColumnsSelectionDsl.Grammar.PlainDslName]} {@include [DslGrammarTemplate.ColumnRef]}
     *
     *  `|` **`this`**`/`**`it`**[**`[`**][cols]{@include [DslGrammarTemplate.ColumnRef]}**`,`**` .. `[**`]`**][cols]
     *
     *  `|` **`this`**`/`**`it`**[**`[`**][cols]**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**[**`]`**][cols]
     *
     *  `|` {@include [AllColumnsSelectionDsl.Grammar.PlainDslName]}**`()`**
     *
     *  `|` **`all`**`(`{@include [AllColumnsSelectionDsl.Grammar.Before]}`|`{@include [AllColumnsSelectionDsl.Grammar.After]}`|`{@include [AllColumnsSelectionDsl.Grammar.From]}`|`{@include [AllColumnsSelectionDsl.Grammar.UpTo]}`)` `(` **`(`**{@include [DslGrammarTemplate.ColumnRef]}**`)`** `|` **`{`** {@include [DslGrammarTemplate.ColumnSelectorRef]} **`\}`** `)`
     *
     *  `|` {@include [AllExceptColumnsSelectionDsl.Grammar.PlainDslName]} **`{ `**{@include [DslGrammarTemplate.ColumnsSelectorRef]}**` \}`**
     *
     *  `|` {@include [AllExceptColumnsSelectionDsl.Grammar.PlainDslName]}**`(`**{@include [DslGrammarTemplate.ColumnRef]}**`,`**` ..`**`)`**
     *
     *  `|` {@include [DslGrammarTemplate.ColumnOrColumnSetRef]} {@include [AndColumnsSelectionDsl.Grammar.InfixName]}` [ `**`{`**` ] `{@include [DslGrammarTemplate.ColumnOrColumnSetRef]}` [ `**`\}`**` ] `
     *
     *  `|` {@include [DslGrammarTemplate.ColumnOrColumnSetRef]}{@include [AndColumnsSelectionDsl.Grammar.Name]} **`(`**`|`**`{ `**{@include [DslGrammarTemplate.ColumnOrColumnSetRef]}**` \}`**`|`**`)`**
     *
     *  `|` `(`
     *  {@include [ColColumnsSelectionDsl.Grammar.PlainDslName]}
     *  `|` {@include [ValueColColumnsSelectionDsl.Grammar.PlainDslName]}
     *  `|` {@include [FrameColColumnsSelectionDsl.Grammar.PlainDslName]}
     *  `|` {@include [ColGroupColumnsSelectionDsl.Grammar.PlainDslName]}
     *  `)[`**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>`**`]`**`(`**{@include [DslGrammarTemplate.ColumnRef]}` | `{@include [DslGrammarTemplate.IndexRef]}**`)`**
     *
     * `|` `(`
     *  {@include [ColsColumnsSelectionDsl.Grammar.PlainDslName]}
     *  `|` {@include [ValueColsColumnsSelectionDsl.Grammar.PlainDslName]}
     *  `|` {@include [FrameColsColumnsSelectionDsl.Grammar.PlainDslName]}
     *  `|` {@include [ColGroupsColumnsSelectionDsl.Grammar.PlainDslName]}
     *  `) [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  `|` {@include [ColsColumnsSelectionDsl.Grammar.PlainDslName]}`[`**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>`**`]`**`(`**{@include [DslGrammarTemplate.ColumnRef]}**`,`**` .. | `{@include [DslGrammarTemplate.IndexRef]}**`,`**` .. | `{@include [DslGrammarTemplate.IndexRangeRef]}**`)`**
     *
     *  `|` {@include [ColsAtAnyDepthColumnsSelectionDsl.Grammar.PlainDslName]}` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  `|` {@include [ColsInGroupsColumnsSelectionDsl.Grammar.PlainDslName]}` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]
     *
     *  `|` {@include [ColsOfColumnsSelectionDsl.Grammar.PlainDslName]}**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>`**` [` **`(`**{@include [DslGrammarTemplate.KTypeRef]}**`)`** `] [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  `|` {@include [ColsOfKindColumnsSelectionDsl.Grammar.PlainDslName]}**`(`**{@include [DslGrammarTemplate.ColumnKindRef]}**`,`**` ..`**`)`**` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  `|` {@include [DropColumnsSelectionDsl.Grammar.PlainDslName]}**`(`**{@include [DslGrammarTemplate.NumberRef]}**`)`**
     *
     *  `|` {@include [DropColumnsSelectionDsl.Grammar.PlainDslWhileName]}**` { `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**
     *
     *  `|` {@include [ExprColumnsSelectionDsl.Grammar.PlainDslName]}**`(`**`[`{@include [DslGrammarTemplate.NameRef]}**`,`**`][`{@include [DslGrammarTemplate.InferRef]}`]`**`)`** **`{ `**{@include [DslGrammarTemplate.ColumnExpressionRef]}**` \}`**
     *
     *  `|` `(`
     *  {@include [FirstColumnsSelectionDsl.Grammar.PlainDslName]}
     *  `|` {@include [LastColumnsSelectionDsl.Grammar.PlainDslName]}
     *  `|` {@include [SingleColumnsSelectionDsl.Grammar.PlainDslName]}
     *  `) [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  `|` {@include [ColumnNameFiltersColumnsSelectionDsl.Grammar.PlainDslNameContains]}**`(`**{@include [DslGrammarTemplate.TextRef]}`[`**`,`** {@include [DslGrammarTemplate.IgnoreCaseRef]}`] | `{@include [DslGrammarTemplate.RegexRef]}**`)`**
     *
     *  `|` {@include [ColumnNameFiltersColumnsSelectionDsl.Grammar.PlainDslNameStartsEndsWith]}**`(`**{@include [DslGrammarTemplate.TextRef]}`[`**`,`** {@include [DslGrammarTemplate.IgnoreCaseRef]}`]`**`)`**
     *
     *  `|` {@include [DslGrammarTemplate.ColumnRef]} {@include [RenameColumnsSelectionDsl.Grammar.InfixNamedName]}`/`{@include [RenameColumnsSelectionDsl.Grammar.InfixIntoName]} {@include [DslGrammarTemplate.ColumnRef]}
     *
     *  `|` {@include [DslGrammarTemplate.ColumnRef]}`(`{@include [RenameColumnsSelectionDsl.Grammar.NamedName]}`|`{@include [RenameColumnsSelectionDsl.Grammar.IntoName]}`)`**`(`**{@include [DslGrammarTemplate.ColumnRef]}**`)`**
     *
     *  `|` {@include [NoneColumnsSelectionDsl.Grammar.PlainDslName]}**`()`**
     *
     *  `|` {@include [TakeColumnsSelectionDsl.Grammar.PlainDslName]}**`(`**{@include [DslGrammarTemplate.NumberRef]}**`)`**
     *
     *  `|` {@include [TakeColumnsSelectionDsl.Grammar.PlainDslWhileName]}**` { `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**
     *
     *  `|` {@include [WithoutNullsColumnsSelectionDsl.Grammar.PlainDslName]}**`()`**
     * }
     * {@comment ColumnSet: -------------------------------------------------------------------------------------------- }
     * {@set [DslGrammarTemplate.ColumnSetFunctionsArg]
     *
     *  {@include [Indent]}[**`[`**][ColumnsSelectionDsl.col]{@include [DslGrammarTemplate.IndexRef]}[**`]`**][ColumnsSelectionDsl.col]
     *
     *  {@include [Indent]}`|` [**`[`**][cols]{@include [DslGrammarTemplate.IndexRef]}**`,`**` .. | `{@include [DslGrammarTemplate.IndexRangeRef]}[**`]`**][cols]`
     *
     *  {@include [Indent]}`|` [**`[`**][cols]**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**[**`]`**][cols]
     *
     *  {@include [Indent]}`|` {@include [AllColumnsSelectionDsl.Grammar.ColumnSetName]}**`()`**
     *
     *  {@include [Indent]}`|` .**`all`**`(`{@include [AllColumnsSelectionDsl.Grammar.Before]}`|`{@include [AllColumnsSelectionDsl.Grammar.After]}`|`{@include [AllColumnsSelectionDsl.Grammar.From]}`|`{@include [AllColumnsSelectionDsl.Grammar.UpTo]}`)` `(` **`(`**{@include [DslGrammarTemplate.ColumnRef]}**`)`** `|` **`{`** {@include [DslGrammarTemplate.ConditionRef]} **`\}`** `)`
     *
     *  {@include [Indent]}`|` {@include [AndColumnsSelectionDsl.Grammar.Name]} **`(`**`|`**`{ `**{@include [DslGrammarTemplate.ColumnOrColumnSetRef]}**` \}`**`|`**`)`**
     *
     *  {@include [Indent]}`|` `(`
     *  {@include [ColColumnsSelectionDsl.Grammar.ColumnSetName]}
     *  `|` {@include [ValueColColumnsSelectionDsl.Grammar.ColumnSetName]}
     *  `|` {@include [FrameColColumnsSelectionDsl.Grammar.ColumnSetName]}
     *  `|` {@include [ColGroupColumnsSelectionDsl.Grammar.ColumnSetName]}
     *  `)`**`(`**{@include [DslGrammarTemplate.IndexRef]}**`)`**
     *
     *  {@include [Indent]}`|` `(`
     *  {@include [ColsColumnsSelectionDsl.Grammar.ColumnSetName]}
     *  `|` {@include [ValueColsColumnsSelectionDsl.Grammar.ColumnSetName]}
     *  `|` {@include [FrameColsColumnsSelectionDsl.Grammar.ColumnSetName]}
     *  `|` {@include [ColGroupsColumnsSelectionDsl.Grammar.ColumnSetName]}
     *  `) [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  {@include [Indent]}`|` {@include [ColsColumnsSelectionDsl.Grammar.ColumnSetName]}**`(`**{@include [DslGrammarTemplate.IndexRef]}**`,`**` .. | `{@include [DslGrammarTemplate.IndexRangeRef]}**`)`**
     *
     *  {@include [Indent]}`|` {@include [ColsAtAnyDepthColumnsSelectionDsl.Grammar.ColumnSetName]}` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  {@include [Indent]}`|` {@include [ColsInGroupsColumnsSelectionDsl.Grammar.ColumnSetName]}` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  {@include [Indent]}`|` {@include [ColsOfColumnsSelectionDsl.Grammar.ColumnSetName]}**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>`**` [` **`(`**{@include [DslGrammarTemplate.KTypeRef]}**`)`** `] [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  {@include [Indent]}`|` {@include [ColsOfKindColumnsSelectionDsl.Grammar.ColumnSetName]}**`(`**{@include [DslGrammarTemplate.ColumnKindRef]}**`,`**` ..`**`)`**` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  {@include [Indent]}`|` {@include [DistinctColumnsSelectionDsl.Grammar.ColumnSetName]}**`()`**
     *
     *  {@include [Indent]}`|` {@include [DropColumnsSelectionDsl.Grammar.ColumnSetName]}**`(`**{@include [DslGrammarTemplate.NumberRef]}**`)`**
     *
     *  {@include [Indent]}`|` {@include [DropColumnsSelectionDsl.Grammar.ColumnSetWhileName]}**` { `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**
     *
     *  {@include [Indent]}`|` {@include [AllExceptColumnsSelectionDsl.Grammar.ColumnSetName]} `[`**` { `**`]` {@include [DslGrammarTemplate.ColumnsResolverRef]} `[`**` \} `**`]`
     *
     *  {@include [Indent]}`|` {@include [AllExceptColumnsSelectionDsl.Grammar.ColumnSetName]} {@include [DslGrammarTemplate.ColumnRef]}
     *
     *  {@include [Indent]}`|` .{@include [AllExceptColumnsSelectionDsl.Grammar.ColumnSetName]}**`(`**{@include [DslGrammarTemplate.ColumnRef]}**`,`**` ..`**`)`**
     *
     *  {@include [Indent]}`|` {@include [FilterColumnsSelectionDsl.Grammar.ColumnSetName]}**` {`** {@include [DslGrammarTemplate.ConditionRef]} **`\}`**
     *
     *  {@include [Indent]}`|` `(`
     *  {@include [FirstColumnsSelectionDsl.Grammar.ColumnSetName]}
     *  `|` {@include [LastColumnsSelectionDsl.Grammar.ColumnSetName]}
     *  `|` {@include [SingleColumnsSelectionDsl.Grammar.ColumnSetName]}
     *  `) [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  {@include [Indent]}`|` {@include [ColumnNameFiltersColumnsSelectionDsl.Grammar.ColumnSetNameStartsEndsWith]}**`(`**{@include [DslGrammarTemplate.TextRef]}`[`**`,`** {@include [DslGrammarTemplate.IgnoreCaseRef]}`]`**`)`**
     *
     *  {@include [Indent]}`|` {@include [ColumnNameFiltersColumnsSelectionDsl.Grammar.ColumnSetNameContains]}**`(`**{@include [DslGrammarTemplate.TextRef]}`[`**`,`** {@include [DslGrammarTemplate.IgnoreCaseRef]}`] | `{@include [DslGrammarTemplate.RegexRef]}**`)`**
     *
     *  {@include [Indent]}`|` {@include [SimplifyColumnsSelectionDsl.Grammar.ColumnSetName]}**`()`**
     *
     *  {@include [Indent]}`|` {@include [TakeColumnsSelectionDsl.Grammar.ColumnSetName]}**`(`**{@include [DslGrammarTemplate.NumberRef]}**`)`**
     *
     *  {@include [Indent]}`|` {@include [TakeColumnsSelectionDsl.Grammar.ColumnSetWhileName]}**` { `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**
     *
     *  {@include [Indent]}`|` {@include [WithoutNullsColumnsSelectionDsl.Grammar.ColumnSetName]}**`()`**
     * }
     * {@comment ColumnGroup: -------------------------------------------------------------------------------------------- }
     * {@set [DslGrammarTemplate.ColumnGroupFunctionsArg]
     *
     *  {@include [Indent]}`|` [**`[`**][cols]{@include [DslGrammarTemplate.ColumnRef]}**`,`**` ..`[**`]`**][cols]
     *
     *  {@include [Indent]}`|` [**`[`**][cols]**`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**[**`]`**][cols]
     *
     *  {@include [Indent]}`|`[**` {`**][ColumnsSelectionDsl.select] {@include [DslGrammarTemplate.ColumnsSelectorRef]} [**`\}`**][ColumnsSelectionDsl.select]
     *
     *  {@include [Indent]}`|` {@include [AllColumnsSelectionDsl.Grammar.ColumnGroupName]}**`()`**
     *
     *  {@include [Indent]}`|` .**`allCols`**`(`{@include [AllColumnsSelectionDsl.Grammar.Before]}`|`{@include [AllColumnsSelectionDsl.Grammar.After]}`|`{@include [AllColumnsSelectionDsl.Grammar.From]}`|`{@include [AllColumnsSelectionDsl.Grammar.UpTo]}`)` `(` **`(`**{@include [DslGrammarTemplate.ColumnRef]}**`)`** `|` **`{`** {@include [DslGrammarTemplate.ColumnSelectorRef]} **`\}`** `)`
     *
     *  {@include [Indent]}`|` {@include [AllExceptColumnsSelectionDsl.Grammar.ColumnGroupName]} **` { `**{@include [DslGrammarTemplate.ColumnsSelectorRef]}**` \} `**
     *
     *  {@include [Indent]}`|` {@include [AllExceptColumnsSelectionDsl.Grammar.ColumnGroupName]}**`(`**{@include [DslGrammarTemplate.ColumnNoAccessorRef]}**`,`**` ..`**`)`**
     *
     *  {@include [Indent]}`|` {@include [AndColumnsSelectionDsl.Grammar.Name]} **`(`**`|`**`{ `**{@include [DslGrammarTemplate.ColumnOrColumnSetRef]}**` \}`**`|`**`)`**
     *
     *  {@include [Indent]}`| (`
     *  {@include [ColColumnsSelectionDsl.Grammar.ColumnGroupName]}
     *  `|` {@include [ValueColColumnsSelectionDsl.Grammar.ColumnGroupName]}
     *  `|` {@include [FrameColColumnsSelectionDsl.Grammar.ColumnGroupName]}
     *  `|` {@include [ColGroupColumnsSelectionDsl.Grammar.ColumnGroupName]}
     *  `)[`**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>`**`]`**`(`**{@include [DslGrammarTemplate.ColumnRef]}` | `{@include [DslGrammarTemplate.IndexRef]}**`)`**
     *
     *  {@include [Indent]}`|` `(`
     *   {@include [ColsColumnsSelectionDsl.Grammar.ColumnGroupName]}
     *   `|` {@include [ValueColsColumnsSelectionDsl.Grammar.ColumnGroupName]}
     *   `|` {@include [FrameColsColumnsSelectionDsl.Grammar.ColumnGroupName]}
     *   `|` {@include [ColGroupsColumnsSelectionDsl.Grammar.ColumnGroupName]}
     *   `) [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  {@include [Indent]}`|` {@include [ColsColumnsSelectionDsl.Grammar.ColumnGroupName]}`[`**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>`**`]`**`(`**{@include [DslGrammarTemplate.ColumnRef]}**`,`**` .. | `{@include [DslGrammarTemplate.IndexRef]}**`,`**` .. | `{@include [DslGrammarTemplate.IndexRangeRef]}**`)`**
     *
     *  {@include [Indent]}`|` {@include [ColsAtAnyDepthColumnsSelectionDsl.Grammar.ColumnGroupName]}` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  {@include [Indent]}`|` {@include [ColsInGroupsColumnsSelectionDsl.Grammar.ColumnGroupName]}` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  {@include [Indent]}`|` {@include [ColumnNameFiltersColumnsSelectionDsl.Grammar.ColumnGroupNameStartsWith]}**`(`**{@include [DslGrammarTemplate.TextRef]}`[`**`,`** {@include [DslGrammarTemplate.IgnoreCaseRef]}`]`**`)`**
     *
     *  {@include [Indent]}`|` {@include [ColumnNameFiltersColumnsSelectionDsl.Grammar.ColumnGroupNameContains]}**`(`**{@include [DslGrammarTemplate.TextRef]}`[`**`,`** {@include [DslGrammarTemplate.IgnoreCaseRef]}`] | `{@include [DslGrammarTemplate.RegexRef]}**`)`**
     *
     *  {@include [Indent]}`|` {@include [ColsOfKindColumnsSelectionDsl.Grammar.ColumnGroupName]}**`(`**{@include [DslGrammarTemplate.ColumnKindRef]}**`,`**` ..`**`)`**` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  {@include [Indent]}`|` {@include [WithoutNullsColumnsSelectionDsl.Grammar.ColumnGroupName]}**`()`**
     *
     *  {@include [Indent]}`|` {@include [DropColumnsSelectionDsl.Grammar.ColumnGroupName]}**`(`**{@include [DslGrammarTemplate.NumberRef]}**`)`**
     *
     *  {@include [Indent]}`|` {@include [DropColumnsSelectionDsl.Grammar.ColumnGroupWhileName]}**` { `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**
     *
     *  {@include [Indent]}`|` {@include [AllExceptColumnsSelectionDsl.Grammar.ColumnGroupExperimentalName]} **` { `**{@include [DslGrammarTemplate.ColumnsSelectorRef]}**` \} EXPERIMENTAL!`**
     *
     *  {@include [Indent]}`|` {@include [AllExceptColumnsSelectionDsl.Grammar.ColumnGroupExperimentalName]}**`(`**{@include [DslGrammarTemplate.ColumnNoAccessorRef]}**`,`**` ..`**`) EXPERIMENTAL!`**
     *
     *  {@include [Indent]}`|` `(`
     *  {@include [FirstColumnsSelectionDsl.Grammar.ColumnGroupName]}
     *  `|` {@include [LastColumnsSelectionDsl.Grammar.ColumnGroupName]}
     *  `|` {@include [SingleColumnsSelectionDsl.Grammar.ColumnGroupName]}
     *  `) [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  {@include [Indent]}`|` {@include [SelectColumnsSelectionDsl.Grammar.ColumnGroupName]}**` {`** {@include [DslGrammarTemplate.ColumnsSelectorRef]} **`\}`**
     *
     *  {@include [Indent]}`|` {@include [TakeColumnsSelectionDsl.Grammar.ColumnGroupName]}**`(`**{@include [DslGrammarTemplate.NumberRef]}**`)`**
     *
     *  {@include [Indent]}`|` {@include [TakeColumnsSelectionDsl.Grammar.ColumnGroupWhileName]}**` { `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`**
     *
     *  {@include [LineBreak]}
     *
     *  {@include [DslGrammarTemplate.SingleColumnRef]}
     *
     *  {@include [Indent]}{@include [ColsOfColumnsSelectionDsl.Grammar.ColumnGroupName]}**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>`**` [` **`(`**{@include [DslGrammarTemplate.KTypeRef]}**`)`** `] [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     *
     *  {@include [LineBreak]}
     *
     *  {@include [DslGrammarTemplate.ColumnGroupNoSingleColumnRef]}
     *
     *  {@include [Indent]}{@include [ColsOfColumnsSelectionDsl.Grammar.ColumnGroupName]}**`<`**{@include [DslGrammarTemplate.ColumnTypeRef]}**`>(`**{@include [DslGrammarTemplate.KTypeRef]}**`)`** ` [` **`{ `**{@include [DslGrammarTemplate.ConditionRef]}**` \}`** `]`
     * }
     */
    public interface DslGrammar

    /**
     * Invokes the given [ColumnsSelector] using this [ColumnsSelectionDsl].
     */
    public operator fun <C> ColumnsSelector<T, C>.invoke(): ColumnsResolver<C> =
        this@invoke(this@ColumnsSelectionDsl, this@ColumnsSelectionDsl)

    /**
     * ## Deprecated: Columns by Index Range from List of Columns
     * Helper function to create a [ColumnSet] from a list of columns by specifying a range of indices.
     *
     * ### Deprecated
     *
     * Deprecated because it's too niche. Let us know if you have a good use for it!
     */
    @Deprecated(
        message = COL_SELECT_DSL_LIST_DATACOLUMN_GET,
        replaceWith = ReplaceWith(COL_SELECT_DSL_LIST_DATACOLUMN_GET_REPLACE),
        level = DeprecationLevel.ERROR,
    )
    public operator fun <C> List<DataColumn<C>>.get(range: IntRange): ColumnSet<C> =
        ColumnsList(subList(range.first, range.last + 1))

    // region select
    // NOTE: due to invoke conflicts these cannot be moved out of the interface

    /**
     * @include [SelectColumnsSelectionDsl.CommonSelectDocs]
     * @set [SelectColumnsSelectionDsl.CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[select][SingleColumn.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { myColGroup `[{][SingleColumn.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][SingleColumn.select]` }`
     */
    public operator fun <C, R> SingleColumn<DataRow<C>>.invoke(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        select(selector)

    /**
     * @include [SelectColumnsSelectionDsl.CommonSelectDocs]
     * @set [SelectColumnsSelectionDsl.CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[select][KProperty.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup `[`{`][KProperty.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[`}`][KProperty.select]` }`
     *
     * ## NOTE: {@comment TODO fix warning}
     * If you get a warning `CANDIDATE_CHOSEN_USING_OVERLOAD_RESOLUTION_BY_LAMBDA_ANNOTATION`, you
     * can safely ignore this. It is caused by a workaround for a bug in the Kotlin compiler
     * ([KT-64092](https://youtrack.jetbrains.com/issue/KT-64092/OVERLOADRESOLUTIONAMBIGUITY-caused-by-lambda-argument)).
     */
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowInvoke")
    public operator fun <C, R> KProperty<DataRow<C>>.invoke(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        select(selector)

    /**
     * @include [SelectColumnsSelectionDsl.CommonSelectDocs]
     * @set [SelectColumnsSelectionDsl.CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { Type::myColGroup.`[select][KProperty.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { DataSchemaType::myColGroup `[`{`][KProperty.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[`}`][KProperty.select]` }`
     */
    @OptIn(ExperimentalTypeInference::class)
    @OverloadResolutionByLambdaReturnType
    public operator fun <C, R> KProperty<C>.invoke(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /**
     * @include [SelectColumnsSelectionDsl.CommonSelectDocs]
     * @set [SelectColumnsSelectionDsl.CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "myColGroup".`[select][String.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "myColGroup" `[{][String.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][String.select]` }`
     */
    public operator fun <R> String.invoke(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        select(selector)

    /**
     * @include [SelectColumnsSelectionDsl.CommonSelectDocs]
     * @set [SelectColumnsSelectionDsl.CommonSelectDocs.ExampleArg]
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"].`[select][ColumnPath.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { "pathTo"["myColGroup"] `[{][ColumnPath.select]` colA `[and][ColumnsSelectionDsl.and]` colB `[}][ColumnPath.select]` }`
     *
     * `df.`[select][DataFrame.select]` { `[pathOf][pathOf]`("pathTo", "myColGroup").`[select][ColumnPath.select]` { someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() } }`
     *
     * `df.`[select][DataFrame.select]` { `[pathOf][pathOf]`("pathTo", "myColGroup")`[() {][ColumnPath.select]` someCol `[and][ColumnsSelectionDsl.and]` `[colsOf][SingleColumn.colsOf]`<`[String][String]`>() `[}][ColumnPath.select]` }`
     */
    public operator fun <R> ColumnPath.invoke(selector: ColumnsSelector<*, R>): ColumnSet<R> =
        select(selector)

    // endregion
}
