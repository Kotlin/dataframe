package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.aggregation.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.removeAll
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_EXCEPT
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_EXCEPT_COLUMN_PATH
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_REPLACE
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_REPLACE_VARARG
import org.jetbrains.kotlinx.dataframe.util.ALL_EXCEPT_COLUMN_PATH
import kotlin.reflect.KProperty

// region ColumnsSelectionDsl

/**
 * ## (All) (Cols) Except [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [Grammar] for all functions in this interface.
 */
@Suppress("ClassName")
public interface AllExceptColumnsSelectionDsl {

    /**
     * ## (All) (Cols) Except Grammar
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * [(What is this notation?)][org.jetbrains.kotlinx.dataframe.documentation.DslGrammar]
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  ### Definitions:
     *  `columnSet: `[`ColumnSet`][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[`SingleColumn`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[`String`][String]`  |  `[`KProperty`][kotlin.reflect.KProperty]`<* | `[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `colsSelector: `[`ColumnsSelector`][org.jetbrains.kotlinx.dataframe.ColumnsSelector]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `column: `[`ColumnAccessor`][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[`String`][String]`  | `[`KProperty`][kotlin.reflect.KProperty]`<*> | `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnName: `[`String`][String]`  |  `[`KProperty`][kotlin.reflect.KProperty]`<*>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnNoPath: `[`ColumnAccessor`][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[`String`][String]`  | `[`KProperty`][kotlin.reflect.KProperty]`<*>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnsResolver: `[`ColumnsResolver`][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]
     *
     *
     *
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
     *  `| `[**`allExcept`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]**`(`**[`columnNoPath`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnNoPathDef]**`,`**` ..`**`)`**
     *
     *
     *
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;[**`except`**][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` [`**`  {  `**`] `[`columnsResolver`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsResolverDef]` [`**`  }  `**`]`
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `[**`except`**][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` `[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `**`.`**[**`except`**][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]**`(`**[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**` ..`**`)`**
     *
     *
     *
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[**`allColsExcept`**][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept]**`(`**[`columnName`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumNameDef]**`,`**` ..`**`)`**
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
     * This operation can also be used to exclude columns originating from [Column Groups][ColumnGroup], although
     * they need to be directly included in the [ColumnSet], like when using [`colsAtAnyDepth`][ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][ColumnGroup]):
     *
     * [cols][ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][ColumnSet.except]`(a.b)`
     *
     * `== `[cols][ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][ColumnsSelectionDsl]
     * Instead of having to write [all][ColumnsSelectionDsl.all]`() `[except][ColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][DataFrame.select]`  {  `[allExcept][ColumnsSelectionDsl.allExcept]`  { userData  `[and][ColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
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

        // Example argument
        interface EXAMPLE

        // Parameter argument
        interface PARAM
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][ColumnsSelectionDsl.select]`  {  `[colsOf][ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>() `[except][ColumnSet.except]`  }`
     *
     *  `df.`[select][ColumnsSelectionDsl.select]`  {  `[cols][ColumnsSelectionDsl.cols]`(name, age) `[except][ColumnSet.except]`  }`
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

        // argument
        interface ARGUMENT_1

        // argument
        interface ARGUMENT_2
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
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

        // argument
        interface ARGUMENT_1

        // argument
        interface ARGUMENT_2
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` { "age" `[and][ColumnsSelectionDsl.and]` height } }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` { name.firstName } }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` "age" `[and][ColumnsSelectionDsl.and]` height }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` name.firstName }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>().`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(age, userData.height) }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(name.firstName, name.middleName) }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` "age" }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` "name" }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>().`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`("age", "height") }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`("name") }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` Person::age }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` Person::name }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>().`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(Person::age, Person::height) }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(Person::name) }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>() `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` "userdata"["age"] }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` pathOf("name", "firstName") }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[colsOf][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[Number][Number]`>().`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(pathOf("age"), "userdata"["height"]) }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`("name"["firstName"], "name"["middleName"]) }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
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

        // argument
        interface ARGUMENT_1

        // argument
        interface ARGUMENT_2
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { "age"  `[and][ColumnsSelectionDsl.and]` height } }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { name.firstName } }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`(age, height) }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`(name.firstName, name.middleName) }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`("age", "height") }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`("name") }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`(Person::age, Person::height) }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`(Person::name) }`
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

    @Deprecated(message = ALL_EXCEPT_COLUMN_PATH, level = DeprecationLevel.ERROR)
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][ColumnsSelectionDsl.select]`  {  `<code></code>[allColsExcept][.allColsExcept]<code></code>` }`
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
    @Suppress("ClassName")
    private interface ColumnGroupDocs {

        // receiver
        interface RECEIVER_1

        // receiver
        interface RECEIVER_2

        // type
        interface RECEIVER_TYPE

        // argument
        interface ARGUMENT_1

        // argument
        interface ARGUMENT_2

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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[allColsExcept][SingleColumn.allColsExcept]<code>`  { "age"  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[allColsExcept][SingleColumn.allColsExcept]<code>`("age", "height")`</code>` }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[allColsExcept][SingleColumn.allColsExcept]<code>`(Person::age, Person::height)`</code>` }`
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

    @Deprecated(message = ALL_COLS_EXCEPT_COLUMN_PATH, level = DeprecationLevel.ERROR)
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[allColsExcept][String.allColsExcept]<code>`  { "age"  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[allColsExcept][String.allColsExcept]<code>`("age", "height")`</code>` }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[allColsExcept][String.allColsExcept]<code>`(Person::age, Person::height)`</code>` }`
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

    @Deprecated(message = ALL_COLS_EXCEPT_COLUMN_PATH, level = DeprecationLevel.ERROR)
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[allColsExcept][KProperty.allColsExcept]<code>`  { "age"  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[allColsExcept][KProperty.allColsExcept]<code>`("age", "height")`</code>` }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[allColsExcept][KProperty.allColsExcept]<code>`(Person::age, Person::height)`</code>` }`
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

    @Deprecated(message = ALL_COLS_EXCEPT_COLUMN_PATH, level = DeprecationLevel.ERROR)
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[allColsExcept][ColumnPath.allColsExcept]<code>`  { "age"  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[allColsExcept][ColumnPath.allColsExcept]<code>`("age", "height")`</code>` }`
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
     * This operation can also be used to exclude columns originating from [Column Groups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup], although
     * they need to be directly included in the [ColumnSet][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], like when using [`colsAtAnyDepth`][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth].
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
     * each time it is encountered (including inside [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]):
     *
     * [cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b).`[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [ColumnsSelectionDsl][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
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
     * columns inside the group, not the group itself.
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[allColsExcept][ColumnPath.allColsExcept]<code>`(Person::age, Person::height)`</code>` }`
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

    @Deprecated(message = ALL_COLS_EXCEPT_COLUMN_PATH, level = DeprecationLevel.ERROR)
    public fun ColumnPath.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    // endregion
}

/**
 * Removes the columns in the "other" ColumnsResolver from the current ColumnSet.
 * Returns a new ColumnSet with the remaining columns.
 *
 * @param other The ColumnsResolver containing the columns to be removed.
 * @return The new ColumnSet with the remaining columns.
 */
@Suppress("UNCHECKED_CAST")
internal fun <C> ColumnSet<C>.exceptInternal(other: ColumnsResolver<*>): ColumnSet<C> =
    createColumnSet { context ->
        val resolvedCols = this.resolve(context)
        val resolvedColsToExcept = other.resolve(context)
        resolvedCols.removeAll(resolvedColsToExcept)
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

// endregion
