package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.ExportAsHtml
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.documentation.SelectingColumns
import org.jetbrains.kotlinx.dataframe.impl.DataFrameReceiver
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import kotlin.reflect.KProperty

/** [<code>Columns Selection DSL</code>][ColumnsSelectionDsl] */
internal typealias ColumnsSelectionDslLink = Nothing

@Suppress("UNCHECKED_CAST")
@PublishedApi
internal fun <T> ColumnsSelectionDsl<T>.asSingleColumn(): SingleColumn<DataRow<T>> = this as SingleColumn<DataRow<T>>

/**
 * [<code>DslMarker</code>][DslMarker] for [<code>ColumnsSelectionDsl</code>][ColumnsSelectionDsl] to prevent accessors being used across scopes for nested
 * [<code>ColumnsSelectionDsl.select</code>][ColumnsSelectionDsl.select] calls.
 */
@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS, AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
public annotation class ColumnsSelectionDslMarker

/**
 * ## Columns Selection DSL
 *
 *
 *
 *
 * Select or express columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
 *
 * This DSL is initiated by a [<code>Columns Selector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] lambda,
 * which operates in the context of the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] and
 * expects you to return a [<code>SingleColumn</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn] or [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] (so, a [<code>ColumnsResolver</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]).
 * This is an entity formed by calling any (combination) of the functions
 * in the DSL that is or can be resolved into one or more columns.
 *
 * The Columns Selection DSL allows using [<code>Extension Properties</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi]
 * for specifying columns type- and name-safe.
 *
 * Check out: [<code>Columns Selection DSL Grammar</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.DslGrammar]
 *
 * &nbsp;&nbsp;&nbsp;&nbsp;
 *
 * [See Column Selectors on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html)
 *
 * #### For example:
 *
 * <code>`df`</code>`.`[<code>select</code>][DataFrame.select]` { length `[<code>and</code>][ColumnsSelectionDsl.and]` age }`
 *
 * <code>`df`</code>`.`[<code>select</code>][DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(1..5) }`
 *
 * <code>`df`</code>`.`[<code>select</code>][DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Double</code>][Double]`>() }`
 *
 *
 *
 *
 *
 */
@ColumnsSelectionDslMarker
public interface ColumnsSelectionDsl<out T> : // SingleColumn<DataRow<T>>
    ColumnSelectionDsl<T>,
    // first {}, firstCol()
    FirstColumnsSelectionDsl,
    // last {}, lastCol()
    LastColumnsSelectionDsl,
    // single {}, singleCol()
    SingleColumnsSelectionDsl,
    // col(name), col(5), [5]
    ColColumnsSelectionDsl<T>,
    // valueCol(name), valueCol(5)
    ValueColColumnsSelectionDsl<T>,
    // frameCol(name), frameCol(5)
    FrameColColumnsSelectionDsl<T>,
    // colGroup(name), colGroup(5)
    ColGroupColumnsSelectionDsl<T>,
    // cols {}, cols(), cols(colA, colB), cols(1, 5), cols(1..5), [{}]
    ColsColumnsSelectionDsl<T>,
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
    AllColumnsSelectionDsl<T>,
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
    // nameContains(""), colsNameContains(""), nameStartsWith(""), colsNameEndsWith("")
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
     * ## [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [<code>(What is this notation?)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnGroupReference: `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * `colSelector: `[<code>`ColumnSelector`</code>][org.jetbrains.kotlinx.dataframe.ColumnSelector]
     *
     * `colsSelector: `[<code>`ColumnsSelector`</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector]
     *
     * `column: `[<code>`ColumnAccessor`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * `columnGroup: `[<code>`SingleColumn`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[<code>`DataRow`</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * `columnNoAccessor: `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * `columnOrSet: `[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[<code>`columnSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     * `columnSet: `[<code>`ColumnSet`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     * `columnsResolver: `[<code>`ColumnsResolver`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]
     *
     * `condition: `[<code>`ColumnFilter`</code>][org.jetbrains.kotlinx.dataframe.ColumnFilter]
     *
     * `expression: `[<code>Column Expression</code>][org.jetbrains.kotlinx.dataframe.documentation.ColumnExpression]
     *
     * `ignoreCase: `[<code>`Boolean`</code>][Boolean]
     *
     * `index: `[<code>`Int`</code>][Int]
     *
     * `indexRange: `[<code>`IntRange`</code>][IntRange]
     *
     * `infer: `[<code>`Infer`</code>][org.jetbrains.kotlinx.dataframe.api.Infer]
     *
     * `kind: `[<code>`ColumnKind`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnKind]
     *
     * `kType: `[<code>`KType`</code>][kotlin.reflect.KType]
     *
     * `name: `[<code>`String`</code>][String]
     *
     * `number: `[<code>`Int`</code>][Int]
     *
     * `regex: `[<code>`Regex`</code>][Regex]
     *
     * `singleColumn: `[<code>`SingleColumn`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[<code>`DataRow`</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<*>>`
     *
     * `T: Column type`
     *
     * `text: `[<code>`String`</code>][String]
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called directly in the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]` `[<code>**`..`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnRangeColumnsSelectionDsl.rangeTo]` `[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]
     *
     * `| `**`this`**`/`**`it`**[**`[`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]<code></code>[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**` .. `[**`]`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]
     *
     * `| `**`this`**`/`**`it`**[<code>**`[`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**[<code>**`]`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]
     *
     * `| `[<code>**`all`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]**`()`**
     *
     * `| `**`all`**`(`[<code>**`Before`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`|`[<code>**`After`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`|`[<code>**`From`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`|`[<code>**`UpTo`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`) ( `**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`)`**` | `**`{ `**[<code>`colSelector`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSelectorDef]**` }`**` )`
     *
     * `| `[<code>**`allExcept`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]**`  {  `**[<code>`colsSelector`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsSelectorDef]**` }`**
     *
     * `| `[<code>**`allExcept`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**` ..`**`)`**
     *
     * `| `[<code>`columnOrSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]` `[<code>**`and`**</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]`  [  `**`{`**`  ]  `[<code>`columnOrSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]`  [  `**`}`**`  ]  `
     *
     * `| `[<code>`columnOrSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]__`.`__[<code>**`and`**</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]**` (`**`|`**`{ `**[<code>`columnOrSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]**` }`**`|`**`)`**
     *
     * `| (`[<code>**`col`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`| `[<code>**`valueCol`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol]`| `[<code>**`frameCol`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`| `[<code>**`colGroup`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`)[`**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**
     *
     * `| (`[<code>**`cols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`| `[<code>**`valueCols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`| `[<code>**`frameCols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`| `[<code>**`colGroups`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`) [ `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     * `| `[<code>**`cols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`[`**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**` .. | `[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`,`**` .. | `[<code>`indexRange`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexRangeDef]**`)`**
     *
     * `| `[<code>**`colsAtAnyDepth`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * `| `[<code>**`colsInGroups`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`()`
     *
     * `| `[<code>**colsOf**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`  [  `**`(`**[<code>`kType`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.KTypeDef]**`)`**`  ] [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     * `| `[<code>**`colsOfKind`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]**`(`**[<code>`kind`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnKindDef]**`,`**` ..`**`)`**`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     * `| `[<code>**`drop`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop]`(`[<code>**`Last`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLast]`)`**`(`**[<code>`number`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     * `| `[<code>**`drop`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile]`(`[<code>**`Last`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile]`)`[<code>**`While`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile]**`  {  `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
     *
     * `| `[<code>**`expr`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]**`(`**`[`[<code>`name`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NameDef]**`,`**`][`[<code>`infer`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.InferDef]`]`**`) { `**[<code>`expression`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnExpressionDef]**` }`**
     *
     * `| (`[<code>**`first`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`|`[<code>**`last`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.last]`) [ `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     * `| `[<code>**`single`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`()`
     *
     * `| `[<code>**`nameContains`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]**`(`**[<code>`text`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[<code>`ignoreCase`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`] | `[<code>`regex`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.RegexDef]**`)`**
     *
     * `| `__`name`__`(`[<code>**`Starts`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`|`[<code>**`Ends`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameEndsWith]`)`**`With`**__`(`__[<code>`text`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[<code>`ignoreCase`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`]`**`)`**
     *
     * `| `[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]` `[<code>**named**</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.named]`/`[<code>**into**</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.into]` `[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]
     *
     * `| `[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`(`__`.`__[<code>**named**</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.named]`|`__`.`__[<code>**into**</code>][org.jetbrains.kotlinx.dataframe.api.RenameColumnsSelectionDsl.into]`)`**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`)`**
     *
     * `| `[<code>**`none`**</code>][org.jetbrains.kotlinx.dataframe.api.NoneColumnsSelectionDsl.none]**`()`**
     *
     * `| `[<code>**`take`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take]`(`[<code>**`Last`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLast]`)`**`(`**[<code>`number`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     * `| `[<code>**`take`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile]`(`[<code>**`Last`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile]`)`[<code>**`While`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile]**`  {  `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
     *
     * `| `[<code>**`withoutNulls`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]**`()`**
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;[**`[`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]<code></code>[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef][**`]`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `[<code>**`[`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]<code></code>[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`,`**` .. | `[<code>`indexRange`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexRangeDef][**`]`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `[<code>**`[`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**[<code>**`]`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`all`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `**`.all`**`(`[<code>**`Before`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`|`[<code>**`After`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`|`[<code>**`From`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`|`[<code>**`UpTo`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`) ( `**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`)`**`  |  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` )`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`and`**</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]**` (`**`|`**`{ `**[<code>`columnOrSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]**` }`**`|`**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| (`__`.`__[<code>**`col`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`| `__`.`__[<code>**`valueCol`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol]`| `__`.`__[<code>**`frameCol`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`| `__`.`__[<code>**`colGroup`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`)`**`(`**[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| (`__`.`__[<code>**`cols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`| `__`.`__[<code>**`valueCols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`| `__`.`__[<code>**`frameCols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`| `__`.`__[<code>**`colGroups`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`) [ `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`cols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]**`(`**[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`,`**` .. | `[<code>`indexRange`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexRangeDef]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`colsAtAnyDepth`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`colsInGroups`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`colsOf`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`  [  `**`(`**[<code>`kType`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.KTypeDef]**`)`**`  ] [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`colsOfKind`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]**`(`**[<code>`kind`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnKindDef]**`,`**` ..`**`)`**`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`distinct`**</code>][org.jetbrains.kotlinx.dataframe.api.DistinctColumnsSelectionDsl.distinct]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`drop`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.drop]`(`[<code>**`Last`**</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.dropLast]`)`**`(`**[<code>`number`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`drop`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile]`(`[<code>**`Last`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropLastWhile]`)`[<code>**`While`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.dropWhile]**`  {  `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `[<code>**`except`**</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` [`**`  {  `**`] `[<code>`columnsResolver`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsResolverDef]` [`**`  }  `**`]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `[<code>**`except`**</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` `[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `**`.`**[<code>**`except`**</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**` ..`**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`filter`**</code>][org.jetbrains.kotlinx.dataframe.api.FilterColumnsSelectionDsl.filter]**`  {  `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| (`__`.`__[<code>**`first`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.first]`|`__`.`__[<code>**`last`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.last]`) [ `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`single`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.single]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.name`__`(`[<code>**`Starts`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameStartsWith]`|`[<code>**`Ends`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameEndsWith]`)`**`With`**__`(`__[<code>`text`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[<code>`ignoreCase`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`]`**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`nameContains`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.nameContains]**`(`**[<code>`text`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[<code>`ignoreCase`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`] | `[<code>`regex`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.RegexDef]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`simplify`**</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`take`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.take]`(`[<code>**`Last`**</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.takeLast]`)`**`(`**[<code>`number`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`take`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile]`(`[<code>**`Last`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeLastWhile]`)`[<code>**`While`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.takeWhile]**`  {  `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`withoutNulls`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.withoutNulls]**`()`**
     *
     *
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [<code>Column Group (reference)</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]:
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [<code>`columnGroup`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `[<code>**`[`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]<code></code>[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**` ..`[<code>**`]`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `[<code>**`[`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**[<code>**`]`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `[<code>**`{`**</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]` `[<code>`colsSelector`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsSelectorDef]` `[<code>**`}`**</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`allCols`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `**`.allCols`**`(`[<code>**`Before`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsBefore]`|`[<code>**`After`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allAfter]`|`[<code>**`From`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsFrom]`|`[<code>**`UpTo`**</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allColsUpTo]`) ( `**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`)`**`  |  `**`{ `**[<code>`colSelector`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSelectorDef]**` }`**` )`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`allColsExcept`**</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept]**`  {  `**[<code>`colsSelector`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsSelectorDef]**`  }  `**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`allColsExcept`**</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept]**`(`**[<code>`columnNoAccessor`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnNoAccessorDef]**`,`**` ..`**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`and`**</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]**` (`**`|`**`{ `**[<code>`columnOrSet`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnOrColumnSetDef]**` }`**`|`**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| (`__`.`__[<code>**`col`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.col]`| `__`.`__[<code>**`valueCol`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCol]`| `__`.`__[<code>**`frameCol`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.frameCol]`| `__`.`__[<code>**`colGroup`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroup]`)[`**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]`  |  `[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| (`__`.`__[<code>**`cols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`| `__`.`__[<code>**`valueCols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.valueCols]`| `__`.`__[<code>**`frameCols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`| `__`.`__[<code>**`colGroups`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]`) [ `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`cols`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`[`**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`]`**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**` .. | `[<code>`index`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexDef]**`,`**` .. | `[<code>`indexRange`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IndexRangeDef]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`colsAtAnyDepth`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`colsInGroups`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsInGroups]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.colsName`__`(`[<code>**`Starts`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameStartsWith]`|`[<code>**`Ends`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameEndsWith]`)`**`With`**__`(`__[<code>`text`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[<code>`ignoreCase`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`]`**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`colsNameContains`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnNameFiltersColumnsSelectionDsl.colsNameContains]**`(`**[<code>`text`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.TextDef]`[`**`, `**[<code>`ignoreCase`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.IgnoreCaseDef]`] | `[<code>`regex`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.RegexDef]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`colsOfKind`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colGroups]**`(`**[<code>`kind`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnKindDef]**`,`**` ..`**`)`**`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`colsWithoutNulls`**</code>][org.jetbrains.kotlinx.dataframe.api.WithoutNullsColumnsSelectionDsl.colsWithoutNulls]**`()`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`drop`**</code>][org.jetbrains.kotlinx.dataframe.api.DropColumnsSelectionDsl.dropCols]`(`[<code>**`Last`**</code>][org.jetbrains.kotlinx.dataframe.api.DropColumnsSelectionDsl.dropLastCols]`)`[<code>**`Cols`**</code>][org.jetbrains.kotlinx.dataframe.api.DropColumnsSelectionDsl.dropCols]**`(`**[<code>`number`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`drop`**</code>][org.jetbrains.kotlinx.dataframe.api.DropColumnsSelectionDsl.dropColsWhile]`(`[<code>**`Last`**</code>][org.jetbrains.kotlinx.dataframe.api.DropColumnsSelectionDsl.dropLastColsWhile]`)`[<code>**`ColsWhile`**</code>][org.jetbrains.kotlinx.dataframe.api.DropColumnsSelectionDsl.dropColsWhile]**`  {  `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`except`**</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]**`  {  `**[<code>`colsSelector`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsSelectorDef]**`  }  `**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`except`**</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]**`(`**[<code>`columnNoAccessor`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnNoAccessorDef]**`,`**` ..`**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| (`__`.`__[<code>**`firstCol`**</code>][org.jetbrains.kotlinx.dataframe.api.FirstColumnsSelectionDsl.firstCol]`|`__`.`__[<code>**`lastCol`**</code>][org.jetbrains.kotlinx.dataframe.api.LastColumnsSelectionDsl.lastCol]`) [ `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`singleCol`**</code>][org.jetbrains.kotlinx.dataframe.api.SingleColumnsSelectionDsl.singleCol]`()`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`select`**</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]**`  {  `**[<code>`colsSelector`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsSelectorDef]**` }`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`take`**</code>][org.jetbrains.kotlinx.dataframe.api.TakeColumnsSelectionDsl.takeCols]`(`[<code>**`Last`**</code>][org.jetbrains.kotlinx.dataframe.api.TakeColumnsSelectionDsl.takeLastCols]`)`[<code>**`Cols`**</code>][org.jetbrains.kotlinx.dataframe.api.TakeColumnsSelectionDsl.takeCols]**`(`**[<code>`number`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.NumberDef]**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`take`**</code>][org.jetbrains.kotlinx.dataframe.api.TakeColumnsSelectionDsl.takeColsWhile]`(`[<code>**`Last`**</code>][org.jetbrains.kotlinx.dataframe.api.TakeColumnsSelectionDsl.takeLastColsWhile]`)`[<code>**`ColsWhile`**</code>][org.jetbrains.kotlinx.dataframe.api.TakeColumnsSelectionDsl.takeColsWhile]**`  {  `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [<code>`singleColumn`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.SingleColumnDef]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`colsOf`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>`**`  [  `**`(`**[<code>`kType`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.KTypeDef]**`)`**`  ] [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * [<code>`columnGroupReference`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupNoSingleColumnDef]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`colsOf`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]**`<`**[<code>`T`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnTypeDef]**`>(`**[<code>`kType`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.KTypeDef]**`)`**`  [  `**`{ `**[<code>`condition`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ConditionDef]**` }`**` ]`
     *
     *
     */
    public interface DslGrammar

    /**
     * Invokes the given [<code>ColumnsSelector</code>][ColumnsSelector] using this [<code>ColumnsSelectionDsl</code>][ColumnsSelectionDsl].
     */
    public operator fun <C> ColumnsSelector<T, C>.invoke(): ColumnsResolver<C> =
        this@invoke(this@ColumnsSelectionDsl, this@ColumnsSelectionDsl)

    // region select
    // NOTE: due to invoke conflicts these cannot be moved out of the interface

    /**
     * ## Select from [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than the [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] filter, because now all
     * operations of the DSL are at your disposal.
     *
     * The scope of the new DSL instance is relative to
     * the [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] you are selecting from.
     *
     * The [<code>invoke</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * For more information: [See Select from Column Group on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#select-from-column-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]`  { someCol  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>String</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "myGroupCol"  `[<code>{</code>][kotlin.String.select]`  "colA" and  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]`  { 0 }  `[<code>}</code>][kotlin.String.select]` }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>select</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[<code>asColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[<code>() {</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]`  "colA" and "colB"  `[<code>}</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { myColGroup.`[<code>`select`</code>][SingleColumn.select]`  { someCol  `[<code>`and`</code>][ColumnsSelectionDsl.and]` `[<code>`colsOf`</code>][SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  { myColGroup  `[<code>`{`</code>][SingleColumn.select]`  colA  `[<code>and</code>][ColumnsSelectionDsl.and]`  colB  `[<code>`}`</code>][SingleColumn.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]/[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] for the inverted operation of this function.
     *
     * @param [selector] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    public operator fun <C, R> SingleColumn<DataRow<C>>.invoke(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        select(selector)

    /**
     * ## Select from [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than the [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] filter, because now all
     * operations of the DSL are at your disposal.
     *
     * The scope of the new DSL instance is relative to
     * the [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] you are selecting from.
     *
     * The [<code>invoke</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * For more information: [See Select from Column Group on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#select-from-column-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]`  { someCol  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>String</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "myGroupCol"  `[<code>{</code>][kotlin.String.select]`  "colA" and  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]`  { 0 }  `[<code>}</code>][kotlin.String.select]` }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>select</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[<code>asColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[<code>() {</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]`  "colA" and "colB"  `[<code>}</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { Type::myColGroup.`[<code>`select`</code>][KProperty.select]`  { someCol  `[<code>`and`</code>][ColumnsSelectionDsl.and]` `[<code>`colsOf`</code>][SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  { DataSchemaType::myColGroup  `[<code>`{`</code>][KProperty.select]`  colA  `[<code>`and`</code>][ColumnsSelectionDsl.and]`  colB  `[<code>`}`</code>][KProperty.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]/[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] for the inverted operation of this function.
     *
     * @param [selector] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public operator fun <C, R> KProperty<C>.invoke(selector: ColumnsSelector<C, R>): ColumnSet<R> =
        columnGroup(this).select(selector)

    /**
     * ## Select from [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than the [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] filter, because now all
     * operations of the DSL are at your disposal.
     *
     * The scope of the new DSL instance is relative to
     * the [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] you are selecting from.
     *
     * The [<code>invoke</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * For more information: [See Select from Column Group on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#select-from-column-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]`  { someCol  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>String</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "myGroupCol"  `[<code>{</code>][kotlin.String.select]`  "colA" and  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]`  { 0 }  `[<code>}</code>][kotlin.String.select]` }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>select</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[<code>asColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[<code>() {</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]`  "colA" and "colB"  `[<code>}</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "myColGroup".`[<code>`select`</code>][String.select]`  { someCol  `[<code>`and`</code>][ColumnsSelectionDsl.and]` `[<code>`colsOf`</code>][SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  { "myColGroup"  `[<code>`{`</code>][String.select]`  colA  `[<code>`and`</code>][ColumnsSelectionDsl.and]`  colB  `[<code>`}`</code>][String.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]/[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] for the inverted operation of this function.
     *
     * @param [selector] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    public operator fun <R> String.invoke(selector: ColumnsSelector<*, R>): ColumnSet<R> = select(selector)

    /**
     * ## Select from [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     *
     * Perform a selection of columns using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] on
     * any [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]. This is more powerful than the [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols] filter, because now all
     * operations of the DSL are at your disposal.
     *
     * The scope of the new DSL instance is relative to
     * the [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] you are selecting from.
     *
     * The [<code>invoke</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.invoke] operator is overloaded to work as a shortcut for this method.
     *
     * For more information: [See Select from Column Group on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#select-from-column-group)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.Grammar]
     *
     * #### For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]`  { someCol  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.colsOf]`<`[<code>String</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { "myGroupCol"  `[<code>{</code>][kotlin.String.select]`  "colA" and  `[<code>expr</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.expr]`  { 0 }  `[<code>}</code>][kotlin.String.select]` }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { "pathTo"["myGroupCol"].`[<code>select</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath.select]` { "colA" and "colB" } }`
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { it["myGroupCol"].`[<code>asColumnGroup</code>][org.jetbrains.kotlinx.dataframe.DataColumn.asColumnGroup]`()`[<code>() {</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]`  "colA" and "colB"  `[<code>}</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.select]` }`
     *
     * #### Examples for this overload:
     *
     * `df.`[<code>select</code>][DataFrame.select]` { "pathTo"["myColGroup"].`[<code>`select`</code>][ColumnPath.select]`  { someCol  `[<code>`and`</code>][ColumnsSelectionDsl.and]` `[<code>`colsOf`</code>][SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  { "pathTo"["myColGroup"]  `[<code>`{`</code>][ColumnPath.select]`  colA  `[<code>`and`</code>][ColumnsSelectionDsl.and]`  colB  `[<code>`}`</code>][ColumnPath.select]` }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>`pathOf`</code>][pathOf]`("pathTo", "myColGroup").`[<code>`select`</code>][ColumnPath.select]`  { someCol  `[<code>`and`</code>][ColumnsSelectionDsl.and]` `[<code>`colsOf`</code>][SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() } }`
     *
     * `df.`[<code>select</code>][DataFrame.select]`  {  `[<code>`pathOf`</code>][pathOf]`("pathTo", "myColGroup")`[<code>`() {`</code>][ColumnPath.select]`  someCol  `[<code>`and`</code>][ColumnsSelectionDsl.and]` `[<code>`colsOf`</code>][SingleColumn.colsOf]`<`[<code>`String`</code>][String]`>() `[<code>`}`</code>][ColumnPath.select]` }`
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * See also [<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]/[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] for the inverted operation of this function.
     *
     * @param [selector] The [<code>ColumnsSelector</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector] to use for the selection.
     * @receiver The [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] to select from.
     * @throws [IllegalArgumentException] If [this] is not a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing the columns selected by [selector].
     * @see [SingleColumn.except]
     */
    public operator fun <R> ColumnPath.invoke(selector: ColumnsSelector<*, R>): ColumnSet<R> = select(selector)

    // endregion
}
