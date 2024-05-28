package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApiLink
import org.jetbrains.kotlinx.dataframe.documentation.AccessApiLink
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
import org.jetbrains.kotlinx.dataframe.impl.aggregation.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.allColumnsExceptKeepingStructure
import org.jetbrains.kotlinx.dataframe.impl.columns.changePath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_EXCEPT
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_REPLACE
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_REPLACE_VARARG
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_EXCEPT
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_EXCEPT_REPLACE_RESOLVER
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_EXCEPT_REPLACE_SELECTOR
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_SINGLE_COL_EXCEPT
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_OTHER
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_OTHERS
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_RESOLVER
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_RESOLVERS
import org.jetbrains.kotlinx.dataframe.util.COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_SELECTOR
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## (All) (Cols) Except [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
public interface AllExceptColumnsSelectionDsl {

    /**
     * ## (All) (Cols) Except Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[`ColumnSet`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[`SingleColumn`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[`String`][String]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * `| `[`KProperty`][kotlin.reflect.KProperty]`<* | `[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `colsSelector: `[`ColumnsSelector`][org.jetbrains.kotlinx.dataframe.ColumnsSelector]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `column: `[`ColumnAccessor`][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[`String`][String]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * `| `[`KProperty`][kotlin.reflect.KProperty]`<*> | `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnNoAccessor: `[`String`][String]`  |  `[`KProperty`][kotlin.reflect.KProperty]`<*> | `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnsResolver: `[`ColumnsResolver`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called directly in the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [**`allExcept`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]**`   {   `**[`colsSelector`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsSelectorDef]**` }`**
     *
     *  `| `[**`allExcept`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]**`(`**[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**` ..`**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnSet`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnSetDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;[**`except`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` [`**`  {  `**`] `[`columnsResolver`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsResolverDef]` [`**`  }  `**`]`
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `[**`except`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` `[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `**`.`**[**`except`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]**`(`**[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**` ..`**`)`**
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### What can be called on a [Column Group (reference)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]:
     *
     *  
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  [`columnGroup`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnGroupDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[**`allColsExcept`**][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept]**`  {  `**[`colsSelector`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsSelectorDef]**`  }  `**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[**`allColsExcept`**][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept]**`(`**[`columnNoAccessor`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnNoAccessorDef]**`,`**` ..`**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `[**`exceptNew`**][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.exceptNew]**`  {  `**[`colsSelector`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsSelectorDef]**` } EXPERIMENTAL!`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `[**`exceptNew`**][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.exceptNew]**`(`**[`columnNoAccessor`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnNoAccessorDef]**`,`**` ..`**`) EXPERIMENTAL!`**
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
    public interface Grammar {

        /** [**`allExcept`**][ColumnsSelectionDsl.allExcept] */
        public interface PlainDslName

        /** [**`except`**][ColumnsSelectionDsl.except] */
        public interface ColumnSetName

        /** __`.`__[**`allColsExcept`**][ColumnsSelectionDsl.allColsExcept] */
        public interface ColumnGroupName

        /** [**`exceptNew`**][ColumnsSelectionDsl.exceptNew] */
        public interface ColumnGroupExperimentalName
    }

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar]
     *
     * ### On [ColumnSets][ColumnSet]
     * This function can be explained the easiest with [ColumnSets][ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsOf][colsOf]`<`[Int][Int]`>() `[except][ColumnSet.except]`  (age  `[and][ColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet] created by `age `[and][ColumnsSelectionDsl.and]` height` from the [ColumnSet] created by [colsOf][colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][DataFrame.select]`  {  `[colsAtAnyDepth][ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][DataColumn.name]`() } `[except][ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][ColumnGroup]). You could say the receiver [ColumnSet]
     * is [simplified][ColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][ColumnSet.except]`(a.b)`
     *
     * `== `[cols][ColumnsSelectionDsl.cols]`(a).`[except][ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][ColumnsSelectionDsl]
     * Instead of having to write [all][ColumnsSelectionDsl.all]`() `[except][ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][DataFrame.select]`  {  `[allExcept][ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][ColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][ColumnGroup]
     * The variant of this function on [ColumnGroups][ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[allColsExcept][SingleColumn.allColsExcept]`  { colA  `[and][ColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[select][ColumnsSelectionDsl.select]`  {  `[all][ColumnsSelectionDsl.all]`() `[except][ColumnSet.except]`  { colA  `[and][ColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][DataFrame.select]` { myColGroup.`[allCols][ColumnsSelectionDsl.allCols]`() `[except][ColumnSet.except]`  { myColGroup.colA  `[and][ColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][ColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *
     *
     * @return A [ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    private interface CommonExceptDocs {

        /* Example argument */
        interface ExampleArg

        /* Parameter argument  */
        interface ParamArg
    }

    // region ColumnSet

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][ColumnsSelectionDsl.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>() `[except][ColumnSet.except]` `` }`
     *
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `[cols][ColumnsSelectionDsl.cols]`(name, age) `[except][ColumnSet.except]` `` }`
     *
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    private interface ColumnSetInfixDocs {

        /* argument */
        interface ArgumentArg1

        /* argument */
        interface ArgumentArg2
    }

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][ColumnsSelectionDsl.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>().`[except][ColumnSet.except]` }`
     *
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `[cols][ColumnsSelectionDsl.cols]`(name, age).`[except][ColumnSet.except]` }`
     *
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    private interface ColumnSetVarargDocs {

        /* argument */
        interface ArgumentArg1

        /* argument */
        interface ArgumentArg2
    }

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` ``{ "age" `[and][ColumnsSelectionDsl.and]` height }`` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` ``{ name.firstName }`` }`
     *
     * @param [selector] A lambda in which you specify the columns that need to be
     *   excluded from the [ColumnSet]. The scope of the selector is the same as the outer scope.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    public infix fun <C> ColumnSet<C>.except(selector: () -> ColumnsResolver<*>): ColumnSet<C> = except(selector())

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` ``"age" `[and][ColumnsSelectionDsl.and]` height`` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` ``name.firstName`` }`
     *
     * @param [other] A [ColumnsResolver] containing the columns that need to be
     *   excluded from the [ColumnSet].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    public infix fun <C> ColumnSet<C>.except(other: ColumnsResolver<*>): ColumnSet<C> = exceptInternal(other)

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>().`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(age, userData.height)`` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(name.firstName, name.middleName)`` }`
     *
     * @param [others] Any number of [ColumnsResolvers][ColumnsResolver] containing
     *  the columns that need to be excluded from the [ColumnSet].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    public fun <C> ColumnSet<C>.except(vararg others: ColumnsResolver<*>): ColumnSet<C> = except(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` ``"age"`` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` ``"name"`` }`
     *
     * @param [other] A [String] referring to
     *  the column (relative to the current scope) that needs to be excluded from the [ColumnSet].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    public infix fun <C> ColumnSet<C>.except(other: String): ColumnSet<C> = except(column<Any?>(other))

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>().`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`("age", "height")`` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`("name")`` }`
     *
     * @param [others] Any number of [Strings][String] referring to
     *  the columns (relative to the current scope) that need to be excluded from the [ColumnSet].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    public fun <C> ColumnSet<C>.except(vararg others: String): ColumnSet<C> = except(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` ``Person::age`` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` ``Person::name`` }`
     *
     * @param [other] A [KProperty] referring to
     *  the column (relative to the current scope) that needs to be excluded from the [ColumnSet].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    public infix fun <C> ColumnSet<C>.except(other: KProperty<C>): ColumnSet<C> = except(column(other))

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>().`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(Person::age, Person::height)`` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(Person::name)`` }`
     *
     * @param [others] Any number of [KProperties][KProperty] referring to
     *  the columns (relative to the current scope) that need to be excluded from the [ColumnSet].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    public fun <C> ColumnSet<C>.except(vararg others: KProperty<C>): ColumnSet<C> = except(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` ``"userdata"["age"]`` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` ``pathOf("name", "firstName")`` }`
     *
     * @param [other] A [ColumnPath] referring to
     *  the column (relative to the current scope) that needs to be excluded from the [ColumnSet].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    public infix fun <C> ColumnSet<C>.except(other: ColumnPath): ColumnSet<C> = except(column<Any?>(other))

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>().`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(pathOf("age"), "userdata"["height"])`` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`("name"["firstName"], "name"["middleName"])`` }`
     *
     * @param [others] Any number of [ColumnPaths][ColumnPath] referring to
     *  the columns (relative to the current scope) that need to be excluded from the [ColumnSet].
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    public fun <C> ColumnSet<C>.except(vararg others: ColumnPath): ColumnSet<C> = except(others.toColumnSet())

    // endregion

    // region ColumnsSelectionDsl

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][ColumnsSelectionDsl.select]`  {  `[allExcept][ColumnsSelectionDsl.allExcept]` }`
     *
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `[allExcept][ColumnsSelectionDsl.allExcept]` }`
     *
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    private interface ColumnsSelectionDslDocs {

        /* argument */
        interface ArgumentArg1

        /* argument */
        interface ArgumentArg2
    }

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { "age"  `[and][ColumnsSelectionDsl.and]` height }`` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { name.firstName }`` }`
     *
     * @param [selector] A lambda in which you specify the columns that need to be
     *  excluded from the current selection. The scope of the selector is the same as the outer scope.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun <C> ColumnsSelectionDsl<C>.allExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        this.asSingleColumn().allColsExcept(selector)

    /**
     *
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`(age, height)`` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`(name.firstName, name.middleName)`` }`
     *
     * @param [others] A [ColumnsResolver] containing the columns that need to be
     *  excluded from the current selection.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`("age", "height")`` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`("name")`` }`
     *
     * @param [others] Any number of [Strings][String] referring to
     *  the columns (relative to the current scope) that need to be excluded from the current selection.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: String): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`(Person::age, Person::height)`` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`(Person::name)`` }`
     *
     * @param [others] Any number of [KProperties][KProperty] referring to
     *  the columns (relative to the current scope) that need to be excluded from the current selection.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: KProperty<*>): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`(pathOf("age"), "userdata"["height"])`` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`("name"["firstName"], "name"["middleName"])`` }`
     *
     * @param [others] Any number of [ColumnPaths][ColumnPath] referring to
     *  the columns (relative to the current scope) that need to be excluded from the current selection.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: ColumnPath): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    // endregion

    // region SingleColumn

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `<code></code>[allColsExcept][.allColsExcept]<code></code>` }`
     *
     *  `df.`[select][ColumnsSelectionDsl.select]`  { city  `[and][ColumnsSelectionDsl.and]` `<code></code>[allColsExcept][.allColsExcept]<code></code>` }`
     *
     *
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    private interface ColumnGroupDocs {

        /* receiver */
        interface ReceiverArg1

        /* receiver */
        interface ReceiverArg2

        /* type */
        interface ReceiverType

        /* argument */
        interface ArgumentArg1

        /* argument */
        interface ArgumentArg2

        /**
         */
        interface SingleColumnReceiverArgs

        /**
         */
        interface StringReceiverArgs

        /**
         */
        interface KPropertyReceiverArgs

        /**
         */
        interface ColumnPathReceiverArgs

        /**
         */
        interface SelectorArgs

        /**
         */
        interface StringArgs

        /**
         */
        interface KPropertyArgs

        /**
         */
        interface ColumnPathArgs
    }

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[allColsExcept][SingleColumn.allColsExcept]<code>`  { "age"  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`name.`</code>[allColsExcept][SingleColumn.allColsExcept]<code>` { firstName }`</code>` }`
     *
     * @param [selector] A lambda in which you specify the columns that need to be
     *  excluded from the current selection in [this] column group. The other columns will be included in the selection
     *  by default. The scope of the selector is relative to the column group.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun <C> SingleColumn<DataRow<C>>.allColsExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allColsExceptInternal(selector.toColumns())

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun SingleColumn<DataRow<*>>.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[allColsExcept][SingleColumn.allColsExcept]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`name.`</code>[allColsExcept][SingleColumn.allColsExcept]<code>`("firstName", "middleName")`</code>` }`
     *
     * @param [others] Any number of [Strings][String] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: String): ColumnSet<*> =
        allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[allColsExcept][SingleColumn.allColsExcept]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`name.`</code>[allColsExcept][SingleColumn.allColsExcept]<code>`(Person::firstName, Person::middleName)`</code>` }`
     *
     * @param [others] Any number of [KProperties][KProperty] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[allColsExcept][SingleColumn.allColsExcept]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`name.`</code>[allColsExcept][SingleColumn.allColsExcept]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
     *
     * @param [others] Any number of [ColumnPaths][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg other: ColumnPath): ColumnSet<*> =
        allColsExceptInternal(other.toColumnSet())

    // endregion

    // region String

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[allColsExcept][String.allColsExcept]<code>`  { "age"  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"name".`</code>[allColsExcept][String.allColsExcept]<code>` { firstName }`</code>` }`
     *
     * @param [selector] A lambda in which you specify the columns that need to be
     *  excluded from the current selection in [this] column group. The other columns will be included in the selection
     *  by default. The scope of the selector is relative to the column group.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun String.allColsExcept(selector: ColumnsSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun String.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun String.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[allColsExcept][String.allColsExcept]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"name".`</code>[allColsExcept][String.allColsExcept]<code>`("firstName", "middleName")`</code>` }`
     *
     * @param [others] Any number of [Strings][String] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun String.allColsExcept(vararg others: String): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[allColsExcept][String.allColsExcept]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"name".`</code>[allColsExcept][String.allColsExcept]<code>`(Person::firstName, Person::middleName)`</code>` }`
     *
     * @param [others] Any number of [KProperties][KProperty] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun String.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[allColsExcept][String.allColsExcept]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"name".`</code>[allColsExcept][String.allColsExcept]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
     *
     * @param [others] Any number of [ColumnPaths][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun String.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    // endregion

    // region KProperty

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[allColsExcept][KProperty.allColsExcept]<code>`  { "age"  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[allColsExcept][KProperty.allColsExcept]<code>` { firstName }`</code>` }`
     *
     * @param [selector] A lambda in which you specify the columns that need to be
     *  excluded from the current selection in [this] column group. The other columns will be included in the selection
     *  by default. The scope of the selector is relative to the column group.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun <C> KProperty<C>.allColsExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun KProperty<*>.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun KProperty<*>.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[allColsExcept][KProperty.allColsExcept]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[allColsExcept][KProperty.allColsExcept]<code>`("firstName", "middleName")`</code>` }`
     *
     * @param [others] Any number of [Strings][String] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun KProperty<*>.allColsExcept(vararg others: String): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[allColsExcept][KProperty.allColsExcept]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[allColsExcept][KProperty.allColsExcept]<code>`(Person::firstName, Person::middleName)`</code>` }`
     *
     * @param [others] Any number of [KProperties][KProperty] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun KProperty<*>.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[allColsExcept][KProperty.allColsExcept]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[allColsExcept][KProperty.allColsExcept]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
     *
     * @param [others] Any number of [ColumnPaths][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun KProperty<*>.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    // endregion

    // region ColumnPath

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[allColsExcept][ColumnPath.allColsExcept]<code>`  { "age"  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"pathTo"["myColGroup"].`</code>[allColsExcept][ColumnPath.allColsExcept]<code>` { firstName }`</code>` }`
     *
     * @param [selector] A lambda in which you specify the columns that need to be
     *  excluded from the current selection in [this] column group. The other columns will be included in the selection
     *  by default. The scope of the selector is relative to the column group.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun ColumnPath.allColsExcept(selector: ColumnsSelector<*, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun ColumnPath.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun ColumnPath.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[allColsExcept][ColumnPath.allColsExcept]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"pathTo"["myColGroup"].`</code>[allColsExcept][ColumnPath.allColsExcept]<code>`("firstName", "middleName")`</code>` }`
     *
     * @param [others] Any number of [Strings][String] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun ColumnPath.allColsExcept(vararg others: String): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[allColsExcept][ColumnPath.allColsExcept]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"pathTo"["myColGroup"].`</code>[allColsExcept][ColumnPath.allColsExcept]<code>`(Person::firstName, Person::middleName)`</code>` }`
     *
     * @param [others] Any number of [KProperties][KProperty] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun ColumnPath.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Perform a selection of columns using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl] to
     * exclude from the current selection.
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age`  and  `height`.
     *
     * We can do:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [colsOf][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[Int][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[colsAtAnyDepth][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[in][String.contains]` it.`[name][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [column sets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [Extension Properties API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [simplified][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]
     * The variant of this function on [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allColsExcept][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [allCols][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### Examples for this overload
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[allColsExcept][ColumnPath.allColsExcept]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"pathTo"["myColGroup"].`</code>[allColsExcept][ColumnPath.allColsExcept]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
     *
     * @param [others] Any number of [ColumnPaths][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    public fun ColumnPath.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    // endregion

    // region experiments

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][DataFrame.select]`  {  `[cols][ColumnsSelectionDsl.cols]`(colGroup) `[except][ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][DataFrame.select]`  { colGroup  `[exceptNew][SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except] functions
     * are deleted.
     */
    private interface ExperimentalExceptDocs

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public infix fun <C> SingleColumn<DataRow<C>>.exceptNew(selector: ColumnsSelector<C, *>): SingleColumn<DataRow<C>> =
        exceptExperimentalInternal(selector.toColumns())

    @ExperimentalExceptCsDsl
    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith("exceptNew { other }"),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public infix fun <C> SingleColumn<DataRow<C>>.exceptNew(other: ColumnsResolver<*>): SingleColumn<DataRow<C>> =
        exceptNew { other }

    @ExperimentalExceptCsDsl
    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith("exceptNew { others.toColumnSet() }"),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun <C> SingleColumn<DataRow<C>>.exceptNew(vararg others: ColumnsResolver<*>): SingleColumn<DataRow<C>> =
        exceptNew { others.toColumnSet() }

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public infix fun <C> SingleColumn<DataRow<C>>.exceptNew(other: String): SingleColumn<DataRow<C>> =
        exceptExperimentalInternal(column<Any?>(other))

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public fun <C> SingleColumn<DataRow<C>>.exceptNew(vararg others: String): SingleColumn<DataRow<C>> =
        exceptExperimentalInternal(others.toColumnSet())

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public infix fun <C> SingleColumn<DataRow<C>>.exceptNew(other: KProperty<C>): SingleColumn<DataRow<C>> =
        exceptExperimentalInternal(column(other))

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public fun <C> SingleColumn<DataRow<C>>.exceptNew(vararg others: KProperty<*>): SingleColumn<DataRow<C>> =
        exceptExperimentalInternal(others.toColumnSet())

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public infix fun <C> SingleColumn<DataRow<C>>.exceptNew(other: ColumnPath): SingleColumn<DataRow<C>> =
        exceptExperimentalInternal(column<Any?>(other))

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public fun <C> SingleColumn<DataRow<C>>.exceptNew(vararg others: ColumnPath): SingleColumn<DataRow<C>> =
        exceptExperimentalInternal(others.toColumnSet())

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public infix fun String.exceptNew(selector: ColumnsSelector<*, *>): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptNew(selector)

    @ExperimentalExceptCsDsl
    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith("exceptNew { other }"),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public infix fun String.exceptNew(other: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        exceptNew { other }

    @ExperimentalExceptCsDsl
    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith("exceptNew { others.toColumnSet() }"),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun String.exceptNew(vararg others: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        exceptNew { others.toColumnSet() }

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public infix fun String.exceptNew(other: String): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptExperimentalInternal(column<Any?>(other))

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public fun String.exceptNew(vararg others: String): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptExperimentalInternal(others.toColumnSet())

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public infix fun String.exceptNew(other: KProperty<*>): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptExperimentalInternal(column(other))

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public fun String.exceptNew(vararg others: KProperty<*>): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptExperimentalInternal(others.toColumnSet())

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public infix fun String.exceptNew(other: ColumnPath): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptExperimentalInternal(column<Any?>(other))

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public fun String.exceptNew(vararg others: ColumnPath): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptExperimentalInternal(others.toColumnSet())

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public infix fun <C> KProperty<C>.exceptNew(selector: ColumnsSelector<C, *>): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptExperimentalInternal(selector.toColumns())

    @ExperimentalExceptCsDsl
    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith("exceptNew { other }"),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public infix fun KProperty<*>.exceptNew(other: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        exceptNew { other }

    @ExperimentalExceptCsDsl
    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith("exceptNew { others.toColumnSet() }"),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun KProperty<*>.exceptNew(vararg others: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        exceptNew { others.toColumnSet() }

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public infix fun <C> KProperty<C>.exceptNew(other: String): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptExperimentalInternal(column<Any?>(other))

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public fun <C> KProperty<C>.exceptNew(vararg others: String): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptExperimentalInternal(others.toColumnSet())

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowExceptNew")
    public infix fun <C> KProperty<DataRow<C>>.exceptNew(other: String): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptExperimentalInternal(column<Any?>(other))

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowExceptNew")
    public fun <C> KProperty<DataRow<C>>.exceptNew(vararg others: String): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptExperimentalInternal(others.toColumnSet())

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public infix fun <C> KProperty<C>.exceptNew(other: KProperty<*>): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptExperimentalInternal(column(other))

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public fun <C> KProperty<C>.exceptNew(vararg others: KProperty<*>): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptExperimentalInternal(others.toColumnSet())

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowExceptNew")
    public infix fun <C> KProperty<DataRow<C>>.exceptNew(other: KProperty<*>): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptExperimentalInternal(column(other))

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowExceptNew")
    public fun <C> KProperty<DataRow<C>>.exceptNew(vararg others: KProperty<*>): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptExperimentalInternal(others.toColumnSet())

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public infix fun <C> KProperty<C>.exceptNew(other: ColumnPath): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptExperimentalInternal(column<Any?>(other))

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public fun <C> KProperty<C>.exceptNew(vararg others: ColumnPath): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptExperimentalInternal(others.toColumnSet())

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowExceptNew")
    public infix fun <C> KProperty<DataRow<C>>.exceptNew(other: ColumnPath): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptExperimentalInternal(column<Any?>(other))

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowExceptNew")
    public fun <C> KProperty<DataRow<C>>.exceptNew(vararg others: ColumnPath): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptExperimentalInternal(others.toColumnSet())

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public infix fun ColumnPath.exceptNew(selector: ColumnsSelector<*, *>): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptExperimentalInternal(selector.toColumns<Any?, Any?>())

    @ExperimentalExceptCsDsl
    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith("exceptNew { other }"),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public infix fun ColumnPath.exceptNew(other: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        exceptNew { other }

    @ExperimentalExceptCsDsl
    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith("exceptNew { others.toColumnSet() }"),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun ColumnPath.exceptNew(vararg others: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        exceptNew { others.toColumnSet() }

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public infix fun ColumnPath.exceptNew(other: String): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptExperimentalInternal(column<Any?>(other))

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public fun ColumnPath.exceptNew(vararg others: String): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptExperimentalInternal(others.toColumnSet())

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public infix fun ColumnPath.exceptNew(other: KProperty<*>): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptExperimentalInternal(column(other))

    @ExperimentalExceptCsDsl
    public fun ColumnPath.exceptNew(vararg others: KProperty<*>): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptExperimentalInternal(others.toColumnSet())

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public infix fun ColumnPath.exceptNew(other: ColumnPath): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptExperimentalInternal(column<Any?>(other))

    /**
     * ## EXPERIMENTAL: Except on Column Group
     *
     * Selects the current column group itself, except for the specified columns. This is different from
     * [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept] in that it does not 'lift' the columns out of the group, but instead selects the group itself.
     *
     * As usual, all overloads for each [Access API][org.jetbrains.kotlinx.dataframe.documentation.AccessApi] are available.
     *
     * These produce the same result:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  { colGroup  `[exceptNew][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * These functions are experimental and may be removed or changed in the future.
     *
     * Trying these functions requires you to `@`[`OptIn`][OptIn]`(`[ExperimentalExceptCsDsl][org.jetbrains.kotlinx.dataframe.api.ExperimentalExceptCsDsl]`::class)` first.
     *
     * ## NOTE:
     * `exceptNew` will likely be renamed to `except` when the deprecated [SingleColumn.except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except] functions
     * are deleted.
     */
    @ExperimentalExceptCsDsl
    public fun ColumnPath.exceptNew(vararg others: ColumnPath): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptExperimentalInternal(others.toColumnSet())

    // endregion

    // region deprecated

    @Deprecated(
        message = COL_SELECT_DSL_SINGLE_COL_EXCEPT,
        replaceWith = ReplaceWith(COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_SELECTOR),
        level = DeprecationLevel.ERROR,
    )
    public infix fun <C> SingleColumn<DataRow<C>>.except(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        allColsExcept(selector)

    @Deprecated(
        message = COL_SELECT_DSL_SINGLE_COL_EXCEPT,
        replaceWith = ReplaceWith(COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_RESOLVER),
        level = DeprecationLevel.ERROR,
    )
    public infix fun SingleColumn<DataRow<*>>.except(other: ColumnsResolver<*>): ColumnSet<*> = allColsExcept { other }

    @Deprecated(
        message = COL_SELECT_DSL_SINGLE_COL_EXCEPT,
        replaceWith = ReplaceWith(COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_RESOLVERS),
        level = DeprecationLevel.ERROR,
    )
    public fun SingleColumn<DataRow<*>>.except(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    @Deprecated(
        message = COL_SELECT_DSL_SINGLE_COL_EXCEPT,
        replaceWith = ReplaceWith(COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_OTHER),
        level = DeprecationLevel.ERROR,
    )
    public infix fun SingleColumn<DataRow<*>>.except(other: String): ColumnSet<*> = allColsExcept(other)

    @Deprecated(
        message = COL_SELECT_DSL_SINGLE_COL_EXCEPT,
        replaceWith = ReplaceWith(COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_OTHERS),
        level = DeprecationLevel.ERROR,
    )
    public fun SingleColumn<DataRow<*>>.except(vararg others: String): ColumnSet<*> = allColsExcept(*others)

    @Deprecated(
        message = COL_SELECT_DSL_SINGLE_COL_EXCEPT,
        replaceWith = ReplaceWith(COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_OTHER),
        level = DeprecationLevel.ERROR,
    )
    public infix fun SingleColumn<DataRow<*>>.except(other: KProperty<*>): ColumnSet<*> = allColsExcept(other)

    @Deprecated(
        message = COL_SELECT_DSL_SINGLE_COL_EXCEPT,
        replaceWith = ReplaceWith(COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_OTHERS),
        level = DeprecationLevel.ERROR,
    )
    public fun SingleColumn<DataRow<*>>.except(vararg others: KProperty<*>): ColumnSet<*> = allColsExcept(*others)

    @Deprecated(
        message = COL_SELECT_DSL_SINGLE_COL_EXCEPT,
        replaceWith = ReplaceWith(COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_OTHER),
        level = DeprecationLevel.ERROR,
    )
    public infix fun SingleColumn<DataRow<*>>.except(other: ColumnPath): ColumnSet<*> = allColsExcept(other)

    @Deprecated(
        message = COL_SELECT_DSL_SINGLE_COL_EXCEPT,
        replaceWith = ReplaceWith(COL_SELECT_DSL_SINGLE_COL_EXCEPT_REPLACE_OTHERS),
        level = DeprecationLevel.ERROR,
    )
    public fun SingleColumn<DataRow<*>>.except(vararg others: ColumnPath): ColumnSet<*> = allColsExcept(*others)

    @Deprecated(
        message = COL_SELECT_DSL_EXCEPT,
        replaceWith = ReplaceWith(COL_SELECT_DSL_EXCEPT_REPLACE_SELECTOR),
        level = DeprecationLevel.ERROR,
    )
    public fun <C> ColumnsSelectionDsl<C>.except(selector: ColumnsSelector<C, *>): ColumnSet<*> = allExcept(selector)

    @Deprecated(
        message = COL_SELECT_DSL_EXCEPT,
        replaceWith = ReplaceWith(COL_SELECT_DSL_EXCEPT_REPLACE_RESOLVER),
        level = DeprecationLevel.ERROR,
    )
    public fun ColumnsSelectionDsl<*>.except(vararg others: ColumnsResolver<*>): ColumnSet<*> = allExcept(*others)

    @Deprecated(
        message = COL_SELECT_DSL_EXCEPT,
        replaceWith = ReplaceWith(COL_SELECT_DSL_EXCEPT_REPLACE_RESOLVER),
        level = DeprecationLevel.ERROR,
    )
    public fun ColumnsSelectionDsl<*>.except(vararg others: String): ColumnSet<*> = allExcept(*others)

    @Deprecated(
        message = COL_SELECT_DSL_EXCEPT,
        replaceWith = ReplaceWith(COL_SELECT_DSL_EXCEPT_REPLACE_RESOLVER),
        level = DeprecationLevel.ERROR,
    )
    public fun ColumnsSelectionDsl<*>.except(vararg others: KProperty<*>): ColumnSet<*> = allExcept(*others)

    @Deprecated(
        message = COL_SELECT_DSL_EXCEPT,
        replaceWith = ReplaceWith(COL_SELECT_DSL_EXCEPT_REPLACE_RESOLVER),
        level = DeprecationLevel.ERROR,
    )
    public fun ColumnsSelectionDsl<*>.except(vararg others: ColumnPath): ColumnSet<*> = allExcept(*others)

    // endregion
}

/**
 * Removes the columns in the "other" ColumnsResolver from the current ColumnSet while keeping the structure intact.
 * Returns a new ColumnSet with the remaining columns.
 *
 * @param other The ColumnsResolver containing the columns to be removed.
 * @return The new ColumnSet with the remaining columns.
 */
@Suppress("UNCHECKED_CAST")
internal fun <C> ColumnSet<C>.exceptInternal(other: ColumnsResolver<*>): ColumnSet<C> = createColumnSet { context ->
    val resolvedCols = this.resolve(context)
    val resolvedColsToExcept = other.resolve(context)
    resolvedCols.allColumnsExceptKeepingStructure(resolvedColsToExcept)
} as ColumnSet<C>

/**
 * Returns a new ColumnSet that contains all columns from inside the receiver column group
 * except those specified in the "other" ColumnsResolver.
 *
 * @param other The ColumnsResolver containing the columns to be removed.
 * @return The new ColumnSet with the remaining columns.
 */
internal fun SingleColumn<DataRow<*>>.allColsExceptInternal(other: ColumnsResolver<*>): ColumnSet<Any?> =
    selectInternal { all().exceptInternal(other) }

/**
 * Returns a new SingleColumn<DataRow<C>> that has the same structure as the receiver, but excludes columns
 * specified in the "other" ColumnsResolver.
 *
 * @param other The [ColumnsResolver] to use for excluding columns.
 * @return A new [SingleColumn] with the filtered columns excluded.
 */
@Suppress("UNCHECKED_CAST")
internal fun <C> SingleColumn<DataRow<C>>.exceptExperimentalInternal(
    other: ColumnsResolver<*>,
): SingleColumn<DataRow<C>> = this.ensureIsColumnGroup().transformSingle { singleCol ->
    val columnsToExcept = singleCol.asColumnGroup()
        .getColumnsWithPaths { other }
        .map { it.changePath(singleCol.path + it.path) }

    val newCols = listOf(singleCol).allColumnsExceptKeepingStructure(columnsToExcept)

    newCols as List<ColumnWithPath<DataRow<*>>>
}.singleInternal() as SingleColumn<DataRow<C>>

/**
 * Functions annotated with this annotation are experimental and will be removed or renamed in the future.
 */
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
@Target(AnnotationTarget.FUNCTION)
public annotation class ExperimentalExceptCsDsl

// endregion
