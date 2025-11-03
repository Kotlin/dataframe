package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.impl.aggregation.toColumns
import org.jetbrains.kotlinx.dataframe.impl.columns.allColumnsExceptKeepingStructure
import org.jetbrains.kotlinx.dataframe.impl.columns.changePath
import org.jetbrains.kotlinx.dataframe.impl.columns.createColumnSet
import org.jetbrains.kotlinx.dataframe.impl.columns.transformSingle
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_EXCEPT
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_EXCEPT_REPLACE
import org.jetbrains.kotlinx.dataframe.util.ALL_COLS_EXCEPT_REPLACE_VARARG
import org.jetbrains.kotlinx.dataframe.util.DEPRECATED_ACCESS_API
import org.jetbrains.kotlinx.dataframe.util.EXCEPT_REPLACE
import org.jetbrains.kotlinx.dataframe.util.EXCEPT_REPLACE_VARARG
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
     *  `columnGroup: `[`SingleColumn`][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[`DataRow`][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `colsSelector: `[`ColumnsSelector`][org.jetbrains.kotlinx.dataframe.ColumnsSelector]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `column: `[`ColumnAccessor`][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnNoAccessor: `[`String`][String]`  |  `[`ColumnPath`][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
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
     *  `| `[**`allExcept`**][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]**`(`**[`column`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**` ..`**`)`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[**`allColsExcept`**][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept]**`(`**[`columnNoAccessor`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnNoAccessorDef]**`,`**` ..`**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[**`except`**][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]**`  {  `**[`colsSelector`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsSelectorDef]**`  }  `**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[**`except`**][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]**`(`**[`columnNoAccessor`][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnNoAccessorDef]**`,`**` ..`**`)`**
     *
     *
     *
     *
     *
     *
     */
    public interface Grammar {

        /** [**`allExcept`**][ColumnsSelectionDsl.allExcept] */
        public typealias PlainDslName = Nothing

        /** [**`except`**][ColumnsSelectionDsl.except] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[**`allColsExcept`**][ColumnsSelectionDsl.allColsExcept] */
        public typealias ColumnGroupName = Nothing

        /** __`.`__[**`except`**][ColumnsSelectionDsl.except] */
        public typealias ColumnGroupExceptName = Nothing
    }

    // region ColumnSet

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public infix fun <C> ColumnSet<C>.except(other: KProperty<C>): ColumnSet<C> = except(column(other))

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> ColumnSet<C>.except(vararg others: KProperty<C>): ColumnSet<C> = except(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: KProperty<*>): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
    // region allColsExcept

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
        replaceWith = ReplaceWith(ALL_COLS_EXCEPT_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun SingleColumn<DataRow<*>>.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_EXCEPT_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[allColsExcept][SingleColumn.allColsExcept]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
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

    // region except

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[except][SingleColumn.except]<code>`  { "age"  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`name.`</code>[except][SingleColumn.except]<code>` { firstName }`</code>` }`
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
    public fun <C> SingleColumn<DataRow<C>>.except(selector: ColumnsSelector<C, *>): SingleColumn<DataRow<C>> =
        exceptInternal(selector.toColumns())

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(EXCEPT_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun <C> SingleColumn<DataRow<C>>.except(vararg others: ColumnsResolver<*>): SingleColumn<DataRow<C>> =
        except { others.toColumnSet() }

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[except][SingleColumn.except]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`name.`</code>[except][SingleColumn.except]<code>`("firstName", "middleName")`</code>` }`
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
    public fun <C> SingleColumn<DataRow<C>>.except(vararg others: String): SingleColumn<DataRow<C>> =
        exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[except][SingleColumn.except]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`name.`</code>[except][SingleColumn.except]<code>`(Person::firstName, Person::middleName)`</code>` }`
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> SingleColumn<DataRow<C>>.except(vararg others: KProperty<*>): SingleColumn<DataRow<C>> =
        exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[except][SingleColumn.except]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`name.`</code>[except][SingleColumn.except]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
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
    public fun <C> SingleColumn<DataRow<C>>.except(vararg others: ColumnPath): SingleColumn<DataRow<C>> =
        exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[except][String.except]<code>`  { "age"  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"name".`</code>[except][String.except]<code>` { firstName }`</code>` }`
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
    public fun String.except(selector: ColumnsSelector<*, *>): SingleColumn<DataRow<*>> =
        columnGroup(this).except(selector)

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(EXCEPT_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun String.except(other: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        except { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(EXCEPT_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun String.except(vararg others: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        except { others.toColumnSet() }

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[except][String.except]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"name".`</code>[except][String.except]<code>`("firstName", "middleName")`</code>` }`
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
    public fun String.except(vararg others: String): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[except][String.except]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"name".`</code>[except][String.except]<code>`(Person::firstName, Person::middleName)`</code>` }`
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun String.except(vararg others: KProperty<*>): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[except][String.except]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"name".`</code>[except][String.except]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
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
    public fun String.except(vararg others: ColumnPath): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[except][KProperty.except]<code>`  { "age"  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[except][KProperty.except]<code>` { firstName }`</code>` }`
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.except(selector: ColumnsSelector<C, *>): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptInternal(selector.toColumns())

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(EXCEPT_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    @AccessApiOverload
    public fun KProperty<*>.except(other: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        except { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(EXCEPT_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    @AccessApiOverload
    public fun KProperty<*>.except(vararg others: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        except { others.toColumnSet() }

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[except][KProperty.except]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[except][KProperty.except]<code>`("firstName", "middleName")`</code>` }`
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.except(vararg others: String): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[except][KProperty.except]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[except][KProperty.except]<code>`("firstName", "middleName")`</code>` }`
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
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowExceptNew")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<DataRow<C>>.except(vararg others: String): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[except][KProperty.except]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[except][KProperty.except]<code>`(Person::firstName, Person::middleName)`</code>` }`
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.except(vararg others: KProperty<*>): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[except][KProperty.except]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[except][KProperty.except]<code>`(Person::firstName, Person::middleName)`</code>` }`
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
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowExceptNew")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<DataRow<C>>.except(vararg others: KProperty<*>): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[except][KProperty.except]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[except][KProperty.except]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.except(vararg others: ColumnPath): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[except][KProperty.except]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[except][KProperty.except]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
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
    @Suppress("INAPPLICABLE_JVM_NAME")
    @JvmName("KPropertyDataRowExceptNew")
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<DataRow<C>>.except(vararg others: ColumnPath): SingleColumn<DataRow<C>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[except][ColumnPath.except]<code>`  { "age"  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"pathTo"["myColGroup"].`</code>[except][ColumnPath.except]<code>` { firstName }`</code>` }`
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
    public fun ColumnPath.except(selector: ColumnsSelector<*, *>): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(selector.toColumns<Any?, Any?>())

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(EXCEPT_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun ColumnPath.except(other: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        except { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(EXCEPT_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun ColumnPath.except(vararg others: ColumnsResolver<*>): SingleColumn<DataRow<*>> =
        except { others.toColumnSet() }

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[except][ColumnPath.except]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"pathTo"["myColGroup"].`</code>[except][ColumnPath.except]<code>`("firstName", "middleName")`</code>` }`
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
    public fun ColumnPath.except(vararg others: String): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[except][ColumnPath.except]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"pathTo"["myColGroup"].`</code>[except][ColumnPath.except]<code>`(Person::firstName, Person::middleName)`</code>` }`
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun ColumnPath.except(vararg others: KProperty<*>): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[except][ColumnPath.except]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"pathTo"["myColGroup"].`</code>[except][ColumnPath.except]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
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
    public fun ColumnPath.except(vararg others: ColumnPath): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    // endregion
    // endregion

    // region String

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
        replaceWith = ReplaceWith(ALL_COLS_EXCEPT_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun String.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_EXCEPT_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun String.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun String.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[allColsExcept][String.allColsExcept]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
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
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun <C> KProperty<C>.allColsExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        columnGroup(this).allColsExcept(selector)

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_EXCEPT_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    @AccessApiOverload
    public fun KProperty<*>.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_EXCEPT_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    @AccessApiOverload
    public fun KProperty<*>.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsExcept(vararg others: String): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[allColsExcept][KProperty.allColsExcept]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
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
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun KProperty<*>.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    // endregion

    // region ColumnPath

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
        replaceWith = ReplaceWith(ALL_COLS_EXCEPT_REPLACE),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun ColumnPath.allColsExcept(other: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { other }

    @Deprecated(
        message = ALL_COLS_EXCEPT,
        replaceWith = ReplaceWith(ALL_COLS_EXCEPT_REPLACE_VARARG),
        level = DeprecationLevel.ERROR,
    ) // present solely to redirect users to the right function
    public fun ColumnPath.allColsExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        allColsExcept { others.toColumnSet() }

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
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

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [Columns Selection DSL][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * ### Check out: [Grammar][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [ColumnSets][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [Int] columns apart from `age` and `height`.
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
     * Instead of having to write [all][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[except][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[allExcept][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[and][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
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
     * ### On [ColumnGroups][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [ColumnGroup][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [allColsExcept][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[except][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[select][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[cols][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[except][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[select][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[allColsExcept][ColumnPath.allColsExcept]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
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
}

/**
 * Removes the columns in the "other" ColumnsResolver from the current ColumnSet while keeping the structure intact.
 * Returns a new ColumnSet with the remaining columns.
 *
 * @param other The ColumnsResolver containing the columns to be removed.
 * @return The new ColumnSet with the remaining columns.
 */
@Suppress("UNCHECKED_CAST")
internal fun <C> ColumnSet<C>.exceptInternal(other: ColumnsResolver<*>): ColumnSet<C> =
    createColumnSet { context ->
        val resolvedCols = this.resolve(context)
        val resolvedColsToExcept = other.resolve(context).toSet()
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
@JvmName("exceptInternalSingleColumn")
internal fun <C> SingleColumn<DataRow<C>>.exceptInternal(other: ColumnsResolver<*>): SingleColumn<DataRow<C>> =
    ensureIsColumnGroup().transformSingle { singleCol ->
        val columnsToExcept = singleCol
            .asColumnGroup()
            .getColumnsWithPaths { other }
            .map { it.changePath(singleCol.path + it.path) }
            .toSet()

        val newCols = listOf(singleCol).allColumnsExceptKeepingStructure(columnsToExcept)

        newCols as List<ColumnWithPath<DataRow<*>>>
    }.singleInternal() as SingleColumn<DataRow<C>>

// endregion
