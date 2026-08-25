package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.ColumnsSelector
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.AccessApiOverload
import org.jetbrains.kotlinx.dataframe.annotations.Interpretable
import org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.ColumnGroupDocs.ARGUMENT_1
import org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.ColumnGroupDocs.ARGUMENT_2
import org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.ColumnGroupDocs.RECEIVER_1
import org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.ColumnGroupDocs.RECEIVER_2
import org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.ColumnGroupDocs.RECEIVER_TYPE
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.ColumnWithPath
import org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver
import org.jetbrains.kotlinx.dataframe.columns.SingleColumn
import org.jetbrains.kotlinx.dataframe.columns.toColumnSet
import org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApiLink
import org.jetbrains.kotlinx.dataframe.documentation.DocumentationUrls
import org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate
import org.jetbrains.kotlinx.dataframe.documentation.ExcludeFromSources
import org.jetbrains.kotlinx.dataframe.documentation.Indent
import org.jetbrains.kotlinx.dataframe.documentation.LineBreak
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
 * ## (All) (Cols) Except [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
 *
 * See [<code>Grammar</code>][Grammar] for all functions in this interface.
 */
@Suppress("ClassName")
public interface AllExceptColumnsSelectionDsl {

    /**
     * ## (All) (Cols) Except Grammar
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
     *  `columnSet: `[<code>`ColumnSet`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]`<*>`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnGroup: `[<code>`SingleColumn`</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn]`<`[<code>`DataRow`</code>][org.jetbrains.kotlinx.dataframe.DataRow]`<*>> | `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `colsSelector: `[<code>`ColumnsSelector`</code>][org.jetbrains.kotlinx.dataframe.ColumnsSelector]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `column: `[<code>`ColumnAccessor`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnAccessor]`  |  `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnNoAccessor: `[<code>`String`</code>][String]`  |  `[<code>`ColumnPath`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath]
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *  `columnsResolver: `[<code>`ColumnsResolver`</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnsResolver]
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
     *  [<code>**`allExcept`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]**`   {   `**[<code>`colsSelector`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsSelectorDef]**` }`**
     *
     *  `| `[<code>**`allExcept`**</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**` ..`**`)`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;[<code>**`except`**</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` [`**`  {  `**`] `[<code>`columnsResolver`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsResolverDef]` [`**`  }  `**`]`
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `[<code>**`except`**</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` `[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `**`.`**[<code>**`except`**</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]**`(`**[<code>`column`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnDef]**`,`**` ..`**`)`**
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
     *  &nbsp;&nbsp;&nbsp;&nbsp;__`.`__[<code>**`allColsExcept`**</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept]**`  {  `**[<code>`colsSelector`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsSelectorDef]**`  }  `**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`allColsExcept`**</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept]**`(`**[<code>`columnNoAccessor`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnNoAccessorDef]**`,`**` ..`**`)`**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`except`**</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]**`  {  `**[<code>`colsSelector`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnsSelectorDef]**`  }  `**
     *
     *  &nbsp;&nbsp;&nbsp;&nbsp;`| `__`.`__[<code>**`except`**</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]**`(`**[<code>`columnNoAccessor`</code>][org.jetbrains.kotlinx.dataframe.documentation.DslGrammarTemplateColumnsSelectionDsl.DslGrammarTemplate.ColumnNoAccessorDef]**`,`**` ..`**`)`**
     *
     *
     *
     *
     *
     *
     */
    public interface Grammar {

        /** [<code>**`allExcept`**</code>][ColumnsSelectionDsl.allExcept] */
        public typealias PlainDslName = Nothing

        /** [<code>**`except`**</code>][ColumnsSelectionDsl.except] */
        public typealias ColumnSetName = Nothing

        /** __`.`__[<code>**`allColsExcept`**</code>][ColumnsSelectionDsl.allColsExcept] */
        public typealias ColumnGroupName = Nothing

        /** __`.`__[<code>**`except`**</code>][ColumnsSelectionDsl.except] */
        public typealias ColumnGroupExceptName = Nothing
    }

    // region ColumnSet

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Number</code>][Number]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` { "age" `[<code>and</code>][ColumnsSelectionDsl.and]` height } }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` { name.firstName } }`
     *
     * @param [selector] A lambda in which you specify the columns that need to be
     *   excluded from the [<code>ColumnSet</code>][ColumnSet]. The scope of the selector is the same as the outer scope.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    @Interpretable("ColumnSetExceptSelector")
    public infix fun <C> ColumnSet<C>.except(selector: () -> ColumnsResolver<*>): ColumnSet<C> = except(selector())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Number</code>][Number]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` "age" `[<code>and</code>][ColumnsSelectionDsl.and]` height }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` name.firstName }`
     *
     * @param [other] A [<code>ColumnsResolver</code>][ColumnsResolver] containing the columns that need to be
     *   excluded from the [<code>ColumnSet</code>][ColumnSet].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    @Interpretable("ColumnSetExceptColumnsResolver")
    public infix fun <C> ColumnSet<C>.except(other: ColumnsResolver<*>): ColumnSet<C> = exceptInternal(other)

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Number</code>][Number]`>().`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(age, userData.height) }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(name.firstName, name.middleName) }`
     *
     * @param [others] Any number of [<code>ColumnsResolvers</code>][ColumnsResolver] containing
     *  the columns that need to be excluded from the [<code>ColumnSet</code>][ColumnSet].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    @Interpretable("ColumnSetExceptColumnsResolvers")
    public fun <C> ColumnSet<C>.except(vararg others: ColumnsResolver<*>): ColumnSet<C> = except(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Number</code>][Number]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` "age" }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` "name" }`
     *
     * @param [other] A [<code>String</code>][String] referring to
     *  the column (relative to the current scope) that needs to be excluded from the [<code>ColumnSet</code>][ColumnSet].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    @Interpretable("ColumnSetExceptString")
    public infix fun <C> ColumnSet<C>.except(other: String): ColumnSet<C> = except(column<Any?>(other))

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Number</code>][Number]`>().`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`("age", "height") }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`("name") }`
     *
     * @param [others] Any number of [<code>Strings</code>][String] referring to
     *  the columns (relative to the current scope) that need to be excluded from the [<code>ColumnSet</code>][ColumnSet].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    @Interpretable("ColumnSetExceptStrings")
    public fun <C> ColumnSet<C>.except(vararg others: String): ColumnSet<C> = except(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Number</code>][Number]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` Person::age }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` Person::name }`
     *
     * @param [other] A [<code>KProperty</code>][KProperty] referring to
     *  the column (relative to the current scope) that needs to be excluded from the [<code>ColumnSet</code>][ColumnSet].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Number</code>][Number]`>().`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(Person::age, Person::height) }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(Person::name) }`
     *
     * @param [others] Any number of [<code>KProperties</code>][KProperty] referring to
     *  the columns (relative to the current scope) that need to be excluded from the [<code>ColumnSet</code>][ColumnSet].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Number</code>][Number]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` "userdata"["age"] }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` pathOf("name", "firstName") }`
     *
     * @param [other] A [<code>ColumnPath</code>][ColumnPath] referring to
     *  the column (relative to the current scope) that needs to be excluded from the [<code>ColumnSet</code>][ColumnSet].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    @Interpretable("ColumnSetExceptColumnPath")
    public infix fun <C> ColumnSet<C>.except(other: ColumnPath): ColumnSet<C> = except(column<Any?>(other))

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsOf]`<`[<code>Number</code>][Number]`>().`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(pathOf("age"), "userdata"["height"]) }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(name, age).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`("name"["firstName"], "name"["middleName"]) }`
     *
     * @param [others] Any number of [<code>ColumnPaths</code>][ColumnPath] referring to
     *  the columns (relative to the current scope) that need to be excluded from the [<code>ColumnSet</code>][ColumnSet].
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     *
     */
    @Interpretable("ColumnSetExceptColumnPaths")
    public fun <C> ColumnSet<C>.except(vararg others: ColumnPath): ColumnSet<C> = except(others.toColumnSet())

    // endregion

    // region ColumnsSelectionDsl

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { "age"  `[<code>and</code>][ColumnsSelectionDsl.and]` height }`` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { name.firstName }`` }`
     *
     * @param [selector] A lambda in which you specify the columns that need to be
     *  excluded from the current selection. The scope of the selector is the same as the outer scope.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("CSDslAllExceptSelector")
    public fun <C> ColumnsSelectionDsl<C>.allExcept(selector: ColumnsSelector<C, *>): ColumnSet<*> =
        this.asSingleColumn().allColsExcept(selector)

    /**
     *
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`(age, height)`` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`(name.firstName, name.middleName)`` }`
     *
     * @param [others] A [<code>ColumnsResolver</code>][ColumnsResolver] containing the columns that need to be
     *  excluded from the current selection.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("CSDslAllExceptColumnsResolvers")
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: ColumnsResolver<*>): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`("age", "height")`` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`("name")`` }`
     *
     * @param [others] Any number of [<code>Strings</code>][String] referring to
     *  the columns (relative to the current scope) that need to be excluded from the current selection.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("CSDslAllExceptStrings")
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: String): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`(Person::age, Person::height)`` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`(Person::name)`` }`
     *
     * @param [others] Any number of [<code>KProperties</code>][KProperty] referring to
     *  the columns (relative to the current scope) that need to be excluded from the current selection.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`(pathOf("age"), "userdata"["height"])`` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`("name"["firstName"], "name"["middleName"])`` }`
     *
     * @param [others] Any number of [<code>ColumnPaths</code>][ColumnPath] referring to
     *  the columns (relative to the current scope) that need to be excluded from the current selection.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("CSDslAllExceptColumnPaths")
    public fun ColumnsSelectionDsl<*>.allExcept(vararg others: ColumnPath): ColumnSet<*> =
        asSingleColumn().allColsExceptInternal(others.toColumnSet())

    // endregion

    // region SingleColumn
    // region allColsExcept

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[<code>allColsExcept</code>][SingleColumn.allColsExcept]<code>`  { "age"  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`name.`</code>[<code>allColsExcept</code>][SingleColumn.allColsExcept]<code>` { firstName }`</code>` }`
     *
     * @param [selector] A lambda in which you specify the columns that need to be
     *  excluded from the current selection in [this] column group. The other columns will be included in the selection
     *  by default. The scope of the selector is relative to the column group.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("ColumnGroupAllColsExceptSelector")
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[<code>allColsExcept</code>][SingleColumn.allColsExcept]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`name.`</code>[<code>allColsExcept</code>][SingleColumn.allColsExcept]<code>`("firstName", "middleName")`</code>` }`
     *
     * @param [others] Any number of [<code>Strings</code>][String] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("ColumnGroupAllColsExceptStrings")
    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: String): ColumnSet<*> =
        allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[<code>allColsExcept</code>][SingleColumn.allColsExcept]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`name.`</code>[<code>allColsExcept</code>][SingleColumn.allColsExcept]<code>`(Person::firstName, Person::middleName)`</code>` }`
     *
     * @param [others] Any number of [<code>KProperties</code>][KProperty] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[<code>allColsExcept</code>][SingleColumn.allColsExcept]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`name.`</code>[<code>allColsExcept</code>][SingleColumn.allColsExcept]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
     *
     * @param [others] Any number of [<code>ColumnPaths</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("ColumnGroupAllColsExceptColumnPaths")
    public fun SingleColumn<DataRow<*>>.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        allColsExceptInternal(others.toColumnSet())

    // endregion

    // region except

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[<code>except</code>][SingleColumn.except]<code>`  { "age"  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`name.`</code>[<code>except</code>][SingleColumn.except]<code>` { firstName }`</code>` }`
     *
     * @param [selector] A lambda in which you specify the columns that need to be
     *  excluded from the current selection in [this] column group. The other columns will be included in the selection
     *  by default. The scope of the selector is relative to the column group.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("ColumnGroupExceptSelector")
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[<code>except</code>][SingleColumn.except]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`name.`</code>[<code>except</code>][SingleColumn.except]<code>`("firstName", "middleName")`</code>` }`
     *
     * @param [others] Any number of [<code>Strings</code>][String] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("ColumnGroupExceptStrings")
    public fun <C> SingleColumn<DataRow<C>>.except(vararg others: String): SingleColumn<DataRow<C>> =
        exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[<code>except</code>][SingleColumn.except]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`name.`</code>[<code>except</code>][SingleColumn.except]<code>`(Person::firstName, Person::middleName)`</code>` }`
     *
     * @param [others] Any number of [<code>KProperties</code>][KProperty] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`userData.`</code>[<code>except</code>][SingleColumn.except]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`name.`</code>[<code>except</code>][SingleColumn.except]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
     *
     * @param [others] Any number of [<code>ColumnPaths</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("ColumnGroupExceptColumnPaths")
    public fun <C> SingleColumn<DataRow<C>>.except(vararg others: ColumnPath): SingleColumn<DataRow<C>> =
        exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[<code>except</code>][String.except]<code>`  { "age"  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"name".`</code>[<code>except</code>][String.except]<code>` { firstName }`</code>` }`
     *
     * @param [selector] A lambda in which you specify the columns that need to be
     *  excluded from the current selection in [this] column group. The other columns will be included in the selection
     *  by default. The scope of the selector is relative to the column group.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("StringExceptSelector")
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[<code>except</code>][String.except]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"name".`</code>[<code>except</code>][String.except]<code>`("firstName", "middleName")`</code>` }`
     *
     * @param [others] Any number of [<code>Strings</code>][String] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("StringExceptStrings")
    public fun String.except(vararg others: String): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[<code>except</code>][String.except]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"name".`</code>[<code>except</code>][String.except]<code>`(Person::firstName, Person::middleName)`</code>` }`
     *
     * @param [others] Any number of [<code>KProperties</code>][KProperty] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[<code>except</code>][String.except]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"name".`</code>[<code>except</code>][String.except]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
     *
     * @param [others] Any number of [<code>ColumnPaths</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("StringExceptColumnPaths")
    public fun String.except(vararg others: ColumnPath): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[<code>except</code>][KProperty.except]<code>`  { "age"  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[<code>except</code>][KProperty.except]<code>` { firstName }`</code>` }`
     *
     * @param [selector] A lambda in which you specify the columns that need to be
     *  excluded from the current selection in [this] column group. The other columns will be included in the selection
     *  by default. The scope of the selector is relative to the column group.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[<code>except</code>][KProperty.except]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[<code>except</code>][KProperty.except]<code>`("firstName", "middleName")`</code>` }`
     *
     * @param [others] Any number of [<code>Strings</code>][String] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[<code>except</code>][KProperty.except]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[<code>except</code>][KProperty.except]<code>`("firstName", "middleName")`</code>` }`
     *
     * @param [others] Any number of [<code>Strings</code>][String] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[<code>except</code>][KProperty.except]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[<code>except</code>][KProperty.except]<code>`(Person::firstName, Person::middleName)`</code>` }`
     *
     * @param [others] Any number of [<code>KProperties</code>][KProperty] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[<code>except</code>][KProperty.except]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[<code>except</code>][KProperty.except]<code>`(Person::firstName, Person::middleName)`</code>` }`
     *
     * @param [others] Any number of [<code>KProperties</code>][KProperty] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[<code>except</code>][KProperty.except]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[<code>except</code>][KProperty.except]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
     *
     * @param [others] Any number of [<code>ColumnPaths</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[<code>except</code>][KProperty.except]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[<code>except</code>][KProperty.except]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
     *
     * @param [others] Any number of [<code>ColumnPaths</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[<code>except</code>][ColumnPath.except]<code>`  { "age"  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"pathTo"["myColGroup"].`</code>[<code>except</code>][ColumnPath.except]<code>` { firstName }`</code>` }`
     *
     * @param [selector] A lambda in which you specify the columns that need to be
     *  excluded from the current selection in [this] column group. The other columns will be included in the selection
     *  by default. The scope of the selector is relative to the column group.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("ColumnPathExceptSelector")
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[<code>except</code>][ColumnPath.except]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"pathTo"["myColGroup"].`</code>[<code>except</code>][ColumnPath.except]<code>`("firstName", "middleName")`</code>` }`
     *
     * @param [others] Any number of [<code>Strings</code>][String] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("ColumnPathExceptStrings")
    public fun ColumnPath.except(vararg others: String): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[<code>except</code>][ColumnPath.except]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"pathTo"["myColGroup"].`</code>[<code>except</code>][ColumnPath.except]<code>`(Person::firstName, Person::middleName)`</code>` }`
     *
     * @param [others] Any number of [<code>KProperties</code>][KProperty] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[<code>except</code>][ColumnPath.except]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"pathTo"["myColGroup"].`</code>[<code>except</code>][ColumnPath.except]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
     *
     * @param [others] Any number of [<code>ColumnPaths</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("ColumnPathExceptColumnPaths")
    public fun ColumnPath.except(vararg others: ColumnPath): SingleColumn<DataRow<*>> =
        columnGroup(this).exceptInternal(others.toColumnSet())

    // endregion
    // endregion

    // region String

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[<code>allColsExcept</code>][String.allColsExcept]<code>`  { "age"  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"name".`</code>[<code>allColsExcept</code>][String.allColsExcept]<code>` { firstName }`</code>` }`
     *
     * @param [selector] A lambda in which you specify the columns that need to be
     *  excluded from the current selection in [this] column group. The other columns will be included in the selection
     *  by default. The scope of the selector is relative to the column group.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("StringAllColsExceptSelector")
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[<code>allColsExcept</code>][String.allColsExcept]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"name".`</code>[<code>allColsExcept</code>][String.allColsExcept]<code>`("firstName", "middleName")`</code>` }`
     *
     * @param [others] Any number of [<code>Strings</code>][String] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("StringAllColsExceptStrings")
    public fun String.allColsExcept(vararg others: String): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[<code>allColsExcept</code>][String.allColsExcept]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"name".`</code>[<code>allColsExcept</code>][String.allColsExcept]<code>`(Person::firstName, Person::middleName)`</code>` }`
     *
     * @param [others] Any number of [<code>KProperties</code>][KProperty] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`"userData".`</code>[<code>allColsExcept</code>][String.allColsExcept]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"name".`</code>[<code>allColsExcept</code>][String.allColsExcept]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
     *
     * @param [others] Any number of [<code>ColumnPaths</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("StringAllColsExceptColumnPaths")
    public fun String.allColsExcept(vararg others: ColumnPath): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    // endregion

    // region KProperty

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[<code>allColsExcept</code>][KProperty.allColsExcept]<code>`  { "age"  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[<code>allColsExcept</code>][KProperty.allColsExcept]<code>` { firstName }`</code>` }`
     *
     * @param [selector] A lambda in which you specify the columns that need to be
     *  excluded from the current selection in [this] column group. The other columns will be included in the selection
     *  by default. The scope of the selector is relative to the column group.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[<code>allColsExcept</code>][KProperty.allColsExcept]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[<code>allColsExcept</code>][KProperty.allColsExcept]<code>`("firstName", "middleName")`</code>` }`
     *
     * @param [others] Any number of [<code>Strings</code>][String] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[<code>allColsExcept</code>][KProperty.allColsExcept]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[<code>allColsExcept</code>][KProperty.allColsExcept]<code>`(Person::firstName, Person::middleName)`</code>` }`
     *
     * @param [others] Any number of [<code>KProperties</code>][KProperty] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`DataSchemaPerson::userData.`</code>[<code>allColsExcept</code>][KProperty.allColsExcept]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`Person::name.`</code>[<code>allColsExcept</code>][KProperty.allColsExcept]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
     *
     * @param [others] Any number of [<code>ColumnPaths</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[<code>allColsExcept</code>][ColumnPath.allColsExcept]<code>`  { "age"  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height }`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"pathTo"["myColGroup"].`</code>[<code>allColsExcept</code>][ColumnPath.allColsExcept]<code>` { firstName }`</code>` }`
     *
     * @param [selector] A lambda in which you specify the columns that need to be
     *  excluded from the current selection in [this] column group. The other columns will be included in the selection
     *  by default. The scope of the selector is relative to the column group.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("ColumnPathAllColsExceptSelector")
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
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[<code>allColsExcept</code>][ColumnPath.allColsExcept]<code>`("age", "height")`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"pathTo"["myColGroup"].`</code>[<code>allColsExcept</code>][ColumnPath.allColsExcept]<code>`("firstName", "middleName")`</code>` }`
     *
     * @param [others] Any number of [<code>Strings</code>][String] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("ColumnPathAllColsExceptStrings")
    public fun ColumnPath.allColsExcept(vararg others: String): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[<code>allColsExcept</code>][ColumnPath.allColsExcept]<code>`(Person::age, Person::height)`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"pathTo"["myColGroup"].`</code>[<code>allColsExcept</code>][ColumnPath.allColsExcept]<code>`(Person::firstName, Person::middleName)`</code>` }`
     *
     * @param [others] Any number of [<code>KProperties</code>][KProperty] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Deprecated(DEPRECATED_ACCESS_API)
    @AccessApiOverload
    public fun ColumnPath.allColsExcept(vararg others: KProperty<*>): ColumnSet<*> =
        columnGroup(this).allColsExceptInternal(others.toColumnSet())

    /**
     * ## (All) (Cols) Except
     *
     * Exclude a selection of columns from the current selection using the [<code>Columns Selection DSL</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl].
     *
     * For more information: [See (All) (Cols) Except on the documentation website.](https://kotlin.github.io/dataframe/columnselectors.html#all-cols-except)
     *
     * ### Check out: [<code>Grammar</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.Grammar]
     *
     * ### On [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * This function can be explained the easiest with [<code>ColumnSets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]. Let's say we want all
     * [<code>Int</code>][Int] columns apart from `age` and `height`.
     *
     * We can do:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  (age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height) }`
     *
     * which will 'subtract' the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by `age `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height` from the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] created by [<code>colsOf</code>][org.jetbrains.kotlinx.dataframe.api.colsOf]`<`[<code>Int</code>][Int]`>()`.
     *
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * This operation can also be used to exclude columns from [<code>Column Groups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup].
     *
     * For instance:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>colsAtAnyDepth</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.colsAtAnyDepth]`  { "a"  `[<code>in</code>][String.contains]` it.`[<code>name</code>][org.jetbrains.kotlinx.dataframe.DataColumn.name]`() } `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` userData.age }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     *
     * Note that the selection of columns to exclude from [<code>column sets</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] is always done relative to the outer
     * scope. Use the [<code>Extension Properties API</code>][org.jetbrains.kotlinx.dataframe.documentation.AccessApis.ExtensionPropertiesApi] to prevent scoping issues if possible.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Special case: If a column that needs to be removed appears multiple times in the [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet], it is excepted
     * each time it is encountered (including inside [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]). You could say the receiver [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet]
     * is [<code>simplified</code>][org.jetbrains.kotlinx.dataframe.api.SimplifyColumnsSelectionDsl.simplify] before the operation is performed:
     *
     * [<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a, a, a.b, a.b).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * `== `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(a).`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`(a.b)`
     *
     * ### In the [<code>ColumnsSelectionDsl</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl]
     * Instead of having to write [<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.except]` { ... }` in the DSL,
     * you can use [<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]` { ... }` to achieve the same result.
     *
     * For example:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>allExcept</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.allExcept]`  { userData.age  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` height } }`
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: All Cols Except
     * The variant of this function on [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] is a bit different as it changes the scope relative to
     * the column group.
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * In other words:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.allColsExcept]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } }`
     *
     * is shorthand for
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `[<code>all</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.all]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` colB } } }`
     *
     * or
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { myColGroup.`[<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols]`() `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]`  { myColGroup.colA  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` myColGroup.colB } }`
     *
     * &nbsp;&nbsp;&nbsp;&nbsp;
     *
     * Also note the name change, similar to [<code>allCols</code>][org.jetbrains.kotlinx.dataframe.api.AllColumnsSelectionDsl.allCols], this makes it clearer that you're selecting
     * columns inside the group, 'lifting' them out.
     *
     * ### On [<code>ColumnGroups</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup]: Except
     * This variant can be used to exclude some nested columns from a [<code>ColumnGroup</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnGroup] in the selection.
     * In contrast to [<code>allColsExcept</code>][org.jetbrains.kotlinx.dataframe.api.AllExceptColumnsSelectionDsl.allColsExcept],
     * this function does not 'lift' the columns out of the group, preserving the structure.
     *
     * So:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]` { colGroup.`[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.SingleColumn.except]` { col } }`
     *
     * is shorthand for:
     *
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.DataFrame.select]`  {  `[<code>cols</code>][org.jetbrains.kotlinx.dataframe.api.ColumnsSelectionDsl.cols]`(colGroup) `[<code>except</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet.except]` colGroup.col }`
     *
     * ### Examples for this overload
     * `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  {  `<code>`pathOf("userData").`</code>[<code>allColsExcept</code>][ColumnPath.allColsExcept]<code>`(pathOf("age"), "extraData"["item1"])`</code>` }`
     *
     *  `df.`[<code>select</code>][org.jetbrains.kotlinx.dataframe.api.SelectColumnsSelectionDsl.select]`  { city  `[<code>and</code>][org.jetbrains.kotlinx.dataframe.api.AndColumnsSelectionDsl.and]` `<code>`"pathTo"["myColGroup"].`</code>[<code>allColsExcept</code>][ColumnPath.allColsExcept]<code>`(pathOf("firstName"), "middleNames"["first"])`</code>` }`
     *
     * @param [others] Any number of [<code>ColumnPaths</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnPath] referring to
     *  the columns (relative to the column group) that need to be excluded from the current selection in [this]
     *  column group. The other columns will be included in the selection by default.
     * @return A [<code>ColumnSet</code>][org.jetbrains.kotlinx.dataframe.columns.ColumnSet] containing all columns in [this] except the specified ones.
     * @see ColumnsSelectionDsl.select
     * @see ColumnsSelectionDsl.all
     * @see ColumnsSelectionDsl.allBefore
     * @see ColumnsSelectionDsl.allAfter
     * @see ColumnsSelectionDsl.allUpTo
     * @see ColumnsSelectionDsl.allFrom
     */
    @Interpretable("ColumnPathAllColsExceptColumnPaths")
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
 * @param other The [<code>ColumnsResolver</code>][ColumnsResolver] to use for excluding columns.
 * @return A new [<code>SingleColumn</code>][SingleColumn] with the filtered columns excluded.
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
